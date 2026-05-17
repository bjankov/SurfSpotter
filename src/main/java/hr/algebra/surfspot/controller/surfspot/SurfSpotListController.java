package hr.algebra.surfspot.controller.surfspot;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.model.Instructor;
import hr.algebra.surfspot.model.SurfSpot;
import hr.algebra.surfspot.service.SurfSpotService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class SurfSpotListController {
    private static final Logger log = LoggerFactory.getLogger(SurfSpotListController.class);

    @FXML private TableView<SurfSpot> surfSpotTable;
    @FXML private TableColumn<SurfSpot, String> nameColumn;
    @FXML private TableColumn<SurfSpot, String> locationColumn;
    @FXML private TableColumn<SurfSpot, String> difficultyColumn;

    @FXML private Label locationLabel;
    @FXML private Label coordinatesLabel;
    @FXML private Label waveDetailsLabel;
    @FXML private Label windDetailsLabel;
    @FXML private Label seasonLabel;
    @FXML private ImageView spotImageView;
    @FXML private ListView<Instructor> instructorListView;

    private final SurfSpotService surfSpotService;
    private final SceneNavigator sceneNavigator;

    public SurfSpotListController(SurfSpotService surfSpotService, SceneNavigator sceneNavigator) {
        this.surfSpotService = surfSpotService;
        this.sceneNavigator = sceneNavigator;
    }

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        difficultyColumn.setCellValueFactory(new PropertyValueFactory<>("difficultyDisplayValue"));

        loadSurfSpots();

        surfSpotTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldSelection, newSelection) -> populateDetails(newSelection)
        );

        clearDetails();
    }

    private void loadSurfSpots() {
        try {
            List<SurfSpot> data = surfSpotService.findAll();
            ObservableList<SurfSpot> observableData = FXCollections.observableArrayList(data);
            surfSpotTable.setItems(observableData);
            log.info("Loaded {} surf spots into table", data.size());
        } catch (Exception e) {
            log.error("Failed to load surf spots from service", e);
        }
    }

    private void populateDetails(SurfSpot spot) {
        if (spot == null) {
            clearDetails();
            return;
        }

        String coastName = (spot.getLocation() != null && spot.getLocation().getCoast() != null)
                ? spot.getLocation().getCoast().getName() : "?";
        locationLabel.setText(String.format("%s, %s", coastName, spot.getCountryName()));

        coordinatesLabel.setText(spot.getLocation().getCoordinates().toString());
        waveDetailsLabel.setText(spot.getWaveDetails().toString());
        seasonLabel.setText(spot.getBestSeason().toString());

        if (spot.getWindDirectionDegrees() != null) {
            windDetailsLabel.setText(spot.getFormattedWindDetails());
        } else {
            windDetailsLabel.setText("Nije unesen");
        }

        if (spot.getInstructors() != null) {
            instructorListView.setItems(FXCollections.observableArrayList(spot.getInstructors()));
        } else {
            instructorListView.getItems().clear();
        }

        if (spot.getBestSeason() != null && !spot.getBestSeason().isEmpty()) {
            seasonLabel.setText(spot.getFormattedBestSeason());
        } else {
            seasonLabel.setText("Nije određena");
        }

        displayImage(spot.getImagePath());
    }

    private void displayImage(String imagePath) {
        if (imagePath != null && !imagePath.isBlank()) {
            File file = new File(imagePath);
            if (file.exists()) {
                spotImageView.setImage(new Image(file.toURI().toString()));
                return;
            }
        }
        spotImageView.setImage(null);
    }

    private void clearDetails() {
        locationLabel.setText("-");
        coordinatesLabel.setText("-");
        waveDetailsLabel.setText("-");
        windDetailsLabel.setText("-");
        seasonLabel.setText("-");
        instructorListView.getItems().clear();
        spotImageView.setImage(null);
    }

    @FXML
    private void handleAdd() {
        log.info("Navigating to form for new surf spot creation");
        sceneNavigator.navigateToSurfSpotForm(null);
    }

    @FXML
    private void handleEdit() {
        SurfSpot selected = surfSpotTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            log.info("Editing surf spot: {}", selected.getId());
            sceneNavigator.navigateToSurfSpotForm(selected);
        } else {
            log.warn("Edit clicked but no surf spot selected");
        }
    }

    @FXML
    private void handleDelete() {
        SurfSpot selected = surfSpotTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            log.warn("Delete clicked but no surf spot selected");
            return;
        }

        try {
            surfSpotService.delete(selected.getId());
            loadSurfSpots();
            log.info("Surf spot {} successfully deleted", selected.getName());
        } catch (Exception e) {
            log.error("Failed to delete surf spot", e);
        }
    }
}