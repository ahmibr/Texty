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
router.post('/conversations', apiChatController.request);
router.post('/messages', apiChatController.request);
router.post('/create', apiChatController.request);
router.post('/send', apiChatController.request);

module.exports = router;
