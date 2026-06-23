package hr.algebra.surfspot.controller.instructor;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.controller.BaseController;
import hr.algebra.surfspot.model.Instructor;
import hr.algebra.surfspot.model.SurfingSchool;
import hr.algebra.surfspot.service.InstructorService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;

public class InstructorListController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(InstructorListController.class);

    @FXML private TableView<Instructor> instructorTable;
    @FXML private TableColumn<Instructor, String> firstNameColumn;
    @FXML private TableColumn<Instructor, String> lastNameColumn;
    @FXML private TableColumn<Instructor, String> schoolColumn;

    private final InstructorService instructorService;
    private final SceneNavigator sceneNavigator;

    public InstructorListController(InstructorService instructorService, SceneNavigator sceneNavigator) {
        this.instructorService = instructorService;
        this.sceneNavigator = sceneNavigator;
    }

    @FXML
    public void initialize() {
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        schoolColumn.setCellValueFactory(cellData -> {
            SurfingSchool school = cellData.getValue().getSchool();
            return new SimpleStringProperty(school != null ? school.getName() : "Nema škole");
        });
        loadInstructors();
    }

    private void loadInstructors() {
        Thread.startVirtualThread( () -> {
            try {
                List<Instructor> instructors = this.instructorService.findAll();

                Platform.runLater(() -> {
                    instructorTable.setItems(observableArrayList(instructors));
                    log.info("Loaded {} instructors", instructors.size());
                });
            } catch (Exception ex) {
                Platform.runLater(() -> log.error("Failed to load instructors", ex));
            }
        });
    }

    @FXML
    private void handleAdd() {
        log.info("Navigating to form for new instructor creation");
        sceneNavigator.navigateToInstructorForm(null);
    }

    @FXML
    private void handleEdit() {
        Instructor selectedInstructor = instructorTable.getSelectionModel().getSelectedItem();
        if (selectedInstructor != null) {
            log.info("Editing instructor: {}", selectedInstructor.getId());
            sceneNavigator.navigateToInstructorForm(selectedInstructor);
        } else {
            log.warn("Edit clicked but no instructor selected");
        }
    }

    @FXML
    private void handleDelete() {
        Instructor selectedInstructor = instructorTable.getSelectionModel().getSelectedItem();

        if (selectedInstructor == null) {
            log.warn("Pokušaj brisanja bez odabranog instruktora.");
            return;
        }

        Thread.startVirtualThread(() -> {
            try {
                instructorService.delete(selectedInstructor.getId());
                Platform.runLater(() -> log.info("Deleted instructor with ID: {}", selectedInstructor.getId()));
            } catch (Exception e) {
                Platform.runLater(() -> log.warn("Failed to delete instructor with ID: {}", selectedInstructor.getId(), e));
            }
        });
    }

}