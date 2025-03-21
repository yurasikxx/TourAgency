package server.services;

import server.models.User;

public interface UserService {
    User getUserById(int id);
    User getUserByUsername(String username);
    User authenticate(String username, String password);
    void register(User user);
    double getBalance(int userId);
    void updateBalance(int userId, double newBalance);
}