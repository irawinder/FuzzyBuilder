package edu.mit.ira.fuzzy.fx.scene;
import edu.mit.ira.fuzzy.base.TileArray;
import edu.mit.ira.fuzzy.builder.DevelopmentEditor;
import edu.mit.ira.fuzzy.fx.base.Container2D;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Navigate extends Container2D {
	
	// "Back End" Elements to Render to Container
    protected DevelopmentEditor form_model; 
    String appName, appVersion;
    
    // Pane that contains multiple tabs
    TabPane nav;
    
    // Tabs to bring into navigation pane
    Tab readMe, objectModel;
    
    /**
 	 * Instantiate a new Navigation Container with a given ID and friendly name
 	 * 
 	 * @param id
 	 * @param friendlyName
 	 */
	public Navigate(String id, String friendlyName) {
		super(id, friendlyName);
		
		// Set fill of container
		this.setFill(Color.TRANSPARENT);
		
		// Instantiate persistent view containers
		nav = new TabPane();
		readMe = new Tab();
		objectModel = new Tab();
		
		nav.getTabs().add(readMe);
        nav.getTabs().add(objectModel);
        
        VBox content = new VBox(nav);
		nodes2D.getChildren().clear();
		nodes2D.getChildren().add(content);
	}

	/**
     * Set the back end content of the model and initialize view model
     * 
     * @param form_model
     * @param map_model
     */
	public void init(DevelopmentEditor form_model, String appName, String appVersion) {
		setFormModel(form_model);
		this.appName = appName;
		this.appVersion = appVersion;
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
		initReadMe();
        initObjectModel();
		
        // Handle Mouse Events
		handleMouseEvents();
	}
	
	/**
	 * Initialize ReadMe text into a Tab()
	 */
	private void initReadMe() {
		
		ScrollPane sp = new ScrollPane();
		readMe.setText("Read Me");
		readMe.setContent(sp);
        
		Text t = new Text();
		t.setText(readMeText());
		t.setWrappingWidth(this.getWidth() - 4*TEXT_BUFFER);
		
		sp.setContent(t);
		sp.setPadding(TEXT_INSETS);
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
	 * Make readme text
	 * 
	 * @return readme text
	 */
	private String readMeText() {
		String readMe = "";
		readMe += appName + " (" + appVersion + ")";
		readMe += "\n";
		readMe += "\n" + "Press 'r' to generate random site";
		readMe += "\n" + "Press 's' to load an example site";
		readMe += "\n";
		readMe += "\n" + "Press 'e' to toggle editing mode";
		readMe += "\n" + "Click control points to interact with them";
		readMe += "\n";
		readMe += "\n" + "Press 'a' to add control point";
		if (form_model.addPoint)
			readMe += " <--";
		readMe += "\n" + "Press 'x' to remove control point";
		if (form_model.removePoint)
			readMe += " <--";
		readMe += "\n" + "Press 'i' to edit Site";
		if (form_model.editVertices)
			readMe += " <--";
		readMe += "\n" + "Press 'p' to edit Plots";
		if (form_model.editPlots)
			readMe += " <--";
		readMe += "\n" + "Press 'o' to edit Voids";
		if (form_model.editVoids)
			readMe += " <--";
		readMe += "\n" + "Press 'c' clear all control points";
		readMe += "\n";
		readMe += "\n" + "Press '-' or '+' to resize tiles";
		readMe += "\n" + "Press '[', '{', ']', or '}' to rotate tiles";
//		readMe += "\n" + "Press 'm' to toggle 2D/3D view";
//		readMe += "\n" + "Press 'v' to toggle View Model";
		readMe += "\n" + "Press 't' to hide/show Tiles";
		if (form_model.showTiles)
			readMe += " <--";
		readMe += "\n" + "Press 'l' to hide/show PolyLines";
		if (form_model.showPolygons)
			readMe += " <--";
		readMe += "\n";
		readMe += "\n" + "Press '1' to show Site";
		if (form_model.viewState == 1)
			readMe += " <--";
		readMe += "\n" + "Press '2' to show Zones";
		if (form_model.viewState == 2)
			readMe += " <--";
		readMe += "\n" + "Press '3' to show Footprints";
		if (form_model.viewState == 3)
			readMe += " <--";
		readMe += "\n" + "Press '4' to show Footprints + Form";
		if (form_model.viewState == 4)
			readMe += " <--";
		readMe += "\n" + "Press '5' to show Form Only";
		if (form_model.viewState == 5)
			readMe += " <--";
		
		return readMe;
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
