package edu.mit.ira.fuzzy.fx.scene;

import edu.mit.ira.fuzzy.base.ControlPoint;
import edu.mit.ira.fuzzy.base.Point;
import edu.mit.ira.fuzzy.base.Tile;
import edu.mit.ira.fuzzy.base.TileArray;
import edu.mit.ira.fuzzy.builder.DevelopmentEditor;
import edu.mit.ira.fuzzy.fx.node.Underlay;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
/**
 * Massing View Model
 *
 * @author Ira Winder
 *
 */
public class Massing extends SubScene implements ContentContainer {
	
	// TODO
	// Draw Control Point at Mouse Hover
	// Draw Tagged Control Point Labels
	// Draw Info at Mouse Hover
	// Draw Info/Instructions
	// Draw Attribute Summary
	
    // "Back End" Elements to Render to Container
    private DevelopmentEditor form_model; 
    private Underlay map_model;
    
    // "Front End" Nodes to Pass to Parent JavaFX Scene
 	private Group nodes3D, nodes2D;
 	
 	// Dynamic 3D View Parameters
 	private Camera camera;
 	private Color background;
 	private Translate zoom;
 	private Translate pan;
 	private Rotate rotateV;
 	private Rotate rotateH;
 	private double scaler;
 	boolean showUnderlay;
 	
 	// Default Values
 	private double DEFAULT_ZOOM = -1000;
 	private double DEFAULT_PAN_X = 325;
 	private double DEFAULT_PAN_Y = 425;
 	private double DEFAULT_ROTATE_V = -20;
 	private double DEFAULT_ROTATE_H = -45;
 	private double DEFAULT_NEAR_CLIP = 10;
 	private double DEFAULT_FAR_CLIP = 10000;
 	private double DEFAULT_SCALER = 1.0;
 	private double DEFAULT_LIGHT_DISPLACEMENT = 400;
 	private double DEFAULT_MAP_Z     = -0.4;
 	private double DEFAULT_POLY_Z    = -0.3;
 	private double DEFAULT_CONTROL_Z = -0.2;
 	private double DEFAULT_SITE_Z    = +0.0;
 	private double DEFAULT_ZONE_Z    = +0.1;
 	private double DEFAULT_FOOT_Z    = +0.2;
 	private double DEFAULT_BASE_Z    = +0.3;
 	
 	private double MIN_ZOOM = -100;
 	private double MAX_ZOOM = -2000;
 	
