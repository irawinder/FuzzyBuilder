package edu.mit.ira.voxel;

/**
 * ControlPoint class, a point with associated abstract attributes of 
 * tag, type, and weight
 * @author ira
 *
 */
public class ControlPoint extends Point {

	// String tag of point
	private String tag;

	// The type of control point
	private String type;

	// Numerical weight of point
	private Float weight;

	// Activate control point
	private boolean active;

	/**
	 * Constructor for Control Point
	 * @param x x-location
	 * @param y y-location
	 */
	public ControlPoint(float x, float y) {
		super(x,y);
		tag = "";
		weight = (float) 1;
		active = true;
	}

	/**
	 * Turn ControlPoint Off
	 */
	public void on() {
		active = true;
	}

	/**
	 * Turn Control Point Off
	 */
	public void off() {
		active = false;
	}

	/**
	 * Is ControlPoint Active
	 * @return true if ControlPoint is active
	 */
	public boolean active() {
		return active;
	}

	/**
	 * Set the Tag Value of the Point
	 * @param tag value
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * Get the Tag Value of the Point
	 * @return tag
	 */
	public String getTag() {
		return this.tag;
	}

	/**
	 * Set the Type Value of the Point
	 * @param type value
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Get the Type Value of the Point
	 * @return type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Set the Weight of the Point
	 * @param weight value
	 */
	public void setWeight(float weight) {
		this.weight = weight;
	}

	/**
	 * Get the Weight of the Point
	 * @return weight
	 */
	public float getWeight() {
		return this.weight;
	}

	@Override
	public String toString() {
		return tag + "; Point[" + x + ", " + y + ", " + z + "]";
	}

}