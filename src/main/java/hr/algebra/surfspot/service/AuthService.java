package hr.algebra.surfspot.service;

import hr.algebra.surfspot.exception.AuthenticationException;
import hr.algebra.surfspot.model.User;
import hr.algebra.surfspot.repository.UserRepository;
import hr.algebra.surfspot.security.PasswordService;
import hr.algebra.surfspot.validation.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final PasswordService passwordService;


    public AuthService(UserRepository userRepository, PasswordService passwordService,  UserValidator userValidator) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.userValidator = userValidator;
    }

    public User login(String usernameOrEmail, String password) {
        userValidator.validateLogin(usernameOrEmail, password);

        User user = findUser(usernameOrEmail)
                .orElseThrow(() -> {
                    log.warn("Login attempt with non-existent identity: {}", usernameOrEmail);
                    return new AuthenticationException("Neispravni podaci sa prijavu.");
                });

        user.setPermissions(userRepository.findPermissionsByUserId(user.getId()));

        if (!passwordService.verify(password, user.getPasswordHash())) {
            log.warn("Failed login attempt for user: {}", user.getUsername());
            throw new AuthenticationException("Neispravni podaci za prijavu");
        }

        log.info("Successful login for user: {}", user.getUsername());
        return user;
    }

    public User register(String username, String email, String password) {
        userValidator.validateRegistration(username, email, password);

        String hashedPassword = passwordService.hash(password);
        User user = User.builder()
                .username(username)
                .email(email)
                .passwordHash(hashedPassword)
                .build();

        log.info("User registered successfully: {}", username);
        return userRepository.save(user);
    }

    private Optional<User> findUser(String usernameOrEmail) {
        return isEmail(usernameOrEmail)
                ? userRepository.findByEmail(usernameOrEmail)
                : userRepository.findByName(usernameOrEmail);
    }

    private boolean isEmail(String input) {
        return input.contains("@");
    }
}
