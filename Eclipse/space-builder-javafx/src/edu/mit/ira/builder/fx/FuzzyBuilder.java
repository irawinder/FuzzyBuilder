package edu.mit.ira.builder.fx;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.stage.Stage;

/**
 * The Main Application Environment to Run Fuzzy Builder
 *
 * If Running from eclipse, you need to put the following VM Argument into the Run Configuration
 * --module-path "/Path/to/javafx-sdk-13/lib" --add-modules javafx.controls
 *
 * @author Ira Winder
 *
 */
public class FuzzyBuilder extends Application {
	
	final private String APPLICATION_NAME = "Fuzzy Builder";
	
	public static void main(String[] args) {
        launch();
    }
	
    @Override
    public void start(Stage appWindow) {
		
    	// Set the Title Bar of the Application
    	appWindow.setTitle(APPLICATION_NAME);
		
		// Content Containers for the Application (Populate these however you wish)
		SubScene toolbar = new Toolbar();
		SubScene version = new Version();
		SubScene canvas = new Canvas();
		SubScene massing = new Massing();
		SubScene outcome = new Outcome();
		SubScene commit = new Commit();
		SubScene navigate = new Navigate();
		SubScene status = new Status();
		
		Scene content = Layout.build(toolbar, navigate, version, canvas, massing, outcome, commit, status);
        
        // Pass Key Commands on to lesser functions
		content.setOnKeyPressed(e -> {
			
			// trigger functions in the Massing Model
        	((Massing) massing).keyPressed(e);
        });
        
        appWindow.setScene(content);
        appWindow.show();
    }
}