package edu.mit.ira.fuzzy.fx.scene;

import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;

/**
 * SubScene element for 3D View Model
 * 
 * @author Ira Winder
 *
 */
public class Container3D extends Container2D {
    
    // "Front End" Nodes to Pass to Parent JavaFX Scene
 	protected Group nodes3D;
 	protected SubScene scene3D;
 	
 	// Camera Object that handles view parameters and user navigation
 	protected Camera3D cam; 
	
	public Container3D() {
		super();
		
		// Initialize Parent Nodes
    	nodes3D = new Group();
    	
    	// Initialize SubScene
        scene3D = new SubScene(nodes3D, getWidth(), getHeight(), true, SceneAntialiasing.BALANCED);
        
        // Bind dimensions of child scene
        scene3D.widthProperty().bind(widthProperty());
        scene3D.heightProperty().bind(heightProperty());
        
    	// Set an ID for scene
        scene3D.setId("scene3D");
        
        // Define the background color for scene
    	scene3D.setFill(DEFAULT_BACKGROUND);
    	
    	// Establish 3D scene as reactive to mouse interaction
    	scene3D.setMouseTransparent(false);
    	scene2D.setMouseTransparent(true);
    	
        // Set the Parent Node of container content
        setRoot(new Group(scene3D, scene2D));
        
        // Initialize 3D Camera for 3D scene
    	cam = new Camera3D(scene3D);
	}
}
