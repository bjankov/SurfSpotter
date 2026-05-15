package hr.algebra.surfspot.controller.instructor;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.model.Instructor;
import hr.algebra.surfspot.service.InstructorService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstructorFormController {
    private static final Logger log = LoggerFactory.getLogger(InstructorFormController.class);

    @FXML private Label formTitleLabel;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;

    private final InstructorService instructorService;
    private final SceneNavigator sceneNavigator;
    private Instructor currentInstructor;

    public InstructorFormController(InstructorService instructorService, SceneNavigator sceneNavigator) {
        this.instructorService = instructorService;
        this.sceneNavigator = sceneNavigator;
    }

    public void setInstructor(Instructor instructor) {
        this.currentInstructor = instructor;
        if (instructor != null) {
            formTitleLabel.setText("Uredi instruktora");
            firstNameField.setText(instructor.getFirstName());
            lastNameField.setText(instructor.getLastName());
        } else {
            formTitleLabel.setText("Novi instruktor");
            firstNameField.clear();
            lastNameField.clear();
        }
    }

    @FXML
    private void handleSave() {
        if (firstNameField.getText().isBlank() || lastNameField.getText().isBlank()) {
            log.warn("Pokušaj spremanja s praznim poljima.");
            return;
        }

        try {
            if (currentInstructor == null) {
                Instructor newInst = Instructor.builder()
                        .firstName(firstNameField.getText().trim())
                        .lastName(lastNameField.getText().trim())
                        .build();
                instructorService.save(newInst);
            } else {
                currentInstructor.setFirstName(firstNameField.getText().trim());
                currentInstructor.setLastName(lastNameField.getText().trim());
                instructorService.update(currentInstructor);
            }
            sceneNavigator.navigateToInstructorList();
        } catch (Exception e) {
            log.error("Neuspjelo spremanje instruktora", e);
        }
    }

    @FXML
    private void handleBack() {
        sceneNavigator.navigateToInstructorList();
    }
}