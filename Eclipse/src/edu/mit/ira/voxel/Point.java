package edu.mit.ira.voxel;

/**
 * A 3D Point Object
 */
public class Point {
	public float x, y, z;

	/**
	 * Constructs a 3D Point object with (x,y) and z-value is set to zero
	 * @param x x-location (horizontal)
	 * @param y y-location (horizontal)
	 */
	public Point(float x, float y) {
		this(x, y, 0);
	}

	/**
	 * Constructs a 3D Point object
	 * @param x x-location (horizontal)
	 * @param y y-location (horizontal)
	 * @param z z-location (vertical)
	 */
	public Point(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public String toString() {
		return "Point[" + x + ", " + y + ", " + z + "]";
	}
}