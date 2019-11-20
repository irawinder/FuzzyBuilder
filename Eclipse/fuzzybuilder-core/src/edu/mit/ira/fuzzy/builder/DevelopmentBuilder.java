package edu.mit.ira.fuzzy.builder;

import java.util.ArrayList;

import edu.mit.ira.fuzzy.base.ControlPoint;
import edu.mit.ira.fuzzy.base.ControlSet;
import edu.mit.ira.fuzzy.base.Development;
import edu.mit.ira.fuzzy.base.Point;
import edu.mit.ira.fuzzy.base.Polygon;
import edu.mit.ira.fuzzy.base.TileArray;

/**
 * DevelopmentBuilder facilitates the making of various TileArrays via 
 * ControlSet that is passed to it.
 * 
 * @author Ira Winder
 *
 */
public class DevelopmentBuilder extends Development {
	
	// Intermediate Polygon used to generate Fuzzy Development()
	public Polygon site_boundary;

	// Intermediate Raster Grid Options to generate Fuzzy Development
	// (dimensions, scale, rotation, translation, units)
	public float tileW, tileH, tile_rotation;
	public String units;
	public Point tile_translation;
	private float minTileX, maxTileX;
	private float minTileY, maxTileY;
	
	// Default Values
	final private static String DEFAULT_NAME = "New Parcel";
	final private static String DEFAULT_UNITS = "meters";
	final private static float DEFAULT_TILE_WIDTH = 30;
	final private static float DEFAULT_TILE_HEIGHT = 10;
	final private static float DEFAULT_TILE_TRANSLATE_X = 0;
	final private static float DEFAULT_TILE_TRANSLATE_Y = 0;
	final private static float DEFAULT_TILE_ROTATION = 0;
	
	/**
	 * Make the Space Building Environment
	 */
	public DevelopmentBuilder() {
		super(DEFAULT_NAME);

		// Initialize Vector Site Polygon
		site_boundary = new Polygon();

		// Initialize Raster-like Site Voxels
		setTileUnits(DEFAULT_UNITS);
		setTileWidth(DEFAULT_TILE_WIDTH);
		setTileHeight(DEFAULT_TILE_HEIGHT);
		setTileTranslation(DEFAULT_TILE_TRANSLATE_X, DEFAULT_TILE_TRANSLATE_Y);
		setTileRotation(DEFAULT_TILE_ROTATION);
		
		minTileX = 0;
		maxTileX = 0;
		minTileY = 0;
		maxTileY = 0;
	}
	
	/**
	 * Set Tile Width
	 * 
	 * @param tileW
	 */
	public void setTileWidth(float tileW) {
		this.tileW = tileW;
	}
	
	/**
	 * Get Tile Width
	 * 
	 * @return tileW
	 */
	public float getTileWidth() {
		return this.tileW;
	}
	
	/**
	 * Set Voxel Height
	 * 
	 * @param tileH
	 */
	public void setTileHeight(float tileH) {
		this.tileH = tileH;
	}
	
	/**
	 * Set Grid Orientation for entire Development
	 * 
	 * @param tile_rotation
	 */
	public void setTileRotation(float tile_rotation) {
		this.tile_rotation = tile_rotation;
	}
	
	/**
	 * Set Origin (X,Y) of Site
	 * 
	 * @param x
	 * @param y
	 */
	public void setTileTranslation(float x, float y) {
		this.tile_translation = new Point(x,y);
	}
	
	/**
	 * Set Name for dimensional units (e.g. "meters")
	 * 
	 * @param units
	 */
	public void setTileUnits(String units) {
		this.units = units;
	}
	
	/**
	 * Get minimum tile X value
	 * 
	 * @return minTileX
	 */
	public float minTileX() {
		return minTileX;
	}
	
	/**
	 * Get maximum tile X value
	 * 
	 * @return maxTileX
	 */
	public float maxTileX() {
		return maxTileX;
	}
	
	/**
	 * Get minimum tile Y value
	 * 
	 * @return minTileY
	 */
	public float minTileY() {
		return minTileY;
	}
	
	/**
	 * Get maximum tile Y value
	 * 
	 * @return maxTileY
	 */
	public float maxTileY() {
		return maxTileY;
	}
	
	/**
	 * Update Model:
	 */
	public void updateModel(char change, ControlSet control) {

		switch (change) {
		case 's':
			buildSite(control);
		case 'z':
			buildZones(control);
		case 'f':
			buildFootprints(control);
			buildBases();
			break;
		}
	}

	/**
	 * Initialize Site Model
	 */
	public void buildSite(ControlSet control) {

		// Define new Space Type
		String type = "site";
		clearType(type);
		TileArray site = new TileArray(name, type);
		site.setParent(getName());

		// Update Polygon according to control points
		site_boundary.clear();
		ArrayList<ControlPoint> vertex_control = control.points("Vertex");
		for (ControlPoint p : vertex_control)
			site_boundary.addVertex(p);

		// Create new Site from polygon
		site.makeTiles(site_boundary, tileW, tileH, units, tile_rotation, tile_translation);

		// Add new spaces to Development
		addSpace(site);
	}

	/**
	 * Subdivide the site into Zones
	 */
	public void buildZones(ControlSet control) {

		// Define new Space Type
		String type = "zone";
		clearType(type);
		ArrayList<TileArray> new_zones = new ArrayList<TileArray>();

		// Create new Zones from Voronoi Sites
		for (TileArray space : spaceList()) {
			if (space.type.equals("site")) {
				ArrayList<ControlPoint> plot_control = control.points("Plot");
				ArrayList<TileArray> zones = space.getVoronoi(plot_control);
				int hue = 0;
				for (TileArray zone : zones) {
					zone.setType(type);
					zone.setHue(hue);
					new_zones.add(zone);
					hue += 40;
				}
			}
		}

		// Add new Spaces to Development
		for (TileArray zone : new_zones)
			addSpace(zone);
	}

	/**
	 * Subdivide Zones into Footprints
	 */
	public void buildFootprints(ControlSet control) {

		// Define new Space Type
		String type = "footprint";
		clearType(type);
		ArrayList<TileArray> new_foot = new ArrayList<TileArray>();

		// Create new Footprints from Zone Space
		for (TileArray space : spaceList()) {
			if (space.type.equals("zone")) {

				// Setback Footprint
				TileArray setback = space.getSetback();
				setback.setName("Setback");
				setback.setType(type);

				// Void Footprint(s)
				int numTiles = 10; // Voids are numTiles tiles big
				float yard_area = numTiles * tileW * tileW;
				ArrayList<TileArray> voidSpace = new ArrayList<TileArray>();
				ArrayList<ControlPoint> void_control = control.points("Void");
				for (ControlPoint p : void_control) {
					if (space.pointInArray(p.x, p.y)) {
						TileArray t = space.getClosestN(p, yard_area);
						t.subtract(setback);
						t.setName(p.getType());
						t.setType(type);
						// Subtract other voids from current to prevent overlap
						for (TileArray prev : voidSpace)
							t.subtract(prev);
						voidSpace.add(t);
					}
				}

				// Building Footprint
				TileArray building = space.getDiff(setback);
				for (TileArray v : voidSpace)
					building.subtract(v);
				building.setName("Building");
				building.setType(type);

				new_foot.add(setback);
				new_foot.add(building);
				for (TileArray v : voidSpace)
					new_foot.add(v);
			}
		}

		// Add new Spaces to Development
		for (TileArray foot : new_foot)
			addSpace(foot);
	}

	/**
	 * A Base is a building component that rests on a Footprint
	 */
	public void buildBases() {

		// Define new Space Type
		String type = "base";
		clearType(type);
		ArrayList<TileArray> new_bases = new ArrayList<TileArray>();

		// Create new Bases from Footprints
		for (TileArray space : spaceList()) {

			// Building
			if (space.name.equals("Building") && space.type.equals("footprint")) {
				TileArray base = space.getExtrusion(0, space.toExtrude());
				base.setName("Podium");
				base.setType(type);
				new_bases.add(base);
			}

			// OpenSpace
			if (space.name.substring(0, 3).equals("Voi") && space.type.equals("footprint")) {
				TileArray base = space.getExtrusion(0, 0);
				base.setName("Courtyard");
				base.setType(type);
				new_bases.add(base);
			}
		}

		// Add new Spaces to Development
		for (TileArray base : new_bases) {
			addSpace(base);
		}
		
		this.calcMinMax();
	}
	
	/**
	 * update minimum and maximum extents of model
	 */
	public void calcMinMax() {
		if (spaceList().size() > 0) {
			minTileX = Float.POSITIVE_INFINITY;
			maxTileX = Float.NEGATIVE_INFINITY;
			minTileY = Float.POSITIVE_INFINITY;
			maxTileY = Float.NEGATIVE_INFINITY;
			for (TileArray space : spaceList()) {
				if (space.hasTiles()) {
					minTileX = Math.min(minTileX, space.minX());
					maxTileX = Math.max(maxTileX, space.maxX());
					minTileY = Math.min(minTileY, space.minY());
					maxTileY = Math.max(maxTileY, space.maxY());
				}
			}
		}
	}
	
	@Override
	public String toString() {
		return this.name + " X-Extents: (" + this.minTileX + " - " + this.maxTileX + "), " + " Y-Extents: (" + this.minTileY + " - " + this.maxTileY + ")";
	}
}