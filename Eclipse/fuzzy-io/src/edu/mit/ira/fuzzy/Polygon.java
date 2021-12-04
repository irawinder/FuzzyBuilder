package edu.mit.ira.fuzzy;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Polygon class: a series of connected, closed points
 * 
 * @author ira
 * 
 */
public class Polygon {
	private ArrayList<Point> vertices;
	private ArrayList<Line> edges;
	private float xMin, xMax, yMin, yMax;

	/**
	 * Constructs an empty polygon
	 */
	public Polygon() {
		vertices = new ArrayList<Point>();
		edges = new ArrayList<Line>();
	}

	/**
	 * Constructs a closed polygon using a list of points. Points are connected in
	 * order with last point connected to first point
	 *
	 * @param vertices list of points that comprise polygon vertices
	 */
	public Polygon(ArrayList<Point> vertices) {
		this();
		for (Point vertex : vertices) {
			this.addVertex(vertex);
		}
	}

	public Polygon clone() {
		Polygon clone = new Polygon();
		for (Point vertex : this.vertices) {
			Point p = new Point(vertex.x, vertex.y, vertex.z);
			clone.addVertex(p);
		}
		return clone;
	}

	/**
	 * Retrieve the list of polygon corners
	 * 
	 * @return corners of polygon
	 */
	public ArrayList<Point> getVertices() {
		return this.vertices;
	}

	/**
	 * Retrieve the list of polygon corners
	 * 
	 * @return corners of polygon
	 */
	public ArrayList<Line> getEdges() {
		return this.edges;
	}

	/**
	 * Add a vertex to the polygon
	 * 
	 * @param p Point location of new vertex
	 */
	public void addVertex(Point p) {
		vertices.add(p);

		// Generate Edges for Polygon once it has more than 3 vertices
		//
		if (vertices.size() > 1) {
			createEdges();
		}

		// Generate Min-Max Values
		//
		calcMinMax();
	}

	/**
	 * Create Polygon Edge Objects, Composed of vertices
	 */
	private void createEdges() {
		edges.clear();
		int n = vertices.size();
		for (int i = 0; i < n - 1; i++) {
			Line l = new Line(vertices.get(i), vertices.get(i + 1));
			edges.add(l);
		}
		if(n > 2) {
			Line l = new Line(vertices.get(n - 1), vertices.get(0));
			edges.add(l);
		}
	}

	/**
	 * Calculate minimum and maximum coordinate values
	 */
	private void calcMinMax() {
		xMin = Float.POSITIVE_INFINITY;
		xMax = Float.NEGATIVE_INFINITY;
		yMin = Float.POSITIVE_INFINITY;
		yMax = Float.NEGATIVE_INFINITY;

		for (Point p : vertices) {
			xMin = Math.min(xMin, p.x);
			xMax = Math.max(xMax, p.x);
			yMin = Math.min(yMin, p.y);
			yMax = Math.max(yMax, p.y);
		}
	}

	/**
	 * @return lowest-bound x coordinate of polygon
	 */
	public float xMin() {
		return this.xMin;
	}

	/**
	 * @return highest-bound x coordinate of polygon
	 */
	public float xMax() {
		return this.xMax;
	}

	/**
	 * @return lowest-bound y coordinate of polygon
	 */
	public float yMin() {
		return this.yMin;
	}

	/**
	 * @return highest-bound y coordinate of polygon
	 */
	public float yMax() {
		return this.yMax;
	}

	/**
	 * clear entire polygon of vertices and edges
	 */
	public void clear() {
		vertices.clear();
		edges.clear();
	}

	/**
	 * 
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	public void translate(float x, float y) {
		this.translate(x, y, 0);
	}

	/**
	 * 
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param z z-coordinate
	 */
	public void translate(float x, float y, float z) {
		for (Point p : vertices) {
			p.x += x;
			p.y += y;
			p.z += z;
		}
		xMin += x;
		xMax += x;
		yMin += y;
		yMax += y;
	}

