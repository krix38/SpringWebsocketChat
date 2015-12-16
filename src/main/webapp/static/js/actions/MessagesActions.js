var Reflux = require("reflux");
'use strict';

var MessageActions = Reflux.createActions([
    "sendMessage",
    "getAllMessages",
    "getMessagesSince",
    "messageReceived"
]);

exports.MessageActions = MessageActions;
