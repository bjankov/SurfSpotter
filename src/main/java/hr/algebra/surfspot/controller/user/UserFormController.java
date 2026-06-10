package hr.algebra.surfspot.controller.user;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.controller.BaseController;
import hr.algebra.surfspot.exception.SurfSpotException;
import hr.algebra.surfspot.model.Role;
import hr.algebra.surfspot.model.User;
import hr.algebra.surfspot.service.RoleService;
import hr.algebra.surfspot.service.UserService;
import hr.algebra.surfspot.util.DisplayConstants;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserFormController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(UserFormController.class);

    @FXML private Label formTitleLabel;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private CheckBox isAdminCheckbox;

    private final UserService userService;
    private final RoleService roleService;
    private final SceneNavigator sceneNavigator;
    private User currentUser;

    public UserFormController(UserService userService, RoleService roleService, SceneNavigator sceneNavigator) {
        this.userService = userService;
        this.roleService = roleService;
        this.sceneNavigator = sceneNavigator;
    }

    public void setUser(User user) {
        this.currentUser = user;
        if (user != null) {
            formTitleLabel.setText("Uredi podatke o korisniku");
            usernameField.setText(user.getUsername());
            emailField.setText(user.getEmail());

            boolean isUserAdmin = user.getRoles() != null &&
                    user.getRoles().stream().anyMatch(role -> DisplayConstants.ADMIN.equals(role.getName()));

            isAdminCheckbox.setSelected(isUserAdmin);
        } else {
            formTitleLabel.setText("Novi korisnik");
            usernameField.clear();
            emailField.clear();
            isAdminCheckbox.setSelected(false);
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
                var userBuilder = User.builder()
                        .username(usernameField.getText().trim())
                        .email(emailField.getText().trim());

                if (isAdminCheckbox.isSelected()) {
                    Role adminRole = roleService.findByName(DisplayConstants.ADMIN)
                            .orElseThrow(() -> new SurfSpotException("Rola ADMIN ne postoji u bazi."));
                    userBuilder.addRole(adminRole);
                }

                userService.save(userBuilder.build());

            } else {
                currentUser.setUsername(usernameField.getText().trim());
                currentUser.setEmail(emailField.getText().trim());

                if (isAdminCheckbox.isSelected()) {
                    Role adminRole = roleService.findByName(DisplayConstants.ADMIN)
                            .orElseThrow(() -> new SurfSpotException("Rola ADMIN ne postoji u bazi."));
                    currentUser.getRoles().add(adminRole);
                } else {
                    currentUser.getRoles().removeIf(role -> DisplayConstants.ADMIN.equals(role.getName()));
                }

                userService.update(currentUser);
            }

            sceneNavigator.navigateToUserList();

        } catch (SurfSpotException e) {
            log.error("Neuspjelo spremanje korisnika", e);
        }
    }

    @FXML
    private void handleBack() {
        sceneNavigator.navigateToUserList();
    }
}