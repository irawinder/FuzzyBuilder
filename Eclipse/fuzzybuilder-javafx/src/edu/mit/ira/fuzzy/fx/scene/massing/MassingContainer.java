package edu.mit.ira.fuzzy.fx.scene.massing;

import edu.mit.ira.fuzzy.builder.DevelopmentEditor;
import edu.mit.ira.fuzzy.fx.node.Underlay;
import edu.mit.ira.fuzzy.fx.scene.ContentContainer;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
/**
 * Massing View Model
 *
 * @author Ira Winder
 *
 */
public class MassingContainer extends SubScene implements ContentContainer {
	
	// TODO
	// Draw Control Point at Mouse Hover
	// Draw Tagged Control Point Labels
	// Draw Info at Mouse Hover
	// Draw Info/Instructions
	// Draw Attribute Summary
	
    // "Back End" Elements to Render to Container
    protected DevelopmentEditor form_model; 
    protected Underlay map_model;
    
    // "Front End" Nodes to Pass to Parent JavaFX Scene
 	protected Group nodes3D, nodes2D;
 	protected SubScene scene3D, scene2D;
 	
 	// Dynamic 3D View Parameters
 	protected Camera camera;
 	protected Color background;
 	protected Translate zoom;
 	protected Translate pan;
 	protected Rotate rotateV;
 	protected Rotate rotateH;
 	protected double minZoom, maxZoom;
 	protected boolean showUnderlay;
 	protected double viewScaler;
 	
 	// Mouse locations on Canvas
 	protected double mousePosX, mousePosY;
 	
 	// Default Camera Values
	final protected static double DEFAULT_SCALER = 4.0;
	final protected static double DEFAULT_ZOOM = -1000;
	final protected static double DEFAULT_PAN_X = 325;
	final protected static double DEFAULT_PAN_Y = 425;
	final protected static double DEFAULT_ROTATE_V = -20;
	final protected static double DEFAULT_ROTATE_H = -45;
	final protected static double DEFAULT_NEAR_CLIP = 10;
	final protected static double DEFAULT_FAR_CLIP = 10000;
	final protected static double DEFAULT_MIN_ZOOM = -100;
	final protected static double DEFAULT_MAX_ZOOM = -2000;
 	
