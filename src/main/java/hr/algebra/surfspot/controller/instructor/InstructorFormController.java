package hr.algebra.surfspot.controller.instructor;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.controller.BaseController;
import hr.algebra.surfspot.model.Instructor;
import hr.algebra.surfspot.model.SurfingSchool;
import hr.algebra.surfspot.service.InstructorService;
import hr.algebra.surfspot.service.SurfingSchoolService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class InstructorFormController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(InstructorFormController.class);

    @FXML private Label formTitleLabel;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ComboBox<SurfingSchool> schoolComboBox;

    private final InstructorService instructorService;
    private final SurfingSchoolService schoolService;

    private final SceneNavigator sceneNavigator;
    private Instructor currentInstructor;

    public InstructorFormController(InstructorService instructorService, SurfingSchoolService schoolService, SceneNavigator sceneNavigator) {
        this.instructorService = instructorService;
        this.schoolService = schoolService;
        this.sceneNavigator = sceneNavigator;
    }

    public void setInstructor(Instructor instructor) {
        this.currentInstructor = instructor;
        if (instructor != null) {
            formTitleLabel.setText("Uredi instruktora");
            firstNameField.setText(instructor.getFirstName());
            lastNameField.setText(instructor.getLastName());
            schoolComboBox.setValue(instructor.getSchool());
        } else {
            formTitleLabel.setText("Novi instruktor");
            firstNameField.clear();
            lastNameField.clear();
            schoolComboBox.setValue(null);
        }
    }

    @FXML
    public void initialize() {
        List<SurfingSchool> schools = schoolService.findAll();
        schoolComboBox.setItems(FXCollections.observableArrayList(schools));

        schoolComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(SurfingSchool school) {
                return school != null ? school.getName() : "";
            }

            @Override
            public SurfingSchool fromString(String string) {
                return null;
            }
        });
    }

    @FXML
    private void handleSave() {
        SurfingSchool selectedSchool = schoolComboBox.getSelectionModel().getSelectedItem();

        if (firstNameField.getText().isBlank() || lastNameField.getText().isBlank()) {
            log.warn("Pokušaj spremanja s praznim poljima.");
            return;
        }

        try {
            if (currentInstructor == null) {
                Instructor newInst = Instructor.builder()
                        .firstName(firstNameField.getText().trim())
                        .lastName(lastNameField.getText().trim())
                        .school(selectedSchool)
                        .build();
                instructorService.save(newInst);
            } else {
                currentInstructor.setFirstName(firstNameField.getText().trim());
                currentInstructor.setLastName(lastNameField.getText().trim());
                currentInstructor.setSchool(selectedSchool);

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