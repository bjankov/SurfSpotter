package hr.algebra.surfspot.controller.surfspot;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.controller.BaseController;
import hr.algebra.surfspot.exception.ResourceNotFoundException;
import hr.algebra.surfspot.model.*;
import hr.algebra.surfspot.service.CoastService;
import hr.algebra.surfspot.service.CountryService;
import hr.algebra.surfspot.service.SurfSpotService;
import hr.algebra.surfspot.util.ImageStorage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import org.controlsfx.control.CheckComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.DoublePredicate;

import static java.util.Collections.disjoint;
import static javafx.collections.FXCollections.observableArrayList;

public class SurfSpotListController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(SurfSpotListController.class);

    @FXML private TableView<SurfSpot> surfSpotTable;
    @FXML private TableColumn<SurfSpot, String> nameColumn;
    @FXML private TableColumn<SurfSpot, String> locationColumn;
    @FXML private TableColumn<SurfSpot, String> difficultyColumn;

    @FXML private Label locationLabel;
    @FXML private Label coordinatesLabel;
    @FXML private Label waveDetailsLabel;
    @FXML private Label windDetailsLabel;
    @FXML private Label seasonLabel;
    @FXML private ImageView spotImageView;
    @FXML private ListView<Instructor> instructorListView;

    @FXML private SplitPane mainSplitPane;
    @FXML private VBox itineraryPanel;
    @FXML private ListView<SurfSpot> itineraryListView;

    // Filteri
    @FXML private TextField spotSearchField;
    @FXML private CheckComboBox<DifficultyLevel> difficultyComboBox;
    @FXML private CheckComboBox<WaveType> waveTypeComboBox;
    @FXML private TextField minWaveHeightField;
    @FXML private TextField maxWaveHeightField;
    @FXML private CheckComboBox<Month> seasonComboBox;
    @FXML private CheckComboBox<Coast> coastComboBox;
    @FXML private CheckComboBox<Country> countryComboBox;

    private final ObservableList<SurfSpot> masterSpotData = FXCollections.observableArrayList();
    private FilteredList<SurfSpot> filteredSpots;

    private SurfSpot draggedSpot;
    private final SurfSpotService surfSpotService;
    private final SceneNavigator sceneNavigator;
    private final CoastService coastService;
    private final CountryService countryService;
    private Thread pendingDetailThread;

    public SurfSpotListController(SurfSpotService surfSpotService, CoastService coastService, CountryService countryService, SceneNavigator sceneNavigator) {
        this.surfSpotService = surfSpotService;
        this.coastService = coastService;
        this.countryService = countryService;
        this.sceneNavigator = sceneNavigator;
    }

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        difficultyColumn.setCellValueFactory(new PropertyValueFactory<>("difficultyDisplayValue"));

        filteredSpots = new FilteredList<>(masterSpotData, _ -> true);
        SortedList<SurfSpot> sortedData = new SortedList<>(filteredSpots);
        sortedData.comparatorProperty().bind(surfSpotTable.comparatorProperty());
        surfSpotTable.setItems(sortedData);

        setupFilterListeners();
        loadSurfSpots();

        surfSpotTable.getSelectionModel().selectedItemProperty().addListener(
                (_, _, newVal) -> populateDetails(newVal)
        );

        mainSplitPane.getItems().remove(itineraryPanel);

        setupDragAndDrop();
        clearDetails();
    }

    private void setupFilterListeners() {
        difficultyComboBox.getItems().addAll(DifficultyLevel.values());
        difficultyComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(DifficultyLevel level) {
                return level == null ? "" : level.getDisplayValue();
            }

            @Override
            public DifficultyLevel fromString(String string) {
                return null;
            }
        });
        waveTypeComboBox.getItems().addAll(WaveType.values());
        waveTypeComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(WaveType type) {
                return type == null ? "" : type.getDisplayValue();
            }

            @Override
            public WaveType fromString(String string) {
                return null;
            }
        });
        coastComboBox.getItems().addAll(coastService.findAll());

        coastComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Coast coast) {
                return coast == null ? "" : coast.getName();
            }
            @Override
            public Coast fromString(String string) { return null; }
        });

        countryComboBox.getItems().addAll(countryService.findAll());

        countryComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Country country) {
                return country == null ? "" : country.name();
            }
            @Override
            public Country fromString(String string) { return null; }
        });

        seasonComboBox.getItems().addAll(Month.values());
        seasonComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Month month) {
                return month == null ? "" : month.getDisplayValue();
            }
            @Override
            public Month fromString(String string) { return null; }
        });

        spotSearchField.textProperty().addListener((_, _, _) -> applyFilters());
        difficultyComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<DifficultyLevel>) _ -> applyFilters());
        waveTypeComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<WaveType>) _ -> applyFilters());

        minWaveHeightField.textProperty().addListener((_, _, _) -> applyFilters());
        maxWaveHeightField.textProperty().addListener((_, _, _) -> applyFilters());

        seasonComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<Month>) _ -> applyFilters());
        coastComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<Coast>) _ -> applyFilters());
        countryComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<Country>) _ -> applyFilters());
    }

    private void applyFilters() {
        filteredSpots.setPredicate(spot -> {

            String searchText = spotSearchField.getText();
            if (searchText != null && !searchText.isBlank()) {
                return spot.getName().toLowerCase().contains(searchText.toLowerCase().trim());
            }

            ObservableList<DifficultyLevel> difficulties = difficultyComboBox.getCheckModel().getCheckedItems();
            if (!difficulties.isEmpty() && !difficulties.contains(spot.getDifficulty())) {
                return false;
            }

            ObservableList<WaveType> waveTypes = waveTypeComboBox.getCheckModel().getCheckedItems();
            if (!waveTypes.isEmpty() && !waveTypes.contains(spot.getWaveType())) {
                return false;
            }

            Double avgHeight = spot.getWaveHeight();
            if (avgHeight != null) {
                if (!isValidWaveHeightFilter(minWaveHeightField, min -> avgHeight < min)) return false;
                if (!isValidWaveHeightFilter(maxWaveHeightField, max -> avgHeight > max)) return false;
            }

            ObservableList<Month> months = seasonComboBox.getCheckModel().getCheckedItems();
            if (!months.isEmpty() && disjoint(months, spot.getBestSeason())) {
                return false;
            }

            ObservableList<Coast> coasts = coastComboBox.getCheckModel().getCheckedItems();
            if (!coasts.isEmpty() && !coasts.contains(spot.getCoast())) {
                return false;
            }

            ObservableList<Country> countries = countryComboBox.getCheckModel().getCheckedItems();
            if (!countries.isEmpty()) {
                Country spotCountry = (spot.getCoast() != null) ? spot.getCoast().getCountry() : null;
                return countries.contains(spotCountry);
            }

            return true;
        });
    }

    @FXML
    private void handleClearFilters() {
        spotSearchField.clear();
        difficultyComboBox.getCheckModel().clearChecks();
        waveTypeComboBox.getCheckModel().clearChecks();
        minWaveHeightField.clear();
        maxWaveHeightField.clear();
        seasonComboBox.getCheckModel().clearChecks();
        coastComboBox.getCheckModel().clearChecks();
        countryComboBox.getCheckModel().clearChecks();
    }

    private void loadSurfSpots() {
        Thread.startVirtualThread(() -> {
            try {
                List<SurfSpot> surfSpots = this.surfSpotService.findAll();
                Platform.runLater(() -> {
                    masterSpotData.setAll(surfSpots);
                    log.info("Loaded {} surf spots", surfSpots.size());
                });
            } catch (Exception e) {
                Platform.runLater(() -> log.error("Failed to load surf spots", e));
            }
        });
    }

    private void populateDetails(SurfSpot spot) {
        if (spot == null) {
            clearDetails();
            return;
        }

        if (pendingDetailThread != null && pendingDetailThread.isAlive()) {
            pendingDetailThread.interrupt();
        }

        pendingDetailThread = Thread.startVirtualThread(() -> {
            try {
                SurfSpot loaded = surfSpotService.findById(spot.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Nije pronađeno..."));

                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }

                Platform.runLater(() -> {
                    String coastName = (loaded.getLocation() != null && loaded.getLocation().getCoast() != null)
                            ? loaded.getLocation().getCoast().getName() : "?";

                    locationLabel.setText(String.format("%s, %s", coastName, loaded.getCountryName()));
                    coordinatesLabel.setText(loaded.getLocation().getCoordinates().toString());
                    waveDetailsLabel.setText(loaded.getWaveDetails().toString());
                    windDetailsLabel.setText(loaded.getWindDirectionDegrees() != null ? loaded.getFormattedWindDetails() : "Nije unesen");
                    seasonLabel.setText(loaded.getBestSeason() != null && !loaded.getBestSeason().isEmpty() ? loaded.getFormattedBestSeason() : "Nije određena");

                    instructorListView.setItems(loaded.getInstructors() != null ? observableArrayList(loaded.getInstructors()) : FXCollections.emptyObservableList());

                    loadSpotImage(loaded.getImagePath());
                });

            } catch (InterruptedException _) {
                log.debug("Detail load cancelled for: {}", spot.getName());
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                Platform.runLater(() -> log.error("Failed to load spot details", e));
            }
        });
    }

    private void clearDetails() {
        locationLabel.setText("-");
        coordinatesLabel.setText("-");
        waveDetailsLabel.setText("-");
        windDetailsLabel.setText("-");
        seasonLabel.setText("-");
        instructorListView.getItems().clear();
        spotImageView.setImage(null);
    }

    private void loadSpotImage(String imagePath) {
        if (imagePath != null && !imagePath.isBlank()) {
            Path fullPath = ImageStorage.getStorageDir().resolve(imagePath);
            if (Files.exists(fullPath)) {
                spotImageView.setImage(new Image(fullPath.toUri().toString(), true));
                return;
            }
        }
        loadDefaultImage();
    }

    private void loadDefaultImage() {
        java.net.URL url = getClass().getResource("/images/default.jpg");
        spotImageView.setImage(url != null ? new Image(url.toExternalForm()) : null);
    }

    private void setupDragAndDrop() {
        setupTableAsDragSource();
        setupListAsDropTarget();
        itineraryListView.setCellFactory(_ -> createItineraryCell());
    }

    private void setupTableAsDragSource() {
        surfSpotTable.setOnDragDetected(event -> {
            SurfSpot selected = surfSpotTable.getSelectionModel().getSelectedItem();
            if (selected != null && selected.getId() != null) {
                draggedSpot = selected;
                startDrag(surfSpotTable.startDragAndDrop(TransferMode.COPY), selected.getName());
            }
            event.consume();
        });
    }

    private void setupListAsDropTarget() {
        itineraryListView.setOnDragOver(event -> {
            if (draggedSpot != null) event.acceptTransferModes(TransferMode.COPY);
            event.consume();
        });

        itineraryListView.setOnDragDropped(event -> {
            addToItinerary(draggedSpot, itineraryListView.getItems().size());
            draggedSpot = null;
            event.setDropCompleted(true);
            event.consume();
        });
    }

    private ListCell<SurfSpot> createItineraryCell() {
        ItineraryCell cell = new ItineraryCell();

        cell.setOnDragDetected(event -> {
            if (!cell.isEmpty()) {
                draggedSpot = cell.getItem();
                startDrag(cell.startDragAndDrop(TransferMode.MOVE), draggedSpot.getName());
            }
            event.consume();
        });

        cell.setOnDragOver(event -> {
            if (draggedSpot != null) event.acceptTransferModes(TransferMode.COPY, TransferMode.MOVE);
            event.consume();
        });

        cell.setOnDragDropped(event -> {
            int targetIndex = cell.isEmpty() ? itineraryListView.getItems().size() : cell.getIndex();
            boolean fromTable = event.getGestureSource() == surfSpotTable;

            if (fromTable) {
                addToItinerary(draggedSpot, targetIndex);
            } else {
                reorderItinerary(itineraryListView.getItems().indexOf(draggedSpot), targetIndex);
            }

            draggedSpot = null;
            event.setDropCompleted(true);
            event.consume();
        });

        return cell;
    }

    private void addToItinerary(SurfSpot spot, int targetIndex) {
        if (spot == null) return;
        boolean duplicate = itineraryListView.getItems().stream()
                .anyMatch(s -> s.getId().equals(spot.getId()));
        if (!duplicate) {
            itineraryListView.getItems().add(targetIndex, spot);
            log.info("Added to itinerary: {}", spot.getName());
        }
    }

    private void reorderItinerary(int sourceIndex, int targetIndex) {
        if (sourceIndex < 0) return;
        ObservableList<SurfSpot> items = itineraryListView.getItems();
        SurfSpot spot = items.remove(sourceIndex);
        items.add(sourceIndex < targetIndex ? targetIndex - 1 : targetIndex, spot);
        itineraryListView.getSelectionModel().select(spot);
    }

    private void startDrag(Dragboard db, String label) {
        ClipboardContent content = new ClipboardContent();
        content.putString(label);
        db.setContent(content);
    }

    private static class ItineraryCell extends ListCell<SurfSpot> {
        private final Label nameLabel = new Label();
        private final HBox layout;

        ItineraryCell() {
            Button deleteButton = new Button("✕");
            deleteButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #e74c3c; " +
                    "-fx-padding: 0 5 0 5; -fx-font-weight: bold; -fx-cursor: hand;");
            deleteButton.setFocusTraversable(false);
            deleteButton.setOnAction(_ -> removeSelf());

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            layout = new HBox(nameLabel, spacer, deleteButton);
            layout.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        }

        @Override
        protected void updateItem(SurfSpot item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                nameLabel.setText(item.getName());
                setGraphic(layout);
            }
        }

        private void removeSelf() {
            SurfSpot item = getItem();
            if (item != null) {
                getListView().getItems().remove(item);
                log.info("Removed spot from itinerary: {}", item.getName());
            }
        }
    }

    @FXML
    private void handleToggleItinerary() {
        if (mainSplitPane.getItems().contains(itineraryPanel)) {
            mainSplitPane.getItems().remove(itineraryPanel);
        } else {
            mainSplitPane.getItems().add(itineraryPanel);
            mainSplitPane.setDividerPositions(0.4, 0.75);
        }
    }

    @FXML
    private void handleAdd() {
        log.info("Navigating to form for new surf spot creation");
        sceneNavigator.navigateToSurfSpotForm(null);
    }

    @FXML
    private void handleEdit() {
        SurfSpot selected = surfSpotTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            sceneNavigator.navigateToSurfSpotForm(selected);
        } else {
            log.warn("Edit clicked with no surf spot selected");
        }
    }

    @FXML
    private void handleDelete() {
        SurfSpot selectedSpot = surfSpotTable.getSelectionModel().getSelectedItem();
        if (selectedSpot == null) {
            log.warn("Delete clicked with no surf spot selected");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                surfSpotService.delete(selectedSpot.getId());
                return null;
            }
        };

        task.setOnSucceeded(_ -> {
            log.info("Deleted surf spot: {}", selectedSpot.getName());
            loadSurfSpots();
        });

        task.setOnFailed(_ -> log.error("Failed to delete surf spot", task.getException()));

        Thread.startVirtualThread(task);
    }

    @FXML
    private void handleExport() {
        if (itineraryListView.getItems().isEmpty()) {
            log.warn("Itinerary is empty, nothing to export.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Itinerary");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));

        File file = fileChooser.showSaveDialog(itineraryListView.getScene().getWindow());
        if (file == null) return;

        Thread.startVirtualThread(() -> {
            try {
                XmlMapper xmlMapper = XmlMapper.builder()
                        .enable(SerializationFeature.INDENT_OUTPUT)
                        .enable(MapperFeature.PROPAGATE_TRANSIENT_MARKER)
                        .build();
                xmlMapper.writer()
                        .withRootName("PlanPutovanja")
                        .writeValue(file, itineraryListView.getItems());
                Platform.runLater(() -> log.info("Itinerary exported to: {}", file.getAbsolutePath()));
            } catch (Exception e) {
                Platform.runLater(() -> log.error("Export failed", e));
            }
        });
    }

    private boolean isValidWaveHeightFilter(TextField field, DoublePredicate condition) {
        String text = field.getText();
        if (text == null || text.isBlank()) {
            field.setStyle("");
            return true;
        }

        try {
            double value = Double.parseDouble(text.replace(',', '.'));
            field.setStyle("");

            if (condition.test(value)) {
                return false;
            }
        } catch (NumberFormatException _) {
            field.setStyle("-fx-border-color: red; -fx-border-width: 2;");
            return false;
        }

        return true;
    }
}