const bcrypt = require('bcryptjs');

var exports = module.exports = {};

exports.isValidPassword = function(userpass, password) {
    return bcrypt.compareSync(password, userpass);
};

exports.generateHash = function(password) {
    return bcrypt.hashSync(password, bcrypt.genSaltSync(8), null);
};
