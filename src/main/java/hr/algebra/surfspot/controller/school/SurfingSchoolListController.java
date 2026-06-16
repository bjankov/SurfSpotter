package hr.algebra.surfspot.controller.school;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.controller.BaseController;
import hr.algebra.surfspot.model.SurfSpot;
import hr.algebra.surfspot.model.SurfingSchool;
import hr.algebra.surfspot.service.SurfingSchoolService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SurfingSchoolListController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(SurfingSchoolListController.class);

    @FXML
    private ListView<SurfingSchool> surfingSchoolListView;

    @FXML
    private ListView<SurfSpot> surfSpotListView;

    private final SurfingSchoolService surfingSchoolService;
    private final SceneNavigator sceneNavigator;

    public SurfingSchoolListController(SurfingSchoolService surfingSchoolService, SceneNavigator sceneNavigator) {
        this.surfingSchoolService = surfingSchoolService;
        this.sceneNavigator = sceneNavigator;
    }

    @FXML
    public void initialize() {
        surfingSchoolListView.getSelectionModel().selectedItemProperty().addListener(
                (_, _, newVal) -> populateSurfSpots(newVal)
        );

        loadSurfingSchools();
    }

    private void loadSurfingSchools() {
        try {
            List<SurfingSchool> schools = surfingSchoolService.findAll();
            Platform.runLater(() -> {
                surfingSchoolListView.setItems(FXCollections.observableArrayList(schools));
                log.info("Loaded {} surfing schools into list", schools.size());
            });
        } catch (Exception e) {
            log.error("Failed to load surfing schools from service", e);
        }
    }

    private void populateSurfSpots(SurfingSchool school) {
        if (school == null) {
            surfSpotListView.getItems().clear();
            return;
        }

        try {
            List<SurfSpot> spots = surfingSchoolService.findSurfSpotsForSchool(school.getId());
            surfSpotListView.setItems(FXCollections.observableArrayList(spots));
            log.info("Loaded {} surf spots for school {}", spots.size(), school.getName());
        } catch (Exception e) {
            log.error("Failed to load surf spots for school {}", school.getId(), e);
        }
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
            log.warn("Edit clicked but no surfing school selected");
        }
    }

    @FXML
    private void handleDelete() {
        SurfingSchool selectedSurfingSchool = surfingSchoolListView.getSelectionModel().getSelectedItem();

        if (selectedSurfingSchool == null) {
            log.warn("Pokušaj brisanja bez odabrane skole.");
            return;
        }

        try {
            surfingSchoolService.delete(selectedSurfingSchool.getId());
            Platform.runLater(() -> {
                loadSurfingSchools();
                log.info("Surfing school {} uspješno obrisan.", selectedSurfingSchool.getName());
            });
        } catch (Exception e) {
            log.error("Greška pri brisanju surfing school", e);
        }
    }
}