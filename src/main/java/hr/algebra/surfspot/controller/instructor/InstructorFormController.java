package hr.algebra.surfspot.controller.instructor;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.controller.BaseController;
import hr.algebra.surfspot.model.Instructor;
import hr.algebra.surfspot.model.SurfingSchool;
import hr.algebra.surfspot.service.InstructorService;
import hr.algebra.surfspot.service.SurfingSchoolService;
import hr.algebra.surfspot.util.DisplayConstants;
import javafx.application.Platform;
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

    @FXML
    private Label formTitleLabel;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private ComboBox<SurfingSchool> schoolComboBox;

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
            formTitleLabel.setText("Uredi instruktora surfanja");
            firstNameField.setText(instructor.getFirstName());
            lastNameField.setText(instructor.getLastName());
            schoolComboBox.setValue(instructor.getSchool());
        } else {
            formTitleLabel.setText("Novi instruktor surfanja");
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
            log.warn("Instructor save attempted, but not all required data supplied");
            showWarning(DisplayConstants.REQUIRE_MANDATORY_DATA);
            return;
        }

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();

        Thread.startVirtualThread(() -> {
            try {
                if (currentInstructor == null) {
                    Instructor newInstructor = Instructor.builder()
                            .firstName(firstName)
                            .lastName(lastName)
                            .school(selectedSchool)
                            .build();
                    instructorService.save(newInstructor);
                } else {
                    Instructor updatedInstructor = Instructor.builder()
                            .from(currentInstructor)
                            .firstName(firstName)
                            .lastName(lastName)
                            .school(selectedSchool)
                            .build();

                    instructorService.update(updatedInstructor);
                }
                Platform.runLater(sceneNavigator::navigateToInstructorList);
            } catch (Exception e) {
                log.error("An error occurred when attempted to save instructor", e);
                Platform.runLater(() -> showError("Došlo je do greške prilikom spremanja instruktora."));
            }
        });
    }

    @FXML
    private void handleBack() {
        sceneNavigator.navigateToInstructorList();
    }
}