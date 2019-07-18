package edu.mit.ira.voxel;

import java.util.ArrayList;

/**
 * A Tile is a primitive particle of space
 * 
 * @author ira
 * 
 */
public class Tile {

	// Center Point of Tile in real "geospatial" coordinates
	public Point location;

	// Integer coordinates of tile
	public int u, v, w;

	// How many units a tile represents
	// i.e. [units/tile]
	public float scale_uv, scale_w;
	public String scale_unit;

	// Unique Tile ID, composite of integer coordinates
	public String id;

	// Type of Tile
	public String type;

	// Adjacent Tiles' names
	public ArrayList<String> adjacent;

	/**
	 * Constructs a Tile
	 * 
	 * @param u integer 'u' coordinate (horizontal)
	 * @param v integer 'v' coordinate (horizontal)
	 * @param w integer 'w' coordinate (vertical)
	 * @param p center-point of Tile
	 */
	public Tile(int u, int v, int w, Point p) {
		this.location = p;
		this.u = u;
		this.v = v;
		this.w = w;
		id = u + "," + v + "," + w;
		type = null;
		scale_uv = 1;
		scale_w = (float) 0.5;
		scale_unit = "";
	}

	/**
	 * Constructs a Tile with vertical coordinate 'w' set to zero
	 * 
	 * @param u integer 'u' coordinate (horizontal)
	 * @param v integer 'v' coordinate (horizontal)
	 * @param p center-point of Tile
	 */
	public Tile(int u, int v, Point p) {
		this(u, v, 0, p);
	}

	/**
	 * Define the scale of the tile
	 * 
	 * @param scale_uv horizontal units per tile
	 * @param scale_w  vertical units per tile
	 * @param unit     friendly name for units (e.g. "meters")
	 */
	public void setScale(float scale_uv, float scale_w, String unit) {
		this.scale_uv = scale_uv;
		this.scale_w = scale_w;
		this.scale_unit = unit;
	}

	/**
	 * Adds name and elevation of neighbor to adjacency map
	 * 
	 * @param _id the id value of an adjacent Tile
	 */
	public void addAdjacent(String _id) {
		adjacent.add(_id);
	}

	@Override
	public String toString() {
		return type + " Tile[" + u + "," + v + "," + w + "] at " + location;
	}
}