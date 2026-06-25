package hr.algebra.surfspot.controller.country;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.controller.BaseController;
import hr.algebra.surfspot.model.Country;
import hr.algebra.surfspot.service.CountryService;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CountryListController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(CountryListController.class);

    @FXML private TableView<Country> countryTable;
    @FXML private TableColumn<Country, String> codeColumn;
    @FXML private TableColumn<Country, String> nameColumn;

    @FXML private TextField countrySearchBox;

    private final CountryService countryService;
    private final SceneNavigator sceneNavigator;

    private final ObservableList<Country> countryObservableList = FXCollections.observableArrayList();
    private FilteredList<Country> filteredCountries;

    public CountryListController(CountryService countryService , SceneNavigator sceneNavigator) {
        this.countryService = countryService;
        this.sceneNavigator = sceneNavigator;
    }

    @FXML
    public void initialize() {
        filteredCountries = new FilteredList<>(countryObservableList, _ -> true);
        countryTable.setItems(filteredCountries);

        codeColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().code()));
        nameColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().name()));

        countrySearchBox.textProperty().addListener((_, _, _) -> updateFilters());

        loadCountries();
    }

    private void updateFilters() {
        String searchText = countrySearchBox.getText() == null ? "" : countrySearchBox.getText().toLowerCase().trim();

        filteredCountries.setPredicate(country -> {
            if (!searchText.isEmpty()) {
                return country.name().toLowerCase().trim().contains(searchText);
            }
            return true;
        });
    }

    private void loadCountries() {
        Thread.startVirtualThread(() -> {
            try {
                List<Country> countries = countryService.findAll();
                Platform.runLater(() -> {
                    countryObservableList.setAll(countries);
                    log.info("Loaded {} countries into table", countries.size());
                });
            } catch (Exception e) {
                Platform.runLater(() -> log.error("Failed to load countries from service", e));
            }
        });
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
            log.warn("Deletion attempted, but no country selected.");
            return;
        }

        Thread.startVirtualThread(() -> {
            try {
                countryService.delete(selectedCountry.code());
                Platform.runLater(() -> {
                    countryObservableList.remove(selectedCountry);
                    log.info("Country {} deleted successfully.", selectedCountry.name());
                });
            } catch (Exception e) {
                Platform.runLater(() -> log.error("Greška pri brisanju države.", e));
            }
        });
    }
}
