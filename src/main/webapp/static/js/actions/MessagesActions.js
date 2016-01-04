'use strict';

var Reflux = require("reflux");

var MessageActions = Reflux.createActions([
    "sendMessage",
    "getAllMessages",
    "getMessagesSince",
    "messageReceived"
]);

exports.MessageActions = MessageActions;
