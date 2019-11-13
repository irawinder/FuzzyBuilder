package edu.mit.ira.builder.processing;

import edu.mit.ira.builder.Builder;
import edu.mit.ira.voxel.Control;
import edu.mit.ira.voxel.ControlPoint;
import edu.mit.ira.voxel.Point;
import edu.mit.ira.voxel.Polygon;
import edu.mit.ira.voxel.Tile;
import edu.mit.ira.voxel.TileArray;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * A Processing GUI that opens a window and allow user interaction
 * 
 * @author ira winder
 * 
 */
public class GUI_Processing extends PApplet {
	
	Builder builder;
	Underlay map;
	
	// Is camera 3D? Otherwise it's 2D;
	public boolean cam3D;

	// Hide or Show Tiles or Polygons
	public boolean showTiles, showPolygons, showText;

	// Hide or Show TileArray Nest Layers
	public int viewState;
	public boolean showSite, showZones, showFootprints, showBases, showTowers, showFloors, showRooms;

	// Is there a specific view mode?
	public String viewModel;
	
	// Intermediate ControlPoints used to generate Development()
	public Control control;
	
	// Point that is currently selected or hovering;
	public ControlPoint selected;
	public ControlPoint hovering;

	// Add or remove point via mouse click
	public boolean addPoint, removePoint;

	// Which control point set are we editing?
	public boolean editVertices, editPlots, editVoids;
	
	// Track attributes for any new control points
	public String new_control_type;
	private int vert_counter;
	private int plot_counter;
	private int void_counter;

	// Update model state?
	public boolean site_change_detected;
	public boolean zone_change_detected;
	public boolean foot_change_detected;
	
	/**
	 * Initial Build State
	 */
	public void initViewState() {
		
		// Init Control Points
		control = new Control();
				
		new_control_type = "zone";
		editVertices = false;
		editPlots = false;
		editVoids = false;
		
		cam3D = true;
		showText = true;
		viewModel = "DOT";
		resetViewState();
	}
	
	/**
	 * Build State When new model is loaded or randomly generated during application operation
	 */
	public void resetViewState() {
		buildingZoneState();
		addPoint = false;
		removePoint = false;
		
		
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
	 * Designed to run every GUI frame to check mouse position
	 * 
	 * @param mousePressed true if mouse button is pressed down
	 * @param mouseX       x-coordinate of mouse on screen
	 * @param mouseY       y-coordinate of mouse on screen
	 * @param existing     ControlPoint closest to mouse
	 * @param new_point    new Point() at mouse, passed to function from GUI()
	 */
	public void listen(boolean mousePressed, int mouseX, int mouseY, ControlPoint point, Point new_point) {

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
			if (cam3D) {
				Point new_location = new_point;
				if (new_location != null) {
					selected.x = new_location.x;
					selected.y = new_location.y;
				}
			} else {
				selected.x = mouseX;
				selected.y = mouseY;
			}
			detectChange(selected.getType());
		}

	}

