package hr.algebra.surfspot.controller.surfspot;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.model.SurfSpot;
import hr.algebra.surfspot.service.SurfSpotService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SurfSpotFormController {
    private static final Logger log = LoggerFactory.getLogger(SurfSpotFormController.class);

    @FXML private Label formTitleLabel;
    @FXML private TextField nameField;

    private final SurfSpotService surfSpotService;
    private final SceneNavigator sceneNavigator;
    private SurfSpot currentSurfSpot;

    public SurfSpotFormController(SurfSpotService surfSpotService, SceneNavigator sceneNavigator) {
        this.surfSpotService = surfSpotService;
        this.sceneNavigator = sceneNavigator;
    }

    public void setSurfSpot(SurfSpot spot) {
        this.currentSurfSpot = spot;
        if (spot != null) {
            formTitleLabel.setText("Uredi surf spot");
            nameField.setText(spot.getName());
        } else {
            formTitleLabel.setText("Novi surf spot");
            nameField.clear();
        }
    }

    @FXML
    private void handleSave() {
        if (nameField.getText().isBlank()) {
            log.warn("Pokušaj spremanja s praznim poljima.");
            return;
        }

        try {
            if (currentSurfSpot == null) {
                SurfSpot newSurfSpot = SurfSpot.builder()
                        .name(nameField.getText())
                        .build();
                surfSpotService.save(newSurfSpot);
            } else {
                currentSurfSpot.setName(nameField.getText().trim());
                surfSpotService.update(currentSurfSpot);
            }
            sceneNavigator.navigateToSurfSpotList();
        } catch (Exception e) {
            log.error("Neuspjelo spremanje surf spota", e);
        }
    }

    @FXML
    private void handleBack() {
        sceneNavigator.navigateToSurfSpotList();
    }
}