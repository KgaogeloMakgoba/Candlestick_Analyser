package com.report;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.model.Candle;
import com.model.PatternMatch;
import com.model.PatternResult;

/**
 * Exports full occurrence data to a CSV file for further analysis.
 */
public class CSVExporter {

    public String export(List<PatternResult> results, String outputPath) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(Path.of(outputPath))) {
            w.write("Pattern,Bias,BarIndex,Date,Open,High,Low,Close,FollowUp,ExpectedFollowUp,Confirmed");
            w.newLine();

            for (PatternResult r : results) {
                for (PatternMatch m : r.getMatch()) {
                    Candle c = m.getCandle();
                    boolean confirmed = r.getExpectedFollowUp() != null
                            && r.getExpectedFollowUp().equalsIgnoreCase(m.getFollowUpDirection());
                    w.write(String.join(",",
                        quote(r.getPatternName()),
                        r.getBias(),
                        String.valueOf(m.getIndex()),
                        quote(c.getDate()),
                        fmt(c.getOpen()), fmt(c.getHigh()), fmt(c.getLow()), fmt(c.getClose()),
                        m.getFollowUpDirection(),
                        r.getExpectedFollowUp() != null ? r.getExpectedFollowUp() : "N/A",
                        r.getExpectedFollowUp() != null ? String.valueOf(confirmed) : "N/A"
                    ));
                    w.newLine();
                }
            }
        }
        return outputPath;
    }

    private String quote(String s) { return "\"" + s.replace("\"", "\"\"") + "\""; }
    private String fmt(double d)   { return String.format("%.4f", d); }
}

