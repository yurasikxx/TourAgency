package server.services;

import server.models.User;

import java.util.List;

public interface UserService {
    User getUserById(int id);

    User getUserByUsername(String username);

    List<User> getAllUsers();

    User authenticate(String username, String password);

    void register(User user);

    double getBalance(int userId);

    void updateBalance(int userId, double newBalance);

    void updateUser(User user);

    void deleteUser(int id);
}