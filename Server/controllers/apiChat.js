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
        if (path === "/getRoomMessages")
            result = await exports.getRoomMessages();
        else if (path === "/getPrivateMessages")
            result = await exports.getPrivateMessages(username, req.body.username);
        else if (path === "/sendRoom")
            result = await exports.sendRoom(username, req.body.message);
        else if (path === "/sendPrivate")
            result = await exports.sendPrivate(username, req.body.username, req.body.message);
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

exports.getUsers = async () => {
    var usernames = await exports.User.findAll({ attributes: ['username'] })
    return utils.usernames2list(usernames);
}

exports.createConversation = async (participants, type) => {
    var conversation = new exports.Conversation({
        participantsNames: participants,
        conversationType: type
    });

    var Savedconversation = await conversation.save();
    if (Savedconversation)
        return Savedconversation;
    else
        return false;
}


exports.createRoom = async () => {
    var usernames = await exports.User.findAll({ attributes: ['username'] });
    usernames = utils.usernames2list(usernames);

    return await exports.createConversation(usernames, true);
}


exports.updateRoom = async (newUsernames) => {
    var room = await exports.Conversation.find({ conversationType: true });
    if (room.length === 0) {
        room = await exports.createRoom();
        if (room)
            return true;
    }
    else {
        room = room[0];
        var usernames = room.participantsNames;
        usernames.push(...newUsernames);
        room.participantsNames = usernames;
        var updatedRoom = await room.save();
        if (updatedRoom)
            return true;
    }

    return false;
}


exports.getRoomMessages = async () => {
    var room = await exports.Conversation.find({ conversationType: true });
    if (room.length !== 0) {
        room = room[0];
        var messages = await exports.Message.find({ conversationId: room._id }).sort({ timeCreated: 1 });
        return { message: "messages was retrived successfully", errors: null, messages: messages };
    }
    else {
        if(await exports.createRoom())
            return { message: "room was created successfully no new messages", errors: null, messages: [] };
        else
            return { message: "room was not created successfully!!!", errors: ["unable to create room please try again"], messages: [] };
    }
}


exports.getPrivateMessages = async (username1, username2) => {
    var conversation = await exports.Conversation.find({ $or: [{ "participantsNames": [username1, username2] }, { "participantsNames": [username2, username1] }] });
    if (conversation.length !== 0) {
        conversation = conversation[0];
        var messages = await exports.Message.find({ conversationId: conversation._id }).sort({ timeCreated: 1 });
        return { message: "messages was retrived successfully", errors: null, messages: messages };
    }
    else {
        var newConversation = await exports.createConversation([username1, username2], false);
        if (newConversation)
            return { message: "conversation was created successfully", errors: null, messages: [] };
        else
            return { message: "conversation was not created successfully", errors: ["Error: error during craeting check usernames are valid and try again!!!"], messages: [] };
    }
}


exports.send = async (username, conversationId, message) => {
    var message = new exports.Message({
        senderUserName: username,
        content: message,
        conversationId: conversationId
    });

    var sentMessage = await message.save();
    if (sentMessage)
        return true;
    else
        return false;
}


exports.sendRoom = async (username, message) => {
    var room = await exports.Conversation.find({ conversationType: true });
    if (room.length === 0)
        room = await exports.createRoom();
    else
        room = room[0];

    if (room) {
        if (await exports.send(username, room._id, message))
            return { message: "message was sent successfully", errors: null };
        else
            return { message: "message was not sent successfully", errors: ["Error: unable to send message please try again!!!"] };
    }
    else
        return { message: "message was not sent successfully", errors: ["Error: unable to create room please try again!!!"] };
}


exports.sendPrivate = async (username1, username2, message) => {
    var conversation = await exports.Conversation.find({ $or: [{ "participantsNames": [username1, username2] }, { "participantsNames": [username2, username1] }] });
    if (conversation.length === 0) {
        conversation = await exports.createConversation([username1, username2], false);
        if (!conversation)
            return { message: "message was not sent successfully", errors: ["Error: unable to create conversation please try again!!!"] };
    }
    else
        conversation = conversation[0];

    if (await exports.send(username1, conversation._id, message))
        return { message: "message was sent successfully", errors: null };
    else
        return { message: "message was not sent successfully", errors: ["Error: unable to send message please try again!!!"] };
}
