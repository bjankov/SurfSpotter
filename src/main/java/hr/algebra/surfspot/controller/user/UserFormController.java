package hr.algebra.surfspot.controller.user;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.model.User;
import hr.algebra.surfspot.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserFormController {
    private static final Logger log = LoggerFactory.getLogger(UserFormController.class);

    @FXML private Label formTitleLabel;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;

    private final UserService userService;
    private final SceneNavigator sceneNavigator;
    private User currentUser;

    public UserFormController(UserService userService, SceneNavigator sceneNavigator) {
        this.userService = userService;
        this.sceneNavigator = sceneNavigator;
    }

    public void setUser(User user) {
        this.currentUser = user;
        if (user != null) {
            formTitleLabel.setText("Uredi podatke o korisniku");
            usernameField.setText(user.getUsername());
        } else {
            formTitleLabel.setText("Novi korisnik");
            usernameField.clear();
        }
    }

    @FXML
    private void handleSave() {
        if (usernameField.getText().isBlank()) {
            log.warn("Pokušaj spremanja s praznim poljima.");
            return;
        }

        try {
            if (currentUser == null) {
                User newUser = User.builder()
                        .username(usernameField.getText())
                        .email(emailField.getText())
                        .build();
                userService.save(newUser);
            } else {
                currentUser.setUsername(usernameField.getText().trim());
                currentUser.setEmail(emailField.getText().trim());
                userService.update(currentUser);
            }
            sceneNavigator.navigateToUserList();
        } catch (Exception e) {
            log.error("Neuspjelo spremanje korisnika", e);
        }
    }

    @FXML
    private void handleBack() {
        sceneNavigator.navigateToCountryList();
    }
}
