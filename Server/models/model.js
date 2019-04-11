const mongoose = require('mongoose');
const Schema = mongoose.Schema;
var ObjectId = mongoose.Schema.Types.ObjectId;
//create schema and model

var exports = module.exports = {};

const conversationSchema = new Schema({
    participantsId: [Number],
    conversationType: {type: Boolean, required: true}
    });
const messageSchema = mongoose.Schema({
    senderId: {type: Number, required: true},
        content: String,
        timeCreated: {type: Date , default: Date.now},
        conversationId: {type: ObjectId, required: true}
});


const messageChar = mongoose.model('conversation' ,messageSchema);
const conversationChar = mongoose.model('message' ,conversationSchema);


exports.messageChar = messageChar;
exports.conversationChar = conversationChar;
