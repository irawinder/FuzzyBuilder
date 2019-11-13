package edu.mit.ira.fuzzy.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

/**
 * A TileArray is a collection of tiles
 * 
 * @author ira
 * 
 */
public class TileArray {

	// Name and Type of TileArray
	public String name;
	public String parent_name;
	public String type;
	
	// V units of eventual extrusion.
	private int toExtrude;

	// Hue Color of array, a number between 0-255
	public float hue;

	// Collection of Tiles
	private HashMap<String, Tile> tileMap;
	private ArrayList<Tile> tileList;

	/**
	 * Construct Empty TileArray
	 */
	public TileArray() {
		this("New Array", "TileArray");
	}

	/**
	 * Construct Empty TileArray
	 * 
	 * @param name Name of TileArray
	 * @param type Type of TileArray
	 */
	public TileArray(String name, String type) {
		this.name = name;
		this.type = type;
		tileMap = new HashMap<String, Tile>();
		tileList = new ArrayList<Tile>();
		this.parent_name = "";
		hue = 0;
		toExtrude = 1;
	}

	/**
	 * Set Name of TileArray
	 * 
	 * @param name name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Set Type of TileArray
	 * 
	 * @param type type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Get the Tile Array Type
	 * 
	 * @return String of tile array type
	 */
	public String getType() {
		return this.type;
	}
	
	/**
	 * Set the Utility Parameter to store amount of children layers to extrude
	 * 
	 * @param toExtrude
	 */
	public void setExtrude(int toExtrude) {
		this.toExtrude = toExtrude;
	}
	
	/**
	 * Get the amount of children layers to extrude
	 * 
	 * @return integer number of layers to extrude
	 */
	public int toExtrude() {
		return toExtrude;
	}

	/**
	 * Set Hue Value of TileArray
	 * 
	 * @param hue a number between 0 and 255
	 */
	public void setHue(float hue) {
		this.hue = hue % 255;
	}
	
	/**
	 * Get the hue color of the zone
	 * 
	 * @return a hue value between 0 and 255
	 */
	public float getHue() {
		return this.hue;
	}
	
	/**
	 * get the hue color of the zone
	 * 
	 * @return a hue value between 0 and 360
	 */
	public float getHueDegree() {
		return 360 * this.hue / 255;
	}

	/**
	 * Set the parent name of the TileArray
	 * 
	 * @param parent The name of the parent from which the TileArray is derived, if
	 *               any
	 */
	public void setParent(String parent) {
		this.parent_name = parent;
	}

	/**
	 * The HashMap key used for entire TileArray
	 * 
	 * @return a key value of format (parent_name + "/" + name)
	 */
	public String hashKey() {
		return parent_name + "/" + name;
	}

	/**
	 * Return Tiles
	 * 
	 * @return HashMap of all tiles in TileArray
	 */
	public HashMap<String, Tile> tileMap() {
		return tileMap;
	}

	/**
	 * Return Tiles
	 * 
	 * @return ArrayList of all tiles in TileArray
	 */
	public ArrayList<Tile> tileList() {
		return tileList;
	}

	/**
	 * Returns true if TileArray contains Tile
	 * 
	 * @param t Tile we want to check for
	 * @return true if TileArray contains Tile t
	 */
	public boolean hasTile(Tile t) {
		return tileMap.get(t.id) != null;
	}

	/**
	 * Returns true if type parameter passed to method matches type parameter of
	 * TileArray
	 * 
	 * @param type type value we wish to compare against
	 * @return returns true of TileArray type is same
	 */
	public boolean isType(String type) {
		return this.type.equals(type);
	}

	/**
	 * Returns true if a point is within the TileArray
	 * 
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return true if (x, y) lies within one of the tiles in TileArray
	 */
	public boolean pointInArray(float x, float y) {
		boolean inArray = false;
		for (Tile t : tileList) {
			float dX = Math.abs(t.location.x - x);
			float dY = Math.abs(t.location.y - y);
			if (dX < 0.51 * t.scale_uv && dY < 0.51 * t.scale_uv + 1) {
				inArray = true;
				break;
			}
		}
		return inArray;
	}

