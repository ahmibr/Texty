const express = require('express');
const jwt = require('jsonwebtoken');
const utils = require('../util');
const userkey = require('../keys').api.userSecret;
const router = express.Router();

var exports = module.exports = {};
exports.User = null;
exports.Conversation = null;
exports.Message = null;

// auth routes
router.post('/signin', function (req, res){
    req.checkBody('username', 'name cannot be empty').notEmpty();
    req.checkBody('username', 'name be at least 4 characters').isLength({min: 4});
    req.checkBody('password', 'invalid password must be at least 8 characters').notEmpty().isLength({min: 8});

    var err = req.validationErrors();
    var errors = [];

    if (err || errors.length) {
        if (err) {
            err.forEach(element => {
                errors.push(element.msg);
            });
            errors = [...new Set(errors)];
        }

        res.json({message: "login was unsuccessful", token: null, errors: errors});
    }
    else {
        var username = req.body.username;
        var password = req.body.password;

        exports.User.findOne({ where: { username: username } }).then(function(user) {
            if (user) {
                if (!utils.isValidPassword(user.password, password))
                    res.json({message: "login was unsuccessful", errors: ["invalid password!!!"] });
                else {
                    user = {
                        id: user.id,
                        username: user.username
                    };
                    jwt.sign({user}, userkey, { expiresIn: '10d' }, (err, token) => {
                        if (err) {
                            res.json({ message: "login was unsuccessful", token: null, errors: ["Couldn't create token!!! please try again"] });
                        }
                        else {
                            user.update({ remember_token: token });
                            res.json({ message: "login was successful", token: token, errors: null });
                        }
                    });
                }
            }
            else
                res.json({ message: "login was unsuccessful", errors: ["Email doesn't exist"] });
        });
    }
});


router.post('/signup', function (req, res) {
    req.checkBody('username', 'name cannot be empty').notEmpty();
    req.checkBody('username', 'name be at least 4 characters').isLength({min: 4});
    req.checkBody('password', 'invalid password must be at least 8 characters').notEmpty().isLength({min: 8});

    var err = req.validationErrors();
    var errors = [];

    if (req.body.password !== req.body.password_confirmation)
        errors.push('invalid password does not match password confirmation');

    if (err || errors.length) {
        if (err) {
            err.forEach(element => {
                errors.push(element.msg);
            });
            errors = [...new Set(errors)];
        }

        res.json({ message: "user was not created successfully", errors: errors });
    }
    else {
        var username = req.body.username;
        var password = req.body.password;

        exports.User.findOne({ where: { username: username } }).then(function(checkUserNameAvailableUser) {
            if (checkUserNameAvailableUser)
                res.json({ message: "user was not created successfully", errors: ["Username is already taken"] });
            else {
                var userPassword = utils.generateHash(password);
                var data = {
                    username: username,
                    password: userPassword,
                };

                exports.User.create(data).then(function(newUser) {
                    if (newUser)
                        res.json({ message: "user was created successfully", errors: null });
                    else
                        res.json({ message: "user was not created successfully", errors: ["Unable to create user please try again"] });
                });
            }
        });
    }
});

router.post('/logup', function (req, res) {
    req.checkBody('username', 'name cannot be empty').notEmpty();
    req.checkBody('username', 'name be at least 4 characters').isLength({min: 4});
    req.checkBody('password', 'invalid password must be at least 8 characters').notEmpty().isLength({min: 8});

    var err = req.validationErrors();
    var errors = [];

    if (req.body.password !== req.body.password_confirmation)
        errors.push('invalid password does not match password confirmation');

    if (err || errors.length) {
        if (err) {
            err.forEach(element => {
                errors.push(element.msg);
            });
            errors = [...new Set(errors)];
        }

        res.json({ message: "user was not created successfully", token: null, errors: errors });
    }
    else {
        var username = req.body.username;
        var password = req.body.password;

        exports.User.findOne({ where: { username: username } }).then(function(checkUserNameAvailableUser) {
            if (checkUserNameAvailableUser)
                res.json({ message: "user was not created successfully", token: null, errors: ["Username is already taken"] });
            else {
                var userPassword = utils.generateHash(password);
                var data = {
                    username: username,
                    password: userPassword
                };

                exports.User.create(data).then(function(newUser) {
                    if (newUser) {
                        newUser = {
                            id: newUser.id,
                            username: newUser.username
                        };
                        jwt.sign({newUser}, userkey, { expiresIn: '10d' }, (err, token) => {
                            if (err) {
                                res.json({ message: "signup was successful", token: null, errors: ["Couldn't create token!!! signin would be required later"] });
                            }
                            else {
                                user.update({ remember_token: token });
                                res.json({ message: "signup was successful", token: token, errors: null });
                            }
                        });
                    }
                    else
                        res.json({ message: "user was not created successfully", token: null, errors: ["Unable to create user please try again"] });
                });
            }
        });
    }
});


