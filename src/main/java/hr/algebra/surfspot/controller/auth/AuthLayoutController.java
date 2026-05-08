package hr.algebra.surfspot.controller.auth;

import hr.algebra.surfspot.context.SceneNavigator;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class AuthLayoutController {

    @FXML
    private StackPane formContainer;

    private final SceneNavigator sceneNavigator;

    public AuthLayoutController(SceneNavigator sceneNavigator) {
        this.sceneNavigator = sceneNavigator;
    }

    public void loadForm(String fxmlPath) {
        try {
            Parent form = sceneNavigator.loadFXML(fxmlPath);

            formContainer.getChildren().clear();
            formContainer.getChildren().add(form);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}