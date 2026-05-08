package hr.algebra.surfspot.app;

import hr.algebra.surfspot.context.ApplicationContext;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SurfSpotApp extends Application {
    private static final Logger log = LoggerFactory.getLogger(SurfSpotApp.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        log.info("Starting SurfSpot application");

        ApplicationContext context = ApplicationContext.getInstance();
        context.setPrimaryStage(primaryStage);

        if (context.isAuthenticated()) {
            log.info("User already authenticated, navigating to main screen");
            context.getNavigator().navigateToMain();
        } else {
            log.info("No authenticated user, navigating to login screen");
            context.getNavigator().navigateToLogin();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
