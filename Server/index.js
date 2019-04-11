const env = require('dotenv').load();
const port = process.env.PORT || 3000;
const bodyParser = require('body-parser');
const expressValidator = require('express-validator');

var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var userIDs = new Map();
var usernames = new Map();

// express config
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
app.use(expressValidator());

// Models
var sequelize_models = require("./models/sequelize");

// setting models for API
require("./routes/api").User = sequelize_models.user;

const routes = require('./routes/web');
app.use('/', routes);


io.on('connection', function(socket){
    console.log('a user connected');

    socket.on('username', function(username){
        userIDs.set(username,socket.id);
        usernames.set(socket.id,username);
        console.log("IDS");
        console.log(userIDs);
        console.log(usernames);
    });

    socket.on('disconnect', function(){
        var username = usernames.get(socket.id);
        console.log(username + ' disconnected');
        usernames.delete(socket.id);
        userIDs.delete(username);
      
    });

    socket.on('chat message', function(msg){
        // console.log(userIDs.get("Ahmed"));
        // io.to(userIDs.get("Ahmed")).emit('private message', msg);
        // console.log('message: ' + msg);
        socket.broadcast.emit('chat message', msg);
        console.log('message: ' + msg);
    });
  
});


//Sync Database
sequelize_models.sequelize.sync().then(function() {
    console.log('Nice! Database looks fine')
}).catch(function(err) {
    console.log(err, "Something went wrong with the Database Update!")
});


http.listen(port, function(){
    console.log('listening on *:' + port);
});