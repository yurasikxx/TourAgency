package server.services;

import server.models.User;

public interface AuthService {
    User getUserByUsername(String username);
    User authenticate(String username, String password);
    void register(User user);
}