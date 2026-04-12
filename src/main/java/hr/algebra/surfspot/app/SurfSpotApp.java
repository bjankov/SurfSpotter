package hr.algebra.surfspot.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class SurfSpotApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL fxmlLocation = getClass().getResource("/fxml/main.fxml");
        if (fxmlLocation == null) {
            throw new IllegalStateException("Ne mogu pronaći main.fxml. Provjeri resources folder!");
        }

        Parent root = FXMLLoader.load(fxmlLocation);

        Scene scene = new Scene(root);

        primaryStage.setTitle("SurfSpot Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
