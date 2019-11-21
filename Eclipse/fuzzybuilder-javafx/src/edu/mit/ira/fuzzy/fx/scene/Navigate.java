package edu.mit.ira.fuzzy.fx.scene;
import edu.mit.ira.fuzzy.builder.DevelopmentEditor;
import edu.mit.ira.fuzzy.fx.base.Container2D;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class Navigate extends Container2D {
	
	// "Back End" Elements to Render to Container
    protected DevelopmentEditor form_model; 
    
    /**
 	 * Instantiate a new Navigation Container with a given ID and friendly name
 	 * 
 	 * @param id
 	 * @param friendlyName
 	 */
	public Navigate(String id, String friendlyName) {
		super(id, friendlyName);
		this.setFill(Color.WHITE);
	}

	/**
     * Set the back end content of the model and initialize view model
     * 
     * @param form_model
     * @param map_model
     */
	public void init(DevelopmentEditor form_model) {
		setFormModel(form_model);
		this.init();
		
		handleMouseEvents();
	}
	
	/**
	 * initialize view model
	 */
	@Override
	public void init() {
		nodes2D.getChildren().clear();
		StackPane content  = new StackPane(new Label(friendlyName));
		nodes2D.getChildren().add(content);
	}
	
	/**
	 * Initialize all mouse event handlers related to scene-wide mouse events
	 */
	private void handleMouseEvents() {
		
	}
	
	/**
	 * Populates the View Model with a form from DevelopmentEditor class
	 * 
	 * @param form_model DevelopmentBuider()
	 */
	private void setFormModel(DevelopmentEditor form_model) {
		this.form_model = form_model;
	}
}
