package hr.algebra.surfspot.controller.country;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.model.Country;
import hr.algebra.surfspot.service.CountryService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CountryListController {
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
            List<Country> data = countryService.findAll();
            ObservableList<Country> observableData = FXCollections.observableArrayList(data);
            countryTable.setItems(observableData);
            log.info("Loaded {} countries into table", data.size());
        } catch (Exception e) {
            log.error("Failed to load countries from service", e);
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

        try {
            countryService.delete(selectedCountry.code());

            loadCountries();
            log.info("Zemlja {} uspješno obrisana.", selectedCountry.name());
        } catch (Exception e) {
            log.error("Greška pri brisanju zemlje.", e);
        }
    }
}
