package edu.mit.ira.fuzzy.fx.node;

import edu.mit.ira.fuzzy.base.ControlPoint;
import edu.mit.ira.fuzzy.base.Point;
import edu.mit.ira.fuzzy.base.Tile;
import edu.mit.ira.fuzzy.base.TileArray;
import edu.mit.ira.fuzzy.builder.DevelopmentEditor;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SubScene;
import javafx.scene.image.ImageView;
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
 * The View3D is the collection of "friendly model objects" 
 * to show in a 3D JavaFX Scene
 * 
 * @author Ira Winder
 *
 */
public class View3D {

	private Underlay map_model;
	
	// Includes generated geometry, settings, and 3D control points
	private DevelopmentEditor form_model;
	
	// Set of All Nodes to Pass to Parent JavaFX Scene
	private Group nodeSet;
	
	private Camera camera;
	private Color background;
	private Translate zoom;
	private Translate pan;
	private Rotate rotateV;
	private Rotate rotateH;

	/**
	 * ViewModel Constructor
	 */
	public View3D() {
		initModel();
	}
	
	public void initModel() {
		setBackground(Color.TRANSPARENT);
		setZoom(0);
		setPan(0,0,0);
		setRotateV(0);
		setRotateH(0);
		setUpCamera();
	}
	
	/**
	 * Zoom level of Camera
	 * 
	 * @param distance (more negative is farther away)
	 */
	public void setZoom(double distance) {
		this.zoom = new Translate(0, 0, ensureRange(distance, -2000, -100));
		setUpCamera();
	}
	
	/**
	 * Set Amount of panning from origin
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setPan(double x, double y, double z) {
		this.pan = new Translate(-x, z, y);
		setUpCamera();
	}
	
	/**
	 * Vertical Rotation (JavaFX uses X_AXIS)
	 * @param angle in degrees
	 */
	public void setRotateV(double angle) {
		this.rotateV = new Rotate(angle, Rotate.X_AXIS);
		setUpCamera();
	}
	
	/**
	 * Horizontal Rotation (JavaFX uses Y_AXIS)
	 * @param angle in degrees
	 */
	public void setRotateH(double angle) {
		this.rotateH = new Rotate(angle, Rotate.Y_AXIS);
		setUpCamera();
		
	}
	
	/**
	 * Initialize a camera for viewing the model
	 */
	public void setUpCamera() {
		Camera _camera = new PerspectiveCamera(true);
		_camera.getTransforms().addAll(rotateV, zoom);
		_camera.setNearClip(10);
		_camera.setFarClip(10000);
		this.camera = _camera;
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
		orientShape((Node) map_model.getImageView(), 0, 0, -0.4f);
	}
	
	/**
	 * Get Nodes, usually to pass to a JavaFX Scene
	 * @return
	 */
	public Group getGroup() {
		return nodeSet;
	}

	/**
	 * Get Camera to Add to a scene
	 * 
	 * @return Camera
	 */
	public Camera getCamera() {
		return camera;
	}

	public Color getBackground() {
		return background;
	}

