package hr.algebra.surfspot.controller.user;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.controller.BaseController;
import hr.algebra.surfspot.model.Role;
import hr.algebra.surfspot.model.User;
import hr.algebra.surfspot.service.UserService;
import hr.algebra.surfspot.util.DisplayConstants;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.controlsfx.control.CheckComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class UserListController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(UserListController.class);

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;

    @FXML private TextField userSearchField;
    @FXML private CheckComboBox<Role> roleCheckComboBox;

    private final UserService userService;
    private final SceneNavigator sceneNavigator;

    private final ObservableList<User> userObservableList = FXCollections.observableArrayList();
    private FilteredList<User> filteredUsers;

    public UserListController(UserService userService , SceneNavigator sceneNavigator) {
        this.userService = userService;
        this.sceneNavigator = sceneNavigator;
    }

    @FXML
    public void initialize() {
        filteredUsers = new FilteredList<>(userObservableList, _ -> true);
        userTable.setItems(filteredUsers);

        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(cellData -> {
                    Set<Role> roles = cellData.getValue().getRoles();
                    return new SimpleStringProperty(roles.contains(Role.ADMIN) ? DisplayConstants.ADMIN : DisplayConstants.USER);
                });

        userSearchField.textProperty().addListener((_, _, _) -> updateFilters());

        roleCheckComboBox.getItems().addAll(Role.values());
        roleCheckComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<Role>) _ -> updateFilters());
        roleCheckComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Role role) {
                return role.getDisplayValue();
            }

            @Override
            public Role fromString(String string) {
                return null;
            }
        });

        loadUsers();
    }

    private void updateFilters() {
        String searchText = userSearchField.getText() == null ? "" : userSearchField.getText().toLowerCase().trim();
        List<Role> selectedRoles = roleCheckComboBox.getCheckModel().getCheckedItems();

        filteredUsers.setPredicate(user -> {
            if (!searchText.isEmpty()) {
                String userInfo = (user.getUsername() + " " + user.getEmail()).toLowerCase();
                if (!userInfo.contains(searchText)) {
                    return false;
                }
            }

            return selectedRoles.isEmpty() || !Collections.disjoint(selectedRoles, user.getRoles());
        });
    }

    private void loadUsers() {
        Thread.startVirtualThread(() -> {
            try {
                List<User> users = userService.findAll();
                Platform.runLater(() -> {
                    userObservableList.setAll(users);
                    log.info("Loaded {} users into table", users.size());
                });
            } catch (Exception e) {
                Platform.runLater(() -> log.error("Failed to load users", e));
            }
        });
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
                Platform.runLater(() -> {
                    userObservableList.remove(selectedUser);
                    log.info("Korisnik {} uspješno obrisan.", selectedUser.getUsername());
                });
            } catch (Exception e) {
                Platform.runLater(() -> log.error("Failed to delete user with IDL: {}", selectedUser.getId(), e));
            }
        });
    }

    @FXML
    private void handleClearFilters() {
        userSearchField.clear();
        roleCheckComboBox.getCheckModel().clearChecks();
    }
}
