package edu.mit.ira.voxel;

import java.util.ArrayList;
import java.util.Random;

/**
 * Control Class is for managing Control Points
 * 
 * @author ira
 *
 */
public class Control {

	private ArrayList<ControlPoint> cPoints;

	/**
	 * Construct Emply List of Control Points
	 */
	public Control() {
		cPoints = new ArrayList<ControlPoint>();
	}

	/**
	 * Return List of ControlPoints
	 * 
	 * @return all Control Points
	 */
	public ArrayList<ControlPoint> points() {
		return cPoints;
	}

	/**
	 * Returns a subset of control points according to their type
	 * 
	 * @param type Type of Control Point to Return
	 * @return ControlPoints of parameter 'type'
	 */
	public ArrayList<ControlPoint> points(String type) {
		ArrayList<ControlPoint> subset = new ArrayList<ControlPoint>();
		for (ControlPoint p : cPoints) {
			if (p.getType().equals(type)) {
				subset.add(p);
			}
		}
		return subset;
	}

	/**
	 * Turns all control points on()
	 */
	public void on() {
		for (ControlPoint p : cPoints)
			p.on();
	}

	/**
	 * Turns all control points off()
	 */
	public void off() {
		for (ControlPoint p : cPoints)
			p.off();
	}

	/**
	 * Turns all control points of a specific type on()
	 * 
	 * @param type Type of ControlPoints to turn on
	 */
	public void on(String type) {
		for (ControlPoint p : cPoints) {
			if (p.getType().equals(type))
				p.on();
		}
	}

	/**
	 * Turns all control points of a specific type off()
	 * 
	 * @param type Type of ControlPoints to turn off
	 */
	public void off(String type) {
		for (ControlPoint p : cPoints) {
			if (p.getType().equals(type))
				p.off();
		}
	}

	/**
	 * Adds a control point randomly located inside a space
	 * 
	 * @param tag   Tag value of control point
	 * @param type  Type value of control point
	 * @param space TileArray to randomly place point within
	 */
	public void addPoint(String tag, String type, TileArray space) {
		ControlPoint p = randomPoint(space);
		if (p == null) {
			addPoint(tag, type, 0, 0);
		} else {
			addPoint(p, tag, type);
		}
	}

	/**
	 * Adds a control point at the given xy coordinates
	 * 
	 * @param tag  Tag value of control point
	 * @param type Type value of control point
	 * @param x    x
	 * @param y    y
	 */
	public void addPoint(String tag, String type, float x, float y) {
		ControlPoint p = new ControlPoint(x, y);
		addPoint(p, tag, type);
	}

	/**
	 * Adds a pre-made control point
	 * 
	 * @param p    pre-made ControlPoint to add to collection
	 * @param tag  Tag value of control point
	 * @param type Type value of control point
	 */
	private void addPoint(ControlPoint p, String tag, String type) {
		p.setTag(tag);
		p.setType(type);
		cPoints.add(p);
	}

	/**
	 * Returns a random control point with a coordinate at an existing tile in the
	 * TileArray
	 * 
	 * @param space TileArray to randomly place point within
	 * @return a ControlPoint within the array
	 */
	private ControlPoint randomPoint(TileArray space) {
		ControlPoint random_point = null;
		if (space.tileList().size() > 0) {
			Random rand = new Random();
			int random_index = rand.nextInt(space.tileList().size());
			Tile random_tile = space.tileList().get(random_index);
			float jitter_x = (float) (rand.nextFloat() - 0.5);
			float jitter_y = (float) (rand.nextFloat() - 0.5);
			// jitter helps keep distance-based dictionary entries unique
			random_point = new ControlPoint(random_tile.location.x + jitter_x, random_tile.location.y + jitter_y);
		}
		return random_point;
	}

	/**
	 * 
	 * @param p ControlPoint to remove
	 */
	public void removePoint(ControlPoint p) {
		cPoints.remove(p);
	}

	/**
	 * Clear all Control Points
	 */
	public void clearPoints() {
		cPoints.clear();
	}

	@Override
	public String toString() {
		String out = "";
		for (ControlPoint p : cPoints)
			out += p + "\n";
		return out;
	}
}