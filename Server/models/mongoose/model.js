const mongoose = require('mongoose');
mongoose.connect(process.env.MONGODB_URI, { useNewUrlParser: true });
const db = mongoose.connection;

db.once('open', function(){
    console.log(">>>>>>>>>>>>>>> conncted to mongoDB <<<<<<<<<<<<<<<");
});

db.on('errors', function(err){
    console.log(err);
});

const Schema = mongoose.Schema;
var ObjectId = mongoose.Schema.Types.ObjectId;
//create schema and model

var exports = module.exports = {};

const conversationSchema = new Schema({
    participantsNames: [String],
    conversationType: {type: Boolean, required: true}
});

const messageSchema = mongoose.Schema({
    senderUserName: {type: String, required: true},
    content: {type: String , required: true},
    timeCreated: {type: Date , default: Date.now},
    conversationId: {type: ObjectId, required: true}
});


const messageChar = mongoose.model('conversation' ,messageSchema);
const conversationChar = mongoose.model('message' ,conversationSchema);


exports.Message = messageChar;
exports.Conversation = conversationChar;
