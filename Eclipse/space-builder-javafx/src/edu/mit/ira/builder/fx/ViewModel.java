package edu.mit.ira.builder.fx;

import edu.mit.ira.builder.Builder;
import edu.mit.ira.voxel.Tile;
import edu.mit.ira.voxel.TileArray;

import java.util.ArrayList;

import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class ViewModel {
	
	private Builder builder;
	private ArrayList<Node> boxArray;
	
	// Master scene;
	private Scene scene;
	
	// Master Group
	private Group group; 
	
	// Master Camera
	private PerspectiveCamera camera;
	
	// Vertical Rotation (JavaFX uses X_AXIS)
    private Rotate rotateV = new Rotate(0, Rotate.X_AXIS);
    
    // Horizontal Rotation (JavaFX uses Y_AXIS)
    private Rotate rotateH = new Rotate(0, Rotate.Y_AXIS);
    
    // Default Horizontal Rotation
    private static final Rotate DEFAULT_ROTATE_H = new Rotate(-20, Rotate.Y_AXIS);
    
    // Default Vertical Rotation
    private static final Rotate DEFAULT_ROTATE_V = new Rotate(-20, Rotate.X_AXIS);
    
    // Zoom level of Camera
    private Translate zoom = new Translate(0, 0, -1000);

    // Center of Entire 3D Scene
    private static final Translate ORIGIN = new Translate(-400, 0, 200);
    
    // Mouse locations on Canvas
    private double mousePosX, mousePosY = 0;
    
    /**
     * Test Scene Constructor
     */
    public ViewModel() {
    	group = new Group();
    	builder = new Builder();
    }
    
    public Scene getScene() {
    	return scene;
    }
    
    public Group getGroup() {
    	return group;
    }
    
    public Camera getCamera() {
    	return camera;
    }
    
    /**
     * Create a scene Model for some test geometry
     * 
     * @return a collection of JavaFX groups (3D objects)
     */
    public void createContent() {
    	
        // Box Array:
    	boxArray = new ArrayList<Node>();
    	
    	// Draw Test
    	for (TileArray space : builder.dev.spaceList()) {
    		Color col = Color.hsb(space.getHueDegree(), 1.0, 1.0, 1.0);
    		for (Tile t : space.tileList()) {
    			if(space.isType("zone")) {
    				boxArray.add(renderTile(t, col, 0));
    			}
    		}
    	}
    	
//    	// Box Array:
//    	double boxW = 0.85;
//    	double gridW = 1.0;
//        for (int i=0; i<11; i++ ) {
//        	for (int j=0; j<11; j++ ) {
//        		
//        		// Box Dimensions and Locations with Random Height
//        		Random rand = new Random();
//        		double boxH = rand.nextFloat();
//        		Box b = new Box(boxW, boxH, boxW);
//        		Translate pos = new Translate(i*gridW, -0.5*boxH, j*gridW);
//        		b.getTransforms().addAll(rotateH, ORIGIN, pos);
//        		
//        		// Box Color (random hue and 90% opacity)
//        		double hue = 360 * rand.nextFloat();
//        		PhongMaterial material = new PhongMaterial(Color.hsb(hue, 1.0, 1.0, 0.9));
//        		b.setMaterial(material);
//        		boxArray.add(b);
//        	}
//        }
    	
        // Create and position camera
        camera = new PerspectiveCamera(true);
        camera.getTransforms().addAll(DEFAULT_ROTATE_H, DEFAULT_ROTATE_V, rotateV, zoom);
        camera.setFarClip(10000);
        camera.setNearClip(0);
        
//        PointLight light = new PointLight();
//        light.setColor(Color.gray(1));
//        light.setTranslateX(-400);
//        light.setTranslateY(-200);
//        light.setTranslateZ(200);

        // Build the Scene Graph
        Group root = new Group();
        root.getChildren().addAll(camera);
        for (Node b : boxArray) root.getChildren().add(b);

        // Use a SubScene
        SubScene subScene = new SubScene(root, 1000, 600, true, 
             SceneAntialiasing.BALANCED);
        subScene.setFill(Color.TRANSPARENT);
        subScene.setCamera(camera);

        group = new Group(subScene);
        scene = new Scene(group);
    }
    
    /**
     * Construct a 3D pixel (i.e. "Voxel") from tile attributes
     * @param t Tile to render
     * @param col Color of voxel
     * @param z_offset manual z_offset for rendering some layers above others even when their z_attributes are the same
     * @return a JavaFX Box Node
     */
    private Box renderVoxel(Tile t, Color col, double z_offset) {
    	float scaler_uv = (float) 0.9;
    	float scaler_w = (float) 0.6;
    	
    	float boxW = scaler_uv * t.scale_uv;
    	float boxH = scaler_w * t.scale_w;
    	Box b = new Box(boxW, boxH, boxW);
    	Translate pos = new Translate(t.location.x, - t.location.z - z_offset - 0.5*boxH, - t.location.y);
    	Rotate rot = new Rotate(180*builder.tile_rotation);
    	b.getTransforms().addAll(rotateH, ORIGIN, pos, rot);

    	// Box Color
    	PhongMaterial material = new PhongMaterial(col);
    	b.setMaterial(material);

    	return b;
    }
    
    /**
     * Construct a 2D pixel (i.e. "Tile") from tile attributes
     * @param t Tile to render
     * @param col Color of tile
     * @param z_offset manual z_offset for rendering some layers above others even when their z_attributes are the same
     * @return a JavaFX Rectangle Node
     */
    private Rectangle renderTile(Tile t, Color col, double z_offset) {
    	float scaler_uv = (float) 0.9;
    	
    	float rectW = scaler_uv * t.scale_uv;
    	Rectangle r = new Rectangle(rectW, rectW);
    	Rotate rotateFlat = new Rotate(90, Rotate.X_AXIS);
    	Translate pos = new Translate(t.location.x, - t.location.z - z_offset, - t.location.y);
    	Rotate rot = new Rotate(180*builder.tile_rotation);
    	r.getTransforms().addAll(rotateH, ORIGIN, pos, rot, rotateFlat);

    	// Box Color
    	r.setFill(col);

    	return r;
    }
    
    /**
     * Handle all mouse events (Pressed, dragged, etc)
     */
    public void handleMouseEvents() {
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
            	
            	// Can scene above and below model, but
            	// Don't allow scene to flip upside down
            	angleV = ensureRange(angleV, -90, 90);
            	
                rotateV.setAngle(angleV);
                rotateH.setAngle(angleH);
            }
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
        });
    }
    
    /**
     * Returns a value capped to a specified minimum and maximum value
     * 
     * @param value input value
     * @param min minimum allowable value
     * @param max maximum allowable value
     * @return constrained value
     */
    double ensureRange(double value, double min, double max) {
    	return Math.min(Math.max(value, min), max);
    }
}