package hr.algebra.surfspot.app;

import hr.algebra.surfspot.context.ApplicationContext;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SurfSpotApp extends Application {
    private static final Logger log = LoggerFactory.getLogger(SurfSpotApp.class);

    @Override
    public void start(Stage primaryStage) {
        log.info("Starting SurfSpot application");

        ApplicationContext context = ApplicationContext.getInstance();
        context.setPrimaryStage(primaryStage);

        context.getSceneNavigator().navigateToMain();
    }

    public static void main(String[] args) {
        launch(args);
    }
}