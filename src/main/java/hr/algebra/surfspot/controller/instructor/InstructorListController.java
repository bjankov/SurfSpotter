package hr.algebra.surfspot.controller.instructor;

import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.controller.BaseController;
import hr.algebra.surfspot.model.Instructor;
import hr.algebra.surfspot.model.SurfingSchool;
import hr.algebra.surfspot.service.InstructorService;
import hr.algebra.surfspot.service.SurfingSchoolService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.controlsfx.control.CheckComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class InstructorListController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(InstructorListController.class);

    @FXML private TableView<Instructor> instructorTable;
    @FXML private TableColumn<Instructor, String> firstNameColumn;
    @FXML private TableColumn<Instructor, String> lastNameColumn;
    @FXML private TableColumn<Instructor, String> schoolColumn;

    @FXML private TextField instructorSearchField;
    @FXML private CheckComboBox<SurfingSchool> schoolComboBox;

    private final InstructorService instructorService;
    private final SurfingSchoolService surfingSchoolService;
    private final SceneNavigator sceneNavigator;

    private final ObservableList<Instructor> instructorObservableList = FXCollections.observableArrayList();
    private FilteredList<Instructor> filteredInstructors;

    public InstructorListController(InstructorService instructorService, SurfingSchoolService surfingSchoolService, SceneNavigator sceneNavigator) {
        this.instructorService = instructorService;
        this.surfingSchoolService = surfingSchoolService;
        this.sceneNavigator = sceneNavigator;
    }

    @FXML
    public void initialize() {
        filteredInstructors = new FilteredList<>(instructorObservableList, _ -> true);
        instructorTable.setItems(filteredInstructors);

        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        schoolColumn.setCellValueFactory(cellData -> {
            SurfingSchool school = cellData.getValue().getSchool();
            return new SimpleStringProperty(school != null ? school.getName() : "Nema škole");
        });

        instructorSearchField.textProperty().addListener((_, _, _) -> updateFilters());

        schoolComboBox.getItems().addAll(surfingSchoolService.findAll());

        schoolComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<SurfingSchool>) _ -> updateFilters());

        loadInitialData();
    }

    private void updateFilters() {
        String searchText = instructorSearchField.getText() == null ? "" : instructorSearchField.getText().toLowerCase().trim();
        List<SurfingSchool> selectedSchools = schoolComboBox.getCheckModel().getCheckedItems();

        filteredInstructors.setPredicate(instructor -> {
            if (!searchText.isEmpty()) {
                String instructorData = (instructor.getFirstName() + " " + instructor.getLastName() + instructor.getSchool().getName()).toLowerCase();
                if (!instructorData.contains(searchText)) {
                    return false;
                }
            }

            return selectedSchools.isEmpty() || selectedSchools.contains(instructor.getSchool());
        });
    }

    private void loadInitialData() {
        Thread.startVirtualThread(() -> {
            try {
                List<Instructor> instructors = instructorService.findAll();

                Platform.runLater(() -> instructorObservableList.setAll(instructors));

                log.info("Loaded {} instructors", instructors.size());
            } catch (Exception e) {
                log.error("Failed to load initial instructor data", e);
                Platform.runLater(() -> showError("Došlo je do pogreške prilikom učitavanja podataka."));
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
            log.warn("Edit attempted, but no instructor selected");
            showWarning("Označite instruktora surfanja kojeg želite uređivati.");
        }
    }

    @FXML
    private void handleDelete() {
        Instructor selectedInstructor = instructorTable.getSelectionModel().getSelectedItem();

        if (selectedInstructor == null) {
            log.warn("Deletion attempted, but no instructor selected");
            showWarning("Označite obalu koju želite obrisati.");
            return;
        }

        if (showConfirmation("Jeste li sigurni da želite izbrisati odabranog instruktora?"))
            Thread.startVirtualThread(() -> {
                try {
                    instructorService.delete(selectedInstructor.getId());
                    Platform.runLater(() -> {
                        instructorObservableList.remove(selectedInstructor);
                        log.info("Deleted instructor with ID: {}", selectedInstructor.getId());
                    });
                } catch (Exception e) {
                    log.error("Failed to delete instructor with ID: {}", selectedInstructor.getId(), e);
                    Platform.runLater(() -> showError("Došlo je do greške prilikom brisanja instruktora."));
                }
            });
    }

    @FXML
    private void handleClearFilters() {
        instructorSearchField.clear();
        schoolComboBox.getCheckModel().clearChecks();
    }
}