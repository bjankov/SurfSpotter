package hr.algebra.surfspot.controller.surfspot;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.controller.BaseController;
import hr.algebra.surfspot.exception.ValidationException;
import hr.algebra.surfspot.model.*;
import hr.algebra.surfspot.service.CoastService;
import hr.algebra.surfspot.service.SurfSpotService;
import hr.algebra.surfspot.util.ImageStorage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
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
        waveTypeComboBox.setItems(FXCollections.observableArrayList(WaveType.values()));
        waveTypeComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(WaveType waveType) {
                return (waveType != null) ? waveType.getDisplayValue() : "";
            }

            @Override
            public WaveType fromString(String string) {
                return null;
            }
        });

        difficultyComboBox.setItems(FXCollections.observableArrayList(DifficultyLevel.values()));
        difficultyComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(DifficultyLevel difficultyLevel) {
                return (difficultyLevel != null) ? difficultyLevel.getDisplayValue() : "";
            }

            @Override
            public DifficultyLevel fromString(String string) {
                return null;
            }
        });

        setupSeasonMenu();
        loadCoasts();
    }

    private void loadCoasts() {
        Thread.startVirtualThread(() -> {
            try {
                List<Coast> coasts = coastService.findAll();
                Platform.runLater(() -> coastComboBox.setItems(FXCollections.observableArrayList(coasts)));
            } catch (Exception e) {
                log.error("Failed to load coasts", e);
                Platform.runLater(() -> showError("Došlo je do pogreške prilikom učitavanja obala."));
            }
        });
    }

    public void setSurfSpot(SurfSpot spot) {
        this.currentSurfSpot = spot;
        if (spot == null) {
            formTitleLabel.setText("Novo mjesto za surfanje");
            clearForm();
            loadDefaultFormImage();
            return;
        }

        formTitleLabel.setText("Uredi mjesto za surfanje");
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
        String name = nameField.getText().trim();
        Coast coast = coastComboBox.getValue();
        DifficultyLevel difficulty = difficultyComboBox.getValue();
        String windDirectionText = windDirectionField.getText().trim();
        String latitudeText = latitudeField.getText().trim();
        String longitudeText = longitudeField.getText().trim();
        String waveHeightText = waveHeightField.getText().trim();

        if (!validateInputs(name, coast, difficulty, windDirectionText, latitudeText, longitudeText, waveHeightText)) return;

        BigDecimal latitude = new BigDecimal(latitudeText);
        BigDecimal longitude = new BigDecimal(longitudeText);
        Double waveHeight = waveHeightText.isBlank() ? null : Double.parseDouble(waveHeightText);
        Integer windDirection = windDirectionText.isBlank() ? null : Integer.parseInt(windDirectionText);

        Thread.startVirtualThread(() -> {
            try {
                SurfSpot spot = SurfSpot.builder()
                        .id(currentSurfSpot != null ? currentSurfSpot.getId() : null)
                        .name(name)
                        .location(buildLocation(latitude, longitude, coast))
                        .waveDetails(buildWaveDetails(waveHeight))
                        .difficulty(difficulty)
                        .windDirectionDegrees(windDirection)
                        .bestSeason(selectedMonths.isEmpty() ? EnumSet.noneOf(Month.class) : EnumSet.copyOf(selectedMonths))
                        .imagePath(resolveImagePath())
                        .build();
                surfSpotService.save(spot);
                log.info("Surf spot {} successfully saved.", spot.getName());
                Platform.runLater(sceneNavigator::navigateToSurfSpotList);
            } catch (ValidationException e) {
                log.warn("Validation failed when saving surf spot: {}", e.getMessage());
                Platform.runLater(() -> showError(e.getMessage()));
            } catch (Exception e) {
                log.error("Failed to save surf spot", e);
                Platform.runLater(() -> showError("Došlo je do greške prilikom spremanja mjesta za surfanje."));
            }
        });
    }

    private boolean validateInputs(String name, Coast coast, DifficultyLevel difficulty,
                                   String windDirection, String latitude, String longitude, String waveHeight) {
        if (name.isBlank()) {
            showWarning("Naziv mjesta za surfanje je obavezan.");
            return false;
        }
        if (coast == null) {
            showWarning("Odaberite obalu.");
            return false;
        }
        if (latitude.isBlank() || longitude.isBlank()) {
            showWarning("Koordinate su obavezne.");
            return false;
        }
        try {
            new BigDecimal(latitude);
            new BigDecimal(longitude);
        } catch (NumberFormatException _) {
            showWarning("Koordinate moraju biti decimalni brojevi.");
            return false;
        }
        if (difficulty == null) {
            showWarning("Odaberite razinu težine.");
            return false;
        }
        if (!windDirection.isBlank()) {
            try {
                Integer.parseInt(windDirection);
            } catch (NumberFormatException _) {
                showWarning("Smjer vjetra mora biti cijeli broj.");
                return false;
            }
        }
        if (!waveHeight.isBlank()) {
            try {
                Double.parseDouble(waveHeight);
            } catch (NumberFormatException _) {
                showWarning("Visina valova mora biti decimalni broj.");
                return false;
            }
        }
        return true;
    }

    private Location buildLocation(BigDecimal latitude, BigDecimal longitude, Coast coast) {
        return new Location(new Coordinates(latitude, longitude), coast);
    }

    private WaveDetails buildWaveDetails(Double waveHeight) {
        return new WaveDetails(waveTypeComboBox.getValue(), waveHeight);
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