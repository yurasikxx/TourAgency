package server.services.impl;

import server.models.User;
import server.database.DAO.UserDAO;
import server.services.UserService;

public class UserServiceImpl implements UserService {
    private UserDAO userDAO;

    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public User getUserById(int id) {
        return userDAO.getUserById(id);
    }

    @Override
    public User getUserByUsername(String username) {
        return userDAO.getUserByUsername(username);
    }

    @Override
    public User authenticate(String username, String password) {
        User user = userDAO.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    @Override
    public void register(User user) {
        userDAO.addUser(user);
    }

    @Override
    public double getBalance(int userId) {
        return userDAO.getBalance(userId);
    }

    @Override
    public void updateBalance(int userId, double newBalance) {
        userDAO.updateBalance(userId, newBalance);
    }
}