package edu.mit.ira.fuzzy.fx.scene.massing;

import java.util.HashMap;

import edu.mit.ira.fuzzy.base.ControlPoint;
import edu.mit.ira.fuzzy.base.Point;
import edu.mit.ira.fuzzy.base.Tile;
import edu.mit.ira.fuzzy.base.TileArray;
import edu.mit.ira.fuzzy.builder.DevelopmentEditor;
import edu.mit.ira.fuzzy.fx.node.Underlay;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PointLight;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Massing extends MassingContainer {
 	
	Group nodesControl, nodesForm;
	
 	// Default Elevation Values
 	final protected static double DEFAULT_LIGHT_DISPLACEMENT = 400;
 	final protected static double DEFAULT_MAP_Z     = - 0.4;
 	final protected static double DEFAULT_POLY_Z    = - 0.3;
 	final protected static double DEFAULT_CONTROL_Z = + 1.0;
 	final protected static double DEFAULT_SITE_Z    = + 0.0;
 	final protected static double DEFAULT_ZONE_Z    = + 0.1;
 	final protected static double DEFAULT_FOOT_Z    = + 0.2;
 	final protected static double DEFAULT_BASE_Z    = + 0.3;
 	final protected static double DEFAULT_GRID_Z    = + 0.5;
 	
 	// Default color and stroke values
 	final protected static double DEFAULT_SATURATION = 0.50;
 	final protected static double DEFAULT_BRIGHTNESS = 0.75;
 	final protected static double DEFAULT_ALPHA = 0.90;
 	final protected static double SUBDUED_SATURATION = 0.30;
 	final protected static double SUBDUED_BRIGHTNESS = 0.75;
 	final protected static double SUBDUED_ALPHA = 0.75;
 	final protected static double SUBTLE_SATURATION = 0.10;
 	final protected static double SUBTLE_BRIGHTNESS = 0.75;
 	final protected static double SUBTLE_ALPHA = 0.5;
 	final protected static double DEFAULT_STROKE = 1.0;
 	final protected static double SUBDUED_STROKE = 2.0;
 	final protected static Color DEFAULT_CONTROL_FILL = Color.TRANSPARENT;
 	final protected static Color DEFAULT_CONTROL_STROKE = Color.gray(SUBDUED_SATURATION, SUBTLE_ALPHA);
 	final protected static Color ACTIVE_COLOR = Color.PURPLE;
 	final protected static Color REMOVE_COLOR = Color.RED;
 	final protected static Color ADD_COLOR = Color.GREEN;
 	final private static PhongMaterial ACTIVE_MATERIAL = new PhongMaterial(ACTIVE_COLOR);
 	final private static PhongMaterial REMOVE_MATERIAL = new PhongMaterial(REMOVE_COLOR);
 	final private static PhongMaterial ADD_MATERIAL = new PhongMaterial(ADD_COLOR);
 	
 	// Default Object Sizes
 	final protected static double DEFAULT_CONTROL_SIZE 	= 5.0; // e.g. radius of the control point sphere, in pixels
 	final protected static double HOVER_SIZE_SCALER 	= 1.5; // increase size of something when hovering
 	final protected static double VOXEL_HEIGHT_BUFFER 	= 0.9; // fraction of voxel to draw, leaving vertical gap between adjacent voxels
 	final protected static double VOXEL_WIDTH_BUFFER 	= 0.8; // fraction of voxel to draw, leaving horizontal gap between adjacent voxels
 	final protected static double GRID_UNIT_WIDTH = 0.50; // fraction of a TileArray() tile width
 	final protected static int	  GRID_UNIT_BLEED =   50; // number of selection grid units to bleed outside of Development extents
 	
 	// Global Objects
  	private Sphere hover;
  	private boolean dragged;
  	
  	// GridMap of geospatial point locations to front-end box element
  	HashMap<Node, Point> gridMap;
  	
  	// GridMap of control spheres tied to their point location
  	HashMap<ControlPoint, Node> controlMap;
 	
	public Massing() {
		super();
		nodesControl = new Group();
		nodesForm = new Group();
		hover = new Sphere();
		dragged = false;
		gridMap = new HashMap<Node, Point>();
		controlMap = new HashMap<ControlPoint, Node>();
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
    	drawControl();
    	drawForm();
    	nodes3D.getChildren().clear();
    	nodes3D.getChildren().addAll(nodesForm, nodesControl);
    	
		setOnMouseMoved((MouseEvent me) -> {
			updateMouseLocation(me);
			updateHover(me);
		});
		
		setOnMouseDragged((MouseEvent me) -> {
			if (form_model.selected == null) {
				dragCamera(me);
			}
			dragged = true;
		});
		
		setOnScroll((ScrollEvent se) -> {
			zoomCamera(se);
		});
		
		setOnMousePressed((MouseEvent me) -> {
			dragged = false;
		});
		
		setOnMouseReleased((MouseEvent me) -> {
//			if (hover.isVisible() && !dragged) {
			if (hover.isVisible()) {
				addPointAtMouse(me);
			}
			form_model.deselect();
			dragged = false;
		});
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
				hover.setVisible(false);
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
			render(form_model, map_model);
		}
    }
	
    public void drawControl() {
    	
    	nodesControl.getChildren().clear();
    	
		// Draw Active Control Points' Inner Sphere
		if (form_model.isEditing()) {
			controlMap.clear();
			for (ControlPoint p : form_model.control.points()) {
				if (p.active()) {
					Sphere is = new Sphere();
					is.setMaterial(ACTIVE_MATERIAL);
					is.setRadius(DEFAULT_SCALER * DEFAULT_CONTROL_SIZE);
					is.setId("active_control_point");
					orientShape((Node) is, viewScaler * p.x, viewScaler * p.y, DEFAULT_CONTROL_Z + 0.1);
					nodesControl.getChildren().add(is);
					controlMap.put(p, is);
					
					// Mouse Events for Control Pointers
					Point newPointAtMouse = null; // not applicable here
					ControlPoint existingPointAtMouse = p;
					boolean mousePressed = true;
					is.setOnMouseEntered(me -> {
						if (form_model.removePoint) is.setMaterial(REMOVE_MATERIAL);
						is.setRadius(HOVER_SIZE_SCALER * DEFAULT_SCALER * DEFAULT_CONTROL_SIZE);
						form_model.listen(!mousePressed, existingPointAtMouse, newPointAtMouse);
						form_model.updateModel();
						hover.setVisible(false);
					});
					is.setOnMouseExited(me -> {
						is.setMaterial(ACTIVE_MATERIAL);
						is.setRadius(DEFAULT_SCALER * DEFAULT_CONTROL_SIZE);
						form_model.listen(!mousePressed, existingPointAtMouse, newPointAtMouse);
						form_model.updateModel();
					});
					is.setOnMousePressed((MouseEvent me) -> {
						if (form_model.removePoint) is.setMaterial(REMOVE_MATERIAL);
						is.setRadius(DEFAULT_SCALER * DEFAULT_CONTROL_SIZE);
						form_model.listen(!mousePressed, existingPointAtMouse, newPointAtMouse);
						form_model.updateModel();
						form_model.selected = existingPointAtMouse;
						hover.setVisible(false);
					});
					is.setOnMouseReleased((MouseEvent me) -> {
//						if (!dragged) {
							form_model.mouseTrigger(newPointAtMouse);
							form_model.deselect();
							form_model.listen(mousePressed, existingPointAtMouse, newPointAtMouse);
							form_model.updateModel();
							render(form_model, map_model);
//						}
					});
//					source.setOnDragDetected(new EventHandler <MouseEvent>() {
//			            public void handle(MouseEvent event) {
//			                /* drag was detected, start drag-and-drop gesture*/
//			                System.out.println("onDragDetected");
//			                
//			                /* allow any transfer mode */
//			                Dragboard db = source.startDragAndDrop(TransferMode.ANY);
//			                
//			                /* put a string on dragboard */
//			                ClipboardContent content = new ClipboardContent();
//			                content.putString(source.getText());
//			                db.setContent(content);
//			                
//			                event.consume();
//			            }
//			        });
//
//			        target.setOnDragOver(new EventHandler <DragEvent>() {
//			            public void handle(DragEvent event) {
//			                /* data is dragged over the target */
//			                System.out.println("onDragOver");
//			                
//			                /* accept it only if it is  not dragged from the same node 
//			                 * and if it has a string data */
//			                if (event.getGestureSource() != target &&
//			                        event.getDragboard().hasString()) {
//			                    /* allow for both copying and moving, whatever user chooses */
//			                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
//			                }
//			                
//			                event.consume();
//			            }
//			        });
//
//			        target.setOnDragEntered(new EventHandler <DragEvent>() {
//			            public void handle(DragEvent event) {
//			                /* the drag-and-drop gesture entered the target */
//			                System.out.println("onDragEntered");
//			                /* show to the user that it is an actual gesture target */
//			                if (event.getGestureSource() != target &&
//			                        event.getDragboard().hasString()) {
//			                    target.setFill(Color.GREEN);
//			                }
//			                
//			                event.consume();
//			            }
//			        });
//
//			        target.setOnDragExited(new EventHandler <DragEvent>() {
//			            public void handle(DragEvent event) {
//			                /* mouse moved away, remove the graphical cues */
//			                target.setFill(Color.BLACK);
//			                
//			                event.consume();
//			            }
//			        });
//			        
//			        target.setOnDragDropped(new EventHandler <DragEvent>() {
//			            public void handle(DragEvent event) {
//			                /* data dropped */
//			                System.out.println("onDragDropped");
//			                /* if there is a string data on dragboard, read it and use it */
//			                Dragboard db = event.getDragboard();
//			                boolean success = false;
//			                if (db.hasString()) {
//			                    target.setText(db.getString());
//			                    success = true;
//			                }
//			                /* let the source know whether the string was successfully 
//			                 * transferred and used */
//			                event.setDropCompleted(success);
//			                
//			                event.consume();
//			            }
//			        });
//
//			        source.setOnDragDone(new EventHandler <DragEvent>() {
//			            public void handle(DragEvent event) {
//			                /* the drag-and-drop gesture ended */
//			                System.out.println("onDragDone");
//			                /* if the data was successfully moved, clear it */
//			                if (event.getTransferMode() == TransferMode.MOVE) {
//			                    source.setText("");
//			                }
//			                
//			                event.consume();
//			            }
//			        });
				}
			}
		}
		
		// Initialize Ghosts Control Point
		hover.setMaterial(ACTIVE_MATERIAL);
		hover.setRadius(DEFAULT_SCALER * DEFAULT_CONTROL_SIZE);
		hover.setId("hover_control_point");
		hover.setVisible(false);
		nodesControl.getChildren().add(hover);

		// Add Theoretical Control Point Grid Space
		if (form_model.isEditing()) nodesControl.getChildren().addAll(nodeGrid());
    }
    
	/**
	 * Populate 2D and 3D graphics objects 
	 * 
	 * @return a collection of JavaFX groups (3D objects)
	 */
	public void drawForm() {
		
		nodes2D.getChildren().clear();
		nodesForm.getChildren().clear();
		
		// Placeholder for 2D overlay
		Label l = new Label("Massing Editor");
    	nodes2D.getChildren().add(l);
		
		// Set up Ambient Lighting Effects
		nodesForm.getChildren().add(overheadLight());
		nodesForm.getChildren().add(sideLight());
		
		// Extra opacity to apply when editing control points
		double editingScaler = 1.0;
		if (form_model.isEditing()) editingScaler = 0.5;
		
		// Draw Site Vector Polygon
		Color site_fill         = Color.TRANSPARENT;
		Color site_stroke       = Color.gray(SUBDUED_SATURATION, 0.25*SUBTLE_ALPHA);
		double site_strokeWeight = 0 * DEFAULT_SCALER * DEFAULT_STROKE;
		if (form_model.showPolygons) {
			site_stroke         = Color.gray(SUBDUED_SATURATION, 0.5*SUBTLE_ALPHA);
			site_strokeWeight   = 2 * DEFAULT_SCALER * SUBDUED_STROKE;
		}
		Polygon site_polygon = new Polygon();
		for (Point p : form_model.site_boundary.getCorners()) {
			site_polygon.getPoints().addAll(new Double[] { (double) viewScaler*p.x, (double) viewScaler*p.y });
		}
		site_polygon.setFill(site_fill);
		site_polygon.setStroke(site_stroke);
		site_polygon.setStrokeWidth(site_strokeWeight);
		orientShape((Node) site_polygon, 0, 0, DEFAULT_POLY_Z);
		nodesForm.getChildren().add(site_polygon);
		
		// Draw Voxel Sites
		for (TileArray space : form_model.spaceList("site")) {
			if(form_model.showSpace(space) && form_model.showTiles) {
				Color col = Color.gray(DEFAULT_SATURATION, editingScaler * SUBDUED_ALPHA);
				for (Tile t : space.tileList()) {
					Box b = renderVoxel(t, col, DEFAULT_SITE_Z, true);
					nodesForm.getChildren().add(b);
				}
			}
		}
		
		// Draw Voxel Zones
		for (TileArray space : form_model.spaceList("zone")) {
			if(form_model.showSpace(space) && form_model.showTiles) {
				Color col = Color.hsb(space.getHueDegree(), SUBDUED_SATURATION, SUBDUED_BRIGHTNESS, DEFAULT_ALPHA);
				for (Tile t : space.tileList()) {
					Box b = renderVoxel(t, col, DEFAULT_ZONE_Z, true);
					nodesForm.getChildren().add(b);
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
					nodesForm.getChildren().add(b);
					if (space.name.equals("Building")) {
						b = renderVoxel(t, col, DEFAULT_FOOT_Z, true);
						nodesForm.getChildren().add(b);
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
						nodesForm.getChildren().add(b);
					}
				}
			}
		}
		
		// Draw Control Point Circles
		if(form_model.viewState != 5 && form_model.isEditing()) {
			for (ControlPoint p : form_model.control.points()) {
				if (p.active()) {
					Circle oc = new Circle();
					oc.setRadius(HOVER_SIZE_SCALER * DEFAULT_SCALER * DEFAULT_CONTROL_SIZE);
					oc.setFill(DEFAULT_CONTROL_FILL);
					oc.setStroke(DEFAULT_CONTROL_STROKE);
					oc.setStrokeWidth(DEFAULT_SCALER * SUBDUED_STROKE);
					orientShape((Node) oc, viewScaler * p.x, viewScaler * p.y, DEFAULT_CONTROL_Z);
					nodesForm.getChildren().add(oc);
				}
			}
		}
		
		// Add and orient Underlay map (draw last to be able to see through bottom!)
		if (showUnderlay) {
			map_model.setImageView();
			orientShape((Node) map_model.getImageView(), 0, 0, DEFAULT_MAP_Z);
			map_model.setScale(viewScaler*map_model.getScaler());
			map_model.getImageView().setMouseTransparent(true);
			nodesForm.getChildren().add(map_model.getImageView());
		}
		
	}
	
	/**
	 * Draw a structured grid of points that a user might select from
	 * 
	 * @return grid of points
	 */
	public Group nodeGrid() {
		Group grid = new Group();
		gridMap.clear();
		double interval = viewScaler * GRID_UNIT_WIDTH * form_model.getTileWidth();
		double minX = viewScaler * form_model.minControlX() - GRID_UNIT_BLEED * interval;
		double maxX = viewScaler * form_model.maxControlX() + GRID_UNIT_BLEED * interval;
		double minY = viewScaler * form_model.minControlY() - GRID_UNIT_BLEED * interval;
		double maxY = viewScaler * form_model.maxControlY() + GRID_UNIT_BLEED * interval;
		double boxW = interval;
		double boxH = 0;
		double boxZ = 0;
		Color col = Color.gray(SUBDUED_SATURATION, SUBTLE_ALPHA);
		for (double x = minX + interval/2; x < maxX; x += interval) {
			for (double y = minY + interval/2; y < maxY; y += interval) {
				double boxX = x;
				double boxY = y;
				Box b = basicBox(boxX, boxY, boxZ, boxW, boxH, DEFAULT_GRID_Z, col);
				b.setMaterial(new PhongMaterial(Color.TRANSPARENT));
				b.setId("grid");
				
				// Mouse Events for Control Pointers
				ControlPoint existingPointAtMouse = null; // not applicable here
				boolean mousePressed = true;
				float locX = (float) (boxX / viewScaler + 0.01f * Math.random());
				float locY = (float) (boxY / viewScaler + 0.01f * Math.random());
				b.setOnMouseEntered(me -> {
					Point newPointAtMouse = new Point(locX, locY);
					form_model.listen(!mousePressed, existingPointAtMouse, newPointAtMouse);
					form_model.updateModel();
					gridMap.put(b, newPointAtMouse);
					// Set Ghost for new Control Point
					if(form_model.hovering != null) {
						hover.setVisible(true);
						hover.setMaterial(ADD_MATERIAL);
						orientShape((Node) hover, viewScaler * newPointAtMouse.x, viewScaler * newPointAtMouse.y, DEFAULT_CONTROL_Z);
					} 
//					else if (form_model.selected != null) {
//						Point new_location = gridMap.get(b);
//						if (new_location != null) {
//							form_model.selected.x = newPointAtMouse.x;
//							form_model.selected.y = newPointAtMouse.y;
//						}
//						form_model.detectChange(form_model.selected.getType());
//						form_model.updateModel();
//						orientShape((Node) controlMap.get(form_model.selected), viewScaler * newPointAtMouse.x, viewScaler * newPointAtMouse.y, DEFAULT_CONTROL_Z);
//					}
				});
				grid.getChildren().add(b);
			}
		}
		return grid;
	}
	
	/**
	 * Construct a 3D Overhead Light Source
	 */
	private Node overheadLight() {
		PointLight light = new PointLight();
		light.setColor(Color.WHITE);
		light.getTransforms().add(new Translate(0, -viewScaler*DEFAULT_LIGHT_DISPLACEMENT, 0));
		return light;
	}
	
	/**
	 * Construct a 3D Side Light Source
	 */
	private Node sideLight() {
		PointLight light = new PointLight();
		light.setColor(Color.WHITE);
		light.getTransforms().add(new Translate(-viewScaler*DEFAULT_LIGHT_DISPLACEMENT, 0, -viewScaler*DEFAULT_LIGHT_DISPLACEMENT));
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
		double boxW = viewScaler * VOXEL_HEIGHT_BUFFER * t.scale_uv;
		double boxH = viewScaler * VOXEL_WIDTH_BUFFER * t.scale_w;
		double boxX = viewScaler * t.location.x;
		double boxY = viewScaler * t.location.y;
		double boxZ = viewScaler * t.location.z;
		if(flat) boxH = 0;
		Rotate rot = new Rotate(RadianToDegree(form_model.tile_rotation), Rotate.Y_AXIS);
		Box b = basicBox(boxX, boxY, boxZ, boxW, boxH, z_offset, col);
		b.getTransforms().add(rot);
		b.setId("voxel");
		
		// Sets Transparent to Mouse Events when Editing
		if (form_model.isEditing()) b.setMouseTransparent(true);

		return b;
	}
	
	public Box basicBox(double boxX, double boxY, double boxZ, double boxW, double boxH, double z_offset, Color col) {
		Box b = new Box(boxW, boxH, boxW);
		Translate pos = new Translate(
				+ boxX, 
				- boxZ - 0.5*boxH - z_offset, 
				- boxY);
		b.getTransforms().addAll(rotateH, pan, pos);
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
	public void orientShape(Node input, double x, double y, double z) {
		Rotate rotateFlat = new Rotate(-90, Rotate.X_AXIS);
		Translate pos = new Translate(x, -z, -y);
		input.getTransforms().clear();
		input.getTransforms().addAll(rotateH, pan, pos, rotateFlat);
	}
}
