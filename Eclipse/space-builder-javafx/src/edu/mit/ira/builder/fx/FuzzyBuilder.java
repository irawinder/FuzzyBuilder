package edu.mit.ira.builder.fx;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
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
	
	// Scene Configuration within Window
	
	/////////////////////////////////////////////
	//      		toolbar					   //
	/////////////////////////////////////////////
	//    //		version		  //		   //
	// n  //////////////////////////  outcome  //
	// a  //					  //		   //
	// v  //		canvas		  //		   //
	// i  //					  ///////////////
	// g  //////////////////////////		   //
	// a  //					  //  commit   //
	// t  //		massing  	  //		   //
	// e  //					  //		   //
	/////////////////////////////////////////////
	//      		status					   //
	/////////////////////////////////////////////
	
	// All Default Dimensions are in Pixels:
	
	final private double DEFAULT_APPLICATION_WIDTH = 1280;
	final private double DEFAULT_APPLICATION_HEIGHT = 800;
	
	final private double TOOL_HEIGHT    = 30;
	final private double VERSION_HEIGHT = 50;
	final private double STATUS_HEIGHT  = 30;
	final private double NAVIGATE_WIDTH = 200;
	final private double OUTCOME_WIDTH  = 400;
	
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
		SubScene toolbar = new SubScene(
				toolbarContent, 
				DEFAULT_APPLICATION_WIDTH, 
				TOOL_HEIGHT);
		SubScene version = new SubScene(
				versionContent, 
				DEFAULT_APPLICATION_WIDTH - OUTCOME_WIDTH, 
				VERSION_HEIGHT);
		SubScene canvas = new SubScene(
				canvasContent, 
				DEFAULT_APPLICATION_WIDTH - OUTCOME_WIDTH - NAVIGATE_WIDTH,
				0.5 * (DEFAULT_APPLICATION_HEIGHT - TOOL_HEIGHT - VERSION_HEIGHT - STATUS_HEIGHT));
		SubScene massing = new SubScene(
				massingContent, 
				DEFAULT_APPLICATION_WIDTH - OUTCOME_WIDTH - NAVIGATE_WIDTH, 
				0.5 * (DEFAULT_APPLICATION_HEIGHT - TOOL_HEIGHT - VERSION_HEIGHT - STATUS_HEIGHT), 
				true, SceneAntialiasing.BALANCED);
		SubScene outcome = new SubScene(
				outcomeContent, 
				OUTCOME_WIDTH, 
				0.5 * (DEFAULT_APPLICATION_HEIGHT - TOOL_HEIGHT - STATUS_HEIGHT));
		SubScene commit = new SubScene(
				commitContent, 
				OUTCOME_WIDTH, 
				0.5 * (DEFAULT_APPLICATION_HEIGHT - TOOL_HEIGHT - STATUS_HEIGHT));
		SubScene navigate = new SubScene(
				navigateContent, 
				NAVIGATE_WIDTH, 
				DEFAULT_APPLICATION_HEIGHT - TOOL_HEIGHT - STATUS_HEIGHT);
		SubScene status = new SubScene(
				statusContent, 
				DEFAULT_APPLICATION_WIDTH, 
				STATUS_HEIGHT);
		
		// Organize the SubScenes into Nested Grid Panes
        GridPane windowPane = new GridPane();
        GridPane mainPane = new GridPane();
        GridPane centerPane = new GridPane();
        GridPane rightPane = new GridPane();
        
        ///////////////////////////////////////
    	//						  			 //
    	//						  			 //
    	//						  			 //
    	//			windowPane	 			 //        
    	//						  			 //   
    	//						  			 //
    	//						  			 //
    	//						  			 //        
    	//						  			 //        
    	//						  			 //        
    	//						  			 //
    	///////////////////////////////////////
        
        // Window GridPane (windowPane)
        windowPane.add(toolbar, 0, 0);
        windowPane.add(mainPane, 0, 1);
        windowPane.add(status, 0, 2);
        
        ///////////////////////////////////////
    	//		toolbar					     //
    	///////////////////////////////////////
    	//						  			 //
    	//						  			 //
    	//						  			 //
    	//		mainPane					 //        
    	//						  			 //        
    	//						  			 //        
    	//						  			 //
    	///////////////////////////////////////
    	//		status						 //
    	///////////////////////////////////////
        
        // Main GridPane (mainPane)
        mainPane.add(navigate, 0, 0);
        mainPane.add(centerPane, 1, 0);
        mainPane.add(rightPane, 2, 0);
        
        ///////////////////////////////////////
    	//    //				//			 //
    	// n  //    centerPane	// rightPane //
    	// a  //				//			 //
    	// v  //				//			 //
    	// i  //				//			 //
    	// g  //				//			 //
    	// a  //				//	 		 //
    	// t  //		  		//			 //
    	// e  //				//			 //
    	///////////////////////////////////////
        
        // Center GridPane (leftPane)
        centerPane.add(version, 0, 0);
        centerPane.add(canvas, 0, 1);
        centerPane.add(massing, 0, 2);
        
        //////////////////////////
    	//		version			//
    	//////////////////////////
    	//						//
    	//		canvas			//
    	//						//
    	//////////////////////////			 
    	//						//
    	//		massing  		//			 
    	//						//			 
    	//////////////////////////
        
        // Right GridPane (rightPane)
        rightPane.add(outcome, 0, 0);
        rightPane.add(commit, 0, 1);
        
        ///////////////
    	//			 //
    	//	outcome  //
    	//			 //
    	//			 //
    	///////////////
    	//			 //
    	//	commit	 //
    	//			 //
    	//			 //
    	///////////////
        
        // Commit all content to a master scene
        Scene allSubScenes = new Scene(windowPane);
        
        // Handle event for resizing the width of the application content
        allSubScenes.widthProperty().addListener(new ChangeListener<Number>() {
        	
    		@Override
    		public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
    			double windowWidth = (double) arg2;
    			toolbar.setWidth(windowWidth);
    	    	version.setWidth(windowWidth - OUTCOME_WIDTH - NAVIGATE_WIDTH);
    	    	canvas.setWidth(windowWidth - OUTCOME_WIDTH - NAVIGATE_WIDTH);
    	    	massing.setWidth(windowWidth - OUTCOME_WIDTH - NAVIGATE_WIDTH);
    	    	status.setWidth(windowWidth);
    		}
        });
        
        // Handle event for resizing the height of the application content
        allSubScenes.heightProperty().addListener(new ChangeListener<Number>() {
        	
    		@Override
    		public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
    			double windowHeight = (double) arg2;
    			canvas.setHeight(0.5*(windowHeight - TOOL_HEIGHT - VERSION_HEIGHT - STATUS_HEIGHT));
    	    	massing.setHeight(0.5*(windowHeight - TOOL_HEIGHT - VERSION_HEIGHT - STATUS_HEIGHT));
    	    	outcome.setHeight(0.5*(windowHeight - TOOL_HEIGHT - STATUS_HEIGHT));
    	    	commit.setHeight(0.5*(windowHeight - TOOL_HEIGHT - STATUS_HEIGHT));
    	    	navigate.setHeight(windowHeight - TOOL_HEIGHT - STATUS_HEIGHT);
    		}
        });
        
        Massing scenario = new Massing(massing.getWidth(), massing.getHeight());
        
        // Pass Key Commands on to lesser functions
        allSubScenes.setOnKeyPressed(e -> {
        	scenario.keyPressed(e);
        });
        
        appWindow.setScene(allSubScenes);
        appWindow.show();
    }
}