	/**
	 * Trigger when any key is pressed, parameters passed from GUI
	 * 
	 * @param key     character that user pressed, passed from GUI
	 * @param keyCode number code of user key input
	 * @param coded   static value to check if key is coded
	 * @param left    code value for LEFT arrow
	 * @param right   code value for RIGHT arrow
	 * @param down    code value for DOWN arrow
	 * @param up      code value for UP arrow
	 */
	public void keyPressed(char key, int keyCode, int coded, int left, int right, int down, int up) {

		switch (key) {
		case 'r':
			builder.initModel();
			control = new Control();
			loadRandomModel(400, 200);
			resetViewState();
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
		case 'm':
			cam3D = !cam3D;
			break;
		case 'v':
			if (viewModel.equals("DOT")) {
				viewModel = "VOXEL";
			} else {
				viewModel = "DOT";
			}
			break;
		case '-':
			if (builder.tileW > 1)
				builder.tileW--;
			site_change_detected = true;
			;
			break;
		case '+':
			if (builder.tileW < 50)
				builder.tileW++;
			site_change_detected = true;
			break;
		case '[':
			builder.tile_rotation -= 0.01;
			site_change_detected = true;
			;
			break;
		case ']':
			builder.tile_rotation += 0.01;
			site_change_detected = true;
			break;
		case '}':
			builder.tile_rotation += 0.1;
			site_change_detected = true;
			;
			break;
		case '{':
			builder.tile_rotation -= 0.1;
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
			for(ControlPoint c : control.points()) System.out.println(c);
			System.out.println("--Other Grid Attributes");
			System.out.println("Grid Size: " + builder.tileW);
			System.out.println("Grid Rotation: " + builder.tile_rotation);
			System.out.println("Grid Pan: " + builder.tile_translation);
			break;
		}

		if (key == coded) {
			if (keyCode == left) {
				builder.tile_translation.x--;
				site_change_detected = true;
			}
			if (keyCode == right) {
				builder.tile_translation.x++;
				site_change_detected = true;
			}
			if (keyCode == down) {
				builder.tile_translation.y++;
				site_change_detected = true;
			}
			if (keyCode == up) {
				builder.tile_translation.y--;
				site_change_detected = true;
			}
		}
	}

