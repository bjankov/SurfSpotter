package hr.algebra.surfspot.controller.school;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.controller.BaseController;
import hr.algebra.surfspot.model.SurfingSchool;
import hr.algebra.surfspot.service.SurfingSchoolService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SurfingSchoolListController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(SurfingSchoolListController.class);

    @FXML private TableView<SurfingSchool> surfingSchoolTable;
    @FXML private TableColumn<SurfingSchool, String> schoolNameColumn;

    private final SurfingSchoolService surfingSchoolService;
    private final SceneNavigator sceneNavigator;

    public SurfingSchoolListController(SurfingSchoolService surfingSchoolService , SceneNavigator sceneNavigator) {
        this.surfingSchoolService = surfingSchoolService;
        this.sceneNavigator = sceneNavigator;
    }

    @FXML
    public void initialize() {
        schoolNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        loadSurfingSchools();
    }

    private void loadSurfingSchools() {
        try {
            List<SurfingSchool> data = surfingSchoolService.findAll();
            ObservableList<SurfingSchool> observableData = FXCollections.observableArrayList(data);
            surfingSchoolTable.setItems(observableData);
            log.info("Loaded {} surfing schools into table", data.size());
        } catch (Exception e) {
            log.error("Failed to load surfing schools from service", e);
        }
    }

    @FXML
    private void handleAdd() {
        log.info("Navigating to form for new surfing school creation");
        sceneNavigator.navigateToSurfingSchoolForm(null);
    }

    @FXML
    private void handleEdit() {
        SurfingSchool selectedSchool = surfingSchoolTable.getSelectionModel().getSelectedItem();
        if (selectedSchool != null) {
            log.info("Editing surfing school: {}", selectedSchool.getId());
            sceneNavigator.navigateToSurfingSchoolForm(selectedSchool);
        } else {
            log.warn("Edit clicked but no surfing school selected");
        }
    }

    @FXML
    private void handleDelete() {
        SurfingSchool selectedSurfingSchool = surfingSchoolTable.getSelectionModel().getSelectedItem();

        if (selectedSurfingSchool == null) {
            log.warn("Pokušaj brisanja bez odabrane skole.");
            return;
        }

        try {
            surfingSchoolService.delete(selectedSurfingSchool.getId());

            loadSurfingSchools();
            log.info("Surfing school {} uspješno obrisan.", selectedSurfingSchool.getName());
        } catch (Exception e) {
            log.error("Greška pri brisanju surfing school", e);
        }
    }
}
