package edu.mit.ira.fuzzy.fx.scene;
import edu.mit.ira.fuzzy.base.TileArray;
import edu.mit.ira.fuzzy.builder.DevelopmentEditor;
import edu.mit.ira.fuzzy.fx.base.Container2D;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * Build and Handle the Content for the Navigation Panel
 * 
 * @author Ira Winder
 *
 */
public class Outcome extends Container2D {
	
	// "Back End" Elements to Render to Container
    protected DevelopmentEditor form_model;
    
    // Pane that contains multiple tabs
    TabPane nav;
    
    // Tabs to bring into navigation pane
    Tab objectModel, performance;
    
    /**
 	 * Instantiate a new Navigation Container with a given ID and friendly name
 	 * 
 	 * @param id
 	 * @param friendlyName
 	 */
	public Outcome(String id, String friendlyName) {
		super(id, friendlyName);
		
		// Set fill of container
		this.setFill(Color.TRANSPARENT);
		
		// Instantiate persistent view containers
		nav = new TabPane();
		nav.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		objectModel = new Tab();
		performance = new Tab();
		
		nav.getTabs().add(objectModel);
		nav.getTabs().add(performance);
        
        VBox content = new VBox(nav);
        setRoot(content);
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
	}
	
	/**
	 * initialize view model
	 */
	@Override
	public void init() {
		// Resize TabPane to current window size
		nav.setPrefWidth(this.getWidth());
		nav.setPrefHeight(this.getHeight());
		
		// Initialize Tabs
        initObjectModel();
        initPerformance();
		
        // Handle Mouse Events
		handleMouseEvents();
	}

	/**
	 * Initialize Object Model Summary into a Tab
	 */
	private void initObjectModel() {
		
		ScrollPane sp = new ScrollPane();
		objectModel.setText("Object Model");
		objectModel.setContent(sp);
		
		Text t = new Text();
		t.setText(objectModelText());
		//t.setWrappingWidth(this.getWidth() - 4*TEXT_BUFFER);
		
		sp.setContent(t);
		sp.setPadding(TEXT_INSETS);
	}
	
	/**
	 * Initialize Performance Graphs
	 */
	private void initPerformance() {
		
		ScrollPane sp = new ScrollPane();
		performance.setText("Performance");
		performance.setContent(sp);
		
		Text t = new Text();
		t.setText("TBA");
		t.setWrappingWidth(this.getWidth() - 4*TEXT_BUFFER);
		
		sp.setContent(t);
		sp.setPadding(TEXT_INSETS);
	}
	
	/**
	 * Make summary text for object model
	 * 
	 * @return summary text
	 */
	private String objectModelText() {
		String summary = "";
		summary += "Scenario Name:";
		summary += "\n" + form_model;
		summary += "\n";
		summary += "\n" + "Tile Dimensions:";
		summary += "\n" + form_model.tileW + " x " + form_model.tileW + " x " + form_model.tileH + " units";
		summary += "\n";
		summary += "\n" + form_model.extents();
		summary += "\n";
		summary += "\n" + "Space Object Directory:";
		for (TileArray space : form_model.spaceList()) {
			if (form_model.showSpace(space)) {
				summary += "\n";
				for(int i=0; i<space.parent_name.length(); i++) {
					if(space.parent_name.substring(i,i+1).contentEquals("/")) {
						summary += ".../ ";
					}
				}
				summary += ".../ " + space;
			}
		}
		return summary;
	}
	
	/**
	 * Initialize all mouse event handlers related to scene-wide mouse events
	 */
	private void handleMouseEvents() {
		// Handle event for resizing the height of the application content
		this.heightProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				double windowHeight = (double) arg2;
				nav.setPrefHeight(windowHeight);
			}
		});
	}
	
	/**
	 * Populates the View Model with a form from DevelopmentEditor class
	 * 
	 * @param form_model DevelopmentBuider()
	 */
	private void setFormModel(DevelopmentEditor form_model) {
		this.form_model = form_model;
	}
	
	/**
	 * Handle Key Events passed to container
	 * 
	 * @param e key event
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		init();
	}
}
