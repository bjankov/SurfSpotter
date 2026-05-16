package hr.algebra.surfspot.controller.surfspot;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.model.Coast;
import hr.algebra.surfspot.model.Coordinates;
import hr.algebra.surfspot.model.DifficultyLevel;
import hr.algebra.surfspot.model.Location;
import hr.algebra.surfspot.model.SurfSpot;
import hr.algebra.surfspot.model.WaveDetails;
import hr.algebra.surfspot.model.WaveType;
import hr.algebra.surfspot.service.CoastService;
import hr.algebra.surfspot.service.SurfSpotService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

public class SurfSpotFormController {
    private static final Logger log = LoggerFactory.getLogger(SurfSpotFormController.class);

    @FXML private Label formTitleLabel;
    @FXML private TextField nameField;
    @FXML private ComboBox<Coast> coastComboBox;
    @FXML private TextField latitudeField;
    @FXML private TextField longitudeField;
    @FXML private ComboBox<WaveType> waveTypeComboBox;
    @FXML private TextField waveHeightField;
    @FXML private ComboBox<DifficultyLevel> difficultyComboBox;
    @FXML private TextField windDirectionField;

    private final SurfSpotService surfSpotService;
    private final CoastService coastService;
    private final SceneNavigator sceneNavigator;
    private SurfSpot currentSurfSpot;

    public SurfSpotFormController(SurfSpotService surfSpotService, CoastService coastService, SceneNavigator sceneNavigator) {
        this.surfSpotService = surfSpotService;
        this.coastService = coastService;
        this.sceneNavigator = sceneNavigator;
    }

    public void initialize() {
        List<Coast> coasts = coastService.findAll();
        coastComboBox.setItems(FXCollections.observableArrayList(coasts));

        waveTypeComboBox.setItems(FXCollections.observableArrayList(WaveType.values()));
        difficultyComboBox.setItems(FXCollections.observableArrayList(DifficultyLevel.values()));
    }

    public void setSurfSpot(SurfSpot spot) {
        this.currentSurfSpot = spot;
        if (spot != null) {
            formTitleLabel.setText("Uredi surf spot");
            nameField.setText(spot.getName());

            if (spot.getLocation() != null) {
                coastComboBox.setValue(spot.getLocation().getCoast());
                latitudeField.setText(spot.getLatitude() != null ? spot.getLatitude().toString() : "");
                longitudeField.setText(spot.getLongitude() != null ? spot.getLongitude().toString() : "");
            }

            if (spot.getWaveDetails() != null) {
                waveTypeComboBox.setValue(spot.getWaveType());
                waveHeightField.setText(spot.getWaveHeight() != null ? spot.getWaveHeight().toString() : "");
            }

            difficultyComboBox.setValue(spot.getDifficulty());
            windDirectionField.setText(spot.getWindDirectionDegrees() != null ? spot.getWindDirectionDegrees().toString() : "");
        } else {
            formTitleLabel.setText("Novi surf spot");
            clearForm();
        }
    }

    @FXML
    private void handleSave() {
        try {
            if (nameField.getText().isBlank() || coastComboBox.getValue() == null) {
                log.warn("Validation failed: Name or Coast is missing.");
                return;
            }

            BigDecimal lat = new BigDecimal(latitudeField.getText().trim());
            BigDecimal lon = new BigDecimal(longitudeField.getText().trim());
            Coordinates coordinates = new Coordinates(lat, lon);

            Coast selectedCoast = coastComboBox.getValue();
            Location location = new Location(coordinates, selectedCoast);

            WaveType waveType = waveTypeComboBox.getValue();
            Double waveHeight = Double.parseDouble(waveHeightField.getText().trim());
            WaveDetails waveDetails = new WaveDetails(waveType, waveHeight);

            SurfSpot spot = SurfSpot.builder()
                    .id(currentSurfSpot != null ? currentSurfSpot.getId() : null)
                    .name(nameField.getText().trim())
                    .location(location)
                    .waveDetails(waveDetails)
                    .difficulty(difficultyComboBox.getValue())
                    .windDirectionDegrees(Integer.parseInt(windDirectionField.getText().trim()))
                    .build();

            surfSpotService.save(spot);
            log.info("Surf spot {} successfully saved.", spot.getName());

            handleBack();

        } catch (NumberFormatException e) {
            log.error("Validation failed: Invalid number format in coordinates, height, or wind direction.", e);
        } catch (Exception e) {
            log.error("Failed to save surf spot", e);
        }
    }

    @FXML
    private void handleBack() {
        sceneNavigator.navigateToSurfSpotList();
    }

    private void clearForm() {
        nameField.clear();
        coastComboBox.setValue(null);
        latitudeField.clear();
        longitudeField.clear();
        waveTypeComboBox.setValue(null);
        waveHeightField.clear();
        difficultyComboBox.setValue(null);
        windDirectionField.clear();
    }
}