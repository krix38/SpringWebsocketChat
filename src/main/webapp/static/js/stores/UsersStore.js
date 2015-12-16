var Reflux = require("reflux");
var Actions = require("../actions/UsersActions.js");
var UsersActions = Actions.UsersActions;
var stompClient = null;
    'use strict';


    var localStorageKey = "users";
    var UsersStore = Reflux.createStore({

            messagesStore: null,

            CSRF: null,

            listenables: [UsersActions],

            onGetAllUsers: function(){
              $.ajax({
              		url: "jsonApi/getConnectedUsers",
              		dataType: 'json',
              		cache: false,
              		success: function(data, status, xhr) {
              			var users = data;
              			this.updateUsersList(users);
              			this.CSRF = xhr.getResponseHeader('X-CSRF-TOKEN');
              		}.bind(this),
              		error: function(xhr, status, err) {
              			alert("fetching users failed")
              		}
              });
            },

            handleUserEvent: function(event){
                this.messagesStore.receiveMessage(event);
                this.onGetAllUsers();
            },

            updateUsersList: function(list){
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

exports.UsersStore = UsersStore;
