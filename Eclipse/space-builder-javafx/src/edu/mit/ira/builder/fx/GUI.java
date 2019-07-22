package edu.mit.ira.builder.fx;

import java.util.ArrayList;
import java.util.Random;

import javafx.application.Application;
import javafx.scene.input.MouseEvent;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

public class GUI extends Application {

	private ArrayList<Box> boxArray;
    
	private Scene scene;
	
	// Vertical Rotation (JavaFX uses X_AXIS)
    private Rotate rotateV = new Rotate(0, Rotate.X_AXIS);
    
    // Horizontal Rotation (JavaFX uses Y_AXIS)
    private Rotate rotateH = new Rotate(0, Rotate.Y_AXIS);
    
    // Default Horizontal Rotation
    private static final Rotate DEFAULT_ROTATE_H = new Rotate(-20, Rotate.Y_AXIS);
    
    // Zoom level of Camera
    private Translate zoom = new Translate(0, 0, -30);

    // Center of Entire 3D Scene
    private static final Translate ORIGIN = new Translate(-5, 0, -5);
    
    // Mouse locations on Canvas
    private double mousePosX, mousePosY = 0;
    
    /**
     * Create a View Model for some test geometry
     * 
     * @return a collection of JavaFX nodes (3D objects)
     */
    public Parent testContent() {
        
        // Box Array:
    	double boxW = 0.85;
    	double gridW = 1.0;
        boxArray = new ArrayList<Box>();
        for (int i=0; i<11; i++ ) {
        	for (int j=0; j<11; j++ ) {
        		
        		// Box Dimensions and Locations with Random Height
        		Random rand = new Random();
        		double boxH = rand.nextFloat();
        		Box b = new Box(boxW, boxH, boxW);
        		Translate pos = new Translate(i*gridW, -0.5*boxH, j*gridW);
        		b.getTransforms().addAll(rotateH, ORIGIN, pos);
        		
        		// Box Color (random hue and 90% opacity)
        		double hue = 360 * rand.nextFloat();
        		PhongMaterial material = new PhongMaterial(Color.hsb(hue, 1.0, 1.0, 0.9));
        		b.setMaterial(material);
        		boxArray.add(b);
        	}
        }

        // Create and position camera
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.getTransforms().addAll(DEFAULT_ROTATE_H, rotateV, zoom);

        // Build the Scene Graph
        Group root = new Group();
        root.getChildren().add(camera);
        for (Box b : boxArray) root.getChildren().add(b);

        // Use a SubScene
        SubScene subScene = new SubScene(root, 1000, 600, true, 
             SceneAntialiasing.BALANCED);
        subScene.setFill(Color.TRANSPARENT);
        subScene.setCamera(camera);

        return new Group(subScene);
    }
    
    /**
     * Handle all mouse events (Pressed, dragged, etc)
     */
    private void handleMouseEvents() {
        scene.setOnMousePressed((MouseEvent me) -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
        });

        scene.setOnMouseDragged((MouseEvent me) -> {
            double dx = + (mousePosX - me.getSceneX());
            double dy = - (mousePosY - me.getSceneY());
            if (me.isPrimaryButtonDown()) {
            	
            	double angleV = rotateV.getAngle() - (dy / 10 * 360) * (Math.PI / 180);
            	double angleH = rotateH.getAngle() - (dx / 10 * -360) * (Math.PI / 180);
            	
            	// Can view above and below model, but
            	// Don't allow view to flip upside down
            	angleV = ensureRange(angleV, -90, 90);
            	
                rotateV.setAngle(angleV);
                rotateH.setAngle(angleH);
            }
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setResizable(false);
        scene = new Scene(testContent());
        handleMouseEvents();
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    double ensureRange(double value, double min, double max) {
    	return Math.min(Math.max(value, min), max);
    }
	
}