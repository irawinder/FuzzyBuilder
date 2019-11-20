package edu.mit.ira.fuzzy.fx.scene.massing;

import java.util.HashMap;

import edu.mit.ira.fuzzy.base.ControlPoint;
import edu.mit.ira.fuzzy.base.Point;
import edu.mit.ira.fuzzy.base.Tile;
import edu.mit.ira.fuzzy.base.TileArray;
import edu.mit.ira.fuzzy.builder.DevelopmentEditor;
import edu.mit.ira.fuzzy.fx.node.Underlay;
import edu.mit.ira.fuzzy.fx.scene.Container3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

// TODO
// Draw Tagged Control Point Labels
// Draw Info at Mouse Hover
// Draw Info/Instructions
// Draw Attribute Summary

public class Massing extends Container3D {
    
    // Scale up/down draw units of back end geometry
    final private static double DEFAULT_VIEW_SCALER = 4.0;
    
 	// Default Elevation Values
 	final private static double DEFAULT_MAP_Z     	= - 0.4;
 	final private static double DEFAULT_POLY_Z   	= - 0.3;
 	final private static double DEFAULT_CONTROL_Z 	= + 1.0;
 	final private static double DEFAULT_SITE_Z    	= + 0.0;
 	final private static double DEFAULT_ZONE_Z    	= + 0.1;
 	final private static double DEFAULT_FOOT_Z    	= + 0.2;
 	final private static double DEFAULT_BASE_Z    	= + 0.3;
 	final private static double DEFAULT_GRID_Z    	= + 0.5;
 	
 	final private static Color ALPHA_COLOR 				= Color.gray(0, 0.5);
 	final private static Color GRAY_COLOR 				= Color.GRAY;
 	final private static Color ACTIVE_COLOR 			= Color.PURPLE;
 	final private static Color REMOVE_COLOR 			= Color.RED;
 	final private static Color ADD_COLOR 				= Color.rgb(0, 150, 0, 0.5);
 	
 	final private static PhongMaterial ALPHA_MATERIAL 	= new PhongMaterial(ALPHA_COLOR);
	final private static PhongMaterial GRAY_MATERIAL 	= new PhongMaterial(GRAY_COLOR);
 	final private static PhongMaterial ACTIVE_MATERIAL 	= new PhongMaterial(ACTIVE_COLOR);
 	final private static PhongMaterial REMOVE_MATERIAL 	= new PhongMaterial(REMOVE_COLOR);
 	final private static PhongMaterial ADD_MATERIAL 	= new PhongMaterial(ADD_COLOR);
 	
 	// Default Object Sizes
 	final protected static double DEFAULT_CONTROL_SIZE 	= 5.0; // e.g. radius of the control point sphere, in pixels
 	final protected static double HOVER_SIZE_SCALER 	= 1.2; // increase size of something when hovering
 	final protected static double VOXEL_HEIGHT_BUFFER 	= 0.9; // fraction of voxel to draw, leaving vertical gap between adjacent voxels
 	final protected static double VOXEL_WIDTH_BUFFER 	= 0.8; // fraction of voxel to draw, leaving horizontal gap between adjacent voxels
 	final protected static double GRID_UNIT_WIDTH 		= 1.0; // fraction of a TileArray() tile width
 	final protected static int	  GRID_UNIT_BLEED 		=  25; // number of selection grid units to bleed outside of Development extents
 	
    // "Back End" Elements to Render to Container
    protected DevelopmentEditor form_model; 
    protected Underlay map_model;
 	
    // "Front End" View Model Nodes
 	private Group light, control, ghost, grid, form, map, overlay;
  	
  	// Is A Control Point being Moved?
  	private boolean isMoving;
  	
  	// GridMap of geospatial point locations to front-end box element
  	private HashMap<Node, Point> gridMap;
  	
  	// GridMap of control spheres tied to their point location
  	private HashMap<ControlPoint, Node> controlMap;
  	
  	
 	
