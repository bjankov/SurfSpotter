package hr.algebra.surfspot.controller.auth;

import hr.algebra.surfspot.context.ApplicationContext;
import hr.algebra.surfspot.controller.BaseController;
import hr.algebra.surfspot.exception.DuplicateRecordException;
import hr.algebra.surfspot.exception.ValidationException;
import hr.algebra.surfspot.model.User;
import hr.algebra.surfspot.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @FXML
    private TextField emailField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField repeatPasswordField;

    private final ApplicationContext applicationContext = ApplicationContext.getInstance();
    private final AuthService authService = applicationContext.getAuthService();

    @FXML
    private void handleRegister() {
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String repeatPassword = repeatPasswordField.getText();

        try {
            // Validate password match
            if (!password.equals(repeatPassword)) {
                showError("Lozinke se ne podudaraju!");
                return;
            }

            // Register user
            User user = authService.register(username, email, password);

            // Auto-login after successful registration
            applicationContext.setCurrentUser(user);
            showInfo("Registracija uspjesna! Dobrodosli, " + username + "!");
            applicationContext.getSceneNavigator().navigateToMain();

        } catch (ValidationException e) {
            log.warn("Validation error during registration: {}", e.getMessage());
            showError(e.getMessage());
        } catch (DuplicateRecordException e) {
            log.warn("Duplicate record during registration: {}", e.getMessage());
            showError(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during registration", e);
            showError("Doslo je do neocekivane greske. Pokusajte ponovo.");
        }
    }

    @FXML
    private void handleLoginLink() {
        applicationContext.getSceneNavigator().navigateToLogin();
    }
}
