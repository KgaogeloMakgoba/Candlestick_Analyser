Candlestick Pattern Analyser is a desktop application built in Java and JavaFX that reads OHLC (Open, High, Low, Close) candlestick data from a CSV file, detects classic candlestick patterns, and tells you how many times each pattern appeared and how often the next candle confirmed the expected move.

What it does

Loads any OHLC CSV file via drag-and-drop or a file browser dialog
Scans every candle in the dataset and detects 3 patterns automatically
For each pattern it shows:
How many times it appeared
What the next candle did (Bullish, Bearish, or Neutral) and in what proportion
A confirmation rate of how often the expected follow-through actually happened

Lets you filter patterns by bias (Bullish / Bearish / Neutral)
Click any pattern card to drill down into a full table of every individual occurrence with its date, bar index, close price, and follow-up direction
Export all occurrence data to a CSV file for further analysis in Excel or any other tool

Patterns detected
The patterns detected are my personal trading patterns
-Swing low/high
-Bullish/Bearish Engulfing
-Long Upper/Lower Shadow
The application is designed to allow the addition ofother patterns
-All other patterns can be added easily by adding the Pattern in the PatternRegistry,implementing the CandlePattern interface
-Then add the Pattern in the all() method.

Project Structure

Core
-Main.java -Application entry point

Model
-Candle.java -Represents OHLC candlestick data
-PatternMatch.java -Stores one detected pattern occurrence
-PatternResult.java -Stores aggregated pattern statistics

Pattern Detection
-CandlePattern.java -Interface for all candlestick patterns
-PatternRegistry.java -Central registry for pattern implementations

Analysis
-CsvParser.java -Reads and validates CSV data
-PatternAnalyzer.java -Detects patterns and evaluates outcomes

Reporting
-CsvExporter.java -Exports analysis results to CSV

UI
-MainView.java` -Main layout and screen navigation
-UploadView.java -Drag-and-drop upload screen
-ResultsView.java -Dashboard displaying analysis results
-PatternCard.java -Reusable pattern statistics card

Styling
-app.css -Global dark theme stylesheet

Data
-'*.csv' -Contains sample data

The .csv files are downloaded from this website https://www.londonstrategicedge.com/datasets and the application supports other .csv with the same format.

To run the JavaFX application,the following must be included in the Virtual Machine arguements
--module-path "C:\javafx-sdk-25\lib" --add-modules=javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media --enable-native-access=javafx.graphics --enable-native-access=javafx.media


