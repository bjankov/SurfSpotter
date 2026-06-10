package hr.algebra.surfspot.controller.coast;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.controller.BaseController;
import hr.algebra.surfspot.model.Coast;
import hr.algebra.surfspot.model.Country;
import hr.algebra.surfspot.service.CoastService;
import hr.algebra.surfspot.service.CountryService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        countryComboBox.setItems(FXCollections.observableArrayList(countryService.findAll()));

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
            log.warn("Pokušaj spremanja s praznim poljima ili neodabranom državom.");
            return;
        }

        try {
            Country selectedCountry = countryComboBox.getValue();

            if (currentCoast == null) {
                Coast newCoast = Coast.builder()
                        .name(nameField.getText().trim())
                        .country(selectedCountry)
                        .build();
                coastService.save(newCoast);
            } else {
                currentCoast.setName(nameField.getText().trim());
                currentCoast.setCountry(selectedCountry);
                coastService.update(currentCoast);
            }

            sceneNavigator.navigateToCoastList();
        } catch (Exception e) {
            log.error("Neuspjelo spremanje obale", e);
        }
    }

    @FXML
    private void handleBack() {
        sceneNavigator.navigateToCoastList();
    }
}