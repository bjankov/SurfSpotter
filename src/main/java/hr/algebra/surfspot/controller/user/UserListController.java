package hr.algebra.surfspot.controller.user;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.model.User;
import hr.algebra.surfspot.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserListController {
    private static final Logger log = LoggerFactory.getLogger(UserListController.class);

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> emailColumn;

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

        loadUsers();
    }

    private void loadUsers() {
        try {
            List<User> data = userService.findAll();
            ObservableList<User> observableData = FXCollections.observableArrayList(data);
            userTable.setItems(observableData);
            log.info("Loaded {} users into table", data.size());
        } catch (Exception e) {
            log.error("Failed to load users from service", e);
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

        try {
            userService.delete(selectedUser.getId());

            loadUsers();
            log.info("Korisnik {} uspješno obrisan.", selectedUser.getUsername());
        } catch (Exception e) {
            log.error("Greška pri brisanju korisnika", e);
        }
    }
}
