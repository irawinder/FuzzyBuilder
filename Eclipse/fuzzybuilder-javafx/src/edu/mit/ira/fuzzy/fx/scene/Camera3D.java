package edu.mit.ira.fuzzy.fx.scene;
import javafx.scene.Camera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 * Camera for a 3D Scene or SubScene
 *
 * @author Ira Winder
 *
 */
public class Camera3D {
 	
	// Point to the in-memory SubScene to which the camera is applied 
	SubScene scene3D;
	
 	// Default Camera Values
	final public static double DEFAULT_SCALER = 1.0;
	final public static double DEFAULT_ZOOM = -1000;
	final public static double DEFAULT_PAN_X = 325;
	final public static double DEFAULT_PAN_Y = 425;
	final public static double DEFAULT_ROTATE_V = -20;
	final public static double DEFAULT_ROTATE_H = -45;
	final public static double DEFAULT_NEAR_CLIP = 10;
	final public static double DEFAULT_FAR_CLIP = 10000;
	final public static double DEFAULT_MIN_ZOOM = -100;
	final public static double DEFAULT_MAX_ZOOM = -2000;
	
 	// Dynamic 3D View Parameters
 	public Camera camera;
 	public Translate zoom;
 	public Translate pan;
 	public Rotate rotateV;
 	public Rotate rotateH;
 	public double minZoom, maxZoom;
 	public double viewScaler;
 	
 	// Mouse locations on Canvas
 	public double mousePosX, mousePosY;
 	
    /**
     * Initialize a new framework for handling 3D Camera Exploration
     * 
     * @param form_model scenario geometry
     * @param map_model scenario basemap
     */
    public Camera3D(SubScene scene3D) {
    	
    	setScene(scene3D);
    	
    	// Initialize Camera
    	this.viewScaler = DEFAULT_SCALER;
    	this.init();
    	
    	// Initialize Mouse Interaction Objects
    	this.mousePosX = 0;
    	this.mousePosY = 0;
    }
    
    /**
     * Set the pointer for the local scene object
     * 
     * @param scene3D
     */
    public void setScene(SubScene scene3D) {
    	this.scene3D = scene3D;
    }
    
    /**
     * Set the global scale of all elements to be draw with camera. 
     * By default, an area of approximately 1000x1000 units will fit within the camera view.
     * 
     * @param viewScaler
     */
    public void setViewScaler(double viewScaler) {
    	this.viewScaler = viewScaler;
    	this.init();
    }
    
    /**
     * Initialize the Camera to default
     */
    public void init() {
    	
    	// Initialize Camera and Pan
    	this.minZoom = viewScaler*DEFAULT_MIN_ZOOM;
    	this.maxZoom = viewScaler*DEFAULT_MAX_ZOOM;
    	this.setZoom(viewScaler*DEFAULT_ZOOM);
    	this.setPan(viewScaler*DEFAULT_PAN_X, viewScaler*DEFAULT_PAN_Y, 0);
    	this.setRotateV(DEFAULT_ROTATE_V);
    	this.setRotateH(DEFAULT_ROTATE_H);
    	this.camera = new PerspectiveCamera(true);
    	this.camera.getTransforms().addAll(rotateV, zoom);
    	this.camera.setNearClip(viewScaler*DEFAULT_NEAR_CLIP);
		this.camera.setFarClip(viewScaler*DEFAULT_FAR_CLIP);
		this.scene3D.setCamera(camera);
    }
    
    /**
     * Get the global scaler for objects draw to screen
     * 
     * @return viewScaler
     */
    public double scaler() {
    	return viewScaler;
    }
	
	/**
	 * Zoom level of Camera
	 * 
	 * @param distance (more negative is farther away)
	 */
	private void setZoom(double distance) {
		this.zoom = new Translate(0, 0, ensureRange(distance, maxZoom, minZoom));
	}
	
	/**
	 * Set Amount of panning from origin
	 * @param x
	 * @param y
	 * @param z
	 */
	private void setPan(double x, double y, double z) {
		this.pan = new Translate(-x, z, y);
	}
	
	/**
	 * Vertical Rotation (JavaFX uses X_AXIS)
	 * @param angle in degrees
	 */
	private void setRotateV(double angle) {
		this.rotateV = new Rotate(angle, Rotate.X_AXIS);
	}
	
	/**
	 * Horizontal Rotation (JavaFX uses Y_AXIS)
	 * @param angle in degrees
	 */
	private void setRotateH(double angle) {
		this.rotateH = new Rotate(angle, Rotate.Y_AXIS);
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
	 * update mouse location
	 * 
	 * @param me mouse event
	 */
	public void move(MouseEvent me) {
		this.mousePosX = me.getScreenX();
		this.mousePosY = me.getScreenY();
	}
	
	/**
	 * Enable zooming of camera via scroll wheel
	 * 
	 * @param se scroll event
	 */
	public void zoom(ScrollEvent se) {
		double dy = viewScaler * se.getDeltaY();
		double new_zoom = zoom.getZ() - dy;
		new_zoom = ensureRange(new_zoom, maxZoom, minZoom);
		this.zoom.setZ(new_zoom);
	}
	
	/**
	 * Enable Panning and Rotating of Camera
	 * 
	 * @param me mouse event
	 */
	public void drag(MouseEvent me) {
		// Mouse displacement while pressed and dragged
		double dx = + (mousePosX - me.getScreenX());
		double dy = - (mousePosY - me.getScreenY());

		// i.e. right mouse button
		if (me.isSecondaryButtonDown()) {

			// Rotate View
			double angleV = rotateV.getAngle() - (dy / 10 * +360) * (Math.PI / 180);
			double angleH = rotateH.getAngle() - (dx / 10 * -360) * (Math.PI / 180);
			angleV = ensureRange(angleV, -90, 90);
			this.rotateV.setAngle(angleV);
			this.rotateH.setAngle(angleH);

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
			this.pan.setX(panU);
			this.pan.setZ(panV);
		}

		// Set new mouse position
		this.mousePosX = me.getScreenX();
		this.mousePosY = me.getScreenY();
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
	public double RadianToDegree(float radian) {
		return (180 * radian / Math.PI)%360;
	}
	
	/**
	 * Convert degree value to radians
	 * 
	 * @param degree
	 * @return radians (0-2PI)
	 */
	public double DegreeToRadian(float radian) {
		return (Math.PI * radian / 180)%(2*Math.PI);
	}
}