package hr.algebra.surfspot.controller.surfspot;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.controller.BaseController;
import hr.algebra.surfspot.model.*;
import hr.algebra.surfspot.service.CoastService;
import hr.algebra.surfspot.service.SurfSpotService;
import hr.algebra.surfspot.util.ImageStorage;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class SurfSpotFormController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(SurfSpotFormController.class);
    private Path selectedImageFile = null;

    @FXML private Label formTitleLabel;
    @FXML private TextField nameField;
    @FXML private ComboBox<Coast> coastComboBox;
    @FXML private TextField latitudeField;
    @FXML private TextField longitudeField;
    @FXML private ComboBox<WaveType> waveTypeComboBox;
    @FXML private TextField waveHeightField;
    @FXML private ComboBox<DifficultyLevel> difficultyComboBox;
    @FXML private TextField windDirectionField;
    @FXML private MenuButton seasonMenuButton;
    @FXML private ImageView formImageView;

    private final SurfSpotService surfSpotService;
    private final CoastService coastService;
    private final SceneNavigator sceneNavigator;
    private final Set<Month> selectedMonths = EnumSet.noneOf(Month.class);
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

        setupSeasonMenu();
    }

    public void setSurfSpot(SurfSpot spot) {
        this.currentSurfSpot = spot;
        if (spot == null) {
            formTitleLabel.setText("Novi surf spot");
            clearForm();
            loadDefaultFormImage();
            return;
        }

        formTitleLabel.setText("Uredi surf spot");
        populateBasicFields(spot);
        populateLocationFields(spot);
        populateWaveFields(spot);
        populateSeasonFields(spot);
        populateFormImage(spot.getImagePath());
    }

    private void populateBasicFields(SurfSpot spot) {
        nameField.setText(spot.getName());
        difficultyComboBox.setValue(spot.getDifficulty());
        windDirectionField.setText(spot.getWindDirectionDegrees() != null
                ? spot.getWindDirectionDegrees().toString() : "");
    }

    private void populateLocationFields(SurfSpot spot) {
        if (spot.getLocation() == null) return;
        coastComboBox.setValue(spot.getLocation().getCoast());
        latitudeField.setText(spot.getLatitude() != null ? spot.getLatitude().toString() : "");
        longitudeField.setText(spot.getLongitude() != null ? spot.getLongitude().toString() : "");
    }

    private void populateWaveFields(SurfSpot spot) {
        if (spot.getWaveDetails() == null) return;
        waveTypeComboBox.setValue(spot.getWaveType());
        waveHeightField.setText(spot.getWaveHeight() != null ? spot.getWaveHeight().toString() : "");
    }

    private void populateSeasonFields(SurfSpot spot) {
        selectedMonths.clear();
        if (spot.getBestSeason() != null) {
            selectedMonths.addAll(spot.getBestSeason());
        }

        for (MenuItem item : seasonMenuButton.getItems()) {
            if (item instanceof CustomMenuItem customItem && customItem.getContent() instanceof CheckBox cb) {
                cb.setSelected(selectedMonths.contains(Month.valueOf(cb.getText())));
            }
        }
        updateSeasonMenuText();
    }

    private void populateFormImage(String imagePath) {
        if (imagePath != null && !imagePath.isBlank()) {
            java.nio.file.Path fullPath = ImageStorage.getStorageDir().resolve(imagePath);
            if (java.nio.file.Files.exists(fullPath)) {
                formImageView.setImage(new Image(fullPath.toUri().toString()));
                return;
            }
        }
        loadDefaultFormImage();
    }

    private void loadDefaultFormImage() {
        java.net.URL defaultUrl = getClass().getResource("/images/default.jpg");
        if (defaultUrl != null) {
            formImageView.setImage(new Image(defaultUrl.toExternalForm()));
        } else {
            formImageView.setImage(null);
        }
    }

    @FXML
    private void handleSave() {
        if (nameField.getText().isBlank() || coastComboBox.getValue() == null) {
            log.warn("Validation failed: Name or Coast is missing.");
            return;
        }

        try {
            SurfSpot spot = SurfSpot.builder()
                    .id(currentSurfSpot != null ? currentSurfSpot.getId() : null)
                    .name(nameField.getText().trim())
                    .location(buildLocation())
                    .waveDetails(buildWaveDetails())
                    .difficulty(difficultyComboBox.getValue())
                    .windDirectionDegrees(Integer.parseInt(windDirectionField.getText().trim()))
                    .bestSeason(selectedMonths.isEmpty() ? EnumSet.noneOf(Month.class) : EnumSet.copyOf(selectedMonths))
                    .imagePath(resolveImagePath())
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

    private String resolveImagePath() {
        if (selectedImageFile == null) {
            return currentSurfSpot != null ? currentSurfSpot.getImagePath() : null;
        }
        try {
            return ImageStorage.saveImage(selectedImageFile);
        } catch (IOException e) {
            log.error("Failed to save image, keeping existing path", e);
            return currentSurfSpot != null ? currentSurfSpot.getImagePath() : null;
        }
    }

    private Location buildLocation() {
        BigDecimal lat = new BigDecimal(latitudeField.getText().trim());
        BigDecimal lon = new BigDecimal(longitudeField.getText().trim());
        return new Location(new Coordinates(lat, lon), coastComboBox.getValue());
    }

    private WaveDetails buildWaveDetails() {
        return new WaveDetails(
                waveTypeComboBox.getValue(),
                Double.parseDouble(waveHeightField.getText().trim())
        );
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

        selectedMonths.clear();
        for (MenuItem item : seasonMenuButton.getItems()) {
            if (item instanceof CustomMenuItem customItem && customItem.getContent() instanceof CheckBox cb) {
                cb.setSelected(false);
            }
        }
        updateSeasonMenuText();
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Odaberi sliku surf spota");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Slike", "*.jpg", "*.jpeg", "*.png")
        );

        java.io.File file = fileChooser.showOpenDialog(formImageView.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file.toPath();
            formImageView.setImage(new Image(file.toURI().toString()));
        }
    }

    private void setupSeasonMenu() {
        seasonMenuButton.getItems().clear();

        for (Month month : Month.values()) {
            CheckBox checkBox = new CheckBox(month.name());
            checkBox.setMaxWidth(Double.MAX_VALUE);

            CustomMenuItem menuItem = new CustomMenuItem(checkBox);
            menuItem.setHideOnClick(false);

            checkBox.selectedProperty().addListener((_, _, newValue) -> {
                if (Boolean.TRUE.equals(newValue)) {
                    selectedMonths.add(month);
                } else {
                    selectedMonths.remove(month);
                }
                updateSeasonMenuText();
            });

            seasonMenuButton.getItems().add(menuItem);
        }
    }

    private void updateSeasonMenuText() {
        if (selectedMonths.isEmpty()) {
            seasonMenuButton.setText("Odaberi mjesece");
        } else {
            if (selectedMonths.size() <= 3) {
                String text = selectedMonths.stream()
                        .map(Enum::name)
                        .collect(Collectors.joining(", "));
                seasonMenuButton.setText(text);
            } else {
                seasonMenuButton.setText(selectedMonths.size() + " mjeseci odabrano");
            }
        }
    }
}