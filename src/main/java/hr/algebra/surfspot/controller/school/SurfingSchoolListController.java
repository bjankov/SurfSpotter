package hr.algebra.surfspot.controller.school;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.controller.BaseController;
import hr.algebra.surfspot.model.SurfSpot;
import hr.algebra.surfspot.model.SurfingSchool;
import hr.algebra.surfspot.service.SurfingSchoolService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SurfingSchoolListController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(SurfingSchoolListController.class);

    @FXML private ListView<SurfingSchool> surfingSchoolListView;
    @FXML private ListView<SurfSpot> surfSpotListView;
    @FXML private TextField schoolSearchField;

    private final SurfingSchoolService surfingSchoolService;
    private final SceneNavigator sceneNavigator;

    private final ObservableList<SurfingSchool> schoolObservableList = FXCollections.observableArrayList();

    public SurfingSchoolListController(SurfingSchoolService surfingSchoolService, SceneNavigator sceneNavigator) {
        this.surfingSchoolService = surfingSchoolService;
        this.sceneNavigator = sceneNavigator;
    }

    @FXML
    public void initialize() {
        FilteredList<SurfingSchool> filteredSchools = new FilteredList<>(schoolObservableList, _ -> true);
        surfingSchoolListView.setItems(filteredSchools);

        schoolSearchField.textProperty().addListener((_, _, query) ->
            filteredSchools.setPredicate(school -> {
                if (query == null || query.isBlank()) {
                    return true;
                }
                String lowerCaseFilter = query.toLowerCase().trim();
                return school.getName().toLowerCase().contains(lowerCaseFilter);
            }));

        surfingSchoolListView.getSelectionModel().selectedItemProperty().addListener(
                (_, _, newVal) -> populateSurfSpots(newVal)
        );

        loadInitialData();
    }

    private void loadInitialData() {
        Thread.startVirtualThread(() -> {
            try {
                List<SurfingSchool> schools = surfingSchoolService.findAll();

                Platform.runLater(() -> schoolObservableList.setAll(schools));
                log.info("Loaded {} surfing schools", schools.size());
            } catch (Exception e) {
                log.error("Failed to load initial surfing school data", e);
                Platform.runLater(() -> showError("Došlo je do pogreške prilikom učitavanja podataka o školama surfanja."));
            }
        });
    }

    private void populateSurfSpots(SurfingSchool school) {
        if (school == null) {
            surfSpotListView.getItems().clear();
            return;
        }

        Thread.startVirtualThread(() -> {
            try {
                List<SurfSpot> spots = surfingSchoolService.findSurfSpotsForSchool(school.getId());

                log.info("Loaded {} surf spots for school {}", spots.size(), school.getName());
                Platform.runLater(() -> surfSpotListView.setItems(FXCollections.observableArrayList(spots)));
            } catch (Exception e) {
                Platform.runLater(() -> log.error("Failed to load surf spots for school {}", school.getId(), e));
            }
        });
    }

    @FXML
    private void handleAdd() {
        log.info("Navigating to form for new surfing school creation");
        sceneNavigator.navigateToSurfingSchoolForm(null);
    }

    @FXML
    private void handleEdit() {
        SurfingSchool selectedSchool = surfingSchoolListView.getSelectionModel().getSelectedItem();
        if (selectedSchool != null) {
            log.info("Editing surfing school: {}", selectedSchool.getId());
            sceneNavigator.navigateToSurfingSchoolForm(selectedSchool);
        } else {
            log.warn("Edit attempted, but no surfing school selected");
        }
    }

    @FXML
    private void handleDelete() {
        SurfingSchool selectedSurfingSchool = surfingSchoolListView.getSelectionModel().getSelectedItem();

        if (selectedSurfingSchool == null) {
            log.warn("Deletion attempted, but no surfing school was selected.");
            showWarning("Označite školu surfanja koju želite obrisati.");
            return;
        }

        if (showConfirmation("Jeste li sigurni da želite izbrisati odabranu školu surfanja?")) {
            Thread.startVirtualThread(() -> {
                try {
                    surfingSchoolService.delete(selectedSurfingSchool.getId());
                    log.info("Deleted surfing school with ID: {}", selectedSurfingSchool.getId());
                    Platform.runLater(() -> schoolObservableList.remove(selectedSurfingSchool));
                } catch (Exception e) {
                    log.error("Failed to delete surfing school with ID: {}", selectedSurfingSchool.getId(), e);
                    Platform.runLater(() -> showError("Došlo je do greške prilikom brisanja škole surfanja"));
                }
            });
        }
    }
}