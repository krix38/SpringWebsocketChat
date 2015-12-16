global.SockJS = require('sockjs-client');
global.Stomp = require('stompjs');

var WsConnector = {

    stompClient: null,
    messagesStore: null,
    usersStore: null,

    connect: function(){
                if(this.stompClient == null || this.stompClient.connected == false){
                     var appPath = window.location.pathname.substring(0, window.location.pathname.lastIndexOf('/'));
                     var socket = new SockJS( appPath + '/jsonApi/wsMsgApi');
                     this.stompClient = Stomp.over(socket);
                     this.stompClient.connect({}, function(frame) {
                         console.log('Connected: ' + frame);
                         this.stompClient.subscribe('/msgStream/messages', function(message){
                             this.messagesStore.receiveMessage(JSON.parse(message.body));
                         }.bind(this));
                         this.stompClient.subscribe('/msgStream/userEvents', function(event){
                             this.usersStore.handleUserEvent(JSON.parse(event.body));
                         }.bind(this));
                     }.bind(this));
                }
             },

    disconnect: function(){
        this.stompClient.disconnect();
    },

    sendMessage: function(msg){
                 this.stompClient.send("/app/wsMsgApi", {}, JSON.stringify({ 'message': msg }));
             }
}

exports.WsConnector = WsConnector;