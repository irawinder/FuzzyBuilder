package edu.mit.ira.fuzzy;

import org.json.JSONObject;

/**
 * A Voxel is a primitive particle of space with a square base
 * 
 * @author ira
 * 
 */
public class Voxel {

	// Center Point of Voxel in real "geospatial" coordinates
	public Point location;

	// Rotation of voxel along Z axis, in radians
	public float rotation;

	// Width and Height of voxel
	public float width, height;

	// Type of Voxel
	public Use type;

	// Local integer coordinates of this voxel (for efficient adjacency analysis)
	public int u, v, w;

	/**
	 * Constructs a default Voxel
	 */
	public Voxel() {
		this.location = new Point();
		this.rotation = 0;
		this.width = 1;
		this.height = 1;
		this.type = null;
		this.u = 0;
		this.v = 0;
		this.w = 0;
	}

	public void setLocation(float x, float y, float z) {
		this.location = new Point(x, y, z);
	}

	public void setCoordinates(int u, int v, int w) {
		this.u = u;
		this.v = v;
		this.w = w;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public void setSize(float width, float height) {
		this.width = width;
		this.height = height;
	}

	public void setUse(Use type) {
		this.type = type;
	}

	public String coordKey() {
		return this.u + "," + this.v + "," + this.w;
	}

	@Override
	public String toString() {
		return type.toString() + " Voxel [" + this.u + "," + this.v + "," + this.w + "] at " + location;
	}

	public JSONObject serialize() {
		JSONObject voxelJSON = new JSONObject();
		voxelJSON.put("location", this.location.serialize());
		voxelJSON.put("rotation", this.rotation);
		voxelJSON.put("width", this.width);
		voxelJSON.put("height", this.height);
		voxelJSON.put("type", this.type.toString());
		return voxelJSON;
	}
}