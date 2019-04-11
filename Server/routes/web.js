const express = require('express');
const router = express.Router();

router.get('/', function(req, res){
    res.sendFile(__dirname + '/index.html');
});

const api_routes = require('./api').Router;
router.use('/api', api_routes);

module.exports = router;