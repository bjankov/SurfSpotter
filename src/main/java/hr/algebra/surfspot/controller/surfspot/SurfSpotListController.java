package hr.algebra.surfspot.controller.surfspot;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.model.SurfSpot;
import hr.algebra.surfspot.service.SurfSpotService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SurfSpotListController {
    private static final Logger log = LoggerFactory.getLogger(SurfSpotListController.class);

    @FXML private TableView<SurfSpot> surfSpotTable;
    @FXML private TableColumn<SurfSpot, String> nameColumn;
    @FXML private TableColumn<SurfSpot, String> locationColumn;
    @FXML private TableColumn<SurfSpot, String> difficultyColumn;

    private final SurfSpotService surfSpotService;
    private final SceneNavigator sceneNavigator;

    public SurfSpotListController(SurfSpotService surfSpotService , SceneNavigator sceneNavigator) {
        this.surfSpotService = surfSpotService;
        this.sceneNavigator = sceneNavigator;
    }

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        difficultyColumn.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        loadSurfSpots();
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
            log.warn("Edit clicked but no instructor selected");
        }
    }

    @FXML
    private void handleDelete() {
        SurfSpot selectedSurfSpot = surfSpotTable.getSelectionModel().getSelectedItem();

        if (selectedSurfSpot == null) {
            log.warn("Pokušaj brisanja bez odabranog mjesta za surfanje.");
            return;
        }

        try {
            surfSpotService.delete(selectedSurfSpot.getId());

            loadSurfSpots();
            log.info("Mjesto za surfanje {} uspješno obrisano.", selectedSurfSpot.getName());
        } catch (Exception e) {
            log.error("Greška pri brisanju surf spota", e);
        }
    }
}