	/**
	 * Triggered once when any mouse button is pressed
	 * 
	 * @param new_point New Point at mouse location
	 */
	public void mousePressed(Point new_point) {
		if (addPoint) {
			Point atMouse = new_point;
			addControlPoint(atMouse.x, atMouse.y);
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
		builder.updateModel(change, control);
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
		control.removePoint(point);
		detectChange(point.getType());
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
		for (TileArray space : builder.dev.spaceList()) {
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
		for (TileArray space : builder.dev.spaceList()) {
			// Add Control Point to Zone
			if (space.type.equals("zone")) {
				String point_prefix = "Void";
				void_counter++;
				control.addPoint(point_prefix + " " + void_counter, point_prefix, space);
			}
		}
	}
	
	/**
	 * Generates a randomly configured model at x, y
	 * @param x
	 * @param y
	 */
	public void loadRandomModel(float x, float y) {
		// Init Random Model and Control Points
		builder.site_boundary.randomShape(x, y, 5, 200, 300);
		initVertexControl(builder.site_boundary);
		builder.initSites(control);
		initPlotControl();
		builder.initZones(control);
		initVoidControl();
		builder.initFootprints(control);
		builder.initBases();
	}
	
	
	
	
	
	
	/**
	 * Initiate an Instance of the PApplet
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		PApplet.main("edu.mit.ira.builder.processing.GUI_Processing");
	}

	/**
	 * Runs before everything else in PApplet
	 */
	public void settings() {

		map = new Underlay("data/site.png", (float) 0.5);

		// Init Application canvas size to match site_map
		//size(map.getWidth(), map.getHeight(), P3D);

		// Set size of canvas to (X, Y) pixels
		size(900, 500, P3D);
	}

	/**
	 * Runs once upon initialization of PApplet class, but after settings() finishes
	 */
	public void setup() {

		surface.setTitle("Space Builder");

		builder = new Builder();
		initViewState();
	}

	/**
	 * Runs every frame unless "noLoop()" is run
	 */
	public void draw() {

		// listen for user inputs and mouse location
		listen(mousePressed, mouseX, mouseY, pointAtMouse(), newPointAtMouse());

		// Update Model "Backend" with New State (if any)
		updateModel();

		// Render the ViewModel "Front End" and GUI to canvas
		render();

		noLoop();
	}

	/**
	 * Runs once when key is pressed
	 */
	public void keyPressed() {
		keyPressed(key, keyCode, CODED, LEFT, RIGHT, DOWN, UP);
		map.keyPressed(key);
		loop();
	}

	/**
	 * Runs once when mouse is pressed down
	 */
	public void mousePressed() {
		mousePressed(newPointAtMouse());
		loop();
	}

	/*
	 * Runs once when mouse button is released
	 */
	public void mouseReleased() {
		deselect();
		loop();
	}

	/*
	 * Runs when mouse has moved
	 */
	public void mouseMoved() {
		loop();
	}

	/**
	 * Runs when mouse has moved while held down
	 */
	public void mouseDragged() {
		loop();
	}

	/**
	 * 
	 * @return new ControlPoint at mouse (Requires processing.core)
	 */
	Point newPointAtMouse() {
		Point mousePoint = null;

		if (cam3D) {
			cam3D();

			// generate a grid of points to search for nearest match
			// centered at (0,0)
			int breadth = 1000;
			int interval = 5;

			float min_distance = Float.POSITIVE_INFINITY;
			for (int x = -breadth; x < breadth; x += interval) {
				for (int y = -breadth; y < breadth; y += interval) {
					float dist_x = mouseX - screenX(x, y);
					float dist_y = mouseY - screenY(x, y);
					float distance = (float) Math.sqrt(Math.pow(dist_x, 2) + Math.pow(dist_y, 2));
					if (distance < 15) {
						if (distance < min_distance) {
							min_distance = distance;
							mousePoint = new Point(x, y);
						}
					}
				}
			}
		} else {
			cam2D();
			mousePoint = new Point(mouseX, mouseY);
		}
		return mousePoint;
	}

	/**
	 * Return Tagged Point Nearest to Mouse (Requires processing.core)
	 * 
	 * @return ControlPoint closest to mouse
	 */
	ControlPoint pointAtMouse() {
		ControlPoint closest = null;
		float min_distance = Float.POSITIVE_INFINITY;
		for (ControlPoint p : control.points()) {
			float dist_x, dist_y;
			if (cam3D) {
				cam3D();
				dist_x = mouseX - screenX(p.x, p.y);
				dist_y = mouseY - screenY(p.x, p.y);
			} else {
				cam2D();
				dist_x = mouseX - p.x;
				dist_y = mouseY - p.y;
			}
			float distance = (float) Math.sqrt(Math.pow(dist_x, 2) + Math.pow(dist_y, 2));
			if (distance < 15) {
				if (distance < min_distance) {
					min_distance = distance;
					closest = p;
				}
			}
		}
		return closest;
	}

	// Front-End Methods that rely heavily on Processing Library Functions

	void cam3D() {
		camera(200, 400, 200, 400, 200, 0, 0, 0, -1); 
		lights(); colorMode(HSB); pointLight(0, 0, 100, 50, 50, 50);
	} 

	void cam2D() {
		camera(); noLights(); perspective();
	}

	void render() {
		hint(ENABLE_DEPTH_TEST);
		background(255);

		// Draw Underlay
		renderUnderlay();

		// Draw Tiles and Voxels
		if (showTiles) {

			for (TileArray space : builder.dev.spaceList()) {
				if (showSpace(space)) {

					// Draw Sites
					//
					if (space.isType("site")) {
						int col = color(0, 50);
						for(Tile t : space.tileList()) renderTile(t, col, -1);
					}

					// Draw Zones
					//
					if (space.isType("zone")) {
						colorMode(HSB); int col = color(space.hue, 100, 225);
						for(Tile t : space.tileList()) renderTile(t, col, -1);
					}

					// Draw Footprints
					//
					if (space.isType("footprint")) {
						colorMode(HSB); int col;
						if(space.name.equals("Building")) {
							col = color(space.hue, 150, 200);
						} else if(space.name.equals("Setback")) {
							col = color(space.hue, 50, 225);
						} else {
							col = color(space.hue, 150, 200);
						}
						for (Tile t : space.tileList()) {
							renderTile(t, col, -1);
							if (space.name.equals("Building")) {
								renderVoxel(t, col, (float) -0.5*t.scale_w);
							}
						}
					}

					// Draw Bases
					//
					if (space.isType("base")) {
						colorMode(HSB); int col = color(space.hue, 150, 200);
						for(Tile t : space.tileList()) {
							// Only draws ground plane if in 2D view mode
							if(t.location.z == 0 || cam3D) { 
								if (space.name.substring(0, 3).equals("Cou")) {
									renderTile(t, col, 0);
								} else {
									renderVoxel(t, col, 0);
								}
							}
						}
					}
				}
			}
		}

		// Draw Vector Polygon
		//
		fill(245, 50); noStroke(); 
		if (showPolygons) {
			stroke(0, 100); 
			strokeWeight(1);
		}
		pushMatrix(); translate(0, 0, -2);
		beginShape();
		for(Point p : builder.site_boundary.getCorners()) vertex(p.x, p.y);
		endShape(CLOSE);
		popMatrix();

		hint(DISABLE_DEPTH_TEST);

		// Draw Tagged Control Points
		//
		for (ControlPoint p : control.points()) {
			fill(150, 100); stroke(0, 150); strokeWeight(1);
			pushMatrix(); translate(0, 0, 1);
			if (p.active()) ellipse(p.x, p.y, 10, 10);
			int size = 4;
			if (!p.active()) {
				stroke(0, 75);
				size = 2;
			}
			line(p.x-size, p.y-size, p.x+size, p.y+size);
			line(p.x-size, p.y+size, p.x+size, p.y-size);
			popMatrix();
		}

		// Draw Tagged Control Points Labels
		//
		for (ControlPoint p : control.points()) {
			if (p.active()) {
				int x, y;
				if (cam3D) {
					cam3D();
					x = (int)screenX(p.x, p.y);
					y = (int)screenY(p.x, p.y);
				} else {
					x = (int)p.x;
					y = (int)p.y;
				}
				if(cam3D) cam2D(); // sets temporarily to 2D camera, if in 3D
				fill(255, 150); stroke(200, 150); strokeWeight(1);
				int textWidth = 7*p.getTag().length();
				rectMode(CORNER); rect(x + 10, y - 7, textWidth, 15, 5);
				fill(50); textAlign(CENTER, CENTER);
				text(p.getTag(), x + 10 + (int)textWidth/2, y - 1);
				if(cam3D) cam3D(); // sets back to 3D camera, if in 3D mode
			}
		}

		// Draw Hovering Control Point
		//
		if (hovering != null && hovering.active()) {
			int col = color(50);
			if (removePoint) {
				colorMode(RGB); 
				col = color(255, 0, 0);
			} else if (addPoint) {
				colorMode(RGB); 
				col = color(0, 255, 00);
			}
			renderCross(hovering.x, hovering.y, 4, col, 2, 1);
		}

		if(cam3D) cam2D(); // sets temporarily to 2D camera, if in 3D

		// Draw Info Text
		//
		fill(100); textAlign(LEFT, TOP);
		String info = "";
		info += "Click and drag control points";
		info += "\n";
		info += "\n" + "Press 'a' to add control point";
		if(addPoint) info += " <--";
		info += "\n" + "Press 'x' to remove control point";
		if(removePoint) info += " <--";
		info += "\n" + "Press 'i' to edit Site";
		if(editVertices) info += " <--";
		info += "\n" + "Press 'p' to edit Plots";
		if(editPlots) info += " <--";
		info += "\n" + "Press 'o' to edit Voids";
		if(editVoids) info += " <--";
		info += "\n" + "Press 'c' clear all control points";
		info += "\n";
		info += "\n" + "Press '-' or '+' to resize tiles";
		info += "\n" + "Press '[', '{', ']', or '}' to rotate tiles";
		info += "\n" + "Press 'r' to generate random site";
		info += "\n" + "Press 'm' to toggle 2D/3D view";
		info += "\n" + "Press 'v' to toggle View Model";
		info += "\n" + "Press 't' to hide/show Tiles";
		if(showTiles) info += " <--";
		info += "\n" + "Press 'l' to hide/show PolyLines";
		if(showPolygons) info += " <--";
		info += "\n";
		info += "\n" + "Press '1' to show Site";
		if(viewState == 1) info += " <--";
		info += "\n" + "Press '2' to show Zones";
		if(viewState == 2) info += " <--";
		info += "\n" + "Press '3' to show Footprints";
		if(viewState == 3) info += " <--";
		info += "\n" + "Press '4' to show Zones + Buildings";
		if(viewState == 4) info += " <--";
		info += "\n" + "Press '5' to show Buildings Only";
		if(viewState == 5) info += " <--";
		//info += "\n" + "Press '6' to show Floors";
		//if(viewState == 6) info += " <--";
		//info += "\n" + "Press '7' to show Rooms";
		//if(viewState == 7) info += " <--";
		if (showText) text(info, 10, 10);
		//text("Framerate: " + int(frameRate), 10, height - 20);

		// Draw Summary
		//
		if (showTiles) {
			fill(100); textAlign(LEFT, TOP);
			String summary = "";
			summary += "View Model: " + viewModel;
			summary += "\n" + "Tile Dimensions:";
			summary += "\n" + builder.tileW + " x " + builder.tileW + " x " + builder.tileH + " units";
			summary += "\n";
			summary += "\n" + builder.dev + "/...";
			for(TileArray space : builder.dev.spaceList()) {
				if (showSpace(space)) {
					summary += "\n~/" + space;
					//summary += "\n" + space.parent_name + "/" + space;
				}
			}
			if (showText) text(summary, width - 175, 10);
		}

		// Mouse Cursor Info
		//
		fill(50); textAlign(LEFT, TOP);
		if (addPoint) {
			text("NEW (" + new_control_type + ")", mouseX + 10, mouseY - 20);
		} else if (removePoint) {
			text("REMOVE", mouseX + 10, mouseY - 20);
		} else if (hovering != null && hovering.active()) {
			text("MOVE", mouseX + 10, mouseY - 20);
		}

		if(cam3D) cam3D(); // sets back to 3D camera, if in 3D mode
	}

	void renderTile(Tile t, int col, float z_offset) {

		float scaler = (float) 0.85;

		fill(col); noStroke();
		pushMatrix(); translate(t.location.x, t.location.y, t.location.z + z_offset);

		if (viewModel.equals("DOT")) {
			ellipse(0, 0, scaler*t.scale_uv, scaler*t.scale_uv);
		} else if (viewModel.equals("VOXEL")) {
			rotate(builder.tile_rotation);
			rectMode(CENTER); rect(0, 0, scaler*t.scale_uv, scaler*t.scale_uv);
		} else {
			ellipse(0, 0, scaler*t.scale_uv, scaler*t.scale_uv);
		}

		popMatrix();
	}

	void renderVoxel(Tile t, int col, float z_offset) {

		float scaler_uv = (float) 0.9;
		float scaler_w  = (float) 0.6;

		fill(col); stroke(0, 50); strokeWeight(1);
		pushMatrix(); translate(t.location.x, t.location.y, t.location.z + z_offset);
		rotate(builder.tile_rotation);
		box(scaler_uv*t.scale_uv, scaler_uv*t.scale_uv, scaler_w*t.scale_w);
		popMatrix();
	}

	void renderCross(float x, float y, float size, int col, float stroke, float z_offset) {
		stroke(col); strokeWeight(stroke);
		pushMatrix(); translate(0, 0, z_offset);
		line(x-5, y-5, x+5, y+5);
		line(x-5, y+5, x+5, y-5);
		popMatrix();
	}

	/**
	 * Renders a Raster Image Underlay
	 */
	public void renderUnderlay() {
		pushMatrix();
		translate(0, 0, -5);
		if (map.show) image(map.getImg(), 0, 0);
		popMatrix();
	}

	/**
	 * A raster Underlay to super-impose on View Model
	 */
	public class Underlay {

		private PImage underlay;
		private boolean show;
		int w, h;

		public Underlay(String file_path, float scaler) {
			show = false;
			underlay = loadImage(file_path);
			w = (int) (scaler * underlay.width);
			h = (int) (scaler * underlay.height);
			underlay.resize(w, h);
		}

		public PImage getImg() {
			return underlay;
		}

		public int getWidth() {
			return w;
		}

		public int getHeight() {
			return h;
		}

		private void toggle() {
			show = !show;
		}

		public boolean show() {
			return show;
		}

		public void keyPressed(char key) {
			switch (key) {
			case 'M':
				toggle();
				break;
			}
		}
	}


}