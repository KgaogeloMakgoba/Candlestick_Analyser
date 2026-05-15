package com.ui;

import com.model.PatternResult;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * A single pattern card shown in the results grid.
 * Displays pattern name, description, count, follow-up bar chart,
 * and confirmation rate.
 */
public class PatternCard {

    private final VBox root;

    public PatternCard(PatternResult r, boolean expanded) {
        this.root = buildCard(r, expanded);
    }

    public Node getNode() { return root; }

    //Build 

    private VBox buildCard(PatternResult r, boolean expanded) {
        VBox card = new VBox(10);
        card.getStyleClass().add("pattern-card");
        card.getStyleClass().add("bias-" + r.getBias().toLowerCase());
        if (expanded) card.getStyleClass().add("pattern-card-active");
        if (r.getCount() == 0) card.getStyleClass().add("pattern-card-empty");
        card.setPrefWidth(310);
        card.setPadding(new Insets(16));
        card.setCursor(r.getCount() > 0
            ? javafx.scene.Cursor.HAND
            : javafx.scene.Cursor.DEFAULT);

        card.getChildren().addAll(
            buildCardHeader(r),
            buildDivider(),
            buildDescription(r)
        );

        if (r.getCount() > 0) {
            card.getChildren().addAll(
                buildFollowUpBars(r),
                buildConfirmRate(r)
            );
            if (r.getCount() > 0) {
                Label hint = new Label(expanded ? "▲ occurrences below" : "↓ click to drill down");
                hint.getStyleClass().add("card-hint");
                card.getChildren().add(hint);
            }
        } else {
            Label none = new Label("Not detected in dataset");
            none.getStyleClass().add("card-none");
            card.getChildren().add(none);
        }

        return card;
    }

    //HEADER(name left, count right)

    private HBox buildCardHeader(PatternResult r) {
        String icon = switch (r.getBias()) {
            case "Bullish" -> "▲";
            case "Bearish" -> "▼";
            default        -> "◆";
        };

        Label nameLbl = new Label(icon + "  " + r.getPatternName().toUpperCase());
        nameLbl.getStyleClass().addAll("card-name", "bias-text-" + r.getBias().toLowerCase());
        HBox.setHgrow(nameLbl, Priority.ALWAYS);

        VBox countBox = new VBox();
        countBox.setAlignment(Pos.TOP_RIGHT);
        Label countLbl = new Label(String.valueOf(r.getCount()));
        countLbl.getStyleClass().add("card-count");
        Label hitsLbl = new Label("hits");
        hitsLbl.getStyleClass().add("card-hits");
        countBox.getChildren().addAll(countLbl, hitsLbl);

        HBox header = new HBox(nameLbl, countBox);
        header.setAlignment(Pos.TOP_LEFT);
        return header;
    }

    //Description of the candle

    private Label buildDescription(PatternResult r) {
        Label desc = new Label(r.getDescription());
        desc.getStyleClass().add("card-desc");
        desc.setWrapText(true);
        return desc;
    }

    //Follow-up candles

    private VBox buildFollowUpBars(PatternResult r) {
        long bull    = r.countFollowUp("Bullish");
        long bear    = r.countFollowUp("Bearish");
        long neutral = r.countFollowUp("Neutral");
        int  total   = r.getCount();
        long max     = Math.max(Math.max(bull, bear), Math.max(neutral, 1));

        Label sectionLbl = new Label("NEXT CANDLE");
        sectionLbl.getStyleClass().add("card-section");

        VBox bars = new VBox(6,
            sectionLbl,
            buildBar("Bullish", bull, max, total, "#00e5a0"),
            buildBar("Bearish", bear, max, total, "#ff3d5a"),
            buildBar("Neutral", neutral, max, total, "#94a3b8")
        );
        return bars;
    }

    private HBox buildBar(String label, long val, long max, int total, String color) {
        Label lbl = new Label(label);
        lbl.getStyleClass().add("bar-label");
        lbl.setPrefWidth(52);
        lbl.setStyle("-fx-text-fill: " + color + ";");

        // Track
        StackPane track = new StackPane();
        track.getStyleClass().add("bar-track");
        track.setPrefHeight(5);
        HBox.setHgrow(track, Priority.ALWAYS);

        // Fill
        double pct = max > 0 ? (double) val / max : 0;
        Region fill = new Region();
        fill.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 3;");
        fill.setPrefHeight(5);
        fill.setMaxWidth(Double.MAX_VALUE);

        // Anchor fill to left
        StackPane.setAlignment(fill, Pos.CENTER_LEFT);
        fill.prefWidthProperty().bind(track.widthProperty().multiply(pct));
        track.getChildren().add(fill);

        Label countLbl = new Label(String.valueOf(val));
        countLbl.getStyleClass().add("bar-count");
        countLbl.setPrefWidth(28);
        countLbl.setStyle("-fx-text-fill: #e2e8f0;");

        double p = total > 0 ? val * 100.0 / total : 0;
        Label pctLbl = new Label(String.format("%.0f%%", p));
        pctLbl.getStyleClass().add("bar-pct");
        pctLbl.setPrefWidth(38);

        return new HBox(6, lbl, track, countLbl, pctLbl);
    }

    //Confirmation Rate

    private HBox buildConfirmRate(PatternResult r) {
        if (r.getExpectedFollowUp() == null) return new HBox();

        String color = "Bullish".equals(r.getExpectedFollowUp()) ? "#00e5a0" : "#ff3d5a";

        Label labelLbl = new Label("Expected " + r.getExpectedFollowUp());
        labelLbl.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 11px;");
        HBox.setHgrow(labelLbl, Priority.ALWAYS);

        Label rateLbl = new Label(String.format("%.1f%%", r.confirmationRate()));
        rateLbl.setStyle("-fx-text-fill: " + color
            + "; -fx-font-size: 15px; -fx-font-weight: bold; -fx-font-family: monospace;");

        HBox box = new HBox(labelLbl, rateLbl);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(6, 10, 6, 10));
        box.setStyle("-fx-background-color: " + color + "18;"
                   + "-fx-border-color: " + color + "50;"
                   + "-fx-border-radius: 6; -fx-background-radius: 6;");
        return box;
    }


    private Region buildDivider() {
        Region div = new Region();
        div.getStyleClass().add("card-divider");
        div.setPrefHeight(1);
        div.setMaxWidth(Double.MAX_VALUE);
        return div;
    }
}

