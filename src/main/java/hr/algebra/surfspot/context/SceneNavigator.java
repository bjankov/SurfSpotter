package hr.algebra.surfspot.context;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class SceneNavigator {
    private static final Logger log = LoggerFactory.getLogger(SceneNavigator.class);

    private final ApplicationContext applicationContext;

    private static final String LOGIN_FXML = "/fxml/auth/login.fxml";
    private static final String REGISTER_FXML = "/fxml/auth/register.fxml";
    private static final String MAIN_FXML = "/fxml/main.fxml";

    public SceneNavigator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void navigateToLogin() {
        log.info("Navigating to login screen");
        loadScene(LOGIN_FXML, "Surf Spotter - Login");
    }

    public void navigateToRegister() {
        log.info("Navigating to register screen");
        loadScene(REGISTER_FXML, "Surf Spotter - Register");
    }

    public void navigateToMain() {
        if (!applicationContext.isAuthenticated()) {
            log.warn("Attempted to navigate to main screen without authentication");
            navigateToLogin();
            return;
        }
        log.info("Navigating to main screen");
        loadScene(MAIN_FXML, "Surf Spotter - Welcome " + applicationContext.getCurrentUser().getUsername());
    }

    private void loadScene(String fxmlPath, String title) {
        try {
            URL fxmlLocation = getClass().getResource(fxmlPath);
            if (fxmlLocation == null) {
                throw new IllegalStateException("Cannot find FXML file: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            Stage stage = applicationContext.getPrimaryStage();
            Scene scene = new Scene(root);

            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();

            log.debug("Scene loaded successfully: {}", fxmlPath);

        } catch (IOException e) {
            log.error("Failed to load scene: {}", fxmlPath, e);
            throw new RuntimeException("Failed to load scene: " + fxmlPath, e);
        }
    }
}
