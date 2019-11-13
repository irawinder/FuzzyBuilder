package edu.mit.ira.fuzzy.fx.stage;
import edu.mit.ira.fuzzy.fx.scene.Canvas;
import edu.mit.ira.fuzzy.fx.scene.Commit;
import edu.mit.ira.fuzzy.fx.scene.Massing;
import edu.mit.ira.fuzzy.fx.scene.Navigate;
import edu.mit.ira.fuzzy.fx.scene.Outcome;
import edu.mit.ira.fuzzy.fx.scene.Status;
import edu.mit.ira.fuzzy.fx.scene.Toolbar;
import edu.mit.ira.fuzzy.fx.scene.Version;
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
        
        // Pass Key Commands on to lesser containers
		content.setOnKeyPressed(e -> {
        	((Massing) massing).keyPressed(e);
        });
        
        appWindow.setScene(content);
        appWindow.show();
    }
}