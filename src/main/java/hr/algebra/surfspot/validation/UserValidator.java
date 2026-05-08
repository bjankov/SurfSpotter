package hr.algebra.surfspot.validation;

import hr.algebra.surfspot.exception.ValidationException;
import hr.algebra.surfspot.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UserValidator {

    private final UserRepository userRepository;

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Email validation regex (RFC 5322 simplified)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public void validateLogin(final String usernameOrEmail, final String password) {
        List<String> errors = new ArrayList<>();

        if (usernameOrEmail == null || usernameOrEmail.isBlank()) {
            errors.add("Korisničko ime ili email je obavezan podatak.");
        }
        if (password == null || password.isBlank()) {
            throw new ValidationException("Lozinka ne može biti prazna!");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }
    }

    public void validateRegistration(final String username, final String email, final String password) {
        List<String> errors = new ArrayList<>();

        checkUsername(username, errors);
        checkEmail(email, errors);
        checkPassword(password, errors);

        if (errors.isEmpty()) {
            checkAvailability(username, email, errors);
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join("\n", errors));
        }
    }

    private void checkUsername(String username, List<String> errors) {
        if (username == null || username.isBlank()) {
            errors.add("Korisnicko ime ne moze biti prazno!");
        } else if (username.length() < 3 || username.length() > 50) {
            errors.add("Korisnicko ime mora imati između 3 i 50 znakova!");
        }
    }

    private void checkEmail(String email, List<String> errors) {
        if (email == null || email.isBlank()) {
            errors.add("Email adresa ne može biti prazna!");
        } else if (!isValidEmail(email)) {
            errors.add("Email adresa nije u ispravnom formatu!");
        }
    }

    private void checkPassword(String password, List<String> errors) {
        if (password == null || password.isBlank()) {
            errors.add("Lozinka ne moze biti prazna!");
        } else if (password.length() < 6) {
            errors.add("Lozinka mora imati barem 6 znakova!");
        }
    }

    private void checkAvailability(String username, String email, List<String> errors) {
        userRepository.findByName(username).ifPresent(user ->
                errors.add("Korisničko ime '" + username + "' je već zauzeto.")
        );

        userRepository.findByEmail(email).ifPresent(user ->
                errors.add("Korisnik s email adresom '" + email + "' već postoji.")
        );
    }
    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}