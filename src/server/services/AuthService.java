package server.services;

import server.models.User;

public interface AuthService {
    User authenticate(String username, String password);
    void register(User user);
}