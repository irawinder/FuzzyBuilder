package edu.mit.ira.fuzzy;

import org.json.JSONObject;

/**
 * A 3D Point object with x, y, and z value
 * 
 * @author ira
 *
 */
public class Point {
	public float x, y, z;

	/**
	 * Constructs a 3D Point object with (x,y) and z-value is set to zero
	 */
	public Point() {
		this(0, 0);
	}

	/**
	 * Constructs a 3D Point object with (x,y) and z-value is set to zero
	 * 
	 * @param x x-location (horizontal)
	 * @param y y-location (horizontal)
	 */
	public Point(float x, float y) {
		this(x, y, 0);
	}

	/**
	 * Constructs a 3D Point object
	 * 
	 * @param x x-location (horizontal)
	 * @param y y-location (horizontal)
	 * @param z z-location (vertical)
	 */
	public Point(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public JSONObject serialize() {
		JSONObject pointJSON = new JSONObject();
		double decimals = 1000d;
		double _x = Math.round(decimals * this.x) / decimals;
		double _y = Math.round(decimals * this.y) / decimals;
		double _z = Math.round(decimals * this.z) / decimals;
		pointJSON.put("x", _x);
		pointJSON.put("y", _y);
		pointJSON.put("z", _z);
		return pointJSON;
	}
}