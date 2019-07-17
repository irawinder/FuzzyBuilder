package edu.mit.ira.voxel;
import java.util.ArrayList;

import processing.core.PApplet;

/**
 * A Processing GUI that opens a window and allow user interaction
 * @author ira
 * 
 */
public class Builder extends PApplet{

	// Initiate an Instance of the PApplet
	public static void main(String[] args) {
		PApplet.main("edu.mit.ira.voxel.Builder");
	}

	// Runs before everything else
	public void settings(){

		// Set size of canvas to (X, Y) pixels
		size(800, 400, P3D);
	}

	// Runs once upon initialization of class, after settings()
	public void setup(){

		// Initialize Model "Backend"
		initModel();

		// Initialize ViewModel "Front End" Settings
		initRender();
	}

	// Runs every frame unless "noLoop()" is run
	public void draw(){

		// listen for user inputs and mouse location
		listen(); 

		// Update Model "Backend" with New State (if any)
		updateModel();

		// Render the ViewModel "Front End" and GUI to canvas
		render();

		noLoop();
	}

	// Initialize Front End:

	// Point that is currently selected or hovering;
	ControlPoint selected;
	ControlPoint hovering;

	// Add or remove point via mouse click
	boolean addPoint, removePoint;

	// Which control point set are we editing?
	boolean editVertices, editPlots, editVoids;

	// Is camera 3D? Otherwise it's 2D;
	boolean cam3D;

	// Hide or Show Tiles or Polygons
	boolean showTiles, showPolygons;

	// Hide or Show TileArray Nest Layers
	int viewState;
	boolean showSite, showZones, showFootprints, showBases, showTowers, showFloors, showRooms;

	// Is there a specific view mode?
	String viewModel;

	// Initialize the View Model
	void initRender() {
		cam3D = true;
		viewModel = "DOT";

		buildingZoneState();

		addPoint = false;
		removePoint = false;
	}

	// Initialize Backend:

	Development dev;
	String dev_name;

	Polygon site_boundary;
	String site_name;

	Control control;
	String new_control_type;
	int vert_counter;
	int plot_counter;
	int void_counter;

	float tileW, tileH, tile_rotation;
	String units;
	Point tile_translation;

	// Update model state?
	boolean site_change_detected;
	boolean zone_change_detected;
	boolean foot_change_detected;

	void initModel() {

		// Init Vector Site Polygon
		site_boundary = new Polygon();
		site_boundary.randomShape(400, 200, 5, 100, 200);

		// Init Raster-like Site Voxels
		dev_name = "New Development";
		dev = new Development(dev_name);
		site_name = "Property";
		tileW = 15;
		tileH = 5;
		units = "pixels";
		tile_translation = new Point(0,0);
		tile_rotation = 0;

		// Init Control Points
		control = new Control();
		new_control_type = "zone";
		editVertices = false;
		editPlots = false;
		editVoids = false;

		// Init Random Model and Control Points
		initVertexControl();
		initSites();
		initPlotControl();
		initZones();
		initVoidControl();
		initFootprints();
		initBases();
	}

	// Update Backend:

	void updateModel() {

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

		switch(change) {
		case 's':
			initSites();
		case 'z':
			initZones();
		case 'f':
			initFootprints();
			initBases();
			break;
		}
	}

	// Initialize Site
	//
	void initSites() {

		//Define new Space Type
		String type = "site";
		dev.clearType(type);
		TileArray site = new TileArray(site_name, type);
		site.setParent(dev_name);

		// Update Polygon according to control points
		site_boundary.clear();
		ArrayList<ControlPoint> vertex_control = control.points("Vertex");
		for(ControlPoint p : vertex_control) site_boundary.addVertex(p);

		// Create new Site from polygon
		site.makeTiles(site_boundary, tileW, tileH, units, tile_rotation, tile_translation);

		// Add new spaces to Development
		dev.addSpace(site);
	}

