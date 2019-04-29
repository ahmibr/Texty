const jwt = require('jsonwebtoken');
const utils = require('../util');
const userkey = require('../keys').api.userSecret;
const mongoose = require('mongoose');

var exports = module.exports = {};

exports.User = null;
exports.Conversation = null;
exports.Message = null;

exports.request = async (req, res) => {
    var path = req.path;
    var result = 404;
    var authData = await jwt.verify(req.body.token, userkey);
    var username = false;
    if (authData)
        username = authData.userData.username;

    if (username) {
        if (path === "/conversations")
        var result = await exports.getConversations(username);
        else if (path === "/messages")
            result = await exports.getMessages(username, req.body.conversation);
        else if (path === "/create")
            result = await exports.findOrCreate(username, req.body.participants);
        else if (path === "/send")
            result = await exports.send(username, req.body.conversation, req.body.message);
    }
    else
        result = 403;
    
    if (result === 404)
        res.sendStatus(404);
    else if (result === 403)
        res.sendStatus(403);
    else
        res.json(result);
}

exports.getConversations = async (username) => {
    var conversations = await exports.Conversation.find({ participantsNames: { $all: [username] } });
    if (conversations) {
        console.log(conversations);
        return { message: "conversations was retrived successfully", conversations: conversations };
    }
    else
        return { message: "no conversations available!!!", conversations: null };
}

exports.getMessages = async (username, conversationId) => {
    var conversation = await exports.Conversation.find({ _id: conversationId, participantsNames: { $all: [username] } });
    if (conversation) {
        var messages = await exports.Message.find({ conversationId: conversationId });
        if (messages.length !== 0)
            return { message: "messages was retrived successfully", errors: null, messages: messages };
        else
            return { message: "no messages available in this conversation!!!", errors: null, messages: null };
    }
    else
        return { message: "messages was not retrived successfully", errors: ["Error: conversation doesn't exist!!!"], messages: null };
}

exports.findOrCreate = async (username, participants) => {
    var usernames = await exports.User.findAll({ attributes: ['username'] });
    if (utils.contains(utils.usernames2list(usernames), participants) && utils.contains(participants, [username])) {
        var conversation = await exports.Conversation.find({ participantsNames: participants });
        if (conversation.length === 0) {
            var newConversation = new exports.Conversation({
                participantsNames: participants,
                conversationType: (participants.length > 2) ? true : false
            });

            var newConversationSaved = await newConversation.save();
            if (newConversationSaved)
                return { message: "conversation was created successfully", errors: null, conversation: newConversationSaved };
            else
                return { message: "conversation was not created successfully", errors: ["Error: error during craeting check usernames are valid and try again!!!"], conversation: null };
        }
        else
            return { message: "conversation was found", errors: null, conversation: conversation };
    }
    else
        return { message: "conversation was not found", errors: ["Error: one or more usernames are invalid!!!"], conversation: null };
}

exports.send = async (username, conversationId, message) => {
    var conversation = await exports.Conversation.find({ _id: conversationId, participantsNames: { $all: [username] } });
    if (conversation.length !== 0) {
        var message = new exports.Message({
            senderUserName: username,
            content: message,
            conversationId: conversationId
        });
    
        var sentMessage = await message.save();
        if (sentMessage)
            return { message: "message was sent successfully", errors: null };
        else
            return { message: "message was not sent successfully", errors: ["Error: unable to send message please try again!!!"] };
    }
    else
        return { message: "message was not sent successfully", errors: ["Error: conversation doesn't exist!!!"] };
}
