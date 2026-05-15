package hr.algebra.surfspot.controller.instructor;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.model.Instructor;
import hr.algebra.surfspot.service.InstructorService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class InstructorListController {
    private static final Logger log = LoggerFactory.getLogger(InstructorListController.class);

    @FXML private TableView<Instructor> instructorTable;
    @FXML private TableColumn<Instructor, String> firstNameColumn;
    @FXML private TableColumn<Instructor, String> lastNameColumn;

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

        loadInstructors();
    }

    private void loadInstructors() {
        try {
            List<Instructor> data = instructorService.findAll();
            ObservableList<Instructor> observableData = FXCollections.observableArrayList(data);
            instructorTable.setItems(observableData);
            log.info("Loaded {} instructors into table", data.size());
        } catch (Exception e) {
            log.error("Failed to load instructors from service", e);
        }
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

        try {
            instructorService.delete(selectedInstructor.getId());

            loadInstructors();
            log.info("Instruktor {} uspješno obrisan.", selectedInstructor.getFirstName() + " " + selectedInstructor.getLastName());
        } catch (Exception e) {
            log.error("Greška pri brisanju instruktora", e);
        }
    }
}