    /**
     * Initialize a new content container for Massing
     * 
     * @param form_model scenario geometry
     * @param map_model scenario basemap
     */
    public Massing() {
    	
    	// Initialize Container and Backend
    	super(EMPTY_GROUP, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    	
    	// Initialize Camera and Pan
    	scaler = DEFAULT_SCALER;
    	setZoom(scaler*DEFAULT_ZOOM);
    	setPan(scaler*DEFAULT_PAN_X, scaler*DEFAULT_PAN_Y, 0);
    	setRotateV(scaler*DEFAULT_ROTATE_V);
    	setRotateH(scaler*DEFAULT_ROTATE_H);
    	camera = new PerspectiveCamera(true);
		camera.getTransforms().addAll(rotateV, zoom);
		camera.setNearClip(scaler*DEFAULT_NEAR_CLIP);
		camera.setFarClip(scaler*DEFAULT_FAR_CLIP);
    	setBackground(Color.hsb(0,0,1.0));
    	showUnderlay = true;
    	
    	// Initialize Parent Nodes
    	nodes3D = new Group();
    	nodes2D = new Group();
        
    	// Initialize 3D SubScene
        SubScene scene3D = new SubScene(nodes3D, getWidth(), getHeight(), true, SceneAntialiasing.BALANCED);
    	scene3D.setFill(background);
        scene3D.setCamera(camera);
    	
        // Initialize Overlay
    	SubScene scene2D = new SubScene(nodes2D, getWidth(), getHeight());
        scene2D.setFill(Color.TRANSPARENT);
        
        // Bind dimensions of child scenes
        scene2D.widthProperty().bind(widthProperty());
        scene2D.heightProperty().bind(heightProperty());
        scene3D.widthProperty().bind(widthProperty());
        scene3D.heightProperty().bind(heightProperty());
        
        // Mouse and Keyboard Events
        handleMouseEvents(this);
        
        // Set the Parent Node of container content
        setRoot(new Group(scene3D, scene2D));
    }
    
    /**
     * Set the back end content of the model
     * 
     * @param form_model
     * @param map_model
     */
    public void render(DevelopmentEditor form_model, Underlay map_model) {
    	setFormModel(form_model);
    	setMapModel(map_model);
    	drawNodes();
    }

	public void keyPressed(KeyEvent e) {

		// Print Camera Position
		if (e.getCode() == KeyCode.C) {
			printCamera();
		}
		// Print Camera Position
		if (e.getCode() == KeyCode.U) {
			showUnderlay = !showUnderlay;
		}
	}
	
	/**
	 * Zoom level of Camera
	 * 
	 * @param distance (more negative is farther away)
	 */
	public void setZoom(double distance) {
		this.zoom = new Translate(0, 0, ensureRange(distance, MAX_ZOOM, MIN_ZOOM));
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

	/**
	 * Populate 2D and 3D graphics objects 
	 * 
	 * @return a collection of JavaFX groups (3D objects)
	 */
	public void drawNodes() {
		
		nodes2D.getChildren().clear();
		nodes3D.getChildren().clear();
		
		Label l = new Label("Massing Overlay");
    	nodes2D.getChildren().add(l);
		
		// Set up Ambient Lighting Effects
		nodes3D.getChildren().add(overheadLight());
		nodes3D.getChildren().add(sideLight());
		
		// Draw Voxel Bases
		for (TileArray space : form_model.spaceList("base")) {
			if(form_model.showSpace(space) && form_model.showTiles) {
				Color col = Color.hsb(space.getHueDegree(), 0.5, 0.8, 0.95);
				for (Tile t : space.tileList()) {
					// if in 2D view mode, Only draws ground plane 
					if (t.location.z == 0) {
						boolean isFlat = space.name.substring(0, 3).equals("Cou");
						Box b = renderVoxel(t, col, DEFAULT_BASE_Z, isFlat);
						nodes3D.getChildren().add(b);
					}
				}
			}
		}

		// Draw Voxel Footprints
		for (TileArray space : form_model.spaceList("footprint")) {
			if(form_model.showSpace(space) && form_model.showTiles) {
				Color col;
				if (space.name.equals("Building")) {
					col = Color.hsb(space.getHueDegree(), 0.5, 0.8);
				} else if (space.name.equals("Setback")) {
					col = Color.hsb(space.getHueDegree(), 0.3, 0.9, 0.75);
				} else {
					col = Color.hsb(space.getHueDegree(), 0.5, 0.8);
				}
				for (Tile t : space.tileList()) {
					Box b = renderVoxel(t, col, DEFAULT_FOOT_Z, true);
					nodes3D.getChildren().add(b);
					if (space.name.equals("Building")) {
						b = renderVoxel(t, col, DEFAULT_FOOT_Z, false);
						nodes3D.getChildren().add(b);
					}
				}
			}
		}
		
		// Draw Voxel Zones
		for (TileArray space : form_model.spaceList("zone")) {
			if(form_model.showSpace(space) && form_model.showTiles) {
				Color col = Color.hsb(space.getHueDegree(), 0.3, 0.9, 0.75);
				for (Tile t : space.tileList()) {
					Box b = renderVoxel(t, col, DEFAULT_ZONE_Z, true);
					nodes3D.getChildren().add(b);
				}
			}
		}
		
		// Draw Voxel Sites
		for (TileArray space : form_model.spaceList("site")) {
			if(form_model.showSpace(space) && form_model.showTiles) {
				Color col = Color.gray(0, 0.2);
				for (Tile t : space.tileList()) {
					Box b = renderVoxel(t, col, DEFAULT_SITE_Z, true);
					nodes3D.getChildren().add(b);
				}
			}
		}
		
		// Draw Tagged Control Points
		for (ControlPoint p : form_model.control.points()) {
			Color control_fill         = Color.TRANSPARENT;
			Color control_stroke       = Color.hsb(0, 0, 0, 0.50);
			float control_strokeWidth = 1.0f;
			
			//Draw Circle
			Circle c = new Circle();
			c.setRadius(5);
			c.setFill(control_fill);
			c.setStroke(control_stroke);
			c.setStrokeWidth(control_strokeWidth);
			orientShape((Node)c, scaler*p.x, scaler*p.y, DEFAULT_CONTROL_Z);
			if (p.active()) nodes3D.getChildren().add(c);
			
			// Draw CrossHairs
			int size = 4;
			if (!p.active()) {
				control_stroke = Color.hsb(0, 0, 0, 0.1);
				size = 2;
			}
			Line l1 = new Line();
			l1.setStartX(scaler*p.x-size); l1.setStartY(scaler*p.y-size); l1.setEndX(scaler*p.x+size); l1.setEndY(scaler*p.y+size);
			l1.setFill(control_fill);
			l1.setStroke(control_stroke);
			l1.setStrokeWidth(control_strokeWidth);
			Line l2 = new Line();
			l2.setStartX(scaler*p.x-size); l2.setStartY(scaler*p.y+size); l2.setEndX(scaler*p.x+size); l2.setEndY(scaler*p.y-size);
			l2.setFill(control_fill);
			l2.setStroke(control_stroke);
			l2.setStrokeWidth(control_strokeWidth);
			orientShape((Node)l1, 0, 0, DEFAULT_CONTROL_Z);
			orientShape((Node)l2, 0, 0, DEFAULT_CONTROL_Z);
			nodes3D.getChildren().addAll(l1, l2);
		}
		
		// Add and orient Underlay map (draw last to be able to see through bottom!)
		if (showUnderlay) {
			map_model.setImageView();
			orientShape((Node) map_model.getImageView(), 0, 0, DEFAULT_MAP_Z);
			map_model.setScale(scaler);
			nodes3D.getChildren().add(map_model.getImageView());
		}
		
		// Draw Site Vector Polygon
		Color site_fill         = Color.TRANSPARENT;
		Color site_stroke       = Color.hsb(0, 0, 0.1, 0.5);
		float site_strokeWeight = 1.0f;
		if (form_model.showPolygons) {
			site_stroke         = Color.hsb(0, 0, 0.1, 1.0);
			site_strokeWeight   = 2.0f;
		}
		Polygon site_polygon = new Polygon();
		for (Point p : form_model.site_boundary.getCorners()) {
			site_polygon.getPoints().addAll(new Double[] { (double) p.x, (double) p.y });
		}
		site_polygon.setFill(site_fill);
		site_polygon.setStroke(site_stroke);
		site_polygon.setStrokeWidth(site_strokeWeight);
		orientShape((Node) site_polygon, 0, 0, DEFAULT_POLY_Z);
		nodes3D.getChildren().add(site_polygon);
	}
	
	/**
	 * Construct a 3D Overhead Light Source
	 */
	private Node overheadLight() {
		PointLight light = new PointLight();
		light.setColor(Color.WHITE);
		light.getTransforms().add(new Translate(0, -scaler*DEFAULT_LIGHT_DISPLACEMENT, 0));
		return light;
	}
	
	/**
	 * Construct a 3D Side Light Source
	 */
	private Node sideLight() {
		PointLight light = new PointLight();
		light.setColor(Color.WHITE);
		light.getTransforms().add(new Translate(-scaler*DEFAULT_LIGHT_DISPLACEMENT, 0, -scaler*DEFAULT_LIGHT_DISPLACEMENT));
		return light;
	}
	
	/**
	 * Construct a 3D pixel (i.e. "Voxel") from tile attributes
	 * @param t Tile to render
	 * @param col Color of voxel
	 * @param z_offset manual z_offset for rendering some layers above others even when their z_attributes are the same
	 * @param flat if true, renders as a flat tile; if false, renders a 3D cuboid
	 * @return a JavaFX Box Node
	 */
	private Box renderVoxel(Tile t, Color col, double z_offset, boolean flat) {
		float scaler_uv = (float) 0.9;
		float scaler_w = (float) 0.8;

		float boxW = scaler_uv * t.scale_uv;
		float boxH = scaler_w * t.scale_w;
		if(flat) boxH = 0;
		Box b = new Box(boxW, boxH, boxW);

		Translate pos = new Translate(
				+ t.location.x, 
				- t.location.z - z_offset - 0.5*boxH, 
				- t.location.y);
		Rotate rot = new Rotate(RadianToDegree(form_model.tile_rotation), Rotate.Y_AXIS);
		b.getTransforms().addAll(rotateH, pan, pos, rot);

		// Box Color
		PhongMaterial material = new PhongMaterial(col);
		b.setMaterial(material);

		return b;
	}
	
	/**
	 * Make and position a 2D image or shape to the 3D environment
	 * 
	 * @param input
	 * @param f
	 */
	public void orientShape(Node input, double d, double e, double f) {
		Rotate rotateFlat = new Rotate(-90, Rotate.X_AXIS);
		Translate pos = new Translate(d, -f, -e);
		input.getTransforms().addAll(rotateH, pan, pos, rotateFlat);
	}

	// Mouse locations on Canvas
	private double mousePosX, mousePosY = 0;
	/**
	 * Actions to take when mouse events are detected
	 * 
	 * @param scene3d
	 */
	public void handleMouseEvents(SubScene scene) {
		
		// Load the mouse location on the scene while pressed down
		scene.setOnMousePressed((MouseEvent me) -> {
			mousePosX = me.getSceneX();
			mousePosY = me.getSceneY();
		});
		
		scene.setOnMouseDragged((MouseEvent me) -> {
			
			// Mouse displacement while pressed and dragged
			double dx = + (mousePosX - me.getSceneX());
			double dy = - (mousePosY - me.getSceneY());

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
				if (rotateV.getAngle() > 0) flip = -1;
				dx_r = + dx * Math.cos(angleH) - flip*dy * Math.sin(angleH);
				dy_r = + dx * Math.sin(angleH) + flip*dy * Math.cos(angleH);
				double panU = pan.getX() - dx_r;
				double panV = pan.getZ() - dy_r;
				pan.setX(panU);
				pan.setZ(panV);
			}
			
			// Set new mouse position
			mousePosX = me.getSceneX();
			mousePosY = me.getSceneY();
		});

		// Enable Zoom in and Zoom Out via scroll wheel
		scene.setOnScroll((ScrollEvent se) -> {
			double dy = se.getDeltaY();
			double new_zoom = zoom.getZ() - dy;
			new_zoom = ensureRange(new_zoom, -2000, -100);
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
	private double RadianToDegree(float radian) {
		return (180 * radian / Math.PI)%360;
	}
	
	/**
	 * Convert degree value to radians
	 * 
	 * @param degree
	 * @return radians (0-2PI)
	 */
	private double DegreeToRadian(float radian) {
		return (Math.PI * radian / 180)%(2*Math.PI);
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
}