router.post('/details', function (req, res) {
    var token = req.body.token;

    jwt.verify(token, userkey, (err, authData) => {
        if(err)
            res.sendStatus(403);
        else {
            exports.User.findOne({
                attributes: [
                    'id',
                    'username',
                    'remember_token'
                ],
                where: { username: authData.user.username, remember_token: token }
            }).then(function (user) {
                if (user) {
                    res.json({ user: user });
                }
                else
                    res.sendStatus(403);
            });
        }
    });
});


router.post('/update', function (req, res) {
    req.checkBody('username', 'name cannot be empty').notEmpty();
    req.checkBody('username', 'name be at least 4 characters').isLength({min: 4});

    var err = req.validationErrors();
    var errors = [];

    if (err || errors.length) {
        if (err) {
            err.forEach(element => {
                errors.push(element.msg);
            });
            errors = [...new Set(errors)];
        }

        res.json({ message: "user data was not updated", new_token: null, errors: errors });
    }
    else {
        var username = req.body.username;
        var token = req.body.token;

        jwt.verify(token, userkey, (err, authData) => {
            if(err)
                res.sendStatus(403);
            else {
                exports.User.findOne({ where: { username: authData.user.username, remember_token: token } }).then(function (user) {
                    if (user) {
                        user.updateAttributes({
                            username: username
                        }).then(function() {
                            user = {
                                id: user.id,
                                username: user.username
                            };
                            jwt.sign({user}, userkey, { expiresIn: '10d' }, (err, new_token) => {
                                if (err)
                                    res.json({message: "user data was updated successfully", new_token: null, errors: ["Couldn't create token!!! login would be required"] });
                                else {
                                    user.update({ remember_token: new_token });
                                    res.json({ message: "user data was updated successfully", new_token: new_token, errors: null });
                                }
                            });
                        });
                    }
                    else
                        res.sendStatus(403);
                });
            }
        });
    }
});


router.post('/reset', function (req, res) {
    req.checkBody('username', 'name cannot be empty').notEmpty();
    req.checkBody('username', 'name be at least 4 characters').isLength({min: 4});
    req.checkBody('password', 'invalid password must be at least 8 characters').notEmpty().isLength({min: 8});

    var err = req.validationErrors();
    var errors = [];

    if (req.body.password !== req.body.password_confirmation)
        errors.push('invalid password does not match password confirmation');

    if (err || errors.length) {
        if (err) {
            err.forEach(element => {
                errors.push(element.msg);
            });
            errors = [...new Set(errors)];
        }

        res.json({ message: "password was not updated", new_token: null, errors: errors });
    }
    else {
        var username = req.body.username;
        var password = req.body.password;
        var token = req.body.token;

        jwt.verify(token, userkey, (err, authData) => {
            if(err)
                res.sendStatus(403);
            else {
                if (authData.user.username === username) {
                    exports.User.findOne({ where: { username: username, remember_token: token } }).then(function (user) {
                        if (user) {
                            var new_password = utils.generateHash(password);
                            user.updateAttributes({ password: new_password }).then(function() {
                                user = {
                                    id: user.id,
                                    username: user.username
                                };
                                jwt.sign({user}, userkey, { expiresIn: '10d' }, (err, new_token) => {
                                    if (err)
                                        res.json({message: "password was updated successfully", new_token: null, errors: ["Couldn't create token!!! login would be required"] });
                                    else {
                                        user.update({ remember_token: new_token });
                                        res.json({ message: "password was updated successfully", new_token: new_token, errors: null });
                                    }
                                });
                            });
                        }
                        else
                            res.sendStatus(403);
                    });
                }
                else
                    res.json( {message: "password was not updated", new_token: null, errors: ["invalid email"] });
            }
        });
    }
});

// chat routes

exports.Router = router;
