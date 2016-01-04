'use strict';

var Reflux = require("reflux");
var Actions = require("../actions/MessagesActions.js");
var MessageActions = Actions.MessageActions;
var stompClient = null;


    var localStorageKey = "messages";
    var MessageStore = Reflux.createStore({

            wsConnector: null,

            listenables: [MessageActions],

            lastMessageId: 0,

            onSendMessage: function(message){
          this.wsConnector.sendMessage(message);
            },

            onGetAllMessages: function(){
              $.ajax({
              		url: "jsonApi/getAllMessages",
              		dataType: 'json',
              		cache: false,
              		success: function(objMessageList) {
              			var messageList = [];
              			var message = "";
              			for (var index = 0, len = objMessageList.length; index < len; ++index) {
              			    message = objMessageList[index];
              			    messageList.push(message.author.login + " : " + message.message);
              			}
              			this.updateMessageList(messageList);
              		}.bind(this),
              		error: function(xhr, status, err) {
              			alert("fetching messages failed")
              		}
              });
            },

            onGetMessagesSince: function(id){
              $.ajax({
              		url: "jsonApi/getMessagesSince/" + id,
              		dataType: 'json',
              		cache: false,
              		success: function(data) {
              			var newMessages = [];
              			this.updateMessageList(this.list.concat(newMessages));
              		}.bind(this),
              		error: function(xhr, status, err) {
              			alert("fetching messages failed")
              		}
              });
            },

            onMessageReceived: function(message){
                this.updateMessageList(this.list.push(message));
            },

            receiveMessage: function(msg){
                if(msg.event){
                    var message = msg.user.login + " has " + msg.event;
                }else{
                    var message = msg.author.login + " : " + msg.message;
                }
                this.list.push(message);
                this.updateMessageList(this.list);
            },

            updateMessageList: function(list){
              localStorage.setItem(localStorageKey, JSON.stringify(list));
              this.list = list;
              this.trigger(list);
            },

            getInitialState: function() {
                var loadedList = localStorage.getItem(localStorageKey);
                if (!loadedList) {
                    this.list = [];
                } else {
                    this.list = JSON.parse(loadedList);
                }
                return this.list;
            }

    })

exports.MessageStore = MessageStore;
