package edu.mit.ira.fuzzy.builder.sample;

import edu.mit.ira.fuzzy.base.Point;
import edu.mit.ira.fuzzy.base.Polygon;
import edu.mit.ira.fuzzy.base.TileArray;
import edu.mit.ira.fuzzy.builder.DevelopmentEditor;

/**
 * Generate a site and buildings according to a randomly generate polygon
 * 
 * @author Ira Winder
 *
 */
public class RandomSite extends DevelopmentEditor{
	
	public RandomSite(float tileW, float tileH, float x, float y, float width, float height) {
		super();
		control.setDefault(x, y);
		load(tileW, tileH, x, y, width, height);
		resetEditor();
	}
	
	/**
	 * Generates a randomly configured model at x, y
	 * 
	 * @param x
	 * @param y
	 */
	public void load(float tileW, float tileH, float x, float y, float width, float height) {
		// Initialize Random Model and Control Points
		setTileWidth(tileW);
		setTileHeight(tileH);
		site_boundary.randomShape(x, y, 5, width, height);
		initVertexControl(site_boundary);
		buildSite(control);
		initPlotControl();
		buildZones(control);
		initVoidControl();
		buildFootprints(control);
		buildBases();
	}
	
	/**
	 * Initialize Vertex Control Points
	 */
	public void initVertexControl(Polygon boundary) {
		vert_counter = 1;
		String point_prefix = "Vertex";
		for (Point p : boundary.getCorners()) {
			control.addPoint(point_prefix + " " + vert_counter, point_prefix, p.x, p.y);
			vert_counter++;
		}
	}

	/**
	 * Initialize Plot Control Points
	 */
	public void initPlotControl() {
		plot_counter = 1;
		for (TileArray space : spaceList()) {
			if (space.type.equals("site")) {
				String point_prefix = "Plot";
				for (int i = 0; i < 3; i++) {
					control.addPoint(point_prefix + " " + plot_counter, point_prefix, space);
					plot_counter++;
				}
			}
		}
	}

	/**
	 * Initialize Void Control Points
	 */
	public void initVoidControl() {
		void_counter = 1;
		for (TileArray space : spaceList()) {
			// Add Control Point to Zone
			if (space.type.equals("zone")) {
				String point_prefix = "Void";
				void_counter++;
				control.addPoint(point_prefix + " " + void_counter, point_prefix, space);
			}
		}
	}
}
