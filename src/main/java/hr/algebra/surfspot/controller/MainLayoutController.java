package hr.algebra.surfspot.controller;

import hr.algebra.surfspot.context.ApplicationContext;
import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.model.Permission;
import hr.algebra.surfspot.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;

public class MainLayoutController {

    @FXML private StackPane contentArea;
    @FXML private ToggleGroup navigationGroup;

    @FXML private ToggleButton usersToggleButton;
    @FXML private ToggleButton countriesToggleButton;
    @FXML private ToggleButton schoolsToggleButton;
    @FXML private ToggleButton instructorsToggleButton;

    private final SceneNavigator navigator;

    public MainLayoutController(SceneNavigator navigator) {
        this.navigator = navigator;
    }

    @FXML
    public void initialize() {
        navigator.setMainContentArea(contentArea);

        User currentUser = ApplicationContext.getInstance().getSession().getCurrentUser();

        setupForUser(currentUser);

        showSurfSpots();
    }

    private void setupForUser(User user) {
        usersToggleButton.managedProperty().bind(usersToggleButton.visibleProperty());
        countriesToggleButton.managedProperty().bind(countriesToggleButton.visibleProperty());

        if (user != null) {
            usersToggleButton.setVisible(user.hasPermission(Permission.MANAGE_USERS));
            countriesToggleButton.setVisible(user.hasPermission(Permission.MANAGE_COUNTRIES));
        } else {
            usersToggleButton.setVisible(false);
            countriesToggleButton.setVisible(false);
        }
    }

    @FXML
    private void showInstructors() {
        navigator.navigateToInstructorList();
    }

    @FXML
    private void showSurfSpots() {
        navigator.navigateToSurfSpotList();
    }

    @FXML
    public void showSurfingSchools() {
        navigator.navigateToSurfingSchoolList();
    }

    @FXML
    private void handleLogout() {
        ApplicationContext.getInstance().getSession().logout();
        navigator.navigateToLogin();
    }

    @FXML
    private void showUsers() {
        if (Boolean.TRUE.equals(ApplicationContext.getInstance().getSession().getCurrentUser().hasPermission(Permission.MANAGE_USERS))) {
            navigator.navigateToUserList();
        }
    }

    @FXML
    private void showCountries() {
        if (Boolean.TRUE.equals(ApplicationContext.getInstance().getSession().getCurrentUser().hasPermission(Permission.MANAGE_COUNTRIES))) {
            navigator.navigateToCountryList();
        }
    }

    public void showCoasts() {
        navigator.navigateToCoastList();
    }
}