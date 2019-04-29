const env = require('dotenv').load();
const port = process.env.PORT || 3000;
const bodyParser = require('body-parser');
const expressValidator = require('express-validator');

var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var userIDs = new Map();
var usernames = new Map();
var usersList = [];

// console.log("Server started");
// app.get('/', function(req, res){
//   res.sendFile(__dirname + '/index.html');
// });

// express config
// app.use(bodyParser.urlencoded({ extended: true }));
// app.use(bodyParser.json());
// app.use(expressValidator());

// Models
// var sequelize_models = require("./models/sequelize");
// var mongoose_models = require("./models/mongoose/model");

// setting models for API
// require("./routes/api").User = sequelize_models.user;
// require("./routes/api").Conversation = mongoose_models.Conversation;
// require("./routes/api").Message = mongoose_models.Message;

const routes = require('./routes/web');
app.use('/', routes);


io.on('connection', function(socket){

  console.log('a user connected');
  socket.on('username', function(username){
    console.log(username + ' set user name');
    userIDs.set(username,socket.id);
    usernames.set(socket.id,username);
    if(!usersList.includes(username)){
      usersList.push(username);
      socket.broadcast.emit("user join",username);
    }
    io.to(socket.id).emit("retrieve list",usersList.filter(function(value, index, arr){ return value !== username;}));
  });

  socket.on('disconnect', function(){
    if(usernames.has(socket.id)){
      var username = usernames.get(socket.id);
      console.log(username + ' disconnected');
      usernames.delete(socket.id);
      userIDs.delete(username);
      usersList = usersList.filter(function(value, index, arr){ return value !== username;});
      socket.broadcast.emit("user leave",username);
    }
  });

  socket.on('group message', function(message){
    if(usernames.has(socket.id)){
      var username = usernames.get(socket.id);
      var dataSent = {"username":username,"message":message};
      socket.broadcast.emit('group message', dataSent);
      console.log('message: ' + message);
    }
  });

  socket.on('private message', function(data){
    var to = data["to"];
    var message = data["message"];

    if(usernames.has(socket.id)){
      var username = usernames.get(socket.id);
      //if user is online
      if(userIDs.has(to)){
        var dataSent = {"username":username,"message":message};
        console.log(dataSent);
        console.log(to);
        io.to(userIDs.get(to)).emit('private message', dataSent);
      }
      console.log('message: ' + message);
    }
  });
  
});


//Sync Database
// sequelize_models.sequelize.sync().then(function() {
//     console.log('Nice! Database looks fine')
// }).catch(function(err) {
//     console.log(err, "Something went wrong with the Database Update!")
// });


http.listen(port, function(){
    console.log('listening on *:' + port);
});