	// Subdivide the site into Zones
	//
	void initZones() {

		//Define new Space Type
		String type = "zone";
		dev.clearType(type);
		ArrayList<TileArray> new_zones = new ArrayList<TileArray>();

		// Create new Zones from Voronoi Sites
		for (TileArray space : dev.spaceList()) {
			if (space.type.equals("site")) {
				ArrayList<ControlPoint> plot_control = control.points("Plot");
				ArrayList<TileArray> zones = space.getVoronoi(plot_control);
				int hue = 0;
				for (TileArray zone : zones) {
					zone.setType(type);
					zone.setHue(hue);
					new_zones.add(zone);
					hue += 40;
				}
			}
		}

		// Add new Spaces to Development
		for (TileArray zone : new_zones) dev.addSpace(zone);
	}

	// Subdivide Zones into Footprints
	//
	void initFootprints() {

		//Define new Space Type
		String type = "footprint";
		dev.clearType(type);
		ArrayList<TileArray> new_foot = new ArrayList<TileArray>();

		// Create new Footprints from Zone Space
		for (TileArray space : dev.spaceList()) {
			if (space.type.equals("zone")) {

				// Setback Footprint
				TileArray setback = space.getSetback();
				setback.setName("Setback");
				setback.setType(type);

				// Void Footprint(s)
				float yard_area = 2700;
				ArrayList<TileArray> voidSpace = new ArrayList<TileArray>();
				ArrayList<ControlPoint> void_control = control.points("Void");
				for (ControlPoint p : void_control) {
					if (space.pointInArray(p.x, p.y)) {
						TileArray t = space.getClosestN(p, yard_area);
						t.subtract(setback);
						t.setName(p.getTag());
						t.setType(type);
						//Subtract other voids from current to prevent overlap
						for(TileArray prev : voidSpace) t.subtract(prev);
						voidSpace.add(t);
					}
				}

				// Building Footprint
				TileArray building = space.getDiff(setback);
				for(TileArray v : voidSpace) building.subtract(v);
				building.setName("Building");
				building.setType(type);

				new_foot.add(setback);
				new_foot.add(building);
				for(TileArray v : voidSpace) new_foot.add(v);
			}
		}

		// Add new Spaces to Development
		for (TileArray foot : new_foot) dev.addSpace(foot);
	}

	// A Base is a building component that rests on a Footprint
	//
	void initBases() {

		//Define new Space Type
		String type = "base";
		dev.clearType(type);
		ArrayList<TileArray> new_bases = new ArrayList<TileArray>();
		int i = 0;

		// Create new Bases from Footprints
		for (TileArray space : dev.spaceList()) {

			// Building
			if (space.name.equals("Building") && space.type.equals("footprint")) {
				TileArray base = space.getExtrusion(-1, i);
				base.setName("Podium");
				base.setType(type);
				new_bases.add(base);
				i++;
			}

			// OpenSpace
			if (space.name.substring(0, 3).equals("Voi") && space.type.equals("footprint")) {
				TileArray base = space.getExtrusion(0, 0);
				base.setName("Courtyard");
				base.setType(type);
				new_bases.add(base);
			}
		}

		// Add new Spaces to Development
		for (TileArray base : new_bases) dev.addSpace(base);
	}

	// Initialize Vertex Control Points
	//
	void initVertexControl() {
		vert_counter = 1;
		String point_prefix = "Vertex";
		for (Point p : site_boundary.getCorners()) {
			control.addPoint(point_prefix + " " + vert_counter, point_prefix, p.x, p.y);
			vert_counter++;
		}
	}

	// Initialize Plot Control Points
	//
	void initPlotControl() {
		plot_counter = 1;
		for (TileArray space : dev.spaceList()) {
			if (space.type.equals("site")) {
				String point_prefix = "Plot";
				for (int i=0; i<3; i++) {
					control.addPoint(point_prefix + " " + plot_counter, point_prefix, space);
					plot_counter++;
				}
			}
		}
	}

	// Initialize Void Control Points
	//
	void initVoidControl() {
		void_counter = 1;
		for (TileArray space : dev.spaceList()) {
			// Add Control Point to Zone
			if (space.type.equals("zone")) {
				String point_prefix = "Void";
				void_counter++;
				control.addPoint(point_prefix + " " + void_counter, point_prefix, space);
			}
		}
	}

