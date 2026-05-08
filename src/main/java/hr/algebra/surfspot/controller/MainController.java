package hr.algebra.surfspot.controller;

import hr.algebra.surfspot.context.ApplicationContext;
import hr.algebra.surfspot.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @FXML
    private Label welcomeLabel;

    private final ApplicationContext applicationContext = ApplicationContext.getInstance();

    @FXML
    public void initialize() {
        User currentUser = applicationContext.getSession().getCurrentUser();
        if (currentUser != null) {
            String welcomeText = "Welcome to the Surf Spotter, " + currentUser.getUsername() + "!";
            if (welcomeLabel != null) {
                welcomeLabel.setText(welcomeText);
            }
            log.info("Main screen initialized for user: {}", currentUser.getUsername());
        }
    }

    @FXML
    private void handleLogout() {
        log.info("User logout initiated");
        applicationContext.getSession().logout();
    }
}
