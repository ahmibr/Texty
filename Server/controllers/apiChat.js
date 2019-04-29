const jwt = require('jsonwebtoken');
const utils = require('../util');
const userkey = require('../keys').api.userSecret;

var exports = module.exports = {};

exports.Conversation = null;
exports.Message = null;

exports.request = async (req, res) => {
    var path = req.path;
    var result = 404;
    if (path === "/conversations")
        var result = await exports.getConversations(req.body.token);
    else if (path === "/messages")
        result = await exports.getMessages(req.body.token, req.body.conversationId);
    else if (path === "/send")
        result = await exports.send(req.body.token, req.body.conversation, req.body.message);
    
    if (result === 404)
        res.sendStatus(404);
    else if (result === 403)
        res.sendStatus(403);
    else
        res.json(result);
}

exports.getConversations = async (token) => {
    var authData = await jwt.verify(token, userkey);
    if (authData) {
        var conversations = await exports.Conversation.find({ participantsNames: { $all: [authData.userData.username] } });
        if (conversations) {
            return { message: "conversations was retrived successfully", conversations: conversations };
        }
        else
            return { message: "no conversations available!!!" };
    }
    else
        return 403;
}

exports.getMessages = async (token, conversationId) => {
    var authData = await jwt.verify(token, userkey);
    if (authData) {
        var messages = await exports.Message.find({ conversationId: conversationId }).sort({ timeCreated: 'desc' });
        if (messages) {
            console.log(messages);
            return { messages: "data was retrived successfully", messages: messages };
        }
        else
            return { message: "no conversations available!!!" };
    }
    else
        return 403;
}

exports.findOrCreate = async (token, participants) => {
    var authData = await jwt.verify(token, userkey);
    if (authData) {
        var conversation = await exports.Conversation.find({ participantsNames: participants });
        if (conversation.length === 0) {
            var conversation = new exports.Conversation({
                senderUserName: authData.userData.username,
                content: message,
                conversationId: conversation
            });

            var err = await message.save();
            if (err)
                return { message: "message was not sent successfully", errors: "no conversations available!!!" };
            else
                return { message: "message was sent successfully", errors: null };
        }
        else {
            return conversation;
        }
    }
    else
        return 403;
}

exports.send = async (token, conversation, message) => {
    var authData = await jwt.verify(token, userkey);
    if (authData) {
        var Message = exports.Message;
        var message = new Message({
            senderUserName: authData.userData.username,
            content: message,
            conversationId: conversation
        });

        var err = await message.save();
        if (err)
            return { message: "message was not sent successfully", errors: "no conversations available!!!" };
        else
            return { message: "message was sent successfully", errors: null };
    }
    else
        return 403;
}
