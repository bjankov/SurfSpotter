package hr.algebra.surfspot.controller.surfspot;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.controller.BaseController;
import hr.algebra.surfspot.model.Instructor;
import hr.algebra.surfspot.model.SurfSpot;
import hr.algebra.surfspot.service.SurfSpotService;
import hr.algebra.surfspot.util.ImageStorage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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

    private SurfSpot draggedSpot;

    private final SurfSpotService surfSpotService;
    private final SceneNavigator sceneNavigator;

    public SurfSpotListController(SurfSpotService surfSpotService, SceneNavigator sceneNavigator) {
        this.surfSpotService = surfSpotService;
        this.sceneNavigator = sceneNavigator;
    }

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        difficultyColumn.setCellValueFactory(new PropertyValueFactory<>("difficultyDisplayValue"));

        loadSurfSpots();
        surfSpotTable.getSelectionModel().selectedItemProperty().addListener(
                (_, _, newVal) -> populateDetails(newVal)
        );

        mainSplitPane.getItems().remove(itineraryPanel);

        setupDragAndDrop();
        clearDetails();
    }

    private void loadSurfSpots() {
        try {
            List<SurfSpot> spots = surfSpotService.findAll();
            Platform.runLater(() -> {
                surfSpotTable.setItems(FXCollections.observableArrayList(spots));
                log.info("Loaded {} surf spots into table", spots.size());
            });
        } catch (Exception e) {
            log.error("Failed to load surf spots", e);
        }
    }

    private void populateDetails(SurfSpot spot) {
        if (spot == null) {
            clearDetails();
            return;
        }

        String coastName = (spot.getLocation() != null && spot.getLocation().getCoast() != null)
                ? spot.getLocation().getCoast().getName() : "?";

        locationLabel.setText(String.format("%s, %s", coastName, spot.getCountryName()));
        coordinatesLabel.setText(spot.getLocation().getCoordinates().toString());
        waveDetailsLabel.setText(spot.getWaveDetails().toString());
        windDetailsLabel.setText(spot.getWindDirectionDegrees() != null ? spot.getFormattedWindDetails() : "Nije unesen");
        seasonLabel.setText(spot.getBestSeason() != null && !spot.getBestSeason().isEmpty()
                ? spot.getFormattedBestSeason() : "Nije određena");

        instructorListView.setItems(spot.getInstructors() != null
                ? FXCollections.observableArrayList(spot.getInstructors())
                : FXCollections.emptyObservableList());

        loadSpotImage(spot.getImagePath());
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
                spotImageView.setImage(new Image(fullPath.toUri().toString()));
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
        SurfSpot selected = surfSpotTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            log.warn("Delete clicked with no surf spot selected");
            return;
        }
        try {
            surfSpotService.delete(selected.getId());
            Platform.runLater(() -> {
                loadSurfSpots();
                log.info("Deleted surf spot: {}", selected.getName());
            });
        } catch (Exception e) {
            log.error("Failed to delete surf spot", e);
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

        try {
            XmlMapper xmlMapper = XmlMapper.builder()
                    .enable(SerializationFeature.INDENT_OUTPUT)
                    .enable(MapperFeature.PROPAGATE_TRANSIENT_MARKER)
                    .build();
            xmlMapper.writer()
                    .withRootName("PlanPutovanja")
                    .writeValue(file, itineraryListView.getItems());
            log.info("Itinerary exported to: {}", file.getAbsolutePath());
        } catch (Exception e) {
            log.error("Export failed", e);
        }
    }
}