package edu.mit.ira.fuzzy.builder;

import edu.mit.ira.fuzzy.base.ControlPoint;
import edu.mit.ira.fuzzy.base.ControlSet;
import edu.mit.ira.fuzzy.base.Point;
import edu.mit.ira.fuzzy.base.TileArray;

/**
 * DevelopmentEditor facilitates the making of various TileArrays via Polygons
 * and/or ControlPoints. This is the "back end" to any elements that are
 * eventually rendered to a graphical user interface
 * 
 * @author Ira Winder
 *
 */
public class DevelopmentEditor extends DevelopmentBuilder {

	// Intermediate ControlPoints used to generate Development()
	public ControlSet control;

	// is the editor on?
	private boolean isEditing;

	// Hide or Show Tiles or Polygons
	public boolean showTiles, showPolygons, showText;

	// Hide or Show TileArray Nest Layers
	public int viewState;
	public boolean showSite, showZones, showFootprints, showBases, showTowers, showFloors, showRooms;

	// Is there a specific view mode?
	public String viewModel;

	// Point that is currently selected or hovering;
	public ControlPoint selected;
	public ControlPoint hovering;

	// Add or remove point via mouse click
	public boolean addPoint, removePoint;

	// Which control point set are we editing?
	public boolean editVertices, editPlots, editVoids;

	// Track attributes for any new control points
	public String new_control_type;
	protected int vert_counter;
	protected int plot_counter;
	protected int void_counter;

	// Update model state?
	public boolean site_change_detected;
	public boolean zone_change_detected;
	public boolean foot_change_detected;

	public DevelopmentEditor() {
		super();
		initEditor();
	}

	/**
	 * Setter for editing state
	 * 
	 * @param isEditing
	 */
	public void setEditing(boolean isEditing) {
		this.isEditing = isEditing;
	}

	/**
	 * Getting for editing state
	 * 
	 * @return
	 */
	public boolean isEditing() {
		return isEditing;
	}

	/**
	 * Initial Build State
	 */
	public void initEditor() {

		// Initialize Control Points
		control = new ControlSet();

		new_control_type = "zone";
		editVertices = false;
		editPlots = false;
		editVoids = false;

		showText = true;
		viewModel = "DOT";
		resetEditor();
	}

	/**
	 * Build State When new model is loaded or randomly generated during application
	 * operation
	 */
	public void resetEditor() {
		buildingZoneState();
		addPoint = false;
		removePoint = false;
	}

	/**
	 * Add a new Control point at (x,y)
	 * 
	 * @param x
	 * @param y
	 */
	public void addControlPoint(float x, float y) {
		if (new_control_type.equals("Vertex")) {
			control.addPoint(new_control_type + " " + vert_counter, new_control_type, x, y);
			vert_counter++;
		} else if (new_control_type.equals("Plot")) {
			control.addPoint(new_control_type + " " + plot_counter, new_control_type, x, y);
			plot_counter++;
		} else if (new_control_type.equals("Void")) {
			control.addPoint(new_control_type + " " + void_counter, new_control_type, x, y);
			void_counter++;
		}
		detectChange(new_control_type);
	}

	/**
	 * Remove a given ControlPoint
	 * 
	 * @param point
	 */
	public void removeControlPoint(ControlPoint point) {
		if (point != null) {
			control.removePoint(point);
			detectChange(point.getType());
		}
	}

	/**
	 * detect change based upon a type string
	 * 
	 * @param type type of ControlPoint that is edited
	 */
	public void detectChange(String type) {
		if (type.equals("Vertex")) {
			site_change_detected = true;
		} else if (type.equals("Plot")) {
			zone_change_detected = true;
		} else if (type.equals("Void")) {
			foot_change_detected = true;
		}
	}

	/**
	 * Update Model:
	 */
	public void updateModel() {

		char change = '0';
		if (site_change_detected) {
			site_change_detected = false;
			change = 's';
		} else if (zone_change_detected) {
			zone_change_detected = false;
			change = 'z';
		} else if (foot_change_detected) {
			foot_change_detected = false;
			change = 'f';
		}
		updateModel(change, control);
	}

	/**
	 * Allow editing of vertices
	 */
	public void toggleVertexEditing() {
		editVertices = !editVertices;
		editPlots = false;
		editVoids = false;
		new_control_type = "Vertex";
		control.off();
		if (editVertices) {
			control.on(new_control_type);
			// auto add points if list is empty
			if (control.points(new_control_type).size() == 0)
				addPoint = true;
			showPolygons = true;
		}
	}

	/**
	 * Allow editing of Plots
	 */
	public void togglePlotEditing() {
		editVertices = false;
		editPlots = !editPlots;
		editVoids = false;
		new_control_type = "Plot";
		control.off();
		if (editPlots)
			control.on(new_control_type);
		// auto add points if list is empty
		if (control.points(new_control_type).size() == 0)
			addPoint = true;
	}

