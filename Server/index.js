const env = require('dotenv').load();
const port = process.env.PORT || 3000;
const bodyParser = require('body-parser');
const expressValidator = require('express-validator');
const chatApi = require("./controllers/apiChat");

var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);

// express config
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
app.use(expressValidator());

// Models
var sequelize_models = require("./models/sequelize");
var mongoose_models = require("./models/mongoose/model");

// setting models for API
require("./controllers/apiAuth").User = sequelize_models.user;
require("./controllers/apiChat").User = sequelize_models.user;
require("./controllers/apiChat").Conversation = mongoose_models.Conversation;
require("./controllers/apiChat").Message = mongoose_models.Message;

const routes = require('./routes/web');
app.use('/', routes);


var userIDs = new Map();
var usernames = new Map();
var usersList = [];

io.on('connection', async (socket) => {
    if (usersList.length === 0)
        usersList = await chatApi.getUsers();
    console.log('a user connected');
    socket.on('username', function(username) {
        console.log(username + ' set user name');
        userIDs.set(username, socket.id);
        usernames.set(socket.id, username);
        if(!usersList.includes(username)){
            usersList.push(username);
            socket.broadcast.emit("user join", username);
        }
        io.to(socket.id).emit("retrieve list",usersList.filter(function(value, index, arr){ return value !== username;}));
    });

    socket.on('disconnect', function() {
        if (usernames.has(socket.id)) {
            var username = usernames.get(socket.id);
            console.log(username + ' disconnected');
            usernames.delete(socket.id);
            userIDs.delete(username);
            usersList = usersList.filter(function(value, index, arr){ return value !== username;});
            socket.broadcast.emit("user leave", username);
        }
    });

    socket.on('get group message', function(){
        if (usernames.has(socket.id)) {
            var username = usernames.get(socket.id);
            chatApi.getRoomMessages(username, message).then(function(result) {
                if (result.errors === null) {
                    var dataSent = { "username":username, "messages": result.messages };
                    socket.broadcast.emit('get group message', dataSent);
                    // console.log('message: ' + message);
                }
            });
        }
    });

    socket.on('group message', function(message){
        if (usernames.has(socket.id)) {
            var username = usernames.get(socket.id);
            chatApi.sendRoom(username, message).then(function(result) {
                if (result.errors === null) {
                    var dataSent = {"username":username, "message":message};
                    socket.broadcast.emit('group message', dataSent);
                    // console.log('message: ' + message);
                }
            });
        }
    });

    socket.on('get private message', function(username2){
        if (usernames.has(socket.id)) {
            var username1 = usernames.get(socket.id);
            if (usersList.includes(username2)) {
                chatApi.getPrivateMessages(username1, username2).then(function(result) {
                    if (result.errors === null) {
                        var dataSent = { "username": username1, "messages": result.messages };
                        socket.broadcast.emit('get private message', dataSent);
                        // console.log('message: ' + message);
                    }
                });
            }
        }
    });

    socket.on('private message', function(data){
        var to = data["to"];
        var message = data["message"];

        if(usernames.has(socket.id)){
            var username = usernames.get(socket.id);
            //if user is online
            if (usersList.includes(to)) {
                chatApi.sendPrivate(username, to, message).then(function (result) {
                    if (result.errors === null) {
                        if (userIDs.has(to)) {
                            var dataSent = {"username": username, "message": message};
                            // console.log(dataSent);
                            // console.log(to);
                            io.to(userIDs.get(to)).emit('private message', dataSent);
                        }
                    }
                });
            }
            // console.log('message: ' + message);
        }
    });
  
});


// Sync Database
sequelize_models.sequelize.sync().then(function() {
    console.log('Nice! Database looks fine')
}).catch(function(err) {
    console.log(err, "Something went wrong with the Database Update!")
});


http.listen(port, function(){
    console.log('listening on *:' + port);
});