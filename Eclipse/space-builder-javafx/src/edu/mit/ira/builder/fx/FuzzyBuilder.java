package edu.mit.ira.builder.fx;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
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
    	
		// DEFAULT JavaFX Node-based content for each SubScene
		StackPane toolbarContent  = new StackPane(new Label("Toolbar"));
		StackPane versionContent  = new StackPane(new Label("Version Tree"));
		StackPane canvasContent   = new StackPane(new Label("Visual Programming Canvas"));
		StackPane massingContent  = new StackPane(new Label("3D Massing"));
		StackPane outcomeContent  = new StackPane(new Label("Performance Graph"));
		StackPane commitContent   = new StackPane(new Label("Commit Scenario"));
		StackPane navigateContent = new StackPane(new Label("Navigation"));
		StackPane statusContent   = new StackPane(new Label("Status"));
		
		// Content Containers for the Application (Populate these however you wish)
		SubScene toolbar = new SubScene(toolbarContent, 100, 100);
		SubScene version = new SubScene(versionContent, 100, 100);
		SubScene canvas = new SubScene(canvasContent, 100, 100);
		Massing massing = new Massing();
		SubScene outcome = new SubScene(outcomeContent, 100, 100);
		SubScene commit = new SubScene(commitContent, 100, 100);
		SubScene navigate = new SubScene(navigateContent, 100, 100);
		SubScene status = new SubScene(statusContent, 100, 100);
		
		Scene content = Layout.build(toolbar, navigate, version, canvas, massing, outcome, commit, status);
        
        // Pass Key Commands on to lesser functions
		content.setOnKeyPressed(e -> {
        	massing.keyPressed(e);
        });
        
        appWindow.setScene(content);
        appWindow.show();
    }
}