    /**
     * Initialize a new content container for Massing
     * 
     * @param form_model scenario geometry
     * @param map_model scenario basemap
     */
    public MassingContainer() {
    	
    	// Initialize Container and Backend
    	super(EMPTY_GROUP, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    	
    	// Initialize Camera and Pan
    	viewScaler = DEFAULT_SCALER;
    	minZoom = viewScaler*DEFAULT_MIN_ZOOM;
    	maxZoom = viewScaler*DEFAULT_MAX_ZOOM;
    	setZoom(viewScaler*DEFAULT_ZOOM);
    	setPan(viewScaler*DEFAULT_PAN_X, viewScaler*DEFAULT_PAN_Y, 0);
    	setRotateV(DEFAULT_ROTATE_V);
    	setRotateH(DEFAULT_ROTATE_H);
    	camera = new PerspectiveCamera(true);
		camera.getTransforms().addAll(rotateV, zoom);
		camera.setNearClip(viewScaler*DEFAULT_NEAR_CLIP);
		camera.setFarClip(viewScaler*DEFAULT_FAR_CLIP);
    	setBackground(Color.TRANSPARENT);
    	showUnderlay = true;
    	
    	// Initialize Mouse Interaction Objects
    	mousePosX = 0;
    	mousePosY = 0;
    	
    	// Initialize Parent Nodes
    	nodes3D = new Group();
    	nodes2D = new Group();
        
    	// Initialize 3D SubScene
        scene3D = new SubScene(nodes3D, getWidth(), getHeight(), true, SceneAntialiasing.BALANCED);
        scene3D.setId("scene3D");
    	scene3D.setFill(background);
        scene3D.setCamera(camera);
    	
        // Initialize Overlay
    	scene2D = new SubScene(nodes2D, getWidth(), getHeight());
    	scene2D.setId("scene2D");
        scene2D.setFill(Color.TRANSPARENT);
        
        // Bind dimensions of child scenes
        scene2D.widthProperty().bind(widthProperty());
        scene2D.heightProperty().bind(heightProperty());
        scene3D.widthProperty().bind(widthProperty());
        scene3D.heightProperty().bind(heightProperty());
        
        // Mouse and Keyboard Events
        handleMouseEvents(this);
        
        // Set the Parent Node of container content
        //setRoot(new Group(scene3D, scene2D));
        setRoot(new Group(scene3D));
    }
    
    /**
     * Reset the Camera to default
     */
    public void defaultCamera() {
    	
    	// Initialize Camera and Pan
    	setZoom(viewScaler*DEFAULT_ZOOM);
    	setPan(viewScaler*DEFAULT_PAN_X, viewScaler*DEFAULT_PAN_Y, 0);
    	setRotateV(DEFAULT_ROTATE_V);
    	setRotateH(DEFAULT_ROTATE_H);
    	camera = new PerspectiveCamera(true);
		camera.getTransforms().addAll(rotateV, zoom);
		camera.setNearClip(viewScaler*DEFAULT_NEAR_CLIP);
		camera.setFarClip(viewScaler*DEFAULT_FAR_CLIP);
		scene3D.setCamera(camera);
    }
	
	/**
	 * Zoom level of Camera
	 * 
	 * @param distance (more negative is farther away)
	 */
	public void setZoom(double distance) {
		this.zoom = new Translate(0, 0, ensureRange(distance, maxZoom, minZoom));
	}
	
	/**
	 * Set Amount of panning from origin
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setPan(double x, double y, double z) {
		this.pan = new Translate(-x, z, y);
	}
	
	/**
	 * Vertical Rotation (JavaFX uses X_AXIS)
	 * @param angle in degrees
	 */
	public void setRotateV(double angle) {
		this.rotateV = new Rotate(angle, Rotate.X_AXIS);
	}
	
	/**
	 * Horizontal Rotation (JavaFX uses Y_AXIS)
	 * @param angle in degrees
	 */
	public void setRotateH(double angle) {
		this.rotateH = new Rotate(angle, Rotate.Y_AXIS);
	}
	
	/**
	 * Background color of infinity
	 * 
	 * @param color
	 */
	public void setBackground(Color color) {
		this.background = color;
	}
	
	/**
	 * Populates the View Model with a form from Builder class
	 * 
	 * @param form form from Builder class
	 */
	public void setFormModel(DevelopmentEditor form_model) {
		this.form_model = form_model;
	}
	
	/**
	 * Populates the View Model with a map from Underlay class
	 * 
	 * @param map_model Map Underlay passed from Underlay class
	 */
	public void setMapModel(Underlay map_model) {
		this.map_model  = map_model;
	}
	
	public void keyPressed(KeyEvent e) {

		// Print Camera Position
		if (e.getText().equals("C")) {
			defaultCamera();
			//printCamera();
		}
		// Print Camera Position
		if (e.getCode() == KeyCode.U) {
			showUnderlay = !showUnderlay;
		}
	}
	
	/**
	 * Print Camera Parameters to console
	 */
	public void printCamera() {
		System.out.println("Camera Zoom: " + zoom.getZ());
		System.out.println("Camera Pan: (" + -pan.getX() + ", " + pan.getZ() + ", " + pan.getY() + ")");
		System.out.println("Camera RotateV: " + rotateV.getAngle());
		System.out.println("Camera RotateH: " + rotateH.getAngle());
	}
	
	/**
	 * Actions to take when mouse events are detected
	 * 
	 * @param scene3d
	 */
	public void handleMouseEvents(SubScene scene) {

		scene.setOnMouseMoved((MouseEvent me) -> {
			mousePosX = me.getScreenX();
			mousePosY = me.getScreenY();
		});
		
		scene.setOnMouseDragged((MouseEvent me) -> {

			// Mouse displacement while pressed and dragged
			double dx = +(mousePosX - me.getScreenX());
			double dy = -(mousePosY - me.getScreenY());

			// i.e. right mouse button
			if (me.isSecondaryButtonDown()) {

				// Rotate View
				double angleV = rotateV.getAngle() - (dy / 10 * +360) * (Math.PI / 180);
				double angleH = rotateH.getAngle() - (dx / 10 * -360) * (Math.PI / 180);
				angleV = ensureRange(angleV, -90, 90);
				rotateV.setAngle(angleV);
				rotateH.setAngle(angleH);

				// i.e. left mouse button
			} else if (me.isPrimaryButtonDown()) {

				// Pan View
				double angleH = DegreeToRadian((float) rotateH.getAngle());
				double dx_r, dy_r;
				int flip = 1;
				if (rotateV.getAngle() > 0)
					flip = -1;
				dx_r = +dx * Math.cos(angleH) - flip * dy * Math.sin(angleH);
				dy_r = +dx * Math.sin(angleH) + flip * dy * Math.cos(angleH);
				double panU = pan.getX() - viewScaler * dx_r;
				double panV = pan.getZ() - viewScaler * dy_r;
				pan.setX(panU);
				pan.setZ(panV);
			}

			// Set new mouse position
			mousePosX = me.getScreenX();
			mousePosY = me.getScreenY();
		});

		// Enable Zoom in and Zoom Out via scroll wheel
		scene.setOnScroll((ScrollEvent se) -> {
			double dy = se.getDeltaY();
			double new_zoom = zoom.getZ() - dy;
			new_zoom = ensureRange(new_zoom, maxZoom, minZoom);
			zoom.setZ(new_zoom);
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
	private double ensureRange(double value, double min, double max) {
		return Math.min(Math.max(value, min), max);
	}

	/**
	 * Convert radian value to degrees
	 * 
	 * @param radian
	 * @return degrees (0-360)
	 */
	protected double RadianToDegree(float radian) {
		return (180 * radian / Math.PI)%360;
	}
	
	/**
	 * Convert degree value to radians
	 * 
	 * @param degree
	 * @return radians (0-2PI)
	 */
	protected double DegreeToRadian(float radian) {
		return (Math.PI * radian / 180)%(2*Math.PI);
	}
}