package hr.algebra.surfspot.controller.coast;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.controller.BaseController;
import hr.algebra.surfspot.model.Coast;
import hr.algebra.surfspot.service.CoastService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CoastListController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(CoastListController.class);

    @FXML private TableView<Coast> coastTable;
    @FXML private TableColumn<Coast, String> coastNameColumn;
    @FXML private TableColumn<Coast, String> coastCountryColumn;

    private final CoastService coastService;
    private final SceneNavigator sceneNavigator;

    public CoastListController(CoastService coastService, SceneNavigator sceneNavigator) {
        this.coastService = coastService;
        this.sceneNavigator = sceneNavigator;
    }

    @FXML
    public void initialize() {
        coastNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        coastCountryColumn.setCellValueFactory(new PropertyValueFactory<>("countryName"));
        loadCoasts();
    }

    private void loadCoasts() {
        try {
            List<Coast> data = coastService.findAll();
            ObservableList<Coast> observableData = FXCollections.observableArrayList(data);
            coastTable.setItems(observableData);
            log.info("Loaded {} coasts into table", data.size());
        } catch (Exception e) {
            log.error("Failed to load coasts from service", e);
        }
    }

    @FXML
    private void handleAdd() {
        log.info("Navigating to form for new coast creation");
        sceneNavigator.navigateToCoastForm(null);
    }

    @FXML
    private void handleEdit() {
        Coast selectedCoast = coastTable.getSelectionModel().getSelectedItem();
        if (selectedCoast != null) {
            log.info("Editing coast: {}", selectedCoast.getId());
            sceneNavigator.navigateToCoastForm(selectedCoast);
        } else {
            log.warn("Edit clicked but no coast selected");
        }
    }

    @FXML
    private void handleDelete() {
        Coast selectedCoast = coastTable.getSelectionModel().getSelectedItem();

        if (selectedCoast == null) {
            log.warn("Pokušaj brisanja bez odabrane obale.");
            return;
        }

        try {
            coastService.delete(selectedCoast.getId());

            loadCoasts();
            log.info("Obala {} uspješno obrisanq.", selectedCoast.getName());
        } catch (Exception e) {
            log.error("Greška pri brisanju obale", e);
        }
    }
}