	/**
	 * Clear All Tiles in TileArray
	 */
	public void clearTiles() {
		tileMap.clear();
		tileList.clear();
	}

	/**
	 * Add Tile to TileArray
	 * 
	 * @param t Tile to add to Array
	 */
	public void addTile(Tile t) {
		tileMap.put(t.id, t);
		tileList.add(t);
	}

	/**
	 * Remove Tile from TileArray
	 * 
	 * @param tileKey Key value by which to look up Tile in HashMap of Tiles in
	 *                TileArray
	 */
	public void removeTile(String tileKey) {
		Tile t = tileMap.get(tileKey);
		tileList.remove(t);
		tileMap.remove(tileKey);
	}

	/**
	 * Remove Tile from TileArray
	 * 
	 * @param t The Tile to Remove from TileArray
	 */
	public void removeTile(Tile t) {
		tileList.remove(t);
		tileMap.remove(t.id);
	}

	/**
	 * Inherit the Tiles from a parent TileArray so that child tiles share the same
	 * location in memory
	 * 
	 * @param parent A parent TileArray
	 */
	public void inheritTiles(TileArray parent) {
		inheritAttributes(parent);
		for (Tile t : parent.tileList())
			addTile(t);
	}

	/**
	 * Inherit Attributes of another TileArray
	 * 
	 * @param parent A parent TileArray
	 */
	public void inheritAttributes(TileArray parent) {
		setHue(parent.hue);
		setParent(parent.parent_name + "/" + parent.name);
		setExtrude(parent.toExtrude);
	}

	/**
	 * Populate a grid of site tiles that fits within an exact vector boundary that
	 * defines site
	 * 
	 * @param boundary    Polygon that defines boundary of site
	 * @param scale_uv    Width of a square tile
	 * @param scale_w     Height of a tile
	 * @param units       Friendly units of a tile (e.g. "meters")
	 * @param rotation    Rotation of Tile Grid
	 * @param translation Translation Vector of Entire Grid
	 */
	public void makeTiles(Polygon boundary, float scale_uv, float scale_w, String units, float rotation,
			Point translation) {

		clearTiles();

		// Create a field of grid points that is certain
		// to uniformly saturate polygon boundary

		// Polygon origin and rectangular bounding box extents
		float origin_x = (float) (0.5 * (boundary.xMax() + boundary.xMin()));
		float origin_y = (float) (0.5 * (boundary.yMax() + boundary.yMin()));
		float boundary_w = boundary.xMax() - boundary.xMin();
		float boundary_h = boundary.yMax() - boundary.yMin();

		// maximum additional bounding box dimensions if polygon is rotated 90 degrees
		float easement = (float) (Math.max(boundary_w, boundary_h) * (Math.sqrt(2) - 1));
		boundary_w += easement;
		boundary_h += easement;

		int U = (int) ((boundary_w / scale_uv) + 1);
		int V = (int) ((boundary_h / scale_uv) + 1);
		float t_x = translation.x % scale_uv;
		float t_y = translation.y % scale_uv;

		for (int u = 0; u < U; u++) {
			for (int v = 0; v < V; v++) {

				// grid coordinates before rotation is applied
				float x_0 = (float) (boundary.xMin() - 0.5 * easement + u * scale_uv);
				float y_0 = (float) (boundary.yMin() - 0.5 * easement + v * scale_uv);

				// translate origin, rotate, shift back, then translate
				float sin = (float) Math.sin(rotation);
				float cos = (float) Math.cos(rotation);
				float x_f = +(x_0 - origin_x) * cos - (y_0 - origin_y) * sin + origin_x + t_x;
				float y_f = +(x_0 - origin_x) * sin + (y_0 - origin_y) * cos + origin_y + t_y;

				Point location = new Point(x_f, y_f);

				// Test which points are in the polygon boundary
				// and add them to tile set
				//
				if (boundary.containsPoint(location)) {
					Tile t = new Tile(u, v, location);
					t.setScale(scale_uv, scale_w, units);
					addTile(t);
				}
			}
		}
	}

