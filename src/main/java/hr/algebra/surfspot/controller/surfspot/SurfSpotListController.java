package hr.algebra.surfspot.controller.surfspot;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import hr.algebra.surfspot.context.SceneNavigator;
import hr.algebra.surfspot.model.Instructor;
import hr.algebra.surfspot.model.SurfSpot;
import hr.algebra.surfspot.service.SurfSpotService;
import hr.algebra.surfspot.util.ImageStorage;
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

public class SurfSpotListController {
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
                (observable, oldSelection, newSelection) -> populateDetails(newSelection)
        );

        if (mainSplitPane != null && itineraryPanel != null) {
            mainSplitPane.getItems().remove(itineraryPanel);
        }

        setupDragAndDrop();
        clearDetails();
    }

    private void loadSurfSpots() {
        try {
            List<SurfSpot> data = surfSpotService.findAll();
            ObservableList<SurfSpot> observableData = FXCollections.observableArrayList(data);
            surfSpotTable.setItems(observableData);
            log.info("Loaded {} surf spots into table", data.size());
        } catch (Exception e) {
            log.error("Failed to load surf spots from service", e);
        }
    }

    private void populateDetails(SurfSpot spot) {
        if (locationLabel == null || coordinatesLabel == null || waveDetailsLabel == null || windDetailsLabel == null || seasonLabel == null) {
            return;
        }

        if (spot == null) {
            clearDetails();
            return;
        }

        String coastName = (spot.getLocation() != null && spot.getLocation().getCoast() != null)
                ? spot.getLocation().getCoast().getName() : "?";
        locationLabel.setText(String.format("%s, %s", coastName, spot.getCountryName()));

        coordinatesLabel.setText(spot.getLocation().getCoordinates().toString());
        waveDetailsLabel.setText(spot.getWaveDetails().toString());
        seasonLabel.setText(spot.getBestSeason().toString());

        if (spot.getWindDirectionDegrees() != null) {
            windDetailsLabel.setText(spot.getFormattedWindDetails());
        } else {
            windDetailsLabel.setText("Nije unesen");
        }

        if (spot.getInstructors() != null && instructorListView != null) {
            instructorListView.setItems(FXCollections.observableArrayList(spot.getInstructors()));
        } else if (instructorListView != null) {
            instructorListView.getItems().clear();
        }

        if (spot.getBestSeason() != null && !spot.getBestSeason().isEmpty()) {
            seasonLabel.setText(spot.getFormattedBestSeason());
        } else {
            seasonLabel.setText("Nije određena");
        }

        if (spot.getImagePath() != null && !spot.getImagePath().isBlank() && spotImageView != null) {
            Path imagePath = ImageStorage.getStorageDir().resolve(spot.getImagePath());

            if (Files.exists(imagePath)) {
                spotImageView.setImage(new Image(imagePath.toUri().toString()));
            } else {
                loadDefaultImage();
            }
        } else {
            loadDefaultImage();
        }
    }

    private void setupDragAndDrop() {
        if (surfSpotTable == null || itineraryListView == null) {
            return;
        }

        surfSpotTable.setOnDragDetected(event -> {
            SurfSpot selected = surfSpotTable.getSelectionModel().getSelectedItem();
            if (selected != null && selected.getId() != null) {
                Dragboard db = surfSpotTable.startDragAndDrop(TransferMode.COPY);
                ClipboardContent content = new ClipboardContent();
                content.putString("ADD:" + selected.getId());
                db.setContent(content);
                event.consume();
            }
        });

        itineraryListView.setOnDragOver(event -> {
            if (event.getGestureSource() == surfSpotTable && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        itineraryListView.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString() && db.getString().startsWith("ADD:")) {
                success = processAddDrop(db.getString(), itineraryListView.getItems().size());
            }
            event.setDropCompleted(success);
            event.consume();
        });

        itineraryListView.setCellFactory(lv -> {
            ListCell<SurfSpot> cell = new ListCell<>() {
                private final HBox graphicBox = new HBox();
                private final Label nameLabel = new Label();
                private final Button deleteButton = new Button("✕");
                private final Region spacer = new Region();

                {
                    deleteButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #e74c3c; -fx-padding: 0 5 0 5; -fx-font-weight: bold; -fx-cursor: hand;");
                    deleteButton.setFocusTraversable(false);

                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    graphicBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    graphicBox.getChildren().addAll(nameLabel, spacer, deleteButton);

                    deleteButton.setOnAction(event -> {
                        SurfSpot item = getItem();
                        if (item != null) {
                            getListView().getItems().remove(item);
                            log.info("Removed spot from itinerary: {}", item.getName());
                        }
                    });
                }

                @Override
                protected void updateItem(SurfSpot item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        nameLabel.setText(item.getName());
                        setText(null);
                        setGraphic(graphicBox);
                    }
                }
            };

            cell.setOnDragDetected(event -> {
                if (cell.isEmpty()) return;
                Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString("MOVE:" + cell.getIndex());
                db.setContent(content);
                event.consume();
            });

            cell.setOnDragOver(event -> {
                if (event.getDragboard().hasString()) {
                    String data = event.getDragboard().getString();
                    if (data.startsWith("MOVE:") && event.getGestureSource() == cell.getListView()) {
                        event.acceptTransferModes(TransferMode.MOVE);
                    } else if (data.startsWith("ADD:") && event.getGestureSource() == surfSpotTable) {
                        event.acceptTransferModes(TransferMode.COPY);
                    }
                }
                event.consume();
            });

            cell.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;

                if (db.hasString()) {
                    String data = db.getString();
                    if (data.startsWith("MOVE:")) {
                        int sourceIndex = Integer.parseInt(data.replace("MOVE:", ""));
                        int targetIndex = cell.isEmpty() ? itineraryListView.getItems().size() : cell.getIndex();

                        SurfSpot spot = itineraryListView.getItems().remove(sourceIndex);
                        if (sourceIndex < targetIndex) {
                            targetIndex--;
                        }
                        itineraryListView.getItems().add(targetIndex, spot);
                        itineraryListView.getSelectionModel().select(spot);
                        success = true;
                    } else if (data.startsWith("ADD:")) {
                        int targetIndex = cell.isEmpty() ? itineraryListView.getItems().size() : cell.getIndex();
                        success = processAddDrop(data, targetIndex);
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            });

            return cell;
        });
    }

    private boolean processAddDrop(String dragData, int targetIndex) {
        try {
            Long id = Long.parseLong(dragData.replace("ADD:", ""));
            SurfSpot spot = surfSpotTable.getItems().stream()
                    .filter(s -> s.getId().equals(id))
                    .findFirst()
                    .orElse(null);

            if (spot != null) {
                boolean alreadyExists = itineraryListView.getItems().stream()
                        .anyMatch(s -> s.getId().equals(spot.getId()));

                if (!alreadyExists) {
                    itineraryListView.getItems().add(targetIndex, spot);
                    log.info("Added to itinerary: {}", spot.getName());
                    return true;
                }
            }
        } catch (NumberFormatException e) {
            log.error("Error parsing ID for Drag&Drop", e);
        }
        return false;
    }

    @FXML
    private void handleToggleItinerary() {
        if (mainSplitPane == null || itineraryPanel == null) return;

        if (mainSplitPane.getItems().contains(itineraryPanel)) {
            mainSplitPane.getItems().remove(itineraryPanel);
        } else {
            mainSplitPane.getItems().add(itineraryPanel);
            mainSplitPane.setDividerPositions(0.4, 0.75);
        }
    }

    private void clearDetails() {
        if (locationLabel == null || coordinatesLabel == null || waveDetailsLabel == null || windDetailsLabel == null || seasonLabel == null) {
            return;
        }

        locationLabel.setText("-");
        coordinatesLabel.setText("-");
        waveDetailsLabel.setText("-");
        windDetailsLabel.setText("-");
        seasonLabel.setText("-");

        if (instructorListView != null) {
            instructorListView.getItems().clear();
        }
        if (spotImageView != null) {
            spotImageView.setImage(null);
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
            log.info("Editing surf spot: {}", selected.getId());
            sceneNavigator.navigateToSurfSpotForm(selected);
        } else {
            log.warn("Edit clicked but no surf spot selected");
        }
    }

    @FXML
    private void handleDelete() {
        SurfSpot selected = surfSpotTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            log.warn("Delete clicked but no surf spot selected");
            return;
        }

        try {
            surfSpotService.delete(selected.getId());
            loadSurfSpots();
            log.info("Surf spot {} successfully deleted", selected.getName());
        } catch (Exception e) {
            log.error("Failed to delete surf spot", e);
        }
    }

    private void loadDefaultImage() {
        if (spotImageView == null) return;
        java.net.URL defaultUrl = getClass().getResource("/images/default.jpg");
        if (defaultUrl != null) {
            spotImageView.setImage(new Image(defaultUrl.toExternalForm()));
        } else {
            spotImageView.setImage(null);
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

        if (file != null) {
            try {
                XmlMapper xmlMapper = XmlMapper.builder()
                        .enable(SerializationFeature.INDENT_OUTPUT)
                        .enable(MapperFeature.PROPAGATE_TRANSIENT_MARKER)
                        .build();

                xmlMapper.writer()
                        .withRootName("PlanPutovanja")
                        .writeValue(file, itineraryListView.getItems());

                log.info("Itinerary successfully exported to: {}", file.getAbsolutePath());
            } catch (Exception e) {
                log.error("Export failed", e);
            }
        }
    }
}