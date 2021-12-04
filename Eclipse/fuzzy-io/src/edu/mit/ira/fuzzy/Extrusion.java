package edu.mit.ira.fuzzy;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Extrusion class: A volumetric upward extrusion of a base polygon
 * 
 * @author ira
 * 
 */
public class Extrusion {

	private Polygon base;
	private Polygon top;
	private ArrayList<Line> sideEdges;
	private ArrayList<Polygon> sideFaces;

	/**
	 * Extrude a flat polygon upward by a given amount. if base polygon is not flat,
	 * the volume is not extruded
	 * 
	 * @param base     a flat polygon (will not extrude if polygon is not flat)
	 * @param distance
	 */
	public Extrusion(Polygon base, float distance) {

		// Clone the base
		this.base = base.clone();

		this.top = null;
		this.sideEdges = new ArrayList<Line>();
		this.sideFaces = new ArrayList<Polygon>();

		// Can't extrude by distance of 0, nor can you extrude a polygon with no vertices
		if (distance != 0 && base.getVertices().size() > 0) {

			// Create the top, offset upward from base by distance parameter
			this.top = this.base.clone();
			this.top.translate(0, 0, distance);

			// Create the sides (vertical rectangles)
			int numVertices = this.base.getVertices().size();
			for (int i = 0; i < numVertices; i++) {

				// Get the corners of the side
				Point p1 = this.base.getVertices().get(i);
				Point p2 = this.top.getVertices().get(i);

				// Add the Edges
				Line edge = new Line(p1, p2);
				this.sideEdges.add(edge);

				// add sideFaces
				if (numVertices > 1) {

					// break if base polygon only has two points, as an
					// addition face would be redundant with the first
					if (numVertices == 2 && i == 1)
						break;

					// Allow for the array to wrap around to the beginning
					int i_next;
					if (i == numVertices - 1) {
						i_next = 0;
					} else {
						i_next = i + 1;
					}

					// Get the corners of the side
					Point p3 = this.top.getVertices().get(i_next);
					Point p4 = this.base.getVertices().get(i_next);

					// Build the side
					Polygon side = new Polygon();
					side.addVertex(new Point(p1.x, p1.y, p1.z));
					side.addVertex(new Point(p2.x, p2.y, p2.z));
					side.addVertex(new Point(p3.x, p3.y, p3.z));
					side.addVertex(new Point(p4.x, p4.y, p4.z));
					this.sideFaces.add(side);
				}
			}
		}
	}

	public JSONObject serialize() {

		// Serialize Edges
		JSONArray edgesJSON = new JSONArray();
		int edgeIndex = 0;

		for (Line edge : this.base.getEdges()) {
			edgesJSON.put(edgeIndex, edge.serialize());
			edgeIndex++;
		}
		if (this.top != null) {
			for (Line edge : this.top.getEdges()) {
				edgesJSON.put(edgeIndex, edge.serialize());
				edgeIndex++;
			}
		}
		for (Line edge : this.sideEdges) {
			edgesJSON.put(edgeIndex, edge.serialize());
			edgeIndex++;
		}

		// Serialize Faces
		JSONArray facesJSON = new JSONArray();
		int faceIndex = 0;

		JSONObject baseJSON = this.base.serialize();
		baseJSON.put("type", "base");
		facesJSON.put(faceIndex, baseJSON);
		faceIndex++;

		if (this.top != null) {
			JSONObject topJSON = this.top.serialize();
			topJSON.put("type", "top");
			facesJSON.put(faceIndex, topJSON);
			faceIndex++;
		}

		for (Polygon side : this.sideFaces) {
			JSONObject sideJSON = side.serialize();
			sideJSON.put("type", "side");
			facesJSON.put(faceIndex, sideJSON);
			faceIndex++;
		}

		JSONObject extrusionJSON = new JSONObject();
		extrusionJSON.put("edges", edgesJSON);
		extrusionJSON.put("faces", facesJSON);
		return extrusionJSON;
	}
}