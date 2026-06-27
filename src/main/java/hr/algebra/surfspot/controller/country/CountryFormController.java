package hr.algebra.surfspot.controller.country;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.controller.BaseController;
import hr.algebra.surfspot.model.Country;
import hr.algebra.surfspot.service.CountryService;
import hr.algebra.surfspot.util.DisplayConstants;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CountryFormController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(CountryFormController.class);

    @FXML
    private Label formTitleLabel;
    @FXML
    private TextField nameField;
    @FXML
    private TextField codeField;

    private final CountryService countryService;
    private final SceneNavigator sceneNavigator;
    private Country currentCountry;

    public CountryFormController(CountryService countryService, SceneNavigator sceneNavigator) {
        this.countryService = countryService;
        this.sceneNavigator = sceneNavigator;
    }

    public void setCountry(Country country) {
        this.currentCountry = country;
        if (country != null) {
            formTitleLabel.setText("Uredi državu");
            codeField.setText(country.code());
            nameField.setText(country.name());
        } else {
            formTitleLabel.setText("Nova drzžava");
            nameField.clear();
        }
    }

    @FXML
    private void handleSave() {
        if (nameField.getText().isBlank() || codeField.getText().isBlank()) {
            log.warn("Country save attempted, but not all required data supplied");
            showWarning(DisplayConstants.REQUIRE_MANDATORY_DATA);
            return;
        }

        String code = codeField.getText().trim();
        String name = nameField.getText().trim();

        Thread.startVirtualThread(() -> {
            try {
                if (currentCountry == null) {
                    Country newCountry = new Country(code, name);
                    countryService.save(newCountry);
                } else {
                    Country updatedCountry = currentCountry.withName(name);
                    countryService.update(updatedCountry);
                }

                Platform.runLater(sceneNavigator::navigateToCountryList);

            } catch (Exception e) {
                log.error("An error occurred when attempted to save country", e);
                Platform.runLater(() -> showError("Došlo je do pogreške prilikom spremanja države."));
            }
        });
    }

        @FXML
        private void handleBack () {
            sceneNavigator.navigateToCountryList();
        }
    }
