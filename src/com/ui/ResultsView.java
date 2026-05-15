package com.ui;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.model.Candle;
import com.model.PatternMatch;
import com.model.PatternResult;
import com.report.CSVExporter;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Results dashboard — shown after a CSV is successfully loaded and analysed.
 */
public class ResultsView {

    private final String            fileName;
    private final List<Candle>      candles;
    private final List<PatternResult> results;
    private final Stage             stage;
    private final Runnable          onNewFile;

    private final VBox root;
    private FlowPane   cardGrid;
    private VBox       occurrencePanel;
    private String     activeFilter = "All";

    public ResultsView(String fileName, List<Candle> candles,
                       List<PatternResult> results, Stage stage, Runnable onNewFile) {
        this.fileName  = fileName;
        this.candles   = candles;
        this.results   = results;
        this.stage     = stage;
        this.onNewFile = onNewFile;
        this.root      = buildUI();
    }

    public Node getNode() { return root; }

    //Build

    private VBox buildUI() {
        VBox page = new VBox(0);
        page.getStyleClass().add("results-page");

        page.getChildren().addAll(
            buildStatsBar(),
            buildToolbar(),
            buildScrollArea()
        );
        return page;
    }

    //Stats Bar

    private HBox buildStatsBar() {
        HBox bar = new HBox(12);
        bar.getStyleClass().add("stats-bar");
        bar.setPadding(new Insets(16, 24, 16, 24));
        bar.setAlignment(Pos.CENTER_LEFT);

        int totalSignals = results.stream().mapToInt(PatternResult::getCount).sum();
        PatternResult top = results.stream()
            .max(Comparator.comparingInt(PatternResult::getCount)).orElse(null);

        String first = candles.get(0).getDate();
        String last  = candles.get(candles.size() - 1).getDate();

        bar.getChildren().addAll(
            statBox("CANDLES",       String.valueOf(candles.size()), "accent"),
            statBox("TOTAL SIGNALS", String.valueOf(totalSignals),   "text"),
            statBox("TOP PATTERN",   top != null ? top.getPatternName() : "—", "bull"),
            statBox("DATE RANGE",    first + " → " + last,          "muted")
        );

        //Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Action buttons
        Button exportBtn = new Button("⬇  Export CSV");
        exportBtn.getStyleClass().add("action-btn");
        exportBtn.setOnAction(e -> exportCsv());

        Button newFileBtn = new Button("↩  New File");
        newFileBtn.getStyleClass().addAll("action-btn", "action-btn-outline");
        newFileBtn.setOnAction(e -> onNewFile.run());

        bar.getChildren().addAll(spacer, exportBtn, newFileBtn);
        return bar;
    }

    private VBox statBox(String label, String value, String valueStyle) {
        Label lbl = new Label(label);
        lbl.getStyleClass().add("stat-label");

        Label val = new Label(value);
        val.getStyleClass().addAll("stat-value", "stat-" + valueStyle);

        VBox box = new VBox(3, lbl, val);
        box.getStyleClass().add("stat-box");
        box.setPadding(new Insets(10, 16, 10, 16));
        return box;
    }

    //Toolbar (file name + filter tabs)

    private VBox buildToolbar() {
        Label fileLbl = new Label("📁  " + fileName + "  ·  " + candles.size() + " bars");
        fileLbl.getStyleClass().add("file-label");

        // Filter toggle buttons
        ToggleGroup tg = new ToggleGroup();
        HBox filters = new HBox(8);
        filters.setAlignment(Pos.CENTER_LEFT);

        for (String f : new String[]{"All", "Bullish", "Bearish", "Neutral"}) {
            ToggleButton tb = new ToggleButton(f);
            tb.getStyleClass().add("filter-btn");
            tb.setToggleGroup(tg);
            if (f.equals("All")) tb.setSelected(true);
            tb.setOnAction(e -> {
                activeFilter = f;
                refreshCards(null);
            });
            filters.getChildren().add(tb);
        }

        VBox toolbar = new VBox(10, fileLbl, filters);
        toolbar.getStyleClass().add("toolbar");
        toolbar.setPadding(new Insets(12, 24, 12, 24));
        return toolbar;
    }

    //Scrollable main area

    private ScrollPane buildScrollArea() {
        cardGrid       = new FlowPane();
        cardGrid.getStyleClass().add("card-grid");
        cardGrid.setHgap(12);
        cardGrid.setVgap(12);
        cardGrid.setPadding(new Insets(20, 24, 20, 24));
        cardGrid.setPrefWrapLength(1050);

        occurrencePanel = new VBox();
        occurrencePanel.getStyleClass().add("occurrence-panel");
        occurrencePanel.setPadding(new Insets(0, 24, 24, 24));

        VBox content = new VBox(0, cardGrid, occurrencePanel);

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.getStyleClass().add("main-scroll");
        VBox.setVgrow(sp, Priority.ALWAYS);

        refreshCards(null);
        return sp;
    }

    //Pattern Cards