	public Massing() {
		super();
		
		cam.setViewScaler(DEFAULT_VIEW_SCALER);
		
		
		light = new Group();	// Lighting Effect Nodes
		control = new Group();	// Control Point Nodes
		ghost = new Group();	// Ghost Control Point (to be placed at mouse)
		grid = new Group();		// Baseline Grid Square Nodes for 3D XY selection
		form = new Group();		// Form Model Voxel Nodes (i.e. "The Development")
		map = new Group();		// Raster basemap image
		overlay = new Group();	// 2D UI overlay
		
    	nodes2D.getChildren().addAll(overlay);
    	nodes3D.getChildren().addAll(control, ghost, grid, light, form, map);
    	
		isMoving = false;
		gridMap = new HashMap<Node, Point>();
		controlMap = new HashMap<ControlPoint, Node>();
		
	}
	
    /**
     * Set the back end content of the model
     * 
     * @param form_model
     * @param map_model
     */
    public void init(DevelopmentEditor form_model, Underlay map_model) {
    	setFormModel(form_model);
    	setMapModel(map_model);
    	this.init();
	}
    
	/**
	 * Populates the View Model with a form from Builder class
	 * 
	 * @param form form from Builder class
	 */
	private void setFormModel(DevelopmentEditor form_model) {
		this.form_model = form_model;
	}
	
	/**
	 * Populates the View Model with a map from Underlay class
	 * 
	 * @param map_model Map Underlay passed from Underlay class
	 */
	private void setMapModel(Underlay map_model) {
		this.map_model  = map_model;
	}
    
	/**
	 * render the current state of the backend model
	 */
	private void init() {
		initLights();
		initControl();
		initGhost();
		initGrid();
    	initForm();
    	initMap();
    	initOverlay();
    	
		handleMouseEvents();
	}
	
	/**
	 * Initialize all event handlers related to scene-wide mouse events
	 */
	private void handleMouseEvents() {
		
		this.setOnMouseMoved((MouseEvent me) -> {
			cam.move(me);
			updateHover(me);
		});
		
		this.setOnMouseDragged((MouseEvent me) -> {
			if (form_model.selected == null) {
				cam.drag(me);
			}
		});
		
		this.setOnScroll((ScrollEvent se) -> {
			cam.zoom(se);
		});
		
		this.setOnMousePressed((MouseEvent me) -> {
			if (form_model.addPoint) {
				if (ghost.isVisible()) addPointAtMouse(me);
			} else if (form_model.removePoint) {
			
			// Done Moving a Point
			} else {
				// If the control point is point moved, deselect it
				if (isMoving) {
					form_model.deselect();
					isMoving = false;
				}
			}
		});
	}
	
    /**
     * Initialize view model for ControlPoints. These nodes are designed to persist for any given scenario.
     */
    private void initControl() {
    	control.getChildren().clear();
    	controlMap.clear();
    	
		// Draw Active Control Points' Inner Sphere
		if (form_model.isEditing()) {
			
			for (ControlPoint p : form_model.control.points()) {
				if (p.active()) {
					
					// Draw Control Point Sphere
					Sphere s = new Sphere();
					s.setMaterial(ACTIVE_MATERIAL);
					s.setRadius(cam.scaler() * DEFAULT_CONTROL_SIZE);
					s.setId("active_control_point");
					orientShape3D((Node) s, cam.scaler() * p.x, cam.scaler() * p.y, DEFAULT_CONTROL_Z);
					
					// Draw Control Point Outer Ring
					Cylinder or = new Cylinder();
					or.setRadius(cam.scaler() * HOVER_SIZE_SCALER * HOVER_SIZE_SCALER * DEFAULT_CONTROL_SIZE);
					or.setHeight(0);
					or.setMaterial(GRAY_MATERIAL);
					orientShape3D((Node) or, cam.scaler() * p.x, cam.scaler() * p.y, DEFAULT_CONTROL_Z);
					
					// Aggregated Control Point View Model
					Group cPoint = new Group(s, or);
					control.getChildren().add(cPoint);
					controlMap.put(p, cPoint);
					
					// Mouse Events for Control Pointers
					Point newPointAtMouse = null; // not applicable here
					ControlPoint existingPointAtMouse = p;
					boolean mousePressed = true;
					s.setOnMouseEntered(me -> {
						if (form_model.removePoint) {
							s.setMaterial(REMOVE_MATERIAL);
						} else {
							s.setMaterial(ACTIVE_MATERIAL);
						}
						s.setRadius(cam.scaler() * HOVER_SIZE_SCALER * DEFAULT_CONTROL_SIZE);
						form_model.listen(!mousePressed, existingPointAtMouse, newPointAtMouse);
						form_model.updateModel();
						ghost.setVisible(false);
					});
					s.setOnMouseExited(me -> {
						s.setMaterial(ACTIVE_MATERIAL);
						s.setRadius(cam.scaler() * DEFAULT_CONTROL_SIZE);
						form_model.listen(!mousePressed, existingPointAtMouse, newPointAtMouse);
						form_model.updateModel();
					});
					s.setOnMousePressed((MouseEvent me) -> {
						s.setRadius(cam.scaler() * DEFAULT_CONTROL_SIZE);
						form_model.mouseTrigger(newPointAtMouse);
						form_model.updateModel();
						ghost.setVisible(false);
						initGrid();
						initControl();
						initForm();
					});
				}
			}
		}
    }
    
