package hr.algebra.surfspot.controller.school;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.controller.BaseController;
import hr.algebra.surfspot.model.SurfingSchool;
import hr.algebra.surfspot.service.SurfingSchoolService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SurfingSchoolFormController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(SurfingSchoolFormController.class);

    @FXML private Label formTitleLabel;
    @FXML private TextField nameField;

    private final SurfingSchoolService surfingSchoolService;
    private final SceneNavigator sceneNavigator;
    private SurfingSchool currentSurfingSchool;

    public SurfingSchoolFormController(SurfingSchoolService surfingSchoolService, SceneNavigator sceneNavigator) {
        this.surfingSchoolService = surfingSchoolService;
        this.sceneNavigator = sceneNavigator;
    }

    public void setSurfingSchool(SurfingSchool school) {
        this.currentSurfingSchool = school;
        if (school != null) {
            formTitleLabel.setText("Uredi surfing school");
            nameField.setText(school.getName());
        } else {
            formTitleLabel.setText("Novi surfing school");
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
            if (currentSurfingSchool == null) {
                SurfingSchool newSurfingSchool = SurfingSchool.builder()
                        .name(nameField.getText())
                        .build();
                surfingSchoolService.save(newSurfingSchool);
            } else {
                currentSurfingSchool.setName(nameField.getText().trim());
                surfingSchoolService.update(currentSurfingSchool);
            }
            sceneNavigator.navigateToSurfingSchoolList();
        } catch (Exception e) {
            log.error("Neuspjelo spremanje surf spota", e);
        }
    }

    @FXML
    private void handleBack() {
        sceneNavigator.navigateToSurfingSchoolList();
    }
}
