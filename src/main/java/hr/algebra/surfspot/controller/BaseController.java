package hr.algebra.surfspot.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public abstract class BaseController {

    protected void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "Greska", message);
    }

    protected void showInfo(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Informacija", message);
    }

    protected void showWarning(String message) {
        showAlert(Alert.AlertType.WARNING, "Upozorenje", message);
    }

    protected void showSuccess(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Uspjeh", message);
    }

    protected boolean showConfirmation(String message) {
        return showAlert(Alert.AlertType.CONFIRMATION, "Potvrdi", message)
                .filter(buttonType -> buttonType == ButtonType.OK)
                .isPresent();
    }

    private Optional<ButtonType> showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
}
