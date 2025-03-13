package main.server.services;

import main.server.models.User;

public interface AuthService {
    User authenticate(String username, String password);
    void register(User user);
}