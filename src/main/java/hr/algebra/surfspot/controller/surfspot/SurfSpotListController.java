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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.DoublePredicate;

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

    private final ObservableList<SurfSpot> spotObservableList = FXCollections.observableArrayList();
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

        filteredSpots = new FilteredList<>(spotObservableList, _ -> true);
        SortedList<SurfSpot> spotSortedList = new SortedList<>(filteredSpots);
        spotSortedList.comparatorProperty().bind(surfSpotTable.comparatorProperty());
        surfSpotTable.setItems(spotSortedList);

        setupFilterControls();
        addFilterListeners();
        loadInitialData();

        surfSpotTable.getSelectionModel().selectedItemProperty().addListener(
                (_, _, newVal) -> populateDetails(newVal)
        );

        mainSplitPane.getItems().remove(itineraryPanel);
        setupDragAndDrop();
        clearDetails();
    }

    private void setupFilterControls() {
        difficultyComboBox.getItems().addAll(DifficultyLevel.values());
        difficultyComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(DifficultyLevel level) {
                return level == null ? "" : level.getDisplayValue();
            }
            @Override
            public DifficultyLevel fromString(String string) { return null; }
        });

        waveTypeComboBox.getItems().addAll(WaveType.values());
        waveTypeComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(WaveType type) {
                return type == null ? "" : type.getDisplayValue();
            }
            @Override
            public WaveType fromString(String string) { return null; }
        });

        coastComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Coast coast) {
                return coast == null ? "" : coast.getName();
            }
            @Override
            public Coast fromString(String string) { return null; }
        });

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
    }

    private void addFilterListeners() {
        spotSearchField.textProperty().addListener((_, _, _) -> updateFilters());
        difficultyComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<DifficultyLevel>) _ -> updateFilters());
        waveTypeComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<WaveType>) _ -> updateFilters());
        minWaveHeightField.textProperty().addListener((_, _, _) -> updateFilters());
        maxWaveHeightField.textProperty().addListener((_, _, _) -> updateFilters());
        seasonComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<Month>) _ -> updateFilters());
        coastComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<Coast>) _ -> updateFilters());
        countryComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<Country>) _ -> updateFilters());
    }

    private void updateFilters() {
        String searchText = spotSearchField.getText() == null ? "" : spotSearchField.getText().toLowerCase().trim();
        List<DifficultyLevel> selectedDifficulties = difficultyComboBox.getCheckModel().getCheckedItems();
        List<WaveType> selectedWaveTypes = waveTypeComboBox.getCheckModel().getCheckedItems();
        List<Month> selectedMonths = seasonComboBox.getCheckModel().getCheckedItems();
        List<Coast> selectedCoasts = coastComboBox.getCheckModel().getCheckedItems();
        List<Country> selectedCountries = countryComboBox.getCheckModel().getCheckedItems();

        filteredSpots.setPredicate(spot ->
                matchesSearch(spot, searchText) &&
                        matchesDifficulty(spot, selectedDifficulties) &&
                        matchesWaveType(spot, selectedWaveTypes) &&
                        matchesWaveHeight(spot) &&
                        matchesSeason(spot, selectedMonths) &&
                        matchesCoast(spot, selectedCoasts) &&
                        matchesCountry(spot, selectedCountries)
        );
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

    private void loadInitialData() {
        Thread.startVirtualThread(() -> {
            try {
                List<SurfSpot> surfSpots = surfSpotService.findAll();
                List<Coast> coasts = coastService.findAll();
                List<Country> countries = countryService.findAll();
                Platform.runLater(() -> {
                    spotObservableList.setAll(surfSpots);
                    coastComboBox.getItems().setAll(coasts);
                    countryComboBox.getItems().setAll(countries);
                    log.info("Loaded {} surf spots, {} coasts, {} countries", surfSpots.size(), coasts.size(), countries.size());
                });
            } catch (Exception e) {
                log.error("Failed to load initial data", e);
                Platform.runLater(() -> showError("Došlo je do pogreške prilikom učitavanja podataka."));
            }
        });
    }

    private void populateDetails(SurfSpot spot) {
        if (spot == null) {
            clearDetails();
            return;
        }

        cancelPendingDetailsTask();

        pendingDetailThread = Thread.startVirtualThread(() -> loadDetailsAsync(spot));
    }

    private void cancelPendingDetailsTask() {
        if (pendingDetailThread != null && pendingDetailThread.isAlive()) {
            pendingDetailThread.interrupt();
        }
    }

    private void loadDetailsAsync(SurfSpot spot) {
        try {
            SurfSpot loaded = surfSpotService.findById(spot.getId())
                                             .orElseThrow(() -> new ResourceNotFoundException("Nije pronađeno..."));

            displayLoadedDetails(loaded);

        } catch (Exception e) {
            handleLoadError(e, spot.getName());
        }
    }

    private void displayLoadedDetails(SurfSpot loaded) {
        String locationText = formatLocationText(loaded);
        String coordinatesText = getCoordinatesText(loaded);
        String waveText = getWaveText(loaded);
        String windText = getWindText(loaded);
        String seasonText = getSeasonText(loaded);
        ObservableList<Instructor> instructors = getInstructorsList(loaded);

        Thread callingThread = Thread.currentThread();

        Platform.runLater(() -> {
            if (callingThread == pendingDetailThread) {
                applyDetailsToUi(locationText, coordinatesText, waveText, windText, seasonText, instructors, loaded.getImagePath());
            }
        });
    }

    private void applyDetailsToUi(String location, String coordinates, String wave, String wind, String season, ObservableList<Instructor> instructors, String imagePath) {
        locationLabel.setText(location);
        coordinatesLabel.setText(coordinates);
        waveDetailsLabel.setText(wave);
        windDetailsLabel.setText(wind);
        seasonLabel.setText(season);
        instructorListView.setItems(instructors);
        loadSpotImage(imagePath);
    }

    private void handleLoadError(Exception e, String spotName) {
        if (e instanceof InterruptedException || Thread.currentThread().isInterrupted()) {
            log.debug("Detail load cancelled for: {}", spotName);
        } else {
            Platform.runLater(() -> log.error("Failed to load spot details", e));
        }
    }

    private String formatLocationText(SurfSpot loaded) {
        String coastName = (loaded.getLocation() != null && loaded.getLocation().getCoast() != null)
                ? loaded.getLocation().getCoast().getName() : "?";
        return String.format("%s, %s", coastName, loaded.getCountryName());
    }

    private String getCoordinatesText(SurfSpot loaded) {
        return (loaded.getLocation() != null && loaded.getLocation().getCoordinates() != null)
                ? loaded.getLocation().getCoordinates().toString() : "-";
    }

    private String getWaveText(SurfSpot loaded) {
        return loaded.getWaveDetails() != null ? loaded.getWaveDetails().toString() : "-";
    }

    private String getWindText(SurfSpot loaded) {
        return loaded.getWindDirectionDegrees() != null ? loaded.getFormattedWindDetails() : "Nije unesen";
    }

    private String getSeasonText(SurfSpot loaded) {
        return (loaded.getBestSeason() != null && !loaded.getBestSeason().isEmpty())
                ? loaded.getFormattedBestSeason() : "Nije određena";
    }

    private ObservableList<Instructor> getInstructorsList(SurfSpot loaded) {
        return loaded.getInstructors() != null
                ? FXCollections.observableArrayList(loaded.getInstructors())
                : FXCollections.emptyObservableList();
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

        if (showConfirmation("Jeste li sigurni da želite izbrisati odabrano mjesto za surfanje?")) {
            Thread.startVirtualThread(() -> {
                try {
                    surfSpotService.delete(selectedSpot.getId());
                    log.info("Deleted surf spot: {}", selectedSpot.getName());
                    Platform.runLater(() -> spotObservableList.remove(selectedSpot));
                } catch (Exception e) {
                    log.error("Failed to delete surf spot", e);
                    Platform.runLater(() -> showError("Došlo je do pogreške prilikom brisanja mjesta za surfanje."));
                }
            });
        }
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
                        .writeValue(file, new Itinerary(new ArrayList<>(itineraryListView.getItems())));
                log.info("Itinerary exported to: {}", file.getAbsolutePath());
            } catch (Exception e) {
                log.error("Export failed", e);
                Platform.runLater(() -> showSuccess("Plan putovanja uspjesno izvezen."));
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

    private boolean matchesSearch(SurfSpot spot, String searchText) {
        if (searchText.isEmpty()) return true;
        return spot.getName() != null && spot.getName().toLowerCase().contains(searchText);
    }

    private boolean matchesDifficulty(SurfSpot spot, List<DifficultyLevel> selectedDifficulties) {
        return selectedDifficulties.isEmpty() || selectedDifficulties.contains(spot.getDifficulty());
    }

    private boolean matchesWaveType(SurfSpot spot, List<WaveType> selectedWaveTypes) {
        return selectedWaveTypes.isEmpty() || selectedWaveTypes.contains(spot.getWaveType());
    }

    private boolean matchesWaveHeight(SurfSpot spot) {
        Double avgHeight = spot.getWaveHeight();
        if (avgHeight == null) return true;

        if (!isValidWaveHeightFilter(minWaveHeightField, min -> avgHeight < min)) return false;
        return isValidWaveHeightFilter(maxWaveHeightField, max -> avgHeight > max);
    }

    private boolean matchesSeason(SurfSpot spot, List<Month> selectedMonths) {
        return selectedMonths.isEmpty() || !Collections.disjoint(selectedMonths, spot.getBestSeason());
    }

    private boolean matchesCoast(SurfSpot spot, List<Coast> selectedCoasts) {
        return selectedCoasts.isEmpty() || selectedCoasts.contains(spot.getCoast());
    }

    private boolean matchesCountry(SurfSpot spot, List<Country> selectedCountries) {
        if (selectedCountries.isEmpty()) return true;

        Country spotCountry = (spot.getCoast() != null) ? spot.getCoast().getCountry() : null;
        return selectedCountries.contains(spotCountry);
    }
}