	/**
	 * Allow editing of voids
	 */
	public void toggleVoidEditing() {
		editVertices = false;
		editPlots = false;
		editVoids = !editVoids;
		new_control_type = "Void";
		control.off();
		if (editVoids) {
			control.on(new_control_type);
			// auto add points if list is empty
			if (control.points(new_control_type).size() == 0)
				addPoint = true;
		}
	}

	/**
	 * Force Activation of editor, toggling to most relevant/recent type
	 */
	public void activateEditor() {
		setEditing(true);
		editVertices = false;
		editPlots = false;
		editVoids = false;
		if (new_control_type.equals("Vertex")) {
			editVertices = true;
		} else if (new_control_type.equals("Plot")) {
			editPlots = true;
		} else if (new_control_type.equals("Void")) {
			editVoids = true;
		}
		control.off();
		control.on(new_control_type);
	}

	/**
	 * A pre-defined layer state for site
	 */
	public void siteState() {
		// Site Layer State
		offState();
		showTiles = true;
		showPolygons = true;
		showSite = true;
		viewState = 1;

		editVertices = true;
		new_control_type = "Vertex";
		control.on(new_control_type);
	}

	/**
	 * A pre-defined layer state for zones
	 */
	public void zoneState() {
		// Zone Layer State
		offState();
		showTiles = true;
		showSite = true;
		showZones = true;
		viewState = 2;

		editPlots = true;
		new_control_type = "Plot";
		control.on(new_control_type);
	}

	/**
	 * A pre-defined layer state for footprints
	 */
	public void footprintState() {
		// Footprint Layer State
		offState();
		showTiles = true;
		showSite = true;
		showFootprints = true;
		viewState = 3;

		editVoids = true;
		new_control_type = "Void";
		control.on(new_control_type);
	}

	/**
	 * A pre-defined layer state for buildings and zones together
	 */
	public void buildingZoneState() {
		// Building + Zone Layer State
		offState();
		showTiles = true;
		showSite = true;
		showFootprints = true;
		showBases = true;
		viewState = 4;

		editPlots = true;
		new_control_type = "Plot";
		control.on(new_control_type);
	}

	/**
	 * A pre-defined layer state for buildings
	 */
	public void buildingState() {
		// Building Layer State
		offState();
		showTiles = true;
		showPolygons = true;
		showBases = true;
		showTowers = true;
		viewState = 5;
		
		editVoids = true;
		new_control_type = "Void";
		control.on(new_control_type);
	}

	/**
	 * A pre-defined layer state for floors
	 */
	public void floorState() {
		// Floor Layer State
		offState();
		showTiles = true;
		showPolygons = true;
		showFloors = true;
		viewState = 6;
	}

	/**
	 * A pre-defined layer state for Rooms
	 */
	public void roomState() {
		// Room Layer State
		offState();
		showTiles = true;
		showPolygons = true;
		showRooms = true;
		viewState = 7;
	}

	/*
	 * A pre-defined layer state for everything off
	 */
	public void offState() {
		showTiles = false;
		showPolygons = false;
		showSite = false;
		showZones = false;
		showFootprints = false;
		showBases = false;
		showTowers = false;
		showFloors = false;
		showRooms = false;

		addPoint = false;
		editPlots = false;
		editVertices = false;
		editVoids = false;
		control.off();
	}

