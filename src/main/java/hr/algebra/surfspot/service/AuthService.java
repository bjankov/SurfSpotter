package hr.algebra.surfspot.service;

import hr.algebra.surfspot.model.User;

public interface AuthService {
    User register(String username, String email, String password);
    User login(String usernameOrEmail, String password);
}