    private void refreshCards(String expandedPattern) {
        cardGrid.getChildren().clear();
        occurrencePanel.getChildren().clear();

        List<PatternResult> filtered = results.stream()
            .filter(r -> switch (activeFilter) {
                case "Bullish" -> "Bullish".equals(r.getBias());
                case "Bearish" -> "Bearish".equals(r.getBias());
                case "Neutral" -> "Neutral".equals(r.getBias());
                default        -> true;
            })
            .sorted(Comparator.comparingInt(PatternResult::getCount).reversed())
            .collect(Collectors.toList());

        for (PatternResult r : filtered) {
            PatternCard card = new PatternCard(r, expandedPattern != null
                && expandedPattern.equals(r.getPatternName()));

            card.getNode().setOnMouseClicked(e -> {
                if (r.getCount() > 0) {
                    if (expandedPattern != null && expandedPattern.equals(r.getPatternName())) {
                        // Collapse
                        refreshCards(null);
                    } else {
                        //Expand — rebuild cards with new expansion, then show table
                        refreshCards(r.getPatternName());
                        showOccurrenceTable(r);
                    }
                }
            });

            cardGrid.getChildren().add(card.getNode());
        }

        //Re-show occurrence table if something is still expanded
        if (expandedPattern != null) {
            results.stream()
                .filter(r -> r.getPatternName().equals(expandedPattern))
                .findFirst()
                .ifPresent(this::showOccurrenceTable);
        }
    }

    //Occurrence Table

    private void showOccurrenceTable(PatternResult r) {
        occurrencePanel.getChildren().clear();

        //Header row
        Label title = new Label(r.getPatternName() + "  —  " + r.getCount() + " occurrences");
        title.getStyleClass().add("occ-title");

        Button closeBtn = new Button("✕");
        closeBtn.getStyleClass().add("close-btn");
        closeBtn.setOnAction(e -> {
            occurrencePanel.getChildren().clear();
            refreshCards(null);
        });

        HBox header = new HBox(title);
        header.setAlignment(Pos.CENTER_LEFT);
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        header.getChildren().addAll(sp, closeBtn);
        header.setPadding(new Insets(14, 16, 10, 16));

        // Table
        TableView<PatternMatch> table = buildOccurrenceTable(r);

        VBox panel = new VBox(0, header, table);
        panel.getStyleClass().add("occ-panel-box");

        occurrencePanel.getChildren().add(panel);
    }

    private TableView<PatternMatch> buildOccurrenceTable(PatternResult r) {
        TableView<PatternMatch> table = new TableView<>();
        table.getStyleClass().add("occ-table");
        table.setMaxHeight(260);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        //column
        TableColumn<PatternMatch, Number> idxCol = new TableColumn<>("#");
        idxCol.setCellValueFactory(cd ->
            new javafx.beans.property.SimpleIntegerProperty(
                r.getMatch().indexOf(cd.getValue()) + 1));
        idxCol.setPrefWidth(50);
        idxCol.setStyle("-fx-alignment: CENTER;");

        //Date column
        TableColumn<PatternMatch, String> dateCol = new TableColumn<>("Date / Bar");
        dateCol.setCellValueFactory(cd ->
            new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getCandle().getDate()));
        dateCol.setPrefWidth(160);

        //Bar index column
        TableColumn<PatternMatch, Number> barCol = new TableColumn<>("Bar Index");
        barCol.setCellValueFactory(cd ->
            new javafx.beans.property.SimpleIntegerProperty(cd.getValue().getIndex()));
        barCol.setPrefWidth(90);
        barCol.setStyle("-fx-alignment: CENTER;");

        //Close price column
        TableColumn<PatternMatch, String> closeCol = new TableColumn<>("Close");
        closeCol.setCellValueFactory(cd ->
            new javafx.beans.property.SimpleStringProperty(
                String.format("%.4f", cd.getValue().getCandle().getClose())));
        closeCol.setPrefWidth(100);
        closeCol.setStyle("-fx-alignment: CENTER-RIGHT;");

        //Follow-up column (colour coded)
        TableColumn<PatternMatch, String> fuCol = new TableColumn<>("Follow-Up");
        fuCol.setCellValueFactory(cd ->
            new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getFollowUpDirection()));
        fuCol.setPrefWidth(120);
        fuCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle(switch (item) {
                        case "Bullish" -> "-fx-text-fill: #00e5a0; -fx-font-weight: bold; -fx-alignment: CENTER;";
                        case "Bearish" -> "-fx-text-fill: #ff3d5a; -fx-font-weight: bold; -fx-alignment: CENTER;";
                        default        -> "-fx-text-fill: #94a3b8; -fx-alignment: CENTER;";
                    });
                }
            }
        });

        //Confirmed column
        TableColumn<PatternMatch, String> confCol = new TableColumn<>("Confirmed");
        confCol.setCellValueFactory(cd -> {
            String fu       = cd.getValue().getFollowUpDirection();
            String expected = r.getExpectedFollowUp();
            String val = expected == null ? "—"
                : expected.equalsIgnoreCase(fu) ? "✔" : "✘";
            return new javafx.beans.property.SimpleStringProperty(val);
        });
        confCol.setPrefWidth(90);
        confCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle(switch (item) {
                    case "✔" -> "-fx-text-fill: #00e5a0; -fx-font-weight: bold; -fx-alignment: CENTER;";
                    case "✘" -> "-fx-text-fill: #ff3d5a; -fx-font-weight: bold; -fx-alignment: CENTER;";
                    default  -> "-fx-text-fill: #64748b; -fx-alignment: CENTER;";
                });
            }
        });

        table.getColumns().addAll(idxCol, dateCol, barCol, closeCol, fuCol, confCol);
        table.getItems().addAll(r.getMatch());
        return table;
    }

    //Export

    private void exportCsv() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export Occurrence Data");
        chooser.setInitialFileName("candlestick_results.csv");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = chooser.showSaveDialog(stage);
        if (file == null) return;

        try {
            new CSVExporter().export(results, file.getAbsolutePath());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export Complete");
            alert.setHeaderText(null);
            alert.setContentText("Exported to:\n" + file.getAbsolutePath());
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Export Failed");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
