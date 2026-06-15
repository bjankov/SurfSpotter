package hr.algebra.surfspot.app;

import hr.algebra.surfspot.context.ApplicationContext;
import hr.algebra.surfspot.util.SeedImageInstaller;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SurfSpotApp extends Application {
    private static final Logger log = LoggerFactory.getLogger(SurfSpotApp.class);

    @Override
    public void start(Stage primaryStage) {
        log.info("Starting SurfSpot application");

        ApplicationContext context = new ApplicationContext();

        // Seedanje fotografija mjesta za surfanje
        SeedImageInstaller.installSeedImages(List.of(
                "pipeline.jpg",
                "teahupoo.jpg",
                "uluwatu.jpg",
                "jeffreys-bay.jpg",
                "bells-beach.jpg",
                "nazare.jpg",
                "hossegor.jpg"
        ));

        context.setPrimaryStage(primaryStage);

        context.getSceneNavigator().navigateToMain();
    }

    public static void main(String[] args) {
        launch(args);
    }
}