package edu.mit.ira.builder.fx;

import edu.mit.ira.builder.Builder;
import edu.mit.ira.voxel.Tile;
import edu.mit.ira.voxel.TileArray;

import java.util.ArrayList;

import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class ViewModel {
	
	private Builder builder;
	
	// Master Group
	private Group blockSet; 
	private ArrayList<Node> blockArray;
	
	// Master Camera
	private Camera camera;
	
	// background color of infinity
	private Color background;
    
	// Center of Entire 3D Scene
    private static final Translate ORIGIN = new Translate(-400, 0, 200);
    
    // Default Horizontal Rotation
    private static final Rotate DEFAULT_ROTATE_H = new Rotate(-20, Rotate.Y_AXIS);
    
    // Default Vertical Rotation
    private static final Rotate DEFAULT_ROTATE_V = new Rotate(-20, Rotate.X_AXIS);
    
    // Zoom level of Camera
    private Translate zoom = new Translate(0, 0, -500);
    
    // Amount of panning initiated by mouse secondary button
    private Translate pan = new Translate(0, 0, 0);
    
    // Vertical Rotation (JavaFX uses X_AXIS)
    private Rotate rotateV = new Rotate(0, Rotate.X_AXIS);
    
    // Horizontal Rotation (JavaFX uses Y_AXIS)
    private Rotate rotateH = new Rotate(0, Rotate.Y_AXIS);
    
    /**
     * Test Scene Constructor
     */
    public ViewModel() {
    	this.camera = setUpCamera(rotateV, zoom, pan);
        this.background = Color.TRANSPARENT;
        buildSite();
    }
    
    private static Camera setUpCamera(Rotate rotateV, Translate zoom, Translate pan) {
        Camera _camera = new PerspectiveCamera(true);
        // Create and position camera
        _camera = new PerspectiveCamera(true);
        _camera.getTransforms().addAll(DEFAULT_ROTATE_H, DEFAULT_ROTATE_V, pan, rotateV, zoom);
        _camera.setNearClip(10);
        _camera.setFarClip(10000);
        return _camera;
    }
    
    public Group getGroup() {
    	return blockSet;
    }
    
    public Camera getCamera() {
    	return camera;
    }
    
    public Color getBackground() {
        return background;
    }
    
    /**
     * Create a scene Model for some test geometry
     * 
     * @return a collection of JavaFX groups (3D objects)
     */
    public void buildSite() {
    	
    	this.blockSet = new Group();
    	
    	builder = new Builder();
    	
        // Box Array:
    	blockArray = new ArrayList<Node>();
    	
    	if (builder.showTiles) {
			for (TileArray space : builder.dev.spaceList()) {
				if (builder.showSpace(space)) {

					// Draw Sites
					//
					if (space.isType("site")) {
						Color col = Color.gray(0, 0.2);
						for (Tile t : space.tileList())
							blockArray.add(renderTile(t, col, -1));
					}

					// Draw Zones
					//
					if (space.isType("zone")) {
						Color col = Color.hsb(space.getHueDegree(), 0.3, 0.9);
						for (Tile t : space.tileList())
							blockArray.add(renderTile(t, col, -1));
					}

					// Draw Footprints
					//
					if (space.isType("footprint")) {
						Color col;
						if (space.name.equals("Building")) {
							col = Color.hsb(space.getHueDegree(), 0.5, 0.8);
						} else if (space.name.equals("Setback")) {
							col = Color.hsb(space.getHueDegree(), 0.3, 0.9);
						} else {
							col = Color.hsb(space.getHueDegree(), 0.5, 0.8);
						}
						for (Tile t : space.tileList()) {
							blockArray.add(renderTile(t, col, -1));
							if (space.name.equals("Building")) {
								blockArray.add(renderVoxel(t, col, 0));
							}
						}
					}

					// Draw Bases
					//
					if (space.isType("base")) {
						Color col = Color.hsb(space.getHueDegree(), 0.5, 0.8);
						for (Tile t : space.tileList()) {
							// Only draws ground plane if in 2D view mode
							if (t.location.z == 0 || builder.cam3D) {
								if (space.name.substring(0, 3).equals("Cou")) {
									blockArray.add(renderTile(t, col, 0));
								} else {
									blockArray.add(renderVoxel(t, col, 0));
								}
							}
						}
					}
				}
			}
		}

        // Build the Scene Graph
        blockSet = new Group();
        blockSet.getChildren().addAll(camera);
        for (Node b : blockArray) blockSet.getChildren().add(b);
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
    	Translate pos = new Translate(t.location.x - 0.5*rectW, - t.location.z - z_offset, - t.location.y - 0.5*rectW);
    	Rotate rot = new Rotate(180*builder.tile_rotation);
    	r.getTransforms().addAll(rotateH, ORIGIN, pos, rot, rotateFlat);

    	// Box Color
    	r.setFill(col);

    	return r;
    }
    
    // Mouse locations on Canvas
    private double mousePosX, mousePosY = 0;
    public void handleMouseEvents(Scene scene) {
    	scene.setOnMousePressed((MouseEvent me) -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
        });
        
        scene.setOnMouseDragged((MouseEvent me) -> {
            double dx = + (mousePosX - me.getSceneX());
            double dy = - (mousePosY - me.getSceneY());
            
            
            if (me.isSecondaryButtonDown()) {
            	
            	// Rotate View
            	
            	double angleV = rotateV.getAngle() - (dy / 10 * 360) * (Math.PI / 180);
            	double angleH = rotateH.getAngle() - (dx / 10 * -360) * (Math.PI / 180);
            	
            	// Can scene above and below model, but
            	// Don't allow scene to flip upside down
            	angleV = ensureRange(angleV, -90, 90);
            	
                rotateV.setAngle(angleV);
                rotateH.setAngle(angleH);
            
            
            } else if (me.isMiddleButtonDown()) {
            	
            	// Pan View
            	
            	double panU = pan.getX() + dx / 10;
            	double panV = pan.getY() - dy / 10;
            	
            	pan.setX(panU);
            	pan.setY(panV);
            	
            	
            }
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
        });
        
        // Enable Zoom in and Zoom Out
        scene.setOnScroll((ScrollEvent se) -> {
            double dy = se.getDeltaY();
        	double new_zoom = zoom.getZ() - dy;
        	new_zoom = ensureRange(new_zoom, -1000, -100);
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
}