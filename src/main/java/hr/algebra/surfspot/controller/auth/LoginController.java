package hr.algebra.surfspot.controller.auth;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.context.UserSession;
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
    @FXML private PasswordField passwordField;

    private final AuthService authService;
    private final UserSession userSession;
    private final SceneNavigator sceneNavigator;

    public LoginController(AuthService authService, UserSession userSession, SceneNavigator sceneNavigator) {
        this.authService = authService;
        this.userSession = userSession;
        this.sceneNavigator = sceneNavigator;
    }

    @FXML
    private void handleLogin() {
        String usernameOrEmail = usernameField.getText();
        String password = passwordField.getText();

        try {
            User user = authService.login(usernameOrEmail, password);
            userSession.login(user);
            sceneNavigator.navigateToMain();

        } catch (ValidationException | AuthenticationException e) {
            log.warn("Login error: {}", e.getMessage());
            showError(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during login", e);
            showError("Došlo je do neočekivane greške.");
        }
    }

    @FXML
    private void handleRegisterLink() {
        sceneNavigator.navigateToRegister();
    }
}