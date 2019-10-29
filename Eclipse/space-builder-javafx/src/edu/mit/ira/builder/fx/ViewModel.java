package edu.mit.ira.builder.fx;

import edu.mit.ira.builder.Builder;
import edu.mit.ira.voxel.Tile;
import edu.mit.ira.voxel.TileArray;

import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class ViewModel {

	// Form Model Classes by Ira
	private Underlay underlay;
	private Builder builder;

	// Master Group
	private Group nodeSet; 

	// Master Camera
	private Camera camera;

	// background color of infinity
	private Color background;

	// Map File Path
	private static final String MAP_FILE_PATH = "data/site.png";

	// Center of Entire 3D Scene
	private static final Translate ORIGIN = new Translate(-719, 0, 410);

	// Default Horizontal Rotation
	private static final Rotate DEFAULT_ROTATE_H = new Rotate(0, Rotate.Y_AXIS);

	// Default Vertical Rotation
	private static final Rotate DEFAULT_ROTATE_V = new Rotate(0, Rotate.X_AXIS);

	// Zoom level of Camera
	private Translate zoom = new Translate(0, 0, -500);

	// Amount of panning initiated by mouse secondary button
	private Translate pan = new Translate(0, 0, 0);

	// Vertical Rotation (JavaFX uses X_AXIS)
	private Rotate rotateV = new Rotate(-20, Rotate.X_AXIS);

	// Horizontal Rotation (JavaFX uses Y_AXIS)
	private Rotate rotateH = new Rotate(-20, Rotate.Y_AXIS);

	/**
	 * Test Scene Constructor
	 */
	public ViewModel(String model_mode) {

		this.camera = setUpCamera(rotateV, zoom, pan);
		this.background = Color.TRANSPARENT;
		builder = new Builder(model_mode);
		underlay  = new Underlay(MAP_FILE_PATH, 0.5, 0.75);
		renderSite();
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
		return nodeSet;
	}

	public Camera getCamera() {
		return camera;
	}

	public Color getBackground() {
		return background;
	}

	public void buildSite(String model_mode) {
		builder.initModel(model_mode);
		builder.resetRender();
	}

	/**
	 * Create a scene Model for some test geometry
	 * 
	 * @return a collection of JavaFX groups (3D objects)
	 */
	public void renderSite() {

		this.nodeSet = new Group();
		
		// Add Camera to Set
		nodeSet.getChildren().addAll(camera);

		// Draw Bases
		for (TileArray space : builder.dev.spaceList("base")) {
			if(builder.showSpace(space) && builder.showTiles) {
				Color col = Color.hsb(space.getHueDegree(), 0.5, 0.8);
				for (Tile t : space.tileList()) {
					// Only draws ground plane if in 2D view mode
					if (t.location.z == 0 || builder.cam3D) {
						if (space.name.substring(0, 3).equals("Cou")) {
							nodeSet.getChildren().add(renderVoxel(t, col, 0, true));
						} else {
							nodeSet.getChildren().add(renderVoxel(t, col, 0, false));
						}
					}
				}
			}
		}

		// Draw Footprints
		for (TileArray space : builder.dev.spaceList("footprint")) {
			if(builder.showSpace(space) && builder.showTiles) {
				Color col;
				if (space.name.equals("Building")) {
					col = Color.hsb(space.getHueDegree(), 0.5, 0.8);
				} else if (space.name.equals("Setback")) {
					col = Color.hsb(space.getHueDegree(), 0.3, 0.9, 0.75);
				} else {
					col = Color.hsb(space.getHueDegree(), 0.5, 0.8);
				}
				for (Tile t : space.tileList()) {
					nodeSet.getChildren().add(renderVoxel(t, col, -1, true));
					if (space.name.equals("Building")) {
						nodeSet.getChildren().add(renderVoxel(t, col, 0, false));
					}
				}
			}
		}

		// Draw Sites
		for (TileArray space : builder.dev.spaceList("footprint")) {
			if(builder.showSpace(space) && builder.showTiles) {
				Color col = Color.gray(0, 0.2);
				for (Tile t : space.tileList())
					nodeSet.getChildren().add(renderVoxel(t, col, -1, true));
			}
		}

		// Draw Zones
		for (TileArray space : builder.dev.spaceList("zone")) {
			if(builder.showSpace(space) && builder.showTiles) {
				Color col = Color.hsb(space.getHueDegree(), 0.3, 0.9, 0.75);
				for (Tile t : space.tileList())
					nodeSet.getChildren().add(renderVoxel(t, col, -2, false));
			}
		}
		
		// Make and position the Basemap Image
		ImageView map = underlay.getImageView();
		Rotate rotateFlat = new Rotate(-90, Rotate.X_AXIS);
		Translate pos = new Translate(0, 2, 0);
		map.getTransforms().addAll(rotateH, ORIGIN, pos, rotateFlat);
		nodeSet.getChildren().addAll(map);

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
		float scaler_w = (float) 0.6;

		float boxW = scaler_uv * t.scale_uv;
		float boxH = scaler_w * t.scale_w;
		if(flat) boxH = 0;
		Box b = new Box(boxW, boxH, boxW);

		Translate pos = new Translate(
				+ t.location.x, 
				- t.location.z - z_offset - 0.5*boxH, 
				- t.location.y);
		Rotate rot = new Rotate(RadianToDegree(builder.tile_rotation), Rotate.Y_AXIS);
		b.getTransforms().addAll(rotateH, ORIGIN, pos, rot);

		// Box Color
		PhongMaterial material = new PhongMaterial(col);
		b.setMaterial(material);

		return b;
	}

	// Mouse locations on Canvas
	private double mousePosX, mousePosY = 0;
	/**
	 * Actions to take when mouse events are detected
	 * 
	 * @param scene
	 */
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


			} else if (me.isPrimaryButtonDown()) {

				// Pan View

				double panU = pan.getX() + dx / 5;
				double panV = pan.getY() - dy / 5;

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
}