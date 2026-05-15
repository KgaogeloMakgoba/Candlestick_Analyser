package com.analysis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.model.Candle;

public class CSVParser {
	
	public List<Candle> parse(String filepath) throws IOException{
		List<String> lines = Files.readAllLines(Path.of(filepath));
		if(lines.isEmpty()) throw new IllegalArgumentException("CSV file is empty");
		
		// Parse header (case-insensitive, strip whitespace)
        String[] header = lines.get(0).toLowerCase().replaceAll("\\s","").split(",");
        int openIndex = indexOf(header, "open");
        int highIndex = indexOf(header, "high");
        int lowIndex = indexOf(header, "low");
        int closeIndex = indexOf(header, "close");
        int dateIndex = indexOfAny(header, "date", "time", "datetime", "timestamp");

        if (openIndex < 0 || highIndex < 0 || lowIndex < 0 || closeIndex < 0) {
            throw new IllegalArgumentException(
                "CSV must contain columns: open, high, low, close. Found: " + Arrays.toString(header));
        }

        List<Candle> candles = new ArrayList<>();
        int skipped = 0;

        for (int row = 1; row < lines.size(); row++) {
            String line = lines.get(row).trim();
            if (line.isEmpty()) continue;
            String[] cols = line.split(",", -1);

            try {
                double o = Double.parseDouble(cols[openIndex].trim());
                double h = Double.parseDouble(cols[highIndex].trim());
                double l = Double.parseDouble(cols[lowIndex].trim());
                double cl = Double.parseDouble(cols[closeIndex].trim());
                String date = (dateIndex >= 0 && dateIndex < cols.length)
                        ? cols[dateIndex].trim() : "Row-" + row;
                candles.add(new Candle(date, o, h, l, cl));
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                skipped++;
            }
        }

        if (skipped > 0) {
            System.out.printf("  [Parser] Skipped %d malformed row(s).%n", skipped);
        }
        if (candles.size() < 3) {
            throw new IllegalArgumentException(
                "Need at least 3 valid candles. Only found: " + candles.size());
        }
        return candles;
    }

    private int indexOf(String[] arr, String target) {
        for (int i = 0; i < arr.length; i++) if (arr[i].contains(target)) return i;
        return -1;
    }

    private int indexOfAny(String[] arr, String... targets) {
        for (String t : targets) {
            int index = indexOf(arr, t);
            if (index >= 0) return index;
        }
        return -1;
		
	}

}
