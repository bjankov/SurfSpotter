package hr.algebra.surfspot.controller.school;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.controller.BaseController;
import hr.algebra.surfspot.model.SurfSpot;
import hr.algebra.surfspot.model.SurfingSchool;
import hr.algebra.surfspot.service.SurfSpotService;
import hr.algebra.surfspot.service.SurfingSchoolService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SurfingSchoolFormController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(SurfingSchoolFormController.class);

    @FXML private Label formTitleLabel;
    @FXML private TextField nameField;
    @FXML private MenuButton surfSpotsMenuButton;

    private final SurfingSchoolService surfingSchoolService;
    private final SurfSpotService surfSpotService;
    private final SceneNavigator sceneNavigator;
    private final Set<Long> selectedSurfSpotIds = new HashSet<>();
    private SurfingSchool currentSurfingSchool;

    public SurfingSchoolFormController(SurfingSchoolService surfingSchoolService,
                                       SurfSpotService surfSpotService,
                                       SceneNavigator sceneNavigator) {
        this.surfingSchoolService = surfingSchoolService;
        this.surfSpotService = surfSpotService;
        this.sceneNavigator = sceneNavigator;
    }

    @FXML
    public void initialize() {
        setupSurfSpotsMenu();
    }

    public void setSurfingSchool(SurfingSchool school) {
        this.currentSurfingSchool = school;
        if (school != null) {
            formTitleLabel.setText("Uredi školu surfanja");
            nameField.setText(school.getName());
            populateSurfSpotFields(school);
        } else {
            formTitleLabel.setText("Nova škola surfanja");
            nameField.clear();
            clearSurfSpotFields();
        }
    }

    @FXML
    private void handleSave() {
        if (nameField.getText().isBlank()) {
            log.warn("Pokušaj spremanja s praznim poljima.");
            return;
        }

        try {
            SurfingSchool savedSchool;
            if (currentSurfingSchool == null) {
                SurfingSchool newSurfingSchool = SurfingSchool.builder()
                        .name(nameField.getText())
                        .build();
                savedSchool = surfingSchoolService.save(newSurfingSchool);
            } else {
                currentSurfingSchool.setName(nameField.getText().trim());
                savedSchool = surfingSchoolService.update(currentSurfingSchool);
            }

            surfingSchoolService.updateSurfSpots(savedSchool.getId(), List.copyOf(selectedSurfSpotIds));

            sceneNavigator.navigateToSurfingSchoolList();
        } catch (Exception e) {
            log.error("Neuspjelo spremanje surfing schoole", e);
        }
    }

    @FXML
    private void handleBack() {
        sceneNavigator.navigateToSurfingSchoolList();
    }

    private void setupSurfSpotsMenu() {
        surfSpotsMenuButton.getItems().clear();

        List<SurfSpot> allSpots = surfSpotService.findAll();
        for (SurfSpot spot : allSpots) {
            CheckBox checkBox = new CheckBox(spot.getName());
            checkBox.setMaxWidth(Double.MAX_VALUE);

            CustomMenuItem menuItem = new CustomMenuItem(checkBox);
            menuItem.setHideOnClick(false);
            menuItem.setUserData(spot.getId());

            checkBox.selectedProperty().addListener((_, _, newValue) -> {
                if (Boolean.TRUE.equals(newValue)) {
                    selectedSurfSpotIds.add(spot.getId());
                } else {
                    selectedSurfSpotIds.remove(spot.getId());
                }
                updateSurfSpotsMenuText();
            });

            surfSpotsMenuButton.getItems().add(menuItem);
        }
    }

    private void populateSurfSpotFields(SurfingSchool school) {
        selectedSurfSpotIds.clear();

        List<SurfSpot> currentSpots = surfingSchoolService.findSurfSpotsForSchool(school.getId());
        for (SurfSpot spot : currentSpots) {
            selectedSurfSpotIds.add(spot.getId());
        }

        for (MenuItem item : surfSpotsMenuButton.getItems()) {
            if (item instanceof CustomMenuItem customItem && customItem.getContent() instanceof CheckBox cb) {
                Long spotId = (Long) customItem.getUserData();
                cb.setSelected(selectedSurfSpotIds.contains(spotId));
            }
        }
        updateSurfSpotsMenuText();
    }

    private void clearSurfSpotFields() {
        selectedSurfSpotIds.clear();
        for (MenuItem item : surfSpotsMenuButton.getItems()) {
            if (item instanceof CustomMenuItem customItem && customItem.getContent() instanceof CheckBox cb) {
                cb.setSelected(false);
            }
        }
        updateSurfSpotsMenuText();
    }

    private void updateSurfSpotsMenuText() {
        if (selectedSurfSpotIds.isEmpty()) {
            surfSpotsMenuButton.setText("Odaberi surf spotove");
        } else if (selectedSurfSpotIds.size() <= 3) {
            String text = surfSpotsMenuButton.getItems().stream()
                    .filter(item -> item instanceof CustomMenuItem customItem
                            && customItem.getContent() instanceof CheckBox cb
                            && cb.isSelected())
                    .map(item -> ((CheckBox) ((CustomMenuItem) item).getContent()).getText())
                    .collect(Collectors.joining(", "));
            surfSpotsMenuButton.setText(text);
        } else {
            surfSpotsMenuButton.setText(selectedSurfSpotIds.size() + " surf spotova odabrano");
        }
    }
}