const mongoose = require('mongoose');
// connect to moongodb
mongoose.connect('mongodb://localhost/testaroo');

// element.on('click' , function(){

// });
mongoose.connection.once('open',function(){
    console.log('Connection has been made');
}).on('error' , function(error){
    console.log('connection error' , error);
});