    /**
     * Initialize Ghost of a Control Point
     */
    public void initGhost() {
    	ghost.getChildren().clear();
    	
    	// Draw Control Point Hover Sphere
		Sphere s = new Sphere();
		s.setMaterial(ADD_MATERIAL);
		s.setRadius(cam.scaler() * DEFAULT_CONTROL_SIZE);
		
		// Draw Control Point Hover Ring
		Cylinder r = new Cylinder();
		r.setMaterial(ALPHA_MATERIAL);
		r.setRadius(cam.scaler() * HOVER_SIZE_SCALER * HOVER_SIZE_SCALER * DEFAULT_CONTROL_SIZE);
		r.setHeight(0);
		
		Group hoverPoint = new Group(r, s);
		ghost.getChildren().add(hoverPoint);
		ghost.setVisible(false);
    }
    
    /**
	 * Initialize a structured grid of points that a user might select from
	 */
	public void initGrid() {
		grid.getChildren().clear();
		gridMap.clear();

		if (form_model.isEditing()) {
			double interval = cam.scaler() * GRID_UNIT_WIDTH * form_model.getTileWidth();
			double minX = cam.scaler() * form_model.minControlX() - GRID_UNIT_BLEED * interval;
			double maxX = cam.scaler() * form_model.maxControlX() + GRID_UNIT_BLEED * interval;
			double minY = cam.scaler() * form_model.minControlY() - GRID_UNIT_BLEED * interval;
			double maxY = cam.scaler() * form_model.maxControlY() + GRID_UNIT_BLEED * interval;
			double boxW = interval;
			double boxH = 0;
			double boxZ = 0;
			Color col = Color.gray(SUBDUED_SATURATION, SUBTLE_ALPHA);
			for (double x = minX + interval / 2; x < maxX; x += interval) {
				for (double y = minY + interval / 2; y < maxY; y += interval) {
					double boxX = x;
					double boxY = y;
					Box b = basicBox(boxX, boxY, boxZ, boxW, boxH, DEFAULT_GRID_Z, col);
					b.setMaterial(new PhongMaterial(Color.TRANSPARENT));
					b.setId("grid");

					// Mouse Events for Control Pointers
					ControlPoint existingPointAtMouse = null; // not applicable here
					boolean mousePressed = true;
					float locX = (float) (boxX / cam.scaler() + 0.01f * Math.random());
					float locY = (float) (boxY / cam.scaler() + 0.01f * Math.random());
					b.setOnMouseEntered(me -> {
						Point newPointAtMouse = new Point(locX, locY);
						form_model.listen(!mousePressed, existingPointAtMouse, newPointAtMouse);
						form_model.updateModel();
						gridMap.put(b, newPointAtMouse);

						// Set Ghost for new Control Point
						if (form_model.addPoint) {
							ghost.setVisible(true);
							orientShape3D((Node) ghost, cam.scaler() * newPointAtMouse.x,
									cam.scaler() * newPointAtMouse.y, DEFAULT_CONTROL_Z);
						}
					
						// Move point around after clicking it
						if (form_model.selected != null) {
							isMoving = true;
							Point new_location = gridMap.get(b);
							if (new_location != null) {
								form_model.selected.x = newPointAtMouse.x;
								form_model.selected.y = newPointAtMouse.y;
							}
							form_model.detectChange(form_model.selected.getType());
							form_model.updateModel();
							if (form_model.selected != null) {
								Group cPoint = (Group) controlMap.get(form_model.selected);
								if (cPoint != null) {
									for (int i = 0; i < cPoint.getChildren().size(); i++) {
										Node n = cPoint.getChildren().get(i);
										orientShape3D(n, cam.scaler() * newPointAtMouse.x,
												cam.scaler() * newPointAtMouse.y, DEFAULT_CONTROL_Z);
									}
								}
							}
							initForm();
						}
					});
					grid.getChildren().add(b);
				}
			}
		}
	}
    
	/**
	 * Set up Ambient Lighting Effects
	 * 
	 * @param lights
	 */
	public void initLights() {
		light.getChildren().clear();
		light.getChildren().add(overheadLight());
		light.getChildren().add(sideLight());
	}
	
	/**
	 * Initialize view model of form. Don't count on anything in here to persist longer than a few frames
	 */
	public void initForm() {
		form.getChildren().clear();
		
		// Extra opacity to apply when editing control points
		double editingScaler = 1.0;
		if (form_model.isEditing()) editingScaler = 0.5;
		
		// Draw Site Vector Polygon
		Color site_fill         = Color.TRANSPARENT;
		Color site_stroke       = Color.gray(SUBDUED_SATURATION, 0.25*SUBTLE_ALPHA);
		double site_strokeWeight = 0 * cam.scaler() * SUBDUED_STROKE;
		if (form_model.showPolygons) {
			site_stroke         = Color.gray(SUBDUED_SATURATION, 0.5*SUBTLE_ALPHA);
			site_strokeWeight   = 2 * cam.scaler() * DEFAULT_STROKE;
		}
		Polygon site_polygon = new Polygon();
		for (Point p : form_model.site_boundary.getCorners()) {
			site_polygon.getPoints().addAll(new Double[] { (double) cam.scaler()*p.x, (double) cam.scaler()*p.y });
		}
		site_polygon.setFill(site_fill);
		site_polygon.setStroke(site_stroke);
		site_polygon.setStrokeWidth(site_strokeWeight);
		orientShape2D((Node) site_polygon, 0, 0, DEFAULT_POLY_Z);
		form.getChildren().add(site_polygon);
		
		// Draw Voxel Sites
		for (TileArray space : form_model.spaceList("site")) {
			if(form_model.showSpace(space) && form_model.showTiles) {
				Color col = Color.gray(DEFAULT_SATURATION, editingScaler * SUBDUED_ALPHA);
				for (Tile t : space.tileList()) {
					Box b = renderVoxel(t, col, DEFAULT_SITE_Z, true);
					form.getChildren().add(b);
				}
			}
		}
		
		// Draw Voxel Zones
		for (TileArray space : form_model.spaceList("zone")) {
			if(form_model.showSpace(space) && form_model.showTiles) {
				Color col = Color.hsb(space.getHueDegree(), SUBDUED_SATURATION, SUBDUED_BRIGHTNESS, DEFAULT_ALPHA);
				for (Tile t : space.tileList()) {
					Box b = renderVoxel(t, col, DEFAULT_ZONE_Z, true);
					form.getChildren().add(b);
				}
			}
		}
		
		// Draw Voxel Footprints
		for (TileArray space : form_model.spaceList("footprint")) {
			if(form_model.showSpace(space) && form_model.showTiles) {
				Color col = Color.hsb(space.getHueDegree(), DEFAULT_SATURATION, DEFAULT_BRIGHTNESS, DEFAULT_ALPHA);
				if (space.name.equals("Setback"))
					col = Color.hsb(space.getHueDegree(), DEFAULT_SATURATION, SUBTLE_BRIGHTNESS, SUBTLE_ALPHA);
				if (space.name.equals("Void"))
					col = Color.hsb(space.getHueDegree(), DEFAULT_SATURATION, SUBDUED_BRIGHTNESS, SUBDUED_ALPHA);
				for (Tile t : space.tileList()) {
					Box b = renderVoxel(t, col, DEFAULT_FOOT_Z, true);
					form.getChildren().add(b);
					if (space.name.equals("Building")) {
						b = renderVoxel(t, col, DEFAULT_FOOT_Z, true);
						form.getChildren().add(b);
					}
				}
			}
		}
		
		// Draw Voxel Bases
		for (TileArray space : form_model.spaceList("base")) {
			if(form_model.showSpace(space) && form_model.showTiles) {
				Color col = Color.hsb(space.getHueDegree(), DEFAULT_SATURATION, DEFAULT_BRIGHTNESS, editingScaler * DEFAULT_ALPHA);
				for (Tile t : space.tileList()) {
					// if in 2D view mode, Only draws ground plane 
					if (t.location.z == 0) {
						boolean isFlat = space.name.substring(0, 3).equals("Cou"); // short for "Courtyard"
						Box b = renderVoxel(t, col, DEFAULT_BASE_Z, isFlat);
						form.getChildren().add(b);
					}
				}
			}
		}
	}
	
