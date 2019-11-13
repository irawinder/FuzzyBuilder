package edu.mit.ira.fuzzy.fx.scene;

import edu.mit.ira.fuzzy.builder.DevelopmentEditor;
import edu.mit.ira.fuzzy.builder.sample.RandomSite;
import edu.mit.ira.fuzzy.builder.sample.ShinagawaSite;
import edu.mit.ira.fuzzy.fx.node.Underlay;
import edu.mit.ira.fuzzy.fx.node.View3D;
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
	
    SubScene scene3D, scene2D;
    View3D view3D;
    
    private Underlay map_model;
	private DevelopmentEditor form_model;
    
    // Migrate Builder() visual parameters to GUI_FX:
    // TODO
    
    /**
     * Initialize a new content container for Massing
     * 
     * @param subscene master container for content
     */
    public Massing() {
    	super(EMPTY_GROUP, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    	makeContent();
    }
    
    @Override
    public void makeContent() {
        
    	map_model = new Underlay();
    	form_model = new DevelopmentEditor();
    	
    	view3D = new View3D();
    	view3D.setZoom(-1000);
    	view3D.setPan(325, 425, 0);
    	view3D.setRotateV(-20);
    	view3D.setRotateH(-45);
    	
    	// Initialize with Random Scenario
    	loadRandomScenario(map_model, form_model, view3D);
    	
    	scene3D = new SubScene(view3D.getGroup(), getWidth(), getHeight(), true, SceneAntialiasing.BALANCED);
    	setViewModel(scene3D, view3D);
    	
    	Label l = new Label("Massing Overlay");
    	Group controls = new Group(l);
        scene2D = new SubScene(controls, getWidth(), getHeight());
        scene2D.setFill(Color.TRANSPARENT);
        
        // Bind dimensions of child scenes
        scene2D.widthProperty().bind(widthProperty());
        scene2D.heightProperty().bind(heightProperty());
        scene3D.widthProperty().bind(widthProperty());
        scene3D.heightProperty().bind(heightProperty());
    	
		// Draw Control Point at Mouse Hover
		
		// Draw Tagged Control Point Labels
		
		// Draw Info at Mouse Hover
		
		// Draw Info/Instructions
		
		// Draw Attribute Summary
    	
        Group root = new Group(scene3D, scene2D);
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
    public void loadRandomScenario(Underlay map_model, DevelopmentEditor form_model, View3D view3D) {
    	map_model.setImage("data/default_site_white.png");
    	map_model.setScale(0.5);
    	map_model.setOpacity(1.00);

    	form_model = new RandomSite(375, 375);
    	
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
    public void loadJRScenario(Underlay map_model, DevelopmentEditor form_model, View3D view3D) {
    	map_model.setImage("data/jr_site.png");
    	map_model.setScale(0.5);
    	map_model.setOpacity(0.75);

    	form_model = new ShinagawaSite();
    	
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