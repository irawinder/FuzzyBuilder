package edu.mit.ira.builder.fx;

import edu.mit.ira.builder.Builder;
import edu.mit.ira.voxel.Point;
import edu.mit.ira.voxel.Tile;
import edu.mit.ira.voxel.TileArray;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 * The ViewModel is the collection of "friendly model objects" 
 * to show in an JavaFX Scene
 * 
 * @author jiw
 *
 */
public class ViewModel {

	// Form Model Classes (by Ira)
	private Underlay map_model;
	private Builder form_model;
	
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
	public ViewModel() {
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
	public void setFormModel(Builder form_model) {
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
		orientShape((Node) map, -0.4f);
		nodeSet.getChildren().addAll(map);
		
		// Draw Site Vector Polygon
		Color fill       = Color.hsb(0, 0, 0.95, 0.5);
		Color stroke     = Color.hsb(0, 0, 1.00, 0.5);
		float strokeWeight = 1.0f;
		if (form_model.showPolygons) {
			stroke       = Color.hsb(0, 0, 1.00, 1.0);
			strokeWeight = 2.0f;
		}
		Polygon site_polygon = new Polygon();
		for(Point p : form_model.site_boundary.getCorners()) {
			site_polygon.getPoints().addAll(new Double[] {(double) p.x, (double) p.y});
		}
		site_polygon.setFill(fill);
		site_polygon.setStroke(stroke);
		site_polygon.setStrokeWidth(strokeWeight);
		orientShape((Node)site_polygon, -0.2f);
		nodeSet.getChildren().add(site_polygon);
				
		// Draw Voxel Bases
		for (TileArray space : form_model.dev.spaceList("base")) {
			if(form_model.showSpace(space) && form_model.showTiles) {
				Color col = Color.hsb(space.getHueDegree(), 0.5, 0.8, 0.95);
				for (Tile t : space.tileList()) {
					// if in 2D view mode, Only draws ground plane 
					if (form_model.cam3D || t.location.z == 0) {
						boolean isFlat = space.name.substring(0, 3).equals("Cou");
						Box b = renderVoxel(t, col, 0, isFlat);
						nodeSet.getChildren().add(b);
					}
				}
			}
		}

		// Draw Voxel Footprints
		for (TileArray space : form_model.dev.spaceList("footprint")) {
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
		for (TileArray space : form_model.dev.spaceList("zone")) {
			if(form_model.showSpace(space) && form_model.showTiles) {
				Color col = Color.hsb(space.getHueDegree(), 0.3, 0.9, 0.75);
				for (Tile t : space.tileList()) {
					Box b = renderVoxel(t, col, 0.2, false);
					nodeSet.getChildren().add(b);
				}
			}
		}
		
		// Draw Voxel Sites
		for (TileArray space : form_model.dev.spaceList("site")) {
			if(form_model.showSpace(space) && form_model.showTiles) {
				Color col = Color.gray(0, 0.2);
				for (Tile t : space.tileList()) {
					Box b = renderVoxel(t, col, 0.3, true);
					nodeSet.getChildren().add(b);
				}
			}
		}
			
		// Draw Tagged Control Points
		
		// Draw Tagged Control Point Labels
		
		// Draw Control Point at Mouse Hover
		
		// Draw Info at Mouse Hover
		
		// Draw Info/Instructions
		
		// Draw Attribute Summary
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
	private void orientShape(Node input, float z_offset) {
		Rotate rotateFlat = new Rotate(-90, Rotate.X_AXIS);
		Translate pos = new Translate(0, -z_offset, 0);
		input.getTransforms().addAll(rotateH, pan, pos, rotateFlat);
	}

	// Mouse locations on Canvas
	private double mousePosX, mousePosY = 0;
	/**
	 * Actions to take when mouse events are detected
	 * 
	 * @param scene
	 */
	public void handleMouseEvents(Scene scene) {
		
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