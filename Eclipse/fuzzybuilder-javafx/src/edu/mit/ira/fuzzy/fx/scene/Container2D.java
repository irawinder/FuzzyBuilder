package edu.mit.ira.fuzzy.fx.scene;

import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;

/**
 * SubScene container for 2D View Model
 * 
 * @author Ira Winder
 *
 */
public class Container2D extends SubScene {
    
	// Default Scene Values
	final protected static double DEFAULT_WIDTH = 100;
	final protected static double DEFAULT_HEIGHT = 100;
	final protected static Color DEFAULT_BACKGROUND = Color.TRANSPARENT;
	final protected static Group EMPTY_GROUP = new Group();
	
 	// Default color and stroke values
 	final protected static double DEFAULT_SATURATION 	= 0.50;
 	final protected static double DEFAULT_BRIGHTNESS 	= 0.75;
 	final protected static double DEFAULT_ALPHA 		= 0.90;
 	final protected static double SUBDUED_SATURATION 	= 0.30;
 	final protected static double SUBDUED_BRIGHTNESS 	= 0.75;
 	final protected static double SUBDUED_ALPHA 		= 0.75;
 	final protected static double SUBTLE_SATURATION 	= 0.10;
 	final protected static double SUBTLE_BRIGHTNESS 	= 0.75;
 	final protected static double SUBTLE_ALPHA 			= 0.5;
 	final protected static double DEFAULT_STROKE 		= 2.0;
 	final protected static double SUBDUED_STROKE 		= 1.0;
	
    // "Front End" Nodes to Pass to Parent JavaFX Scene
 	protected Group nodes2D;
 	protected SubScene scene2D;
	
	public Container2D() {
		super(EMPTY_GROUP, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		
		// Initialize Parent Nodes
    	nodes2D = new Group();
    	
    	// Initialize SubScene
        scene2D = new SubScene(nodes2D, getWidth(), getHeight());
        
        // Bind dimensions of child scene
        scene2D.widthProperty().bind(widthProperty());
        scene2D.heightProperty().bind(heightProperty());
        
    	// Set an ID for scene
        scene2D.setId("scene2D");
        
        // Define the background color for scene
    	scene2D.setFill(DEFAULT_BACKGROUND);
    	
        // Set the Parent Node of container content
        setRoot(new Group(scene2D));
	}
}