	/**
	 * Populate 2D and 3D graphics objects 
	 * 
	 * @return a collection of JavaFX groups (3D objects)
	 */
	public void render() {
		
		this.nodeSet = new Group();
		
		// Set up Ambient Lighting Effects
		nodeSet.getChildren().add(overheadLight());
		nodeSet.getChildren().add(sideLight());
		
		// Add and orient map as Basemap to View Model
		ImageView map = map_model.getImageView();
		nodeSet.getChildren().add(map);

		// Draw Site Vector Polygon
		Color site_fill         = Color.hsb(0, 0, 0.95, 0.5);
		Color site_stroke       = Color.hsb(0, 0, 1.00, 0.5);
		float site_strokeWeight = 1.0f;
		if (form_model.showPolygons) {
			site_stroke         = Color.hsb(0, 0, 1.00, 1.0);
			site_strokeWeight   = 2.0f;
		}
		Polygon site_polygon = new Polygon();
		for (Point p : form_model.site_boundary.getCorners()) {
			site_polygon.getPoints().addAll(new Double[] { (double) p.x, (double) p.y });
		}
		site_polygon.setFill(site_fill);
		site_polygon.setStroke(site_stroke);
		site_polygon.setStrokeWidth(site_strokeWeight);
		orientShape((Node) site_polygon, 0, 0, -0.3f);
		nodeSet.getChildren().add(site_polygon);

		// Draw Voxel Bases
		for (TileArray space : form_model.spaceList("base")) {
			if(form_model.showSpace(space) && form_model.showTiles) {
				Color col = Color.hsb(space.getHueDegree(), 0.5, 0.8, 0.95);
				for (Tile t : space.tileList()) {
					// if in 2D view mode, Only draws ground plane 
					if (t.location.z == 0) {
						boolean isFlat = space.name.substring(0, 3).equals("Cou");
						Box b = renderVoxel(t, col, 0, isFlat);
						nodeSet.getChildren().add(b);
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
					Box b = renderVoxel(t, col, 0.1, true);
					nodeSet.getChildren().add(b);
					if (space.name.equals("Building")) {
						b = renderVoxel(t, col, 0.1, false);
						nodeSet.getChildren().add(b);
					}
				}
			}
		}
		
		// Draw Voxel Zones
		for (TileArray space : form_model.spaceList("zone")) {
			if(form_model.showSpace(space) && form_model.showTiles) {
				Color col = Color.hsb(space.getHueDegree(), 0.3, 0.9, 0.75);
				for (Tile t : space.tileList()) {
					Box b = renderVoxel(t, col, 0.2, false);
					nodeSet.getChildren().add(b);
				}
			}
		}
		
		// Draw Voxel Sites
		for (TileArray space : form_model.spaceList("site")) {
			if(form_model.showSpace(space) && form_model.showTiles) {
				Color col = Color.gray(0, 0.2);
				for (Tile t : space.tileList()) {
					Box b = renderVoxel(t, col, 0.3, true);
					nodeSet.getChildren().add(b);
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
			orientShape((Node)c, p.x, p.y, 0.2f);
			if (p.active()) nodeSet.getChildren().add(c);
			
			// Draw CrossHairs
			int size = 4;
			if (!p.active()) {
				control_stroke = Color.hsb(0, 0, 0, 0.1);
				size = 2;
			}
			Line l1 = new Line();
			l1.setStartX(p.x-size); l1.setStartY(p.y-size); l1.setEndX(p.x+size); l1.setEndY(p.y+size);
			l1.setFill(control_fill);
			l1.setStroke(control_stroke);
			l1.setStrokeWidth(control_strokeWidth);
			Line l2 = new Line();
			l2.setStartX(p.x-size); l2.setStartY(p.y+size); l2.setEndX(p.x+size); l2.setEndY(p.y-size);
			l2.setFill(control_fill);
			l2.setStroke(control_stroke);
			l2.setStrokeWidth(control_strokeWidth);
			orientShape((Node)l1, 0, 0, 0.2f);
			orientShape((Node)l2, 0, 0, 0.2f);
			nodeSet.getChildren().addAll(l1, l2);
		}
	}
	
	/**
	 * Construct a 3D Overhead Light Source
	 */
	private Node overheadLight() {
		PointLight light = new PointLight();
		light.setColor(Color.WHITE);
		light.getTransforms().add(new Translate(0, -400, 0));
		return light;
	}
	
	/**
	 * Construct a 3D Side Light Source
	 */
	private Node sideLight() {
		PointLight light = new PointLight();
		light.setColor(Color.WHITE);
		light.getTransforms().add(new Translate(-400, 0, -400));
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
				- t.location.z + z_offset - 0.5*boxH, 
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
	 * @param z_offset
	 */
	private void orientShape(Node input, float x_offset, float y_offset, float z_offset) {
		Rotate rotateFlat = new Rotate(-90, Rotate.X_AXIS);
		Translate pos = new Translate(x_offset, -z_offset, -y_offset);
		input.getTransforms().addAll(rotateH, pan, pos, rotateFlat);
	}

	// Mouse locations on Canvas
	private double mousePosX, mousePosY = 0;
	/**
	 * Actions to take when mouse events are detected
	 * 
	 * @param scene3d
	 */
	public void handleMouseEvents(SubScene scene3d) {
		
		// Load the mouse location on the scene while pressed down
		scene3d.setOnMousePressed((MouseEvent me) -> {
			mousePosX = me.getSceneX();
			mousePosY = me.getSceneY();
		});
		
		scene3d.setOnMouseDragged((MouseEvent me) -> {
			
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
				double dx_r = dx*Math.cos(angleH) - dy*Math.sin(angleH);
				double dy_r = dy*Math.cos(angleH) + dx*Math.sin(angleH);
				double panU = pan.getX() - dx_r;
				double panV = pan.getZ() - dy_r;
				pan.setX(panU);
				pan.setZ(panV);

			}
			mousePosX = me.getSceneX();
			mousePosY = me.getSceneY();
		});

		// Enable Zoom in and Zoom Out via scroll wheel
		scene3d.setOnScroll((ScrollEvent se) -> {
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