	/**
	 * Get the neighboring Tiles of a specific tile
	 * 
	 * @param t Tile we wish to know the Neighbors of
	 * @return Adjacent Tiles that Exist within a TileArray
	 */
	public ArrayList<Tile> getNeighbors(Tile t) {
		ArrayList<Tile> adjacent = new ArrayList<Tile>();
		for (int dU = -1; dU <= +1; dU++) {
			for (int dV = -1; dV <= +1; dV++) {
				if (!(dU == 0 && dV == 0)) { // tile skips itself
					String tileKey = (t.u + dU) + "," + (t.v + dV) + "," + t.w;
					Tile adj = tileMap.get(tileKey);
					if (adj != null)
						adjacent.add(adj);
				}
			}
		}
		return adjacent;
	}

	/**
	 * Given an input TileArray, returns a new TileArray with just the edges
	 * 
	 * @return new TileArray that includes only the fringe tiles
	 */
	public TileArray getSetback() {
		TileArray setback = new TileArray();
		setback.inheritAttributes(this);

		// Add tiles that are at edge of parent TileArray
		for (Tile t : tileList()) {
			// Tile is on edge of parent cluster (Tile surrounded on all sides has 8
			// neighbors)
			if (getNeighbors(t).size() < 7) {
				setback.addTile(t);
			}
		}
		return setback;
	}

	/**
	 * Returns a TileArray that includes the N closest tiles to a point
	 * 
	 * @param Point center point of new TileArray to return
	 * @param area  Total area of new TileArray to return
	 * @return New TileArray of parameter 'area' centered at parameter 'point'
	 */
	public TileArray getClosestN(ControlPoint point, float area) {
		String p_name = point.getTag();
		TileArray closest = new TileArray(p_name, type);
		closest.inheritAttributes(this);

		// Dictionaries of tiles to sort by distance
		HashMap<Float, Tile> tiles = new HashMap<Float, Tile>();
		ArrayList<Float> distList = new ArrayList<Float>();

		// Calculate all distance
		for (Tile t : tileList()) {
			float dist = (float) Math.sqrt(Math.pow(t.location.x - point.x, 2) + Math.pow(t.location.y - point.y, 2));
			Random rand = new Random();
			float jitter = (float) (0.01 * rand.nextFloat());
			dist += jitter; // makes it unlikely that any two distances will be the same!
			distList.add(dist);
			tiles.put(dist, t);
		}

		// Sort Distance list in Ascending order
		Collections.sort(distList);

		if (tileList().size() > 0) {

			// Calculate how many tiles to add
			Tile sample = tileList().get(0);
			int numTiles = (int) (area / Math.pow(sample.scale_uv, 2));

			// Add closest N tiles to new array
			for (int i = 0; i < numTiles; i++) {
				if (i < tileList.size()) {
					float dist = distList.get(i);
					Tile close = tiles.get(dist);
					closest.addTile(close);
				}
			}
		}

		return closest;
	}

	/**
	 * Returns a new TileArray with child tiles subtracted from parent
	 * 
	 * @param child TileArray to subtract from current TileArray
	 * @return New TileArray with child subtracted from this TileArray
	 */
	public TileArray getDiff(TileArray child) {
		TileArray diff = new TileArray();
		diff.inheritAttributes(this);

		// Unless child tile doesn't exists in parent tile, add parent Tile to new
		// TileArray
		diff.inheritTiles(this);
		for (Tile t : tileList()) {
			if (child.hasTile(t)) {
				diff.removeTile(t);
			}
		}
		return diff;
	}

