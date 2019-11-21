package edu.mit.ira.fuzzy.fx.base;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
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
	
	// Default Dimension
	final protected static double TEXT_BUFFER = 10;
	final protected static Insets TEXT_INSETS = new Insets(TEXT_BUFFER, TEXT_BUFFER, TEXT_BUFFER, TEXT_BUFFER);
	
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
 	
 	final protected static String DEFAULT_ID = "new_container";
 	final protected static String DEFAULT_FRIENDLY_NAME = "New Container";
	
    // "Front End" Nodes to Pass to Parent JavaFX Scene
 	protected Group nodes2D;
 	protected SubScene scene2D;
 	
 	// Friendly name of pane to show user
 	protected String friendlyName;
	
 	/**
 	 * Instantiate a new 2D container with a given ID and friendly name
 	 * 
 	 * @param id
 	 * @param friendlyName
 	 */
	public Container2D(String id, String friendlyName) {
		super(EMPTY_GROUP, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		
		this.setId(id);
		setFriendlyName(friendlyName);
		
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
        
        // Set Background to a random gray color
        double randomBrightness = 0.65 + 0.25*Math.random(); // between 0.65 - 0.90
		this.setFill(Color.hsb(0, 0, randomBrightness));
	}
	
	/**
	 * Empty Constructor with Default Values
	 */
	public Container2D() {
		this(DEFAULT_ID, DEFAULT_FRIENDLY_NAME);
	}
	
	/**
	 * Set Friendly Name of Container
	 * 
	 * @param friendlyName
	 */
	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}
	
	/**
	 * Get Friendly Name of Container
	 * 
	 * @return
	 */
	public String getFriendlyName() {
		return friendlyName;
	}
	
	/**
	 * Render the default container "Front End"
	 */
	public void init() {
		nodes2D.getChildren().clear();
		StackPane content  = new StackPane(new Label(friendlyName));
		nodes2D.getChildren().add(content);
	}
	
	/**
	 * Handle Key Events passed to container
	 * 
	 * @param e key event
	 */
	public void keyPressed(KeyEvent e) {
		// Override in child class
	}
}
