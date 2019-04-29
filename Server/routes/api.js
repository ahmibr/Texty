const express = require('express');
const router = express.Router();
const apiAuthController = require("../controllers/apiAuth");
const apiChatController = require("../controllers/apiChat");

// auth routes
router.post('/signin', apiAuthController.request);
router.post('/signup', apiAuthController.request);
router.post('/logup', apiAuthController.request);
router.post('/details', apiAuthController.request);
router.post('/update', apiAuthController.request);
router.post('/reset', apiAuthController.request);
// chat routes
router.post('/getRoomMessages', apiChatController.request);
router.post('/getPrivateMessages', apiChatController.request);
router.post('/sendRoom', apiChatController.request);
router.post('/sendPrivate', apiChatController.request);

module.exports = router;
