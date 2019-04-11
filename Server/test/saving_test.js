const mocha = require('mocha');
const assert = require('assert');
const Conversation  = require('../models/trymodel').conversationChar;
const Message  = require('../models/model').messageChar;
// test to be added
describe('testing!!!!!!!' , function(){
    //create tests 
    it('saves recored to the database', function(done){
        var conversationChar = new Conversation({   
            conversationType: false
        });
        conversationChar.save().then(function(){
            assert(conversationChar.isNew === false);
            done();
        });
            
    });
});