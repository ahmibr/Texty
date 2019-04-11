var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
const userIDs = new Map();
const usernames = new Map();
var usersList = [];
app.get('/', function(req, res){
  res.sendFile(__dirname + '/index.html');
});



io.on('connection', function(socket){
  console.log('a user connected');

  socket.on('username', function(username){
    userIDs.set(username,socket.id);
    usernames.set(socket.id,username);
    socket.broadcast.emit("user join",username);
    usersList.push(username);
    console.log("I sent users list");
    console.log(usersList);
    io.to(socket.id).emit("retrieve list",usersList);
    console.log(socket.id);
    console.log(usersList.length);
    console.log(usersList);
    console.log("IDs");
    console.log(userIDs);
    console.log(usernames);
  });

  socket.on('disconnect', function(){
    var username = usernames.get(socket.id);
    console.log(username + ' disconnected');
    usernames.delete(socket.id);
    userIDs.delete(username);
    usersList = usersList.filter(function(value, index, arr){ return value !== username;});
    console.log(usersList);
    console.log(socket.id);
    socket.broadcast.emit("user leave",username);
  });

  socket.on('chat message', function(msg){
    // console.log(userIDs.get("Ahmed"));
    // io.to(userIDs.get("Ahmed")).emit('private message', msg);
    // console.log('message: ' + msg);
    socket.broadcast.emit('chat message', msg);
    console.log('message: ' + msg);
  });
  
});

http.listen(3000, function(){
  console.log('listening on *:3000');
});