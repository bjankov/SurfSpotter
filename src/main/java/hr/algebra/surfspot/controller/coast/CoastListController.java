package hr.algebra.surfspot.controller.coast;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.controller.BaseController;
import hr.algebra.surfspot.model.Coast;
import hr.algebra.surfspot.model.Country;
import hr.algebra.surfspot.service.CoastService;
import hr.algebra.surfspot.service.CountryService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.controlsfx.control.CheckComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CoastListController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(CoastListController.class);

    @FXML private TableView<Coast> coastTable;
    @FXML private TableColumn<Coast, String> coastNameColumn;
    @FXML private TableColumn<Coast, String> coastCountryColumn;

    @FXML private TextField coastSearchField;
    @FXML private CheckComboBox<Country> countryComboBox;

    private final CoastService coastService;
    private final CountryService countryService;
    private final SceneNavigator sceneNavigator;

    private final ObservableList<Coast> coastObservableList = FXCollections.observableArrayList();
    private FilteredList<Coast> filteredCoasts;

    public CoastListController(CoastService coastService, CountryService countryService, SceneNavigator sceneNavigator) {
        this.coastService = coastService;
        this.countryService = countryService;
        this.sceneNavigator = sceneNavigator;
    }

    @FXML
    public void initialize() {
        filteredCoasts = new FilteredList<>(coastObservableList, _ -> true);
        coastTable.setItems(filteredCoasts);

        coastNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        coastCountryColumn.setCellValueFactory(new PropertyValueFactory<>("countryName"));

        coastSearchField.textProperty().addListener((_, _, _) -> updateFilters());
        countryComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<Country>) _ -> updateFilters());

        loadInitialData();
    }

    private void updateFilters() {
        String searchText = coastSearchField.getText() == null ? "" : coastSearchField.getText().toLowerCase().trim();
        List<Country> selectedCountries = countryComboBox.getCheckModel().getCheckedItems();

        filteredCoasts.setPredicate(coast -> {
            if (!searchText.isEmpty() && !coast.getName().toLowerCase().contains(searchText)) {
                return false;
            }

            return selectedCountries.isEmpty() || selectedCountries.contains(coast.getCountry());
        });
    }

    private void loadInitialData() {
        Thread.startVirtualThread(() -> {
            try {
                List<Country> countries = countryService.findAll();
                List<Coast> coasts = coastService.findAll();

                Platform.runLater(() -> {
                    countryComboBox.getItems().setAll(countries);
                    coastObservableList.setAll(coasts);
                    log.info("Loaded {} countries and {} coasts", countries.size(), coasts.size());
                });
            } catch (Exception e) {
                Platform.runLater(() -> log.error("Failed to load initial data", e));
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
                Platform.runLater(() -> {
                    coastObservableList.remove(selectedCoast);
                    log.info("Deleted coast with ID: {}", selectedCoast.getId());
                });
            } catch (Exception e) {
                log.error("Failed to delete coast with ID: {}", selectedCoast.getId(), e);
            }
        });
    }

    @FXML
    private void handleClearFilters() {
        coastSearchField.clear();
        countryComboBox.getCheckModel().clearChecks();
    }
}