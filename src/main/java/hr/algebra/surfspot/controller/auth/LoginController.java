package hr.algebra.surfspot.controller.auth;

import hr.algebra.surfspot.context.ApplicationContext;
import hr.algebra.surfspot.controller.BaseController;
import hr.algebra.surfspot.exception.AuthenticationException;
import hr.algebra.surfspot.exception.ValidationException;
import hr.algebra.surfspot.model.User;
import hr.algebra.surfspot.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private final ApplicationContext applicationContext = ApplicationContext.getInstance();
    private final AuthService authService = applicationContext.getAuthService();

    @FXML
    private void handleLogin() {
        String usernameOrEmail = usernameField.getText();
        String password = passwordField.getText();

        try {
            User user = authService.login(usernameOrEmail, password);
            applicationContext.setCurrentUser(user);
            applicationContext.getSceneNavigator().navigateToMain();

        } catch (ValidationException e) {
            log.warn("Validation error during login: {}", e.getMessage());
            showError(e.getMessage());
        } catch (AuthenticationException e) {
            log.warn("Authentication failed: {}", e.getMessage());
            showError(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during login", e);
            showError("Doslo je do neocekivane greske. Pokusajte ponovo.");
        }
    }

    @FXML
    private void handleRegisterLink() {
        applicationContext.getSceneNavigator().navigateToRegister();
    }
}
