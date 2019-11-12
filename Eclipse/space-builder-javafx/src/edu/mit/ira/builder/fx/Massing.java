package edu.mit.ira.builder.fx;

import edu.mit.ira.builder.Builder;
import edu.mit.ira.voxel.Control;
import edu.mit.ira.voxel.ControlPoint;
import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
/**
 * Massing View Model
 *
 * @author Ira Winder
 *
 */
public class Massing extends SubScene implements ContentContainer {
	
    static Group root = new Group();
    SubScene scene3D, scene2D;
    View3D view3D;
    
    Underlay map_model;
	Builder form_model;
    
    // Migrate Builder() visual parameters to GUI_FX:
    // TODO
    
    /**
     * Initialize a new content container for Massing
     * 
     * @param subscene master container for content
     */
    public Massing(double width, double height) {
    	super(root, width, height);
    	makeContent();
    }
    
    @Override
    public void makeContent() {
        
    	map_model = new Underlay();
    	form_model = new Builder();
    	
    	view3D = new View3D();
    	view3D.setZoom(-1000);
    	view3D.setPan(325, 425, 0);
    	view3D.setRotateV(-20);
    	view3D.setRotateH(-45);
    	
    	// Initialize with Random Scenario
    	loadRandomScenario(map_model, form_model, view3D);
    	
    	scene3D = new SubScene(view3D.getGroup(), getWidth(), getHeight(), true, SceneAntialiasing.BALANCED);
    	setViewModel(scene3D, view3D);
    	
    	Label l = new Label("UI Overlay");
    	Group controls = new Group(l);
        scene2D = new SubScene(controls, getWidth(), getHeight());
        scene2D.setFill(Color.TRANSPARENT);
    	
		// Draw Control Point at Mouse Hover
		
		// Draw Tagged Control Point Labels
		
		// Draw Info at Mouse Hover
		
		// Draw Info/Instructions
		
		// Draw Attribute Summary
    	
        root = new Group(scene3D, scene2D);
        setRoot(root);

        // Mouse and Keyboard Events
        view3D.handleMouseEvents(this);
        
    }
    
    // Init View Model
    public void setViewModel(SubScene scene3D, View3D view3D) {
    	scene3D.setRoot(view3D.getGroup());
    	scene3D.setFill(view3D.getBackground());
        scene3D.setCamera(view3D.getCamera());
    }
    
    /**
     * Load Random Site and View Model Parameters
     * 
     * @param map_model
     * @param form_model
     * @param view3D
     * @param scene3D
     */
    public void loadRandomScenario(Underlay map_model, Builder form_model, View3D view3D) {
    	map_model.setImage("data/default_site_white.png");
    	map_model.setScale(0.5);
    	map_model.setOpacity(1.00);

    	form_model.initModel();
    	form_model.loadRandomModel(375, 375);
    	
    	//view3D.initModel();
    	view3D.setBackground(Color.hsb(0,0,1.0));
    	view3D.setFormModel(form_model);
    	view3D.setMapModel(map_model);
		view3D.render();
    }
    
    /**
     * Load JR Site and View Model Parameters
     * 
     * @param map_model
     * @param form_model
     * @param view3D
     * @param scene3D
     */
    public void loadJRScenario(Underlay map_model, Builder form_model, View3D view3D) {
    	map_model.setImage("data/jr_site.png");
    	map_model.setScale(0.5);
    	map_model.setOpacity(0.75);

    	form_model.initModel();
    	loadJRModel(form_model);
    	
    	//view3D.initModel();
    	view3D.setBackground(Color.hsb(0,0,0.2));
    	//view3D.setZoom(-1000);
    	//view3D.setPan(719, 410, 0);
    	//view3D.setRotateV(-20);
    	//view3D.setRotateH(-45);
    	view3D.setFormModel(form_model);
    	view3D.setMapModel(map_model);
		view3D.render();
    }
    
    /**
	 * Load specific ControlPoints (i.e. hard-coded, not random)
	 *
	 */
	public void loadJRModel(Builder form_model) {

		// 2019.07.25 JR Site Vertices by Ira
		// Eventually, we need a method that imports these
		// values from external data (e.g. csv)

		form_model.control = new Control();

		int vert_counter = 1;
		String vertex_prefix = "Vertex";

		form_model.control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 1056, 509);
		vert_counter++;
		form_model.control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 950, 509);
		vert_counter++;
		form_model.control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 887, 504);
		vert_counter++;
		form_model.control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 794, 488);
		vert_counter++;
		form_model.control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 717, 466);
		vert_counter++;
		form_model.control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 589, 425);
		vert_counter++;
		form_model.control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 510, 385);
		vert_counter++;
		form_model.control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 518, 368);
		vert_counter++;
		form_model.control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 432, 336);
		vert_counter++;
		form_model.control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 405, 323);
		vert_counter++;
		form_model.control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 303, 260);
		vert_counter++;
		form_model.control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 307, 242);
		vert_counter++;
		form_model.control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 407, 280);
		vert_counter++;
		form_model.control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 471, 294);
		vert_counter++;
		form_model.control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 567, 321);
		vert_counter++;
		form_model.control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 673, 357);
		vert_counter++;
		form_model.control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 746, 382);
		vert_counter++;
		form_model.control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 888, 435);
		vert_counter++;
		form_model.control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 970, 463);
		vert_counter++;
		form_model.control.addPoint(vertex_prefix + " " + vert_counter, vertex_prefix, 1053, 480);
		vert_counter++;

		int plot_counter = 1;
		String plot_prefix = "Plot";
		form_model.control.addPoint(plot_prefix + " " + plot_counter, plot_prefix, 350, 276);
		plot_counter++;
		form_model.control.addPoint(plot_prefix + " " + plot_counter, plot_prefix, 406, 297);
		plot_counter++;
		form_model.control.addPoint(plot_prefix + " " + plot_counter, plot_prefix, 458, 318);
		plot_counter++;
		form_model.control.addPoint(plot_prefix + " " + plot_counter, plot_prefix, 597, 385);
		plot_counter++;
		form_model.control.addPoint(plot_prefix + " " + plot_counter, plot_prefix, 633, 401);
		plot_counter++;
		form_model.control.addPoint(plot_prefix + " " + plot_counter, plot_prefix, 788, 442);
		plot_counter++;
		form_model.control.addPoint(plot_prefix + " " + plot_counter, plot_prefix, 843, 465);
		plot_counter++;
		form_model.control.addPoint(plot_prefix + " " + plot_counter, plot_prefix, 703, 347);
		plot_counter++;
		form_model.control.addPoint(plot_prefix + " " + plot_counter, plot_prefix, 945, 484);
		plot_counter++;
		form_model.control.addPoint(plot_prefix + " " + plot_counter, plot_prefix, 1010, 498);
		plot_counter++;

		// Init Polygon
		for(ControlPoint p : form_model.control.points()) {
			if(p.getType().equals("Vertex")) {
				form_model.site_boundary.addVertex(p);
			}
		}

		// Override default Grid Properties
		form_model.setTileWidth(11);
		form_model.setTileHeight(5);
		form_model.setTileRotation((float) 0.34);
		form_model.setTileTranslation(0, 0);

		// Init Model from Control Points
		form_model.initSites();
		form_model.initZones();
		form_model.initFootprints();
		form_model.initBases();

	}

	public void keyPressed(KeyEvent e) {

		// Make JR Site
		if (e.getCode() == KeyCode.L) {
			loadJRScenario(map_model, form_model, view3D);
			setViewModel(scene3D, view3D);
			// Make Random Site
		} else if (e.getCode() == KeyCode.R) {
			loadRandomScenario(map_model, form_model, view3D);
			setViewModel(scene3D, view3D);
		}

		// Print Camera Position
		if (e.getCode() == KeyCode.C) {
			view3D.printCamera();
		}
	}

}