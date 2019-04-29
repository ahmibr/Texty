const bcrypt = require('bcryptjs');

var exports = module.exports = {};

exports.isValidPassword = function(userpass, password) {
    return bcrypt.compareSync(password, userpass);
};

exports.generateHash = function(password) {
    return bcrypt.hashSync(password, bcrypt.genSaltSync(8), null);
};

exports.contains = function(arr1, arr2) {
    for (i = 0; i < arr2.length; i++)
        if(!arr1.includes(arr2[i])) return false;

    return true;
}

exports.usernames2list = function(usernames) {
    var list = [];
    for (i = 0; i < usernames.length; i++)
        list.push(usernames[i].username);

    return list;
}
