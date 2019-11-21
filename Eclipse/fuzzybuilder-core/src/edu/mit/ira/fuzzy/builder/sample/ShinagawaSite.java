package edu.mit.ira.fuzzy.builder.sample;

import edu.mit.ira.fuzzy.base.ControlPoint;
import edu.mit.ira.fuzzy.builder.DevelopmentEditor;

/**
 * Load a hard-coded polygon derived from the Shinagawa Rail project
 * 
 * @author Ira Winder
 *
 */
public class ShinagawaSite extends DevelopmentEditor{
	
	public ShinagawaSite() {
		super();
		setName("Shinagawa Station");
		load();
		resetEditor();
	}
	
    /**
	 * Load specific ControlPoints (i.e. hard-coded, not random)
	 *
	 */
	public void load() {

		// 2019.07.25 JR Site Vertices by Ira
		// Eventually, we need a method that imports these
		// values from external data (e.g. csv)

		int vert_counter = 1;
		String vertex_prefix = "Vertex";

		control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 1056, 509);
		vert_counter++;
		control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 950, 509);
		vert_counter++;
		control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 887, 504);
		vert_counter++;
		control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 794, 488);
		vert_counter++;
		control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 717, 466);
		vert_counter++;
		control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 589, 425);
		vert_counter++;
		control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 510, 385);
		vert_counter++;
		control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 518, 368);
		vert_counter++;
		control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 432, 336);
		vert_counter++;
		control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 405, 323);
		vert_counter++;
		control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 303, 260);
		vert_counter++;
		control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 307, 242);
		vert_counter++;
		control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 407, 280);
		vert_counter++;
		control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 471, 294);
		vert_counter++;
		control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 567, 321);
		vert_counter++;
		control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 673, 357);
		vert_counter++;
		control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 746, 382);
		vert_counter++;
		control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 888, 435);
		vert_counter++;
		control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 970, 463);
		vert_counter++;
		control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 1053, 480);
		vert_counter++;

		int plot_counter = 1;
		String plot_prefix = "Plot";
		control.addPoint(plot_prefix + " " + plot_counter, plot_prefix, 350, 276);
		plot_counter++;
		control.addPoint(plot_prefix + " " + plot_counter, plot_prefix, 406, 297);
		plot_counter++;
		control.addPoint(plot_prefix + " " + plot_counter, plot_prefix, 458, 318);
		plot_counter++;
		control.addPoint(plot_prefix + " " + plot_counter, plot_prefix, 597, 385);
		plot_counter++;
		control.addPoint(plot_prefix + " " + plot_counter, plot_prefix, 633, 401);
		plot_counter++;
		control.addPoint(plot_prefix + " " + plot_counter, plot_prefix, 788, 442);
		plot_counter++;
		control.addPoint(plot_prefix + " " + plot_counter, plot_prefix, 843, 465);
		plot_counter++;
		control.addPoint(plot_prefix + " " + plot_counter, plot_prefix, 703, 347);
		plot_counter++;
		control.addPoint(plot_prefix + " " + plot_counter, plot_prefix, 945, 484);
		plot_counter++;
		control.addPoint(plot_prefix + " " + plot_counter, plot_prefix, 1010, 498);
		plot_counter++;

		// Init Polygon
		for(ControlPoint p : control.points()) {
			if(p.getType().equals("Vertex")) {
				site_boundary.addVertex(p);
			}
		}

		// Override default Grid Properties
		setTileWidth(11);
		setTileHeight(5);
		setTileRotation((float) 0.34);
		setTileTranslation(0, 0);

		// Init Model from Control Points
		buildSite(control);
		buildZones(control);
		buildFootprints(control);
		buildBases();

	}
}
