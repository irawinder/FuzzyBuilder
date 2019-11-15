package edu.mit.ira.fuzzy.fx.stage;
import edu.mit.ira.fuzzy.builder.DevelopmentEditor;
import edu.mit.ira.fuzzy.builder.sample.RandomSite;
import edu.mit.ira.fuzzy.builder.sample.ShinagawaSite;
import edu.mit.ira.fuzzy.fx.node.Underlay;
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
import javafx.scene.input.KeyCode;
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
	
	// "Back End" - Current Scenario Object Model
	private static DevelopmentEditor scenario_form;
	private static Underlay scenario_map;;
	
	// "Front End" - Content Containers for the Application
	private static SubScene toolbar, navigate, version, canvas, massing, outcome, commit, status;
	
	public static void main(String[] args) {
        launch();
    }
	
    @Override
    public void start(Stage appWindow) {
		
    	// Set the Title Bar of the Application
    	appWindow.setTitle(APPLICATION_NAME);
    	
    	// Begin the application with a random scenario
    	loadRandomScenario();
    	
    	//Initialize Content Containers (SubScenes)
    	toolbar = new Toolbar();
		version = new Version();
		canvas = new Canvas();
		massing = new Massing();
		outcome = new Outcome();
		commit = new Commit();
		navigate = new Navigate();
		status = new Status();
		
		// Assemble all SubScenes into the main content scene
		Scene content = Layout.build(toolbar, navigate, version, canvas, massing, outcome, commit, status);
		
		// Populate Scenes
     	renderScenes();
     	
		// Handle Key Events for the main content scene
        content.setOnKeyPressed(e -> {
        	
        	// Handle Top-level key commands meant for application-wide event
    		if (e.getCode() == KeyCode.L) {
    			loadShinagawaScenario();
    		} else if (e.getCode() == KeyCode.R) {
    			loadRandomScenario();
    		}
    		
        	// Pass Key Commands on to "back-end" form model
        	if(e.getText().length() == 1) {
        		char key = e.getText().toString().charAt(0);
        		scenario_form.keyPressed(key);
        	} else if(e.getCode().isArrowKey()) {
        		String key = e.getCode().toString();
        		scenario_form.arrowPressed(key);
        	}
        	scenario_form.updateModel();
        	
			// Pass Key Commands on to "front-end" lesser containers
			((Toolbar)   toolbar).keyPressed(e);
			((Version)   version).keyPressed(e);
			((Canvas)     canvas).keyPressed(e);
			((Massing)   massing).keyPressed(e);
			((Outcome)   outcome).keyPressed(e);
			((Commit)     commit).keyPressed(e);
			((Navigate) navigate).keyPressed(e);
			((Status)     status).keyPressed(e);
			
    		// Refresh Scenes with new State
    		renderScenes();
        });
        
        // Set the stage and start the show
        appWindow.setScene(content);
        appWindow.show();
    }
    
    /**
     * Load Random Site and View Model Parameters
     * 
     * @param map_model
     * @param form_model
     */
    public void loadRandomScenario() {
    	scenario_form = new RandomSite(375, 375);
    	scenario_map = new Underlay("data/default_site_white.png", 0.5, 0.75);
    }
    
    /**
     * Load JR Site and View Model Parameters
     * 
     * @param map_model
     * @param form_model
     */
    public void loadShinagawaScenario() {
    	scenario_form = new ShinagawaSite();
    	scenario_map = new Underlay("data/jr_site.png", 0.5, 0.75);
    }
    
    public void renderScenes() {
    	
    	((Toolbar)   toolbar).render();
		((Version)   version).render();
		((Canvas)     canvas).render();
		((Massing)   massing).render(scenario_form, scenario_map);
		((Outcome)   outcome).render();
		((Commit)     commit).render();
		((Navigate) navigate).render();
		((Status)     status).render();
    }
}