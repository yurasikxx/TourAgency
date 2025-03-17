package server.services.impl;

import server.models.User;
import server.database.DAO.UserDAO;
import server.services.AuthService;

public class AuthServiceImpl implements AuthService {
    private UserDAO userDAO;

    public AuthServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
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
}