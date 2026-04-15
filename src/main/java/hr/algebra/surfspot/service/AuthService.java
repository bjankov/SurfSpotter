package hr.algebra.surfspot.service;

import hr.algebra.surfspot.exception.AuthenticationException;
import hr.algebra.surfspot.exception.DuplicateRecordException;
import hr.algebra.surfspot.exception.ValidationException;
import hr.algebra.surfspot.model.User;
import hr.algebra.surfspot.repository.UserRepository;
import hr.algebra.surfspot.security.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.regex.Pattern;

public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    // Email validation regex (RFC 5322 simplified)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    public AuthService(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    /**
     * Authenticate user with username/email and password
     * @param usernameOrEmail Username or email address
     * @param password Plain text password
     * @return Authenticated user
     * @throws AuthenticationException if credentials are invalid
     * @throws ValidationException if input is invalid
     */
    public User login(String usernameOrEmail, String password) {
        // Validate input
        if (usernameOrEmail == null || usernameOrEmail.isBlank()) {
            throw new ValidationException("Korisnicko ime ili email ne moze biti prazno!");
        }
        if (password == null || password.isBlank()) {
            throw new ValidationException("Lozinka ne moze biti prazna!");
        }

        // Find user by username or email
        Optional<User> userOptional;
        if (isEmail(usernameOrEmail)) {
            userOptional = userRepository.findByEmail(usernameOrEmail);
        } else {
            userOptional = userRepository.findByName(usernameOrEmail);
        }

        // Verify user exists
        if (userOptional.isEmpty()) {
            log.warn("Login attempt with non-existent username/email: {}", usernameOrEmail);
            throw new AuthenticationException("Krivi podaci - nije pronaden niti jedan korisnik sa ovim podacima.");
        }

        User user = userOptional.get();

        // Verify password
        if (!passwordService.verify(password, user.getPasswordHash())) {
            log.warn("Failed login attempt for user: {}", user.getUsername());
            throw new AuthenticationException("Krivi podaci - nije pronaden niti jedan korisnik sa ovim podacima.");
        }

        log.info("Successful login for user: {}", user.getUsername());
        return user;
    }

    /**
     * @param username Username
     * @param email Email address
     * @param password Plain text password
     * @throws DuplicateRecordException if username or email already exists
     * @throws ValidationException if input is invalid
     */
    public User register(String username, String email, String password) {
        if (username == null || username.isBlank()) {
            throw new ValidationException("Korisnicko ime ne moze biti prazno!");
        }
        if (username.length() < 3) {
            throw new ValidationException("Korisnicko ime mora imati barem 3 znaka!");
        }
        if (username.length() > 50) {
            throw new ValidationException("Korisnicko ime ne moze biti duze od 50 znakova!");
        }

        if (email != null && !email.isBlank() && !isValidEmail(email)) {
            throw new ValidationException("Email adresa nije u ispravnom formatu!");
        }

        if (password == null || password.isBlank()) {
            throw new ValidationException("Lozinka ne moze biti prazna!");
        }
        if (password.length() < 6) {
            throw new ValidationException("Lozinka mora imati barem 6 znakova!");
        }

        if (userRepository.findByName(username).isPresent()) {
            throw new DuplicateRecordException("Korisnik sa ovim korisnickim imenom vec postoji!");
        }
        if (email != null && !email.isBlank() && userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateRecordException("Korisnik sa ovim emailom vec postoji!");
        }

        String hashedPassword = passwordService.hash(password);
        User user = User.builder()
                .username(username)
                .email(email)
                .passwordHash(hashedPassword)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", username);
        return savedUser;
    }

    public void register(String username, String password) {
        register(username, null, password);
    }

    private boolean isEmail(String input) {
        return input.contains("@");
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