	// Front-End Methods that rely heavily on Processing Library Functions

	// Designed to run every frame to check mouse position
	void listen() {

		if (cam3D) {
			cam3D();
		} else {
			cam2D();
		}

		if (addPoint) {
			Point atMouse = newPointAtMouse();
			if (atMouse != null) {
				ControlPoint ghost = new ControlPoint(atMouse.x, atMouse.y);
				ghost.setTag("ghost");
				hovering = ghost;
			} else {
				hovering = null;
			}
		} else {
			hovering = pointAtMouse();
		}

		if(mousePressed && selected != null && selected.active()) {
			if(cam3D) {
				Point new_location = newPointAtMouse();
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

	Point newPointAtMouse() {
		Point mousePoint = null;

		if(cam3D) {
			// generate a grid of points to search for nearest match
			// centered at (0,0)
			int breadth  = 1000;
			int interval = 5;

			float min_distance = Float.POSITIVE_INFINITY;
			for(int x=-breadth; x<breadth; x+=interval) {
				for(int y=-breadth; y<breadth; y+=interval) {
					float dist_x = mouseX - screenX(x, y);
					float dist_y = mouseY - screenY(x, y);
					float distance = sqrt( sq(dist_x) + sq(dist_y) );
					if (distance < 15) {
						if (distance < min_distance) {
							min_distance = distance;
							mousePoint = new Point(x,y);
						}
					}
				}
			} 
		} else {
			mousePoint = new Point(mouseX, mouseY);
		}
		return mousePoint;
	}

	// Return Tagged Point Nearest to Mouse
	//
	ControlPoint pointAtMouse() {
		ControlPoint closest = null;
		float min_distance = Float.POSITIVE_INFINITY;
		for (ControlPoint p : control.points()) {
			float dist_x, dist_y;
			if(cam3D) {
				dist_x = mouseX - screenX(p.x, p.y);
				dist_y = mouseY - screenY(p.x, p.y);
			} else {
				dist_x = mouseX - p.x;
				dist_y = mouseY - p.y;
			}
			float distance = sqrt( sq(dist_x) + sq(dist_y) );
			if (distance < 15) {
				if (distance < min_distance) {
					min_distance = distance;
					closest = p;
				}
			}
		}
		return closest;
	}

	// Triggered when any key is pressed
	public void keyPressed() {

		switch(key) {
		case 'r':
			initModel();
			initRender();
			addPoint = false;
			removePoint = false;
			break;
		case 'a':
			addPoint = !addPoint;
			removePoint = false;
			if (addPoint) activateEditor();
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
			if (tileW > 1) tileW--;
			site_change_detected = true;;
			break;
		case '+':
			if (tileW < 50) tileW++;
			site_change_detected = true;
			break;
		case '[':
			tile_rotation -= 0.01;
			site_change_detected = true;;
			break;
		case ']':
			tile_rotation += 0.01;
			site_change_detected = true;
			break;
		case '}':
			tile_rotation += 0.1;
			site_change_detected = true;;
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
			//case '6':
			//  floorState();
			//  break;
			//case '7':
			//  roomState();
			//  break;
		}

		if (key == CODED) { 
			if (keyCode == LEFT) {
				tile_translation.x--;
				site_change_detected = true;
			}  
			if (keyCode == RIGHT) {
				tile_translation.x++;
				site_change_detected = true;
			}  
			if (keyCode == DOWN) {
				tile_translation.y++;
				site_change_detected = true;
			}  
			if (keyCode == UP) {
				tile_translation.y--;
				site_change_detected = true;
			}
		}

		loop();
	}  

	// Triggered once when any mouse button is pressed
	public void mousePressed() {
		if (addPoint) {
			Point atMouse = newPointAtMouse();
			addControlPoint(atMouse.x, atMouse.y);
		} else {
			selected = hovering;
			if (removePoint) {
				removeControlPoint(selected);
			}
		}

		loop();
	}

	// Triggered once when any mouse button is released
	public void mouseReleased() {
		selected = null;

		loop();
	}

	public void mouseMoved() {
		loop();
	}

	public void mouseDragged() {
		loop();
	}

	void addControlPoint(float x, float y) {
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

	void removeControlPoint(ControlPoint point) {
		control.removePoint(point);
		detectChange(point.getType());
	}

	// detect change based upon a type string
	void detectChange(String type) {
		if (type.equals("Vertex")) {
			site_change_detected = true;
		} else if (type.equals("Plot")) {
			zone_change_detected = true;
		} else if (type.equals("Void")) {
			foot_change_detected = true;
		}
	}

	void toggleVertexEditing() {
		editVertices = !editVertices;
		editPlots = false;
		editVoids = false;
		new_control_type = "Vertex";
		control.off();
		if (editVertices) {
			control.on(new_control_type);
			// auto add points if list is empty
			if (control.points(new_control_type).size() == 0) addPoint = true;
			showPolygons = true;
		}
	}

	void togglePlotEditing() {
		editVertices = false;
		editPlots = !editPlots;
		editVoids = false;
		new_control_type = "Plot";
		control.off();
		if (editPlots) control.on(new_control_type);
		// auto add points if list is empty
		if (control.points(new_control_type).size() == 0) addPoint = true;
	}

	void toggleVoidEditing() {
		editVertices = false;
		editPlots = false;
		editVoids = !editVoids;
		new_control_type = "Void";
		control.off();
		if (editVoids) {
			control.on(new_control_type);
			// auto add points if list is empty
			if (control.points(new_control_type).size() == 0) addPoint = true;
		}
	}

	// Activate Editor
	void activateEditor() {
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

	// Front-End Methods that rely heavily on Processing Library Functions

	void cam3D() {
		camera(200, 400, 200, 400, 200, 0, 0, 0, -1); 
		lights(); colorMode(HSB); pointLight(0, 0, 100, 50, 50, 50);
	} 

	void cam2D() {
		camera(); noLights(); perspective();
	}

	void siteState() {
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

	void zoneState() {
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

	void footprintState() {
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

	void buildingZoneState() {
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

	void buildingState() {
		// Building Layer State
		offState();
		showTiles = true;
		showPolygons = true;
		showBases = true;
		showTowers = true;
		viewState = 5;
	}

	void floorState() {
		// Floor Layer State
		offState();
		showTiles = true;
		showPolygons = true;
		showFloors = true;
		viewState = 6;
	}

	void roomState() {
		// Room Layer State
		offState();
		showTiles = true;
		showPolygons = true;
		showRooms = true;
		viewState = 7;
	}

	void offState() {
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

	void render() {
		hint(ENABLE_DEPTH_TEST);
		background(255);

		if (showTiles) {

			for (TileArray space : dev.spaceList()) {
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
		fill(245, 225); noStroke(); 
		if (showPolygons) {
			stroke(0, 100); 
			strokeWeight(1);
		}
		pushMatrix(); translate(0, 0, -2);
		beginShape();
		for(Point p : site_boundary.getCorners()) vertex(p.x, p.y);
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
		fill(0); textAlign(LEFT, TOP);
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
		text(info, 10, 10);
		//text("Framerate: " + int(frameRate), 10, height - 20);

		// Draw Summary
		//
		if (showTiles) {
			fill(100); textAlign(LEFT, TOP);
			String summary = "";
			summary += "View Model: " + viewModel;
			summary += "\n" + "Tile Dimensions:";
			summary += "\n" + tileW + " x " + tileW + " x " + tileH + " units";
			summary += "\n";
			summary += "\n" + dev + "/...";
			for(TileArray space : dev.spaceList()) {
				if (showSpace(space)) {
					summary += "\n~/" + space;
					//summary += "\n" + space.parent_name + "/" + space;
				}
			}
			text(summary, width - 175, 10);
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
			rotate(tile_rotation);
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
		rotate(tile_rotation);
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

	boolean showSpace(TileArray space) {
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

}