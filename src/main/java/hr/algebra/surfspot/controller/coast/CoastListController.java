package hr.algebra.surfspot.controller.coast;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.controller.BaseController;
import hr.algebra.surfspot.model.Coast;
import hr.algebra.surfspot.service.CoastService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;

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
        Thread.startVirtualThread(() -> {
            try {
                List<Coast> coasts = coastService.findAll();
                Platform.runLater(() -> {
                    coastTable.setItems(observableArrayList(coasts));
                    log.info("Loaded {} coasts", coasts.size());
                });
            } catch (Exception e) {
                Platform.runLater(() -> log.error("Failed to load coasts", e));
            }
        });
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

        Thread.startVirtualThread(() -> {
            try {
                coastService.delete(selectedCoast.getId());
                Platform.runLater(() -> log.info("Deleted coast with ID: {}", selectedCoast.getId()));
            } catch (Exception e) {
                log.error("Failed to delete coast with ID: {}", selectedCoast.getId(), e);
            }
                });
    }
}