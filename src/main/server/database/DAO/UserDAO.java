package main.server.database.DAO;

import main.server.models.User;

import java.util.List;

public interface UserDAO {
    User getUserById(int id);
    User getUserByUsername(String username);
    List<User> getAllUsers();
    void addUser(User user);
    void updateUser(User user);
    void deleteUser(int id);
}