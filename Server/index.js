var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
const userIDs = new Map();
const usernames = new Map();
var usersList = [];
console.log("Server started");
app.get('/', function(req, res){
  res.sendFile(__dirname + '/index.html');
});



io.on('connection', function(socket){

  console.log('a user connected');
  socket.on('username', function(username){
    console.log(username + ' set user name');
    userIDs.set(username,socket.id);
    usernames.set(socket.id,username);
    socket.broadcast.emit("user join",username);
    usersList.push(username);
    io.to(socket.id).emit("retrieve list",usersList);
  });

  socket.on('disconnect', function(){
    var username = usernames.get(socket.id);
    console.log(username + ' disconnected');
    usernames.delete(socket.id);
    userIDs.delete(username);
    usersList = usersList.filter(function(value, index, arr){ return value !== username;});
    socket.broadcast.emit("user leave",username);
  });

  socket.on('group message', function(message){
    var username = usernames.get(socket.id);
    var dataSent = {"username":username,"message":message};
    socket.broadcast.emit('group message', dataSent);
    console.log('message: ' + message);
  });

  socket.on('private message', function(data){
    var to = data["to"];
    var message = data["message"];
    var username = usernames.get(socket.id);

    //if user is online
    if(userIDs.has(to)){
      var dataSent = {"username":username,"message":message};

      io.to(to).emit('private message', dataSent);
    }
    console.log('message: ' + message);
  });
  
});

http.listen(3000, function(){
  console.log('listening on *:3000');
});