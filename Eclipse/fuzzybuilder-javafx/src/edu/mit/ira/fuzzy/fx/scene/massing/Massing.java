package edu.mit.ira.fuzzy.fx.scene.massing;

import edu.mit.ira.fuzzy.base.ControlPoint;
import edu.mit.ira.fuzzy.base.Point;
import edu.mit.ira.fuzzy.base.Tile;
import edu.mit.ira.fuzzy.base.TileArray;
import edu.mit.ira.fuzzy.builder.DevelopmentEditor;
import edu.mit.ira.fuzzy.fx.node.Underlay;
import javafx.scene.Node;
import javafx.scene.PointLight;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Circle;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Massing extends MassingContainer {
 	
 	// Default Elevation Values
 	final protected static double DEFAULT_LIGHT_DISPLACEMENT = 400;
 	final protected static double DEFAULT_MAP_Z     = - 0.4;
 	final protected static double DEFAULT_POLY_Z    = - 0.3;
 	final protected static double DEFAULT_CONTROL_Z = + 1.0;
 	final protected static double DEFAULT_SITE_Z    = + 0.0;
 	final protected static double DEFAULT_ZONE_Z    = + 0.1;
 	final protected static double DEFAULT_FOOT_Z    = + 0.2;
 	final protected static double DEFAULT_BASE_Z    = + 0.3;
 	
 	// Default color and stroke values
 	final protected static double DEFAULT_SATURATION = 0.50;
 	final protected static double DEFAULT_BRIGHTNESS = 0.75;
 	final protected static double DEFAULT_ALPHA = 0.90;
 	final protected static double SUBDUED_SATURATION = 0.30;
 	final protected static double SUBDUED_BRIGHTNESS = 0.75;
 	final protected static double SUBDUED_ALPHA = 0.75;
 	final protected static double SUBTLE_ALPHA = 0.5;
 	final protected static double DEFAULT_STROKE = 1.0;
 	final protected static double SUBDUED_STROKE = 2.0;
 	final protected static Color DEFAULT_CONTROL_FILL = Color.hsb(0, 0, 1, SUBDUED_ALPHA);
 	final protected static Color DEFAULT_CONTROL_STROKE = Color.gray(SUBDUED_SATURATION, SUBTLE_ALPHA);
 	final protected static Color ACTIVE_COLOR = Color.PURPLE;
 	final protected static Color REMOVE_COLOR = Color.RED;
 	final protected static Color ADD_COLOR = Color.GREEN;
 	final private static PhongMaterial ACTIVE_MATERIAL = new PhongMaterial(ACTIVE_COLOR);
 	final private static PhongMaterial REMOVE_MATERIAL = new PhongMaterial(REMOVE_COLOR);
 	final private static PhongMaterial ADD_MATERIAL = new PhongMaterial(ADD_COLOR);
 	
 	// Default Object Sizes
 	final protected static double DEFAULT_CONTROL_SIZE = 5.0;
 	final protected static double VOXEL_HEIGHT_BUFFER = 0.9;
 	final protected static double VOXEL_WIDTH_BUFFER = 0.8;
 	
	public Massing() {
		super();
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
	
	/**
	 * Populate 2D and 3D graphics objects 
	 * 
	 * @return a collection of JavaFX groups (3D objects)
	 */
	public void drawNodes() {
		
		nodes2D.getChildren().clear();
		nodes3D.getChildren().clear();
		controlMap.clear();
		
		// Placeholder for 2D overlay
		Label l = new Label("Massing Editor");
    	nodes2D.getChildren().add(l);
		
		// Set up Ambient Lighting Effects
		nodes3D.getChildren().add(overheadLight());
		nodes3D.getChildren().add(sideLight());
		
		// Extra opacity to apply when editing control points
		double editingScaler = 1.0;
		if (form_model.isEditing()) editingScaler = 0.5;
		
		// Draw Active Control Points' Inner Sphere
		if (form_model.isEditing()) {
			for (ControlPoint p : form_model.control.points()) {
				if (p.active()) {
					Sphere ic = new Sphere();
					ic.setMaterial(ACTIVE_MATERIAL);
					ic.setRadius(0.65 * DEFAULT_SCALER * DEFAULT_CONTROL_SIZE);
					ic.setId("active_control_point");
					orientShape((Node) ic, viewScaler * p.x, viewScaler * p.y, DEFAULT_CONTROL_Z + 0.1);
					nodes3D.getChildren().add(ic);
					controlMap.put(ic, p);
					
					// Mouse Events
					ControlPoint newPointAtMouse = null;
					boolean pressed = true;
					ic.setOnMouseEntered(me -> {
						ic.setRadius(0.90 * DEFAULT_SCALER * DEFAULT_CONTROL_SIZE);
						if(form_model.addPoint) {
							ic.setMaterial(ADD_MATERIAL);
						} else if(form_model.removePoint) {
							ic.setMaterial(REMOVE_MATERIAL);
						}
						form_model.listen(!pressed, p, newPointAtMouse);
					});
					ic.setOnMouseExited(me -> {
						ic.setRadius(0.65 * DEFAULT_SCALER * DEFAULT_CONTROL_SIZE);
						ic.setMaterial(ACTIVE_MATERIAL);
						form_model.listen(!pressed, p, newPointAtMouse);
					});
					ic.setOnMousePressed((MouseEvent me) -> {
						form_model.mousePressed(newPointAtMouse);
						form_model.listen(pressed, p, newPointAtMouse);
						render(form_model, map_model);
					});
					ic.setOnMouseReleased((MouseEvent me) -> {
						form_model.deselect();
						form_model.listen(!pressed, p, newPointAtMouse);
						render(form_model, map_model);
					});
				}
			}
		}

		// Draw Voxel Sites
		for (TileArray space : form_model.spaceList("site")) {
			if(form_model.showSpace(space) && form_model.showTiles) {
				Color col = Color.gray(DEFAULT_SATURATION, SUBDUED_ALPHA);
				for (Tile t : space.tileList()) {
					Box b = renderVoxel(t, col, DEFAULT_SITE_Z, true);
					nodes3D.getChildren().add(b);
				}
			}
		}
		
		// Draw Voxel Zones
		for (TileArray space : form_model.spaceList("zone")) {
			if(form_model.showSpace(space) && form_model.showTiles) {
				Color col = Color.hsb(space.getHueDegree(), SUBDUED_SATURATION, SUBDUED_BRIGHTNESS, SUBDUED_ALPHA);
				for (Tile t : space.tileList()) {
					Box b = renderVoxel(t, col, DEFAULT_ZONE_Z, true);
					nodes3D.getChildren().add(b);
				}
			}
		}
		
		// Draw Voxel Footprints
		for (TileArray space : form_model.spaceList("footprint")) {
			if(form_model.showSpace(space) && form_model.showTiles) {
				Color col = Color.hsb(space.getHueDegree(), DEFAULT_SATURATION, DEFAULT_BRIGHTNESS, DEFAULT_ALPHA);
				if (space.name.equals("Setback"))
					col = Color.hsb(space.getHueDegree(), SUBDUED_SATURATION, SUBDUED_BRIGHTNESS, SUBDUED_ALPHA);
				for (Tile t : space.tileList()) {
					Box b = renderVoxel(t, col, DEFAULT_FOOT_Z, true);
					nodes3D.getChildren().add(b);
					if (space.name.equals("Building")) {
						b = renderVoxel(t, col, DEFAULT_FOOT_Z, true);
						nodes3D.getChildren().add(b);
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
						nodes3D.getChildren().add(b);
					}
				}
			}
		}
		
		// Draw All Control Point Circles
		if(form_model.viewState != 5 && form_model.isEditing()) {
			for (ControlPoint p : form_model.control.points()) {
				Circle oc = new Circle();
				oc.setRadius(DEFAULT_SCALER * DEFAULT_CONTROL_SIZE);
				oc.setFill(DEFAULT_CONTROL_FILL);
				oc.setStroke(DEFAULT_CONTROL_STROKE);
				oc.setStrokeWidth(DEFAULT_SCALER * SUBDUED_STROKE);
				orientShape((Node) oc, viewScaler * p.x, viewScaler * p.y, DEFAULT_CONTROL_Z);
				nodes3D.getChildren().add(oc);
				controlMap.put(oc, p);
			}
		}
		
		// Add and orient Underlay map (draw last to be able to see through bottom!)
		if (showUnderlay) {
			map_model.setImageView();
			orientShape((Node) map_model.getImageView(), 0, 0, DEFAULT_MAP_Z);
			map_model.setScale(viewScaler*map_model.getScaler());
			nodes3D.getChildren().add(map_model.getImageView());
		}
		
		// Draw Site Vector Polygon
		Color site_fill         = Color.TRANSPARENT;
		Color site_stroke       = Color.gray(SUBDUED_SATURATION, SUBDUED_ALPHA);
		double site_strokeWeight = DEFAULT_SCALER * DEFAULT_STROKE;
		if (form_model.showPolygons) {
			site_stroke         = Color.gray(DEFAULT_SATURATION, DEFAULT_ALPHA);
			site_strokeWeight   = DEFAULT_SCALER * SUBDUED_STROKE;
		}
		Polygon site_polygon = new Polygon();
		for (Point p : form_model.site_boundary.getCorners()) {
			site_polygon.getPoints().addAll(new Double[] { (double) viewScaler*p.x, (double) viewScaler*p.y });
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
		if(flat) boxH = 0;
		Box b = new Box(boxW, boxH, boxW);
		b.setId("voxel");
		
		Translate pos = new Translate(
				+ viewScaler * t.location.x, 
				- viewScaler * t.location.z - z_offset - 0.5*boxH, 
				- viewScaler * t.location.y);
		Rotate rot = new Rotate(RadianToDegree(form_model.tile_rotation), Rotate.Y_AXIS);
		b.getTransforms().addAll(rotateH, pan, pos, rot);

		// Box Color
		PhongMaterial material = new PhongMaterial(col);
		b.setMaterial(material);
		
		// Sets Transparent to Mouse Events when Editing
		if (form_model.isEditing()) b.setMouseTransparent(true);

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
}
