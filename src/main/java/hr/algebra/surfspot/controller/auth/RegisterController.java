package hr.algebra.surfspot.controller.auth;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.context.UserSession;
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

    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField repeatPasswordField;

    private final AuthService authService;
    private final UserSession userSession;
    private final SceneNavigator sceneNavigator;

    public RegisterController(AuthService authService, UserSession userSession, SceneNavigator sceneNavigator) {
        this.authService = authService;
        this.userSession = userSession;
        this.sceneNavigator = sceneNavigator;
    }

    @FXML
    private void handleRegister() {
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String repeatPassword = repeatPasswordField.getText();

        try {
            if (!password.equals(repeatPassword)) {
                showError("Lozinke se ne podudaraju!");
                return;
            }

            User user = authService.register(username, email, password);

            userSession.login(user);

            showInfo("Registracija uspješna! Dobrodošli, " + username + "!");
            sceneNavigator.navigateToMain();

        } catch (ValidationException | DuplicateRecordException e) {
            log.warn("Registration error: {}", e.getMessage());
            showError(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during registration", e);
            showError("Došlo je do neočekivane greške. Pokušajte ponovo.");
        }
    }

    @FXML
    private void handleLoginLink() {
        sceneNavigator.navigateToLogin();
    }
}