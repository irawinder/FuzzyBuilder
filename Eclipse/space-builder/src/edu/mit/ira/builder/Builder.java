package edu.mit.ira.builder;

import java.util.ArrayList;

import edu.mit.ira.voxel.Control;
import edu.mit.ira.voxel.ControlPoint;
import edu.mit.ira.voxel.Development;
import edu.mit.ira.voxel.Point;
import edu.mit.ira.voxel.Polygon;
import edu.mit.ira.voxel.TileArray;

/**
 * Builder facilitates the making of various TileArrays via Polygons and/or
 * ControlPoints. Builder implements many classes from edu.mit.ira.voxel.
 * Builder should be implementable within both Processing or JavaFX GUI 
 * components.
 * 
 * @author ira
 *
 */
public class Builder {

	// Development of spaces
	public Development dev;
	public String dev_name;

	// Intermediate Polygon used to generate Development()
	public Polygon site_boundary;
	public String site_name;

	// Intermediate Raster Grid Options to generate Development
	// (dimensions, scale, rotation, translation, units)
	public float tileW, tileH, tile_rotation;
	public String units;
	public Point tile_translation;

	/**
	 * Make the Space Building Environment
	 */
	public Builder() {
		initModel();
		
	}
	
	public void setTileWidth(float tileW) {
		this.tileW = tileW;
	}
	
	public void setTileHeight(float tileH) {
		this.tileH = tileH;
	}
	
	public void setTileRotation(float tile_rotation) {
		this.tile_rotation = tile_rotation;
	}
	
	public void setTileTranslation(float x, float y) {
		this.tile_translation = new Point(x,y);
	}
	
	public void setTileUnits(String units) {
		this.units = units;
	}

	/**
	 * Initialize the Model
	 */
	public void initModel() {

		// Init Vector Site Polygon
		site_boundary = new Polygon();

		// Init Raster-like Site Voxels
		dev_name = "New Development";
		dev = new Development(dev_name);
		site_name = "Property";
		setTileUnits("pixels");
		setTileWidth(30);
		setTileHeight(10);
		setTileTranslation(0,0);
		setTileRotation(0);
	}
	
	/**
	 * Update Model:
	 */
	public void updateModel(char change, Control control) {

		switch (change) {
		case 's':
			initSites(control);
		case 'z':
			initZones(control);
		case 'f':
			initFootprints(control);
			initBases();
			break;
		}
	}

	/**
	 * Initialize Site Model
	 */
	public void initSites(Control control) {

		// Define new Space Type
		String type = "site";
		dev.clearType(type);
		TileArray site = new TileArray(site_name, type);
		site.setParent(dev_name);

		// Update Polygon according to control points
		site_boundary.clear();
		ArrayList<ControlPoint> vertex_control = control.points("Vertex");
		for (ControlPoint p : vertex_control)
			site_boundary.addVertex(p);

		// Create new Site from polygon
		site.makeTiles(site_boundary, tileW, tileH, units, tile_rotation, tile_translation);

		// Add new spaces to Development
		dev.addSpace(site);
	}

	/**
	 * Subdivide the site into Zones
	 */
	public void initZones(Control control) {

		// Define new Space Type
		String type = "zone";
		dev.clearType(type);
		ArrayList<TileArray> new_zones = new ArrayList<TileArray>();

		// Create new Zones from Voronoi Sites
		for (TileArray space : dev.spaceList()) {
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
			dev.addSpace(zone);
	}

	/**
	 * Subdivide Zones into Footprints
	 */
	public void initFootprints(Control control) {

		// Define new Space Type
		String type = "footprint";
		dev.clearType(type);
		ArrayList<TileArray> new_foot = new ArrayList<TileArray>();

		// Create new Footprints from Zone Space
		for (TileArray space : dev.spaceList()) {
			if (space.type.equals("zone")) {

				// Setback Footprint
				TileArray setback = space.getSetback();
				setback.setName("Setback");
				setback.setType(type);

				// Void Footprint(s)
				float yard_area = 2700;
				ArrayList<TileArray> voidSpace = new ArrayList<TileArray>();
				ArrayList<ControlPoint> void_control = control.points("Void");
				for (ControlPoint p : void_control) {
					if (space.pointInArray(p.x, p.y)) {
						TileArray t = space.getClosestN(p, yard_area);
						t.subtract(setback);
						t.setName(p.getTag());
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
			dev.addSpace(foot);
	}

	/**
	 * A Base is a building component that rests on a Footprint
	 */
	public void initBases() {

		// Define new Space Type
		String type = "base";
		dev.clearType(type);
		ArrayList<TileArray> new_bases = new ArrayList<TileArray>();

		// Create new Bases from Footprints
		for (TileArray space : dev.spaceList()) {

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
		for (TileArray base : new_bases)
			dev.addSpace(base);
	}

}