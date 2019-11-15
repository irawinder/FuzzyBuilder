package edu.mit.ira.fuzzy.fx.stage;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.layout.GridPane;

public class Layout {
	
	// All Default Dimensions are in Pixels or Ratios:
	
	final private static double DEFAULT_APPLICATION_WIDTH = 1280;
	final private static double DEFAULT_APPLICATION_HEIGHT = 800;
	
	final private static double TOOL_HEIGHT    = 30;
	final private static double VERSION_HEIGHT = 50;
	final private static double STATUS_HEIGHT  = 30;
	final private static double NAVIGATE_WIDTH = 200;
	final private static double OUTCOME_WIDTH  = 300;
	
	final private static double CANVAS_HEIGHT_RATIO   = 0.15;
	final private static double OUTCOME_HEIGHT_RATIO  = 0.75;
	
	
	public static Scene build(
			SubScene toolbar, SubScene navigate, SubScene version, SubScene canvas, SubScene massing, SubScene outcome, SubScene commit, SubScene status) {
		
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
		
		toolbar.setWidth(DEFAULT_APPLICATION_WIDTH);
		toolbar.setHeight(TOOL_HEIGHT);
		
		version.setWidth(DEFAULT_APPLICATION_WIDTH - OUTCOME_WIDTH);
		version.setHeight(VERSION_HEIGHT);
		
		canvas.setWidth(DEFAULT_APPLICATION_WIDTH - OUTCOME_WIDTH - NAVIGATE_WIDTH);
		canvas.setHeight(CANVAS_HEIGHT_RATIO * (DEFAULT_APPLICATION_HEIGHT - TOOL_HEIGHT - VERSION_HEIGHT - STATUS_HEIGHT));
		
		massing.setWidth(DEFAULT_APPLICATION_WIDTH - OUTCOME_WIDTH - NAVIGATE_WIDTH);
		massing.setHeight((1 - CANVAS_HEIGHT_RATIO) * (DEFAULT_APPLICATION_HEIGHT - TOOL_HEIGHT - VERSION_HEIGHT - STATUS_HEIGHT));
		
		outcome.setWidth(OUTCOME_WIDTH);
		outcome.setHeight(OUTCOME_HEIGHT_RATIO * (DEFAULT_APPLICATION_HEIGHT - TOOL_HEIGHT - STATUS_HEIGHT));
		
		commit.setWidth(OUTCOME_WIDTH);
		commit.setHeight((1 - OUTCOME_HEIGHT_RATIO) * (DEFAULT_APPLICATION_HEIGHT - TOOL_HEIGHT - STATUS_HEIGHT));
		
		navigate.setWidth(NAVIGATE_WIDTH);
		navigate.setHeight(DEFAULT_APPLICATION_HEIGHT - TOOL_HEIGHT - STATUS_HEIGHT);
		
		status.setWidth(DEFAULT_APPLICATION_HEIGHT - TOOL_HEIGHT - STATUS_HEIGHT);
		status.setHeight(STATUS_HEIGHT);
		
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
		Scene content = new Scene(windowPane);

		// Handle event for resizing the width of the application content
		content.widthProperty().addListener(new ChangeListener<Number>() {

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
		content.heightProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				double windowHeight = (double) arg2;
				canvas.setHeight(CANVAS_HEIGHT_RATIO * (windowHeight - TOOL_HEIGHT - VERSION_HEIGHT - STATUS_HEIGHT));
				massing.setHeight((1 - CANVAS_HEIGHT_RATIO) * (windowHeight - TOOL_HEIGHT - VERSION_HEIGHT - STATUS_HEIGHT));
				outcome.setHeight(OUTCOME_HEIGHT_RATIO * (windowHeight - TOOL_HEIGHT - STATUS_HEIGHT));
				commit.setHeight((1 - OUTCOME_HEIGHT_RATIO) * (windowHeight - TOOL_HEIGHT - STATUS_HEIGHT));
				navigate.setHeight(windowHeight - TOOL_HEIGHT - STATUS_HEIGHT);
			}
		});

		return content;
	}
}
