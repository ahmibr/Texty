const jwt = require('jsonwebtoken');
const utils = require('../util');
const userkey = require('../keys').api.userSecret;
const chatApi = require("./apiChat");

var exports = module.exports = {};
exports.User = null;

exports.validate = (req, username=false, password=false, confirmation=false) => {
    if (username) {
        req.checkBody('username', 'name cannot be empty').notEmpty();
        req.checkBody('username', 'name must be at least 4 characters').isLength({min: 4});
    }
    if (password)
        req.checkBody('password', 'invalid password must be at least 8 characters').notEmpty().isLength({min: 8});

    var err = req.validationErrors();
    var errors = [];

    if (password && confirmation) {
        if (req.body.password !== req.body.passwordConfirmation)
            errors.push('invalid password does not match password confirmation');
    }

    if (err || errors.length) {
        if (err) {
            err.forEach(element => {
                errors.push(element.msg);
            });
            errors = [...new Set(errors)];
        }

        return errors;
    }

    return false;
}

exports.request = async (req, res) => {
    var path = req.path;
    var result = 404;
    if (path === "/signin") {
        errors = exports.validate(req, username=true, password=true);
        
        if (errors)
            result = { message: "login was unsuccessful", token: null, errors: errors };
        else {
            var result = await exports.signin(req.body.username, req.body.password);
        }
    }
    else if (path === "/signup" || path === "/logup") {
        errors = exports.validate(req, username=true, password=true);

        if (errors)
            result = { message: "signup was unsuccessful", token: null, errors: errors };
        else {
            if (path === "/signup")
                result = await exports.signup(req.body.username, req.body.password);
            else
                result = await exports.logup(req.body.username, req.body.password);
        }
    }
    else if (path === "/details")
        result = await exports.details(req.body.token);
    else if (path === "/update") {
        errors = exports.validate(req, username=true);

        if (errors)
            result = { message: "user data was not updated", token: null, errors: errors };
        else
            result = await exports.update(req.body.username, req.body.token);
    }
    else if (path === "/reset") {
        errors = exports.validate(req, username=true, password=true, confirmation=true);

        if (errors)
            result = { message: "password was not updated", token: null, errors: errors };
        else
            result = await exports.reset(req.body.username, req.body.password, req.body.token);
    }
    
    if (result === 404)
        res.sendStatus(404);
    else if (result === 403)
        res.sendStatus(403);
    else
        res.json(result);
}

exports.signin = async (username, password) => {
    var user = await exports.User.findOne({ where: { username: username } });
        if (user) {
            if (!utils.isValidPassword(user.password, password))
                return {message: "login was unsuccessful", errors: ["invalid password!!!"] };
            else {
                userData = {
                    id: user.id,
                    username: user.username
                };

                var token = await jwt.sign({userData}, userkey, { expiresIn: '10d' });
                if (token) {
                    await user.update({ remember_token: token });
                    return { message: "login was successful", token: token, errors: null };
                }
                else
                    return { message: "login was unsuccessful", token: null, errors: ["Couldn't create token!!! please try again"] };
            }
        }
        else
            return { message: "login was unsuccessful", token: null, errors: ["Email doesn't exist"] };
}

exports.signup = async (username, password) => {
    var checkUserNameAvailableUser = await exports.User.findOne({ where: { username: username } });
    if (checkUserNameAvailableUser)
        return { message: "user was not created successfully", errors: ["Username is already taken"] };
    else {
        var userPassword = utils.generateHash(password);
        var data = {
            username: username,
            password: userPassword,
        };

        var newUser = await exports.User.create(data);
        if (newUser) {
            await chatApi.updateRoom();
            return { message: "user was created successfully", errors: null };
        }
        else
            return { message: "user was not created successfully", errors: ["Unable to create user please try again"] };
    }
}

exports.logup = async (username, password) => {
    var checkUserNameAvailableUser = await exports.User.findOne({ where: { username: username } });
    if (checkUserNameAvailableUser)
        return { message: "user was not created successfully", token: null, errors: ["Username is already taken"] };
    else {
        var userPassword = utils.generateHash(password);
        var data = {
            username: username,
            password: userPassword
        };

        var newUser = await exports.User.create(data);
        if (newUser) {
            await chatApi.updateRoom();
            userData = {
                id: newUser.id,
                username: newUser.username
            };
            
            var token = await jwt.sign({userData}, userkey, { expiresIn: '10d' });
            if (token) {
                newUser.update({ remember_token: token });
                return { message: "signup was successful", token: token, errors: null };
            }
            else
                return { message: "signup was successful", token: null, errors: ["Couldn't create token!!! signin would be required later"] };
        }
        else
            return { message: "user was not created successfully", token: null, errors: ["Unable to create user please try again"] };
    }
}

exports.details = async (token) => {
    var authData = await jwt.verify(token, userkey);
    if (authData) {
        var user = await exports.User.findOne({
            attributes: [
                'id',
                'username'
            ],
            where: { username: authData.userData.username, remember_token: token }
        });

        if (user)
            return { user: user };
        else
            return 403;
    }
    else
        return 403;
}

exports.update = async (username, token) => {
    var authData = await jwt.verify(token, userkey);
    if (authData) {
        var user = await exports.User.findOne({ where: { username: authData.userData.username, remember_token: token } });
        if (user) {
            await user.updateAttributes({ username: username });
            userData = {
                id: user.id,
                username: user.username
            };

            var new_token = await jwt.sign({userData}, userkey, { expiresIn: '10d' });
            if (new_token) {
                user.update({ remember_token: new_token });
                return { message: "user data was updated successfully", new_token: new_token, errors: null };
            }
            else
                return { message: "user data was updated successfully", new_token: null, errors: ["Couldn't create token!!! login would be required"] };
        }
        else
            return 403;
    }
    else
        return 403;
}

exports.reset = async (username, password, token) => {
    var authData = await jwt.verify(token, userkey);
    if (authData) {
        if (authData.userData.username === username) {
            var user = await exports.User.findOne({ where: { username: username, remember_token: token } });
            if (user) {
                var new_password = utils.generateHash(password);
                await user.update({ password: new_password });
                userData = {
                    id: user.id,
                    username: user.username
                };

                var new_token = await jwt.sign({userData}, userkey, { expiresIn: '10d' });
                if (new_token) {
                    user.update({ remember_token: new_token });
                    return { message: "password was updated successfully", new_token: new_token, errors: null };
                }
                else
                    return { message: "password was updated successfully", new_token: null, errors: ["Couldn't create token!!! login would be required"] };
            }
            else
                return 403;
        }
        else
            return { message: "password was not updated", new_token: null, errors: ["invalid email"] };
    }
    else
        return 403;
}
