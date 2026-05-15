package com.ui;

import java.io.File;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * The initial upload screen — drag-and-drop zone + browse button.
 */
public class UploadView {

    private final VBox            root;
    private final Consumer<File>  onFilePicked;
    private final Consumer<File>  onDragDrop;

    private Label errorLabel;

    public UploadView(Consumer<File> onFilePicked, Consumer<File> onDragDrop) {
        this.onFilePicked = onFilePicked;
        this.onDragDrop   = onDragDrop;
        this.root         = buildUI();
    }

    public Node getNode() { return root; }

    //Build UI

    private VBox buildUI() {
        //Drop zone
        VBox dropZone = new VBox(16);
        dropZone.getStyleClass().add("drop-zone");
        dropZone.setAlignment(Pos.CENTER);
        dropZone.setMaxWidth(520);
        dropZone.setMaxHeight(300);

        Label iconLbl = new Label("📈");
        iconLbl.getStyleClass().add("upload-icon");

        Label titleLbl = new Label("Drop your OHLC CSV here");
        titleLbl.getStyleClass().add("upload-title");

        Label hintLbl = new Label("Needs columns: open, high, low, close  ·  date is optional");
        hintLbl.getStyleClass().add("upload-hint");

        Button browseBtn = new Button("Browse Files");
        browseBtn.getStyleClass().add("browse-btn");
        browseBtn.setOnAction(e -> openFileChooser());

        errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setWrapText(true);
        errorLabel.setVisible(false);

        dropZone.getChildren().addAll(iconLbl, titleLbl, hintLbl, browseBtn, errorLabel);

        // Drag events
        dropZone.setOnDragOver(this::handleDragOver);
        dropZone.setOnDragEntered(e -> dropZone.getStyleClass().add("drop-zone-active"));
        dropZone.setOnDragExited(e  -> dropZone.getStyleClass().remove("drop-zone-active"));
        dropZone.setOnDragDropped(e -> handleDrop(e, dropZone));

        // Outer wrapper
        VBox wrapper = new VBox(dropZone);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPadding(new Insets(60, 40, 60, 40));
        VBox.setVgrow(wrapper, javafx.scene.layout.Priority.ALWAYS);

        return wrapper;
    }

    //Drag & Drop

    private void handleDragOver(DragEvent e) {
        if (e.getDragboard().hasFiles()) {
            e.acceptTransferModes(TransferMode.COPY);
        }
        e.consume();
    }

    private void handleDrop(DragEvent e, VBox dropZone) {
        Dragboard db = e.getDragboard();
        if (db.hasFiles()) {
            File file = db.getFiles().get(0);
            dropZone.getStyleClass().remove("drop-zone-active");
            if (!file.getName().endsWith(".csv")) {
                showError("Please drop a .csv file.");
            } else {
                onDragDrop.accept(file);
            }
        }
        e.setDropCompleted(true);
        e.consume();
    }

    //File Chooser

    private void openFileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select OHLC CSV File");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        // Get stage from scene
        Stage stage = (Stage) root.getScene().getWindow();
        File file = chooser.showOpenDialog(stage);
        if (file != null) onFilePicked.accept(file);
    }

    //Error
    
    private void showError(String msg) {
        errorLabel.setText("⚠  " + msg);
        errorLabel.setVisible(true);
    }
}

