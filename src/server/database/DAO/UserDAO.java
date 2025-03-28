package server.database.DAO;

import server.models.User;

import java.util.List;

public interface UserDAO {
    User getUserById(int id);

    User getUserByUsername(String username);

    List<User> getAllUsers();

    void addUser(User user);

    void updateUser(User user);

    void deleteUser(int id);

    double getBalance(int userId);

    void updateBalance(int userId, double newBalance);
}