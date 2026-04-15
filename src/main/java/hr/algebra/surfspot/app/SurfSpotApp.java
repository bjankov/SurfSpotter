package hr.algebra.surfspot.app;

import hr.algebra.surfspot.context.ApplicationContext;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main JavaFX application class.
 * Initializes the application context and navigates to the appropriate scene
 * based on authentication state.
 */
public class SurfSpotApp extends Application {
    private static final Logger log = LoggerFactory.getLogger(SurfSpotApp.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        log.info("Starting SurfSpot application");

        // Initialize application context and set primary stage
        ApplicationContext context = ApplicationContext.getInstance();
        context.setPrimaryStage(primaryStage);

        // Navigate based on authentication state
        // Since we have no session persistence yet, always start at login
        if (context.isAuthenticated()) {
            log.info("User already authenticated, navigating to main screen");
            context.getSceneNavigator().navigateToMain();
        } else {
            log.info("No authenticated user, navigating to login screen");
            context.getSceneNavigator().navigateToLogin();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