	public void initMap() {
		map.getChildren().clear();
		
		// Add and orient Underlay map (draw last to be able to see through bottom!)
		map_model.setImageView();
		orientShape2D((Node) map_model.getImageView(), 0, 0, DEFAULT_MAP_Z);
		map_model.setScale(cam.scaler() * map_model.getScaler());
		map_model.getImageView().setMouseTransparent(true);
		map.getChildren().add(map_model.getImageView());
	}
	
	/**
	 * Initialize 2D Overlay
	 */
	public void initOverlay() {
		overlay.getChildren().clear();
		
		// Placeholder for 2D overlay
		Label l = new Label("Massing Editor");
    	overlay.getChildren().add(l);
	}
	
	/**
	 * Construct a 3D pixel (i.e. "Voxel") from tile attributes
	 * 
	 * @param t Tile to render
	 * @param col Color of voxel
	 * @param z_offset manual z_offset for rendering some layers above others even when their z_attributes are the same
	 * @param flat if true, renders as a flat tile; if false, renders a 3D cuboid
	 * @return a JavaFX Box Node
	 */
	private Box renderVoxel(Tile t, Color col, double z_offset, boolean flat) {
		double boxW = cam.scaler() * VOXEL_HEIGHT_BUFFER * t.scale_uv;
		double boxH = cam.scaler() * VOXEL_WIDTH_BUFFER * t.scale_w;
		double boxX = cam.scaler() * t.location.x;
		double boxY = cam.scaler() * t.location.y;
		double boxZ = cam.scaler() * t.location.z;
		if(flat) boxH = 0;
		Rotate rot = new Rotate(cam.RadianToDegree(form_model.tile_rotation), Rotate.Y_AXIS);
		Box b = basicBox(boxX, boxY, boxZ, boxW, boxH, z_offset, col);
		b.getTransforms().add(rot);
		b.setId("voxel");
		
		// Sets Transparent to Mouse Events when Editing
		if (form_model.isEditing()) b.setMouseTransparent(true);

		return b;
	}
	
    /**
     * Update hovering visualization when mouse is moved
     * 
     * @param me mouse event
     */
    public void updateHover(MouseEvent me) {
    	// Hide hovering sphere when not over valid spot
		Node intersected = me.getPickResult().getIntersectedNode();
		String id = intersected.getId();
		if (id != null) {
			if (id.equals("scene3D")) {
				ghost.setVisible(false);
			}
		}
    }
    
    /**
     * Add Control Point where mouse is
     * 
     * @param me mouse event
     */
    public void addPointAtMouse(MouseEvent me) {
    	boolean mousePressed = true;
		ControlPoint existingPointAtMouse = null; // not applicable here
    	if (form_model.hovering != null && form_model.addPoint) {
			Point newPointAtMouse = form_model.hovering;
			form_model.mouseTrigger(newPointAtMouse);
			form_model.listen(mousePressed, existingPointAtMouse, newPointAtMouse);
			form_model.updateModel();
			this.init(form_model, map_model);
		}
    }
    
	/**
	 * Key commands that effect the container
	 * 
	 * @param e key event
	 */
	public void keyPressed(KeyEvent e) {

		// Reset Camera Position
		if (e.getText().equals("C")) {
			cam.init();
		}
		// toggle map model visibility
		if (e.getCode() == KeyCode.U) {
			map_model.setVisible(!map_model.isVisible());
		}
		// Torn Editing on/off
		if (e.getCode() == KeyCode.E) {
			
		}
		initControl();
		initGrid();
		initGhost();
		initForm();
	}
}