	/**
	 * Check if a point is within the polygon
	 * 
	 * @param p Point we wish to know whether inside polygon or not
	 * @return Returns 'true' if Point p is inside of polygon
	 */
	public boolean containsPoint(Point p) {
		int num_nodes = vertices.size();

		// Make a horizontal line to cut through geometries
		//
		Point o = new Point(this.xMin(), p.y);
		Point f = new Point(this.xMax(), p.y);
		Line horizontal = new Line(o, f);

		// If polygon has 3 or more vertices, count how many times a
		// horizontal line drawn from negative infinity to point p
		// intersects with the polygon. If odd, the Point p is inside
		// the polygon - https://en.wikipedia.org/wiki/Point_in_polygon
		//
		if (num_nodes > 2) {
			int num_intersect = 0;
			for (Line l : edges) {
				Point intersect = horizontal.lineIntersect(l);
				if (intersect != null && intersect.x < p.x) {
					num_intersect++;
				}
			}
			if (num_intersect % 2 == 1) { // check for odd
				return true;
			} else {
				return false;
			}

		} else {
			return false;
		}
	}

	/**
	 * Check if a Polygon is inside of this polygon
	 *
	 * @param polygon shape that we wish to know is contained
	 * @return returns "true" if polygon paramter is inside of this polygon
	 */
	public boolean containsPolygon(Polygon polygon) {
		for (Point p : polygon.getVertices()) {
			if (!this.containsPoint(p)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check if a Polygon intersects another polygon
	 *
	 * @param polygon shape that we wish to know is intersecting
	 * @return returns "true" if polygons intersect polygon
	 */
	public boolean intersectsPolygon(Polygon polygon) {
		if (this.containsPolygon(polygon)) {
			return true;
		} else if (polygon.containsPolygon(this)) {
			return true;
		} else {
			for (Line thisEdge : this.edges) {
				for (Line thatEdge : polygon.edges) {
					if (thisEdge.lineIntersect(thatEdge) != null) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Check if Polygon points are coplanar
	 * 
	 * @return true of polygon vertices are in the same plane
	 */
	public boolean isCoplanar() {

		// polygons of three points or less are always coplanar
		if (this.vertices.size() < 4) {
			return true;

		} else {

			// derive the equation for the plan defined by the first three points
			// https://www.tutorialspoint.com/program-to-find-equation-of-a-plane-passing-through-3-points-in-cplusplus
			// ax + by + cz = d
			Point p1 = this.vertices.get(0);
			Point p2 = this.vertices.get(1);
			Point p3 = this.vertices.get(2);
			float a1 = p2.x - p1.x;
			float b1 = p2.y - p1.y;
			float c1 = p2.z - p1.z;
			float a2 = p3.x - p1.x;
			float b2 = p3.y - p1.y;
			float c2 = p3.z - p1.z;
			float a = b1 * c2 - b2 * c1;
			float b = a2 * c1 - a1 * c2;
			float c = a1 * b2 - b1 * a2;
			float d = (-a * p1.x - b * p1.y - c * p1.z);

			// check that all remaining points are in this plan
			for (int i = 3; i < this.vertices.size(); i++) {
				Point vertex = this.vertices.get(i);
				float d_i = a * vertex.x + b * vertex.y + c * vertex.z;

				// floating points aren't precise enough, so we allow for a
				// number that is "close enough"
				//
				float tolerance = (float) 0.001;
				if (d_i < d - tolerance || d_i > d + tolerance) {
					return false;
				}
			}
			return true;
		}
	}

	public JSONObject serialize() {

		JSONArray vertexArray = new JSONArray();
		for (int i = 0; i < this.vertices.size(); i++) {
			JSONObject vertex = vertices.get(i).serialize();
			vertexArray.put(i, vertex);
		}

		JSONObject polygonJSON = new JSONObject();
		polygonJSON.put("vertices", vertexArray);
		return polygonJSON;
	}
}