import com.ui.MainView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	
	    @Override
	    public void start(Stage stage) {
	        MainView view = new MainView(stage);

	        Scene scene = new Scene(view.getRoot(), 1100, 750);
	        
	        scene.getStylesheets().add(
	            getClass().getResource("/styles/app.css").toExternalForm()
	        );
	        

	        stage.setTitle("Candlestick Pattern Analyser");
	        stage.setScene(scene);
	        stage.setMinWidth(800);
	        stage.setMinHeight(600);
	        stage.show();
	    }

	    public static void main(String[] args) {
	        launch(args);
	    }
	

	
}
