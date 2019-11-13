package edu.mit.ira.fuzzy.fx.scene;

import edu.mit.ira.fuzzy.builder.DevelopmentEditor;
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
	
    // "Back End" Elements to Render to Container
    DevelopmentEditor form_model; 
    Underlay map_model;
    
    SubScene scene3D, scene2D;
    View3D view3D;
    
    /**
     * Initialize a new content container for Massing
     * 
     * @param subscene master container for content
     */
    public Massing(DevelopmentEditor form_model, Underlay map_model) {
    	super(EMPTY_GROUP, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    	this.form_model = form_model;
    	this.map_model = map_model;
    	makeContent();
    }
    
    public void set(DevelopmentEditor form_model, Underlay map_model) {
    	this.form_model = form_model;
    	this.map_model = map_model;
    }
    
    @Override
    public void makeContent() {
        
    	view3D = new View3D();
    	view3D.setZoom(-1000);
    	view3D.setPan(325, 425, 0);
    	view3D.setRotateV(-20);
    	view3D.setRotateH(-45);
    	view3D.setUpCamera();
    	
    	view3D.setBackground(Color.hsb(0,0,1.0));
    	view3D.setFormModel(form_model);
    	view3D.setMapModel(map_model);
		view3D.render();
    	
    	scene3D = new SubScene(view3D.getGroup(), getWidth(), getHeight(), true, SceneAntialiasing.BALANCED);
    	scene3D.setRoot(view3D.getGroup());
    	scene3D.setFill(view3D.getBackground());
        scene3D.setCamera(view3D.getCamera());
    	
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
    
    public void refreshContent() {
    	
    	view3D.setFormModel(form_model);
    	view3D.setMapModel(map_model);
		view3D.render();
    	scene3D.setRoot(view3D.getGroup());
    	
    	Group root = new Group(scene3D, scene2D);
        setRoot(root);
    }

	public void keyPressed(KeyEvent e) {

		// Print Camera Position
		if (e.getCode() == KeyCode.C) {
			view3D.printCamera();
		}
	}

}