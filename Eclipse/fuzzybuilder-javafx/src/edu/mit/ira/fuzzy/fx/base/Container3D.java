package edu.mit.ira.fuzzy.fx.base;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 * SubScene container for 3D View Model
 * 
 * @author Ira Winder
 *
 */
public class Container3D extends Container2D {
    
	// Default height of a default light source
	final protected static double DEFAULT_LIGHT_Z = 400;
	
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
	
	/**
	 * Construct a 3D Overhead Light Source
	 */
	protected Node overheadLight() {
		PointLight light = new PointLight();
		light.setColor(Color.WHITE);
		light.getTransforms().add(new Translate(0, -cam.scaler()*DEFAULT_LIGHT_Z, 0));
		return light;
	}
	
	/**
	 * Construct a 3D Side Light Source
	 */
	protected Node sideLight() {
		PointLight light = new PointLight();
		light.setColor(Color.WHITE);
		light.getTransforms().add(new Translate(-cam.scaler()*DEFAULT_LIGHT_Z, 0, -cam.scaler()*DEFAULT_LIGHT_Z));
		return light;
	}
	
	/**
	 * Draw and Orient a Basic Box within the 3D container
	 * 
	 * @param boxX
	 * @param boxY
	 * @param boxZ
	 * @param boxW
	 * @param boxH
	 * @param z_offset
	 * @param col
	 * @return JavaFX Box Object
	 */
	protected Box basicBox(double boxX, double boxY, double boxZ, double boxW, double boxH, double z_offset, Color col) {
		Box b = new Box(boxW, boxH, boxW);
		Translate pos = new Translate(
				+ boxX, 
				- boxZ - 0.5*boxH - z_offset, 
				- boxY);
		b.getTransforms().addAll(cam.rotateH, cam.pan, pos);
		PhongMaterial material = new PhongMaterial(col);
		b.setMaterial(material);
		return b;
	}
	
	/**
	 * Make and position a 2D image or shape to the 3D environment
	 * 
	 * @param input
	 * @param x
	 * @param y
	 * @param z
	 */
	protected void orientShape2D(Node input, double x, double y, double z) {
		Rotate rotateFlat = new Rotate(-90, Rotate.X_AXIS);
		Translate pos = new Translate(x, -z, -y);
		input.getTransforms().clear();
		input.getTransforms().addAll(cam.rotateH, cam.pan, pos, rotateFlat);
	}
	
	/**
	 * Make and position a 3D shape to the 3D environment
	 * 
	 * @param input
	 * @param x
	 * @param y
	 * @param z
	 */
	protected void orientShape3D(Node input, double x, double y, double z) {
		Translate pos = new Translate(x, -z, -y);
		input.getTransforms().clear();
		input.getTransforms().addAll(cam.rotateH, cam.pan, pos);
	}
}
