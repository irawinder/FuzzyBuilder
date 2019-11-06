package edu.mit.ira.builder.fx;

import edu.mit.ira.builder.Builder;
import edu.mit.ira.voxel.Control;
import edu.mit.ira.voxel.ControlPoint;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * The Main Application Environment to Run Fuzzy Builder
 *
 * If Running from eclipse, you need to put the following VM Argument into the Run Configuration
 * --module-path "/Path/to/javafx-sdk-13/lib" --add-modules javafx.controls
 *
 * @author jiw
 *
 */
public class GUI_FX extends Application {

	static final int WINDOW_WIDTH = 1280;
    static final int WINDOW_HEIGHT = 720;
    static final Color TEXT_COLOR = Color.gray(0.8);

    @Override
    public void start(Stage stage) {
    	
//    	// A Satellite Image to place on the ground
//    	Underlay map_model = new Underlay();
//    	map_model.setImage("data/site.png");
//    	map_model.setScale(0.5);
//    	map_model.setOpacity(0.75);
    	
    	// A Satellite Image to place on the ground
    	Underlay map_model = new Underlay();
    	map_model.setImage("data/default_site_white.png");
    	map_model.setScale(0.5);
    	map_model.setOpacity(0.75);

    	// A Cuboid Form built by Ira
    	Builder form_model = new Builder();
    	form_model.initModel();
    	//loadJRModel(form_model);
    	form_model.loadRandomModel(375, 375);

    	// View Model
    	ViewModel view_model = new ViewModel();
    	view_model.setBackground(Color.hsb(0,0,0.2));
    	view_model.setZoom(-1000);
    	//view_model.setPan(719, 410, 0);
    	view_model.setPan(325, 425, 0);
    	view_model.setRotateV(-20);
    	view_model.setRotateH(-45);
    	view_model.setFormModel(form_model);
    	view_model.setMapModel(map_model);
		view_model.render();

    	stage.setTitle("Space Builder FX");

    	final SubScene scene3D = new SubScene(view_model.getGroup(),
        		WINDOW_WIDTH, WINDOW_HEIGHT, true, SceneAntialiasing.BALANCED);
//        final SubScene sceneUI = new SubScene(settings.getGroup(), WINDOW_WIDTH,
//                WINDOW_HEIGHT);

        scene3D.setFill(view_model.getBackground());
        scene3D.setCamera(view_model.getCamera());

        final Group root = new Group(scene3D);
        //final Group root = new Group(scene3D, sceneUI);
        final Scene master = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Mouse and Keyboard Events
        view_model.handleMouseEvents(master);
        master.setOnKeyPressed(e -> {

        	// Make JR Site
            if (e.getCode() == KeyCode.L) {
            	form_model.initModel();
        		loadJRModel(form_model);
        		form_model.resetViewState();
        		view_model.setFormModel(form_model);
        		view_model.render();
            	scene3D.setRoot(view_model.getGroup());

            // Make Random Site
            } else if (e.getCode() == KeyCode.R) {
            	form_model.initModel();
            	form_model.loadRandomModel(375, 375);
        		form_model.resetViewState();
        		view_model.setFormModel(form_model);
        		view_model.render();
            	scene3D.setRoot(view_model.getGroup());
            }
            
			// Print Camera Position
			if (e.getCode() == KeyCode.C) {
				view_model.printCamera();
			}

        });

        stage.setScene(master);
        stage.show();

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
		form_model.setTileRotation((float) 0.34);
		form_model.setTileTranslation(0, 0);

		// Init Model from Control Points
		form_model.initSites();
		form_model.initZones();
		form_model.initFootprints();
		form_model.initBases();

	}

    public static void main(String[] args) {
        launch(args);
    }

}