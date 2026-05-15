package hr.algebra.surfspot.controller;

import hr.algebra.surfspot.context.SceneNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;

public class MainLayoutController {

    @FXML private StackPane contentArea;
    @FXML private ToggleGroup navigationGroup;

    private final SceneNavigator navigator;

    public MainLayoutController(SceneNavigator navigator) {
        this.navigator = navigator;
    }

    @FXML
    public void initialize() {
        navigator.setMainContentArea(contentArea);

        showSurfSpots();
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
        navigator.navigateToLogin();
    }

    public void showCoasts() {
        navigator.navigateToCoastList();
    }
}