const mocha = require('mocha');
const assert = require('assert');
const Conversation  = require('../models/trymodel').Conversation;
const Message  = require('../models/model').Message;

// test to be added
describe('testing!!!!!!!' , function(){
    //create tests 
    it('saves recored to the database', function(done){
        var conversation = new Conversation({   
            conversationType: false
        });

        conversation.save().then(function(){
            assert(conversation.isNew === false);
            done();
        });

    });
});