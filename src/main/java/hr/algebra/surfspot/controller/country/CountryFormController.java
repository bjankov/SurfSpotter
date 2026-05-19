package hr.algebra.surfspot.controller.country;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.model.Country;
import hr.algebra.surfspot.service.CountryService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CountryFormController {
    private static final Logger log = LoggerFactory.getLogger(CountryFormController.class);

    @FXML private Label formTitleLabel;
    @FXML private TextField nameField;
    @FXML private TextField codeField;

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
            formTitleLabel.setText("Uredi drzavu");
            nameField.setText(country.name());
        } else {
            formTitleLabel.setText("Nova drzava");
            nameField.clear();
        }
    }

    @FXML
    private void handleSave() {
        if (nameField.getText().isBlank() || codeField.getText().isBlank()) {
            log.warn("Pokušaj spremanja s praznim poljima.");
            return;
        }

        try {
            if (currentCountry == null) {
                Country newCountry = new Country(codeField.getText().trim(), nameField.getText().trim());
                countryService.save(newCountry);
            } else {
                Country updatedCountry = new Country(currentCountry.code(), nameField.getText().trim());
                countryService.update(updatedCountry);
            }
            sceneNavigator.navigateToCountryList();
        } catch (Exception e) {
            log.error("Neuspjelo spremanje države.", e);
        }
    }

    @FXML
    private void handleBack() {
        sceneNavigator.navigateToCountryList();
    }
}