	/**
	 * Determine if a space is to be rendered in a given state
	 * 
	 * @param space space to evaluate for rendering
	 * @return true if GUI should render
	 */
	public boolean showSpace(TileArray space) {
		if (showSite && space.type.equals("site")) {
			return true;
		} else if (showZones && space.type.equals("zone")) {
			return true;
		} else if (showFootprints && space.type.equals("footprint")) {
			return true;
		} else if (showBases && space.type.equals("base")) {
			return true;
		} else if (showFloors && space.type.equals("floor")) {
			return true;
		} else if (showRooms && space.type.equals("room")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Designed to run on any mouse movement to check mouse position
	 * 
	 * @param mousePressed true if mouse button is pressed down
	 * @param mouseX       x-coordinate of mouse on screen
	 * @param mouseY       y-coordinate of mouse on screen
	 * @param existing     ControlPoint closest to mouse
	 * @param new_point    new Point() at mouse, passed to function from GUI()
	 */
	public void listen(boolean mousePressed, ControlPoint point, Point new_point) {

		if (addPoint) {
			Point atMouse = new_point;
			if (atMouse != null) {
				ControlPoint ghost = new ControlPoint(atMouse.x, atMouse.y);
				ghost.setTag("ghost");
				hovering = ghost;
			} else {
				hovering = null;
			}
		} else {
			hovering = point;
		}

		if (mousePressed && selected != null && selected.active()) {
			Point new_location = new_point;
			if (new_location != null) {
				selected.x = new_location.x;
				selected.y = new_location.y;
			}
			detectChange(selected.getType());
		}
	}

	/**
	 * Triggered once when any mouse button is pressed
	 * 
	 * @param new_point New Point at mouse location
	 */
	public void mousePressed(Point new_point) {
		if (addPoint) {
			if (new_point != null) {
				Point atMouse = new_point;
				addControlPoint(atMouse.x, atMouse.y);
			}
		} else {
			selected = hovering;
			if (removePoint) {
				removeControlPoint(selected);
			}
		}
	}

	/*
	 * Runs once when mouse button is released
	 */
	public void deselect() {
		selected = null;
	}

	/**
	 * Trigger when any key is pressed, parameters passed from GUI
	 * 
	 * @param key character that user pressed, passed from GUI
	 */
	public void keyPressed(char key) {
		switch (key) {
		case 'e':
			if(isEditing()) {
				setEditing(false);
			} else {
				activateEditor();
			}
			break;
		case 'a':
			addPoint = !addPoint;
			removePoint = false;
			if (addPoint)
				activateEditor();
			break;
		case 'x':
			removePoint = !removePoint;
			addPoint = false;
			break;
		case 'p':
			togglePlotEditing();
			break;
		case 'o':
			toggleVoidEditing();
			break;
		case 'i':
			toggleVertexEditing();
			break;
		case 'c':
			control.clearPoints();
			site_change_detected = true;
			removePoint = false;
			buildingZoneState();
			addPoint = true;
			toggleVertexEditing();
			break;
		case 'v':
			if (viewModel.equals("DOT")) {
				viewModel = "VOXEL";
			} else {
				viewModel = "DOT";
			}
			break;
		case '-':
			if (tileW > 1)
				tileW--;
			site_change_detected = true;
			;
			break;
		case '+':
			if (tileW < 50)
				tileW++;
			site_change_detected = true;
			break;
		case '[':
			tile_rotation -= 0.01;
			site_change_detected = true;
			;
			break;
		case ']':
			tile_rotation += 0.01;
			site_change_detected = true;
			break;
		case '}':
			tile_rotation += 0.1;
			site_change_detected = true;
			;
			break;
		case '{':
			tile_rotation -= 0.1;
			site_change_detected = true;
			break;
		case 't':
			showTiles = !showTiles;
			break;
		case 'l':
			showPolygons = !showPolygons;
			break;
		case 'h':
			showText = !showText;
			break;
		case '1':
			siteState();
			break;
		case '2':
			zoneState();
			break;
		case '3':
			footprintState();
			break;
		case '4':
			buildingZoneState();
			break;
		case '5':
			buildingState();
			break;
		// case '6':
		// floorState();
		// break;
		// case '7':
		// roomState();
		// break;
		case '0':
			System.out.println("--Site Vertices");
			System.out.println("--Zone Points");
			for (ControlPoint c : control.points())
				System.out.println(c);
			System.out.println("--Other Grid Attributes");
			System.out.println("Grid Size: " + tileW);
			System.out.println("Grid Rotation: " + tile_rotation);
			System.out.println("Grid Pan: " + tile_translation);
			break;
		}
	}

	/**
	 * User Pressed the arrow key. Parameters passed from Processing GUI
	 * 
	 * @param keyCode number code of user key input
	 * @param coded   static value to check if key is coded
	 * @param left    code value for LEFT arrow
	 * @param right   code value for RIGHT arrow
	 * @param down    code value for DOWN arrow
	 * @param up      code value for UP arrow
	 */
	public void arrowPressed(char key, int keyCode, int coded, int left, int right, int down, int up) {
		if (key == coded) {
			if (keyCode == left) {
				tile_translation.x--;
				site_change_detected = true;
			}
			if (keyCode == right) {
				tile_translation.x++;
				site_change_detected = true;
			}
			if (keyCode == down) {
				tile_translation.y++;
				site_change_detected = true;
			}
			if (keyCode == up) {
				tile_translation.y--;
				site_change_detected = true;
			}
		}
	}

	/**
	 * User Pressed the arrow key. Parameters passed from JavaFX GUI
	 * 
	 * @param arrow LEFT, RIGHT, DOWN, or UP
	 */
	public void arrowPressed(String arrow) {
		if (arrow.equals("LEFT")) {
			tile_translation.x--;
			site_change_detected = true;
		}
		if (arrow.equals("RIGHT")) {
			tile_translation.x++;
			site_change_detected = true;
		}
		if (arrow.equals("DOWN")) {
			tile_translation.y++;
			site_change_detected = true;
		}
		if (arrow.equals("UP")) {
			tile_translation.y--;
			site_change_detected = true;
		}
	}
}
