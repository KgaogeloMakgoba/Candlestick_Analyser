package com.ui;


import java.io.File;
import java.util.List;

import com.analysis.CSVParser;
import com.analysis.PatternAnalyzer;
import com.model.Candle;
import com.model.PatternResult;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Root view — owns the stage reference and switches between
 * the upload screen and the results dashboard.
 */
public class MainView {

    private final BorderPane root;
    private final Stage      stage;

    public MainView(Stage stage) {
        this.stage = stage;
        this.root  = new BorderPane();
        root.getStyleClass().add("root-pane");

        showHeader();
        showUploadScreen();
    }

    public BorderPane getRoot() { return root; }

    //HEADER

    private void showHeader() {
        HBox header = new HBox();
        header.getStyleClass().add("app-header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 24, 16, 24));

        Label icon  = new Label("◈");
        icon.getStyleClass().add("header-icon");

        Label title = new Label("CANDLESTICK");
        title.getStyleClass().add("header-title");

        Label sub   = new Label(" ANALYST");
        sub.getStyleClass().add("header-subtitle");

        header.getChildren().addAll(icon, title, sub);
        root.setTop(header);
    }

    //Uploading Screen

    public void showUploadScreen() {
        UploadView uploadView = new UploadView(
            this::handleFilePicked,
            this::handleFilePicked
        );
        root.setCenter(uploadView.getNode());
    }

    //File handling

    //Called from UploadView when the you pick a file or drag one in. 
    public void handleFilePicked(File file) {
        try {
            List<Candle>        candles = new CSVParser().parse(file.getAbsolutePath());
            List<PatternResult> results = new PatternAnalyzer().analyzer(candles);
            showResults(file.getName(), candles, results);
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    //Results on Dashboard

    private void showResults(String fileName, List<Candle> candles, List<PatternResult> results) {
        ResultsView resultsView = new ResultsView(
            fileName, candles, results, stage,
            () -> showUploadScreen()          //"New File" callback
        );
        root.setCenter(resultsView.getNode());
    }

    //Error Banner

    private void showError(String message) {
        Label err = new Label("⚠  " + message);
        err.getStyleClass().add("error-label");
        err.setWrapText(true);
        err.setMaxWidth(600);

        VBox box = new VBox(err);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40));
        root.setCenter(box);
        //Go back to upload after showing error
        showUploadScreen();
    }
}

