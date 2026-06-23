package hr.algebra.surfspot.controller.country;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.controller.BaseController;
import hr.algebra.surfspot.model.Country;
import hr.algebra.surfspot.service.CountryService;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;

public class CountryListController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(CountryListController.class);

    @FXML private TableView<Country> countryTable;
    @FXML private TableColumn<Country, String> codeColumn;
    @FXML private TableColumn<Country, String> nameColumn;

    private final CountryService countryService;
    private final SceneNavigator sceneNavigator;

    public CountryListController(CountryService countryService , SceneNavigator sceneNavigator) {
        this.countryService = countryService;
        this.sceneNavigator = sceneNavigator;
    }

    @FXML
    public void initialize() {
        codeColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().code()));
        nameColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().name()));

        loadCountries();
    }

    private void loadCountries() {
        try {
            List<Country> countries = countryService.findAll();
            Platform.runLater(() -> {
                countryTable.setItems(observableArrayList(countries));
                log.info("Loaded {} countries into table", countries.size());
            });
        } catch (Exception e) {
            Platform.runLater(() -> log.error("Failed to load countries from service", e));
        }
    }

    @FXML
    private void handleAdd() {
        log.info("Navigating to form for new country creation");
        sceneNavigator.navigateToCountryForm(null);
    }

    @FXML
    private void handleEdit() {
        Country selectedCountry = countryTable.getSelectionModel().getSelectedItem();
        if (selectedCountry != null) {
            log.info("Editing country: {}", selectedCountry.code());
            sceneNavigator.navigateToCountryForm(selectedCountry);
        } else {
            log.warn("Edit clicked but no country selected");
        }
    }

    @FXML
    private void handleDelete() {
        Country selectedCountry = countryTable.getSelectionModel().getSelectedItem();

        if (selectedCountry == null) {
            log.warn("Pokušaj brisanja bez odabrane zemlje.");
            return;
        }

        Thread.startVirtualThread(() -> {
            try {
                countryService.delete(selectedCountry.code());
                Platform.runLater(() -> log.info("Zemlja {} uspješno obrisana.", selectedCountry.name()));
            } catch (Exception e) {
                Platform.runLater(() -> log.error("Greška pri brisanju zemlje.", e));
            }
        });
    }
}
