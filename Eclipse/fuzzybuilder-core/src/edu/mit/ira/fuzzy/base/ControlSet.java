package edu.mit.ira.fuzzy.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

/**
 * Control Class is for managing a set of multiple Control Points
 * 
 * @author ira
 *
 */
public class ControlSet {

	private ArrayList<ControlPoint> pointList;
	private HashMap<UUID, ControlPoint> pointMap;
	
	private float minX, maxX;
	private float minY, maxY;
	private float defaultX, defaultY;
	
	/**
	 * Construct Empty List of Control Points
	 */
	public ControlSet() {
		pointList = new ArrayList<ControlPoint>();
		pointMap = new HashMap<UUID, ControlPoint>();
		
		defaultX = 0;
		defaultY = 0;
		
		minX = defaultX;
		maxX = defaultX;
		minY = defaultY;
		maxY = defaultY;
	}
	
	/**
	 * set default control point location
	 * 
	 * @param x
	 * @param y
	 */
	public void setDefault(float x, float y) {
		defaultX = x;
		defaultY = y;
		minX = defaultX;
		maxX = defaultX;
		minY = defaultY;
		maxY = defaultY;
	}

	/**
	 * Return List of ControlPoints
	 * 
	 * @return all Control Points
	 */
	public ArrayList<ControlPoint> points() {
		return pointList;
	}
	
	/**
	 * Return HashMap of ControlPoints
	 * 
	 * @return all Control Points as HashMap
	 */
	public HashMap<UUID, ControlPoint> pointMap() {
		return pointMap;
	}
	
	/**
	 * Get minimum X value
	 * 
	 * @return minX
	 */
	public float minX() {
		return minX;
	}
	
	/**
	 * Get maximum X value
	 * 
	 * @return maxX
	 */
	public float maxX() {
		return maxX;
	}
	
	/**
	 * Get minimum Y value
	 * 
	 * @return minY
	 */
	public float minY() {
		return minY;
	}
	
	/**
	 * Get maximum Y value
	 * 
	 * @return maxY
	 */
	public float maxY() {
		return maxY;
	}

	/**
	 * Returns a subset of control points according to their type
	 * 
	 * @param type Type of Control Point to Return
	 * @return ControlPoints of parameter 'type'
	 */
	public ArrayList<ControlPoint> points(String type) {
		ArrayList<ControlPoint> subset = new ArrayList<ControlPoint>();
		for (ControlPoint p : pointList) {
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
		for (ControlPoint p : pointList)
			p.on();
	}

	/**
	 * Turns all control points off()
	 */
	public void off() {
		for (ControlPoint p : pointList)
			p.off();
	}

	/**
	 * Turns all control points of a specific type on()
	 * 
	 * @param type Type of ControlPoints to turn on
	 */
	public void on(String type) {
		for (ControlPoint p : pointList) {
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
		for (ControlPoint p : pointList) {
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
	 * Adds a premade control point
	 * 
	 * @param p    premade ControlPoint to add to collection
	 * @param tag  Tag value of control point
	 * @param type Type value of control point
	 */
	private void addPoint(ControlPoint p, String tag, String type) {
		p.setTag(tag);
		p.setType(type);
		pointList.add(p);
		pointMap.put(p.getUniqueID(), p);
		calcMinMax();
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
		pointList.remove(p);
		calcMinMax();
	}

	/**
	 * Clear all Control Points
	 */
	public void clearPoints() {
		pointList.clear();
		minX = defaultX;
		maxX = defaultX;
		minY = defaultY;
		maxY = defaultY;
	}
	
	/**
	 * update minimum and maximum extents of model
	 */
	public void calcMinMax() {
		if (pointList.size() > 0) {
			minX = Float.POSITIVE_INFINITY;
			maxX = Float.NEGATIVE_INFINITY;
			minY = Float.POSITIVE_INFINITY;
			maxY = Float.NEGATIVE_INFINITY;
			for (ControlPoint p : pointList) {
				minX = Math.min(minX, p.x);
				maxX = Math.max(maxX, p.x);
				minY = Math.min(minY, p.y);
				maxY = Math.max(maxY, p.y);
				//System.out.println((float) minX + ", " + (float) maxX + ", " + (float) minY + ", " + (float) maxY);
			}
		} else {
			minX = defaultX;
			maxX = defaultX;
			minY = defaultY;
			maxY = defaultY;
		}
	}

	@Override
	public String toString() {
		String out = "";
		for (ControlPoint p : pointList)
			out += p + "\n";
		return out;
	}
}