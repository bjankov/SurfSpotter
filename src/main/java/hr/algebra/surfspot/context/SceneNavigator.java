package hr.algebra.surfspot.context;

import hr.algebra.surfspot.controller.auth.AuthLayoutController;
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
        loadAuthLayout("/fxml/auth/login.fxml", "Prijava");
    }

    public void navigateToRegister() {
        loadAuthLayout("/fxml/auth/register.fxml", "Registracija");
    }

    // OVO JE DODANO: Zaštićena ruta (Protected Route)
    public void navigateToMain() {
        // 1. Guard provjera
        if (!context.isAuthenticated()) {
            navigateToLogin();
            return; // Prekidamo izvršavanje, korisnik ide na login
        }

        // 2. Ako je prijavljen, učitaj glavni ekran
        try {
            Parent root = loadFXML("/fxml/main.fxml");
            Stage stage = context.getPrimaryStage();
            stage.setTitle("SurfSpot - Glavni izbornik");
            // Main ekran je obično veći od auth ekrana
            stage.setScene(new Scene(root, 1024, 768));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Greška pri učitavanju Main ekrana", e);
        }
    }

    private void loadAuthLayout(String formFxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auth/main_auth_container.fxml"));
            loader.setControllerFactory(context::getController);

            Parent root = loader.load();
            AuthLayoutController layoutController = loader.getController();
            layoutController.loadForm(formFxmlPath);

            Stage stage = context.getPrimaryStage();
            stage.setTitle("SurfSpot - " + title);
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Greška pri učitavanju AuthLayout-a", e);
        }
    }

    public Parent loadFXML(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(context::getController);
        return loader.load();
    }
}