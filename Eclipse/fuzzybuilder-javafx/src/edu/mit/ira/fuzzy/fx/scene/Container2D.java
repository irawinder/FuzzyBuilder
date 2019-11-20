package edu.mit.ira.fuzzy.fx.scene;

import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;

/**
 * SubScene element for 2D View Model
 * 
 * @author Ira Winder
 *
 */
public class Container2D extends SubScene {
    
	final static double DEFAULT_WIDTH = 100;
	final static double DEFAULT_HEIGHT = 100;
	final static Color DEFAULT_BACKGROUND = Color.TRANSPARENT;
	final static Group EMPTY_GROUP = new Group();
	
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
