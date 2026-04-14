package hr.algebra.surfspot.controller;

import javafx.scene.control.Alert;

public abstract class BaseController {
    protected void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