	/**
	 * Returns a new TileArray with child tiles added to parent
	 * 
	 * @param child TileArray to add to current TileArray
	 * @return New TileArray with child subtracted from this TileArray
	 */
	public TileArray getSum(TileArray child) {
		TileArray add = new TileArray();
		add.inheritAttributes(this);

		// If parent tile doesn't exists in child TileArray, add child Tile to new
		// TileArray
		add.inheritTiles(this);
		for (Tile t : child.tileList()) {
			if (!hasTile(t)) {
				add.addTile(t);
			}
		}
		return add;
	}

	/**
	 * Removes tiles from an existing TileArray, mutating it
	 * 
	 * @param child Child to subtract from existing TileArray
	 */
	public void subtract(TileArray child) {
		for (Tile t : child.tileList()) {
			if (hasTile(t)) {
				removeTile(t);
			}
		}
	}

	/**
	 * Add tiles from an existing TileArray, mutating it
	 * 
	 * @param child Child to add to current TileArray
	 */
	public void add(TileArray child) {
		for (Tile t : child.tileList()) {
			if (!hasTile(t)) {
				addTile(t);
			}
		}
	}

	/**
	 * Returns a new List of TileArrays generated according to Voronoi logic Need
	 * input of Tagged control points, where points are the nodes of Voronoi Cells
	 * https://en.wikipedia.org/wiki/Voronoi_diagram
	 * 
	 * @param points Site points that define Voronoi cells
	 * @return List of New TileArrays that Voronoi nest within the current TileArray
	 */
	public ArrayList<TileArray> getVoronoi(ArrayList<ControlPoint> points) {
		HashMap<String, TileArray> voronoiMap = new HashMap<String, TileArray>();
		ArrayList<TileArray> voronoiList = new ArrayList<TileArray>();

		// Initialize Voronoi "Cells" Based Upon Tagged Point Collection
		for (ControlPoint p : points) {
			String p_name = p.getTag();
			TileArray cell = new TileArray(p_name, this.type);
			cell.inheritAttributes(this);
			cell.setExtrude((int)p.getWeight());
			voronoiMap.put(p_name, cell);
			voronoiList.add(cell);
		}

		// Fore Each Tile in Site, Check Which Control Point (i.e. Voronoi Site Point)
		// it is closest to. This resembles a Voronoi algorithm
		//
		if (points.size() > 0) {
			for (Tile t : tileList()) {
				float min_distance = Float.POSITIVE_INFINITY;
				String closest_cell_name = "";
				for (ControlPoint p : points) {
					float distance = (float) Math
							.sqrt(Math.pow(p.x - t.location.x, 2) + Math.pow(p.y - t.location.y, 2));
					if (distance < min_distance) {
						min_distance = distance;
						closest_cell_name = p.getTag();
					}
				}
				TileArray closest_cell = voronoiMap.get(closest_cell_name);
				closest_cell.addTile(t);
			}
		}

		return voronoiList;
	}

	/**
	 * Return New 3D TileArray of Extruded Tiles
	 * 
	 * @param lowestFloor  the lowest z-level to extrude to
	 * @param highestFloor the highest z-level to extrude to
	 * @return return a volumetric TileArray extruded from current TileArray
	 */
	public TileArray getExtrusion(int lowestFloor, int highestFloor) {
		TileArray extrusion = new TileArray();
		extrusion.inheritAttributes(this);

		// Build Extrusion
		//
		for (Tile t : tileList()) {
			if (lowestFloor != highestFloor) {
				for (int i = lowestFloor; i < highestFloor; i++) {
					if (i == 0) {
						// Existing Ground-level tiles are referenced
						extrusion.addTile(t);
					} else {
						// New Tile must be created above and below ground
						Point newPoint = new Point(t.location.x, t.location.y, i * t.scale_w);
						Tile newTile = new Tile(t.u, t.v, i, newPoint);
						newTile.setScale(t.scale_uv, t.scale_w, t.scale_unit);
						extrusion.addTile(newTile);
					}
				}
			}
		}
		return extrusion;
	}

	@Override
	public String toString() {
		return this.name + " (" + this.type + "):" + tileMap.size() + "t";
	}
}