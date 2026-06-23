package hr.algebra.surfspot.controller.user;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.controller.BaseController;
import hr.algebra.surfspot.model.Role;
import hr.algebra.surfspot.model.User;
import hr.algebra.surfspot.service.UserService;
import hr.algebra.surfspot.util.DisplayConstants;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

import static javafx.collections.FXCollections.observableArrayList;

public class UserListController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(UserListController.class);

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML public TableColumn<User, String> roleColumn;

    private final UserService userService;
    private final SceneNavigator sceneNavigator;

    public UserListController(UserService userService , SceneNavigator sceneNavigator) {
        this.userService = userService;
        this.sceneNavigator = sceneNavigator;
    }

    @FXML
    public void initialize() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(cellData -> {
                    Set<Role> roles = cellData.getValue().getRoles();
                    return new SimpleStringProperty(roles.contains(Role.ADMIN) ? DisplayConstants.ADMIN : DisplayConstants.USER);
                });
        loadUsers();
    }

    private void loadUsers() {
        try {
            List<User> users = userService.findAll();
            Platform.runLater(() -> {
                userTable.setItems(observableArrayList(users));
                log.info("Loaded {} users into table", users.size());
            });
        } catch (Exception e) {
            Platform.runLater(() -> log.error("Failed to load users", e));
        }
    }

    @FXML
    private void handleEdit() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            log.info("Editing user: {}", selectedUser.getId());
            sceneNavigator.navigateToUserForm(selectedUser);
        } else {
            log.warn("Edit clicked but no user selected");
        }
    }

    @FXML
    private void handleDelete() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            log.warn("Pokušaj brisanja bez odabranog korisnika.");
            return;
        }

        Thread.startVirtualThread(() -> {
            try {
                userService.delete(selectedUser.getId());
                Platform.runLater(() -> log.info("Korisnik {} uspješno obrisan.", selectedUser.getUsername()));
            } catch (Exception e) {
                Platform.runLater(() -> log.error("Failed to delete user with IDL: {}", selectedUser.getId(), e));
            }
        });
    }
}
