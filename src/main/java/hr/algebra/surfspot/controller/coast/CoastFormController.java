package hr.algebra.surfspot.controller.coast;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.controller.BaseController;
import hr.algebra.surfspot.model.Coast;
import hr.algebra.surfspot.model.Country;
import hr.algebra.surfspot.service.CoastService;
import hr.algebra.surfspot.service.CountryService;
import hr.algebra.surfspot.util.DisplayConstants;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CoastFormController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(CoastFormController.class);

    @FXML private Label formTitleLabel;
    @FXML private TextField nameField;
    @FXML private ComboBox<Country> countryComboBox;

    private final CoastService coastService;
    private final CountryService countryService;
    private final SceneNavigator sceneNavigator;
    private Coast currentCoast;

    public CoastFormController(CoastService coastService, CountryService countryService, SceneNavigator sceneNavigator) {
        this.coastService = coastService;
        this.countryService = countryService;
        this.sceneNavigator = sceneNavigator;
    }

    public void initialize() {
        Thread.startVirtualThread(() -> {
            try {
                List<Country> countries = countryService.findAll();
                Platform.runLater(() -> countryComboBox.setItems(FXCollections.observableArrayList(countries)));
            } catch (Exception e) {
                Platform.runLater(() -> log.error("Došlo je do pogreške prilikom učitavanja država.", e));
                log.error("An error occurred during country loading in CoastForm");
            }
        });

        countryComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Country country) {
                return country == null ? "" : country.name();
            }
            @Override
            public Country fromString(String string) { return null; }
        });
    }

    public void setCoast(Coast coast) {
        this.currentCoast = coast;
        if (coast != null) {
            formTitleLabel.setText("Uredi obalu");
            nameField.setText(coast.getName());
            countryComboBox.setValue(coast.getCountry());
        } else {
            formTitleLabel.setText("Nova obala");
            nameField.clear();
        }
    }

    @FXML
    private void handleSave() {
        if (nameField.getText().isBlank() || countryComboBox.getValue() == null) {
            log.warn("Coast save attempted, but not all required data supplied");
            showWarning(DisplayConstants.REQUIRE_MANDATORY_DATA);
            return;
        }

        String name = nameField.getText().trim();
        Country selectedCountry = countryComboBox.getValue();

        Thread.startVirtualThread(() -> {
            try {
                if (currentCoast == null) {
                    Coast newCoast = Coast.builder()
                            .name(name)
                            .country(selectedCountry)
                            .build();
                    coastService.save(newCoast);
                } else {
                    Coast updatedCoast = Coast.builder()
                            .from(currentCoast)
                            .name(name)
                            .country(selectedCountry)
                            .build();
                    coastService.update(updatedCoast);
                }

                Platform.runLater(sceneNavigator::navigateToCoastList);

            } catch (Exception e) {
                log.error("An error occurred when attempted to save coast", e);
                Platform.runLater(() -> showError("Došlo je do greške prilikom spremanja obale."));
            }
        });
    }

    @FXML
    private void handleBack() {
        sceneNavigator.navigateToCoastList();
    }
}