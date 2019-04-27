module.exports = function(sequelize, Sequelize) {

    var User = sequelize.define('user', {
        id: {
            primaryKey: true,
            autoIncrement: true,
            type: Sequelize.INTEGER
        },

        username: {
            unique: true,
            notEmpty: true,
            allowNull: false,
            type: Sequelize.STRING
        },

        password: {
            notEmpty: true,
            allowNull: false,
            type: Sequelize.STRING
        },

        remember_token: {
            type: Sequelize.STRING
        },

        status: {
            type: Sequelize.ENUM('active', 'inactive'),
            defaultValue: 'active'
        }
    });

    return User;
}