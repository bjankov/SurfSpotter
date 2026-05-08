package hr.algebra.surfspot.context;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneNavigator {

    private final ApplicationContext context;

    public SceneNavigator(ApplicationContext context) {
        this.context = context;
    }

    public void navigateToLogin() {
        loadScene("/fxml/auth/login.fxml", "Login");
    }

    public void navigateToRegister() {
        loadScene("/fxml/auth/register.fxml", "Register");
    }

    public void navigateToMain() {
        loadScene("/fxml/main.fxml", "Main");
    }

    private void loadScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            loader.setControllerFactory(context::getController);

            Parent root = loader.load();
            Stage stage = context.getPrimaryStage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Greška pri učitavanju FXML-a: " + fxmlPath, e);
        }
    }
}