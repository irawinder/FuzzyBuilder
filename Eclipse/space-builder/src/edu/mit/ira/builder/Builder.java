package edu.mit.ira.builder;

import java.util.ArrayList;

import edu.mit.ira.voxel.Control;
import edu.mit.ira.voxel.ControlPoint;
import edu.mit.ira.voxel.Development;
import edu.mit.ira.voxel.Point;
import edu.mit.ira.voxel.Polygon;
import edu.mit.ira.voxel.TileArray;

/**
 * Builder facilitates the making of various TileArrays via Polygons and/or
 * ControlPoints. Builder implements many classes from edu.mit.ira.voxel.
 * Builder should be implementable within both Processing or JavaFX GUI 
 * components.
 * 
 * @author ira
 *
 */
public class Builder {

	// Development of spaces
	public Development dev;
	public String dev_name;

	// Intermediate Polygon used to generate Development()
	public Polygon site_boundary;
	public String site_name;

	// Intermediate ControlPoints used to generate Development()
	public Control control;

	// Intermediate Raster Grid Options to generate Development
	// (dimensions, scale, rotation, translation, units)
	public float tileW, tileH, tile_rotation;
	public String units;
	public Point tile_translation;

	// Track attributes for any new control points
	public String new_control_type;
	private int vert_counter;
	private int plot_counter;
	private int void_counter;

	// Update model state?
	private boolean site_change_detected;
	private boolean zone_change_detected;
	private boolean foot_change_detected;

	// Point that is currently selected or hovering;
	public ControlPoint selected;
	public ControlPoint hovering;

	// Add or remove point via mouse click
	public boolean addPoint, removePoint;

	// Which control point set are we editing?
	public boolean editVertices, editPlots, editVoids;

	// Is camera 3D? Otherwise it's 2D;
	public boolean cam3D;

	// Hide or Show Tiles or Polygons
	public boolean showTiles, showPolygons, showText;

	// Hide or Show TileArray Nest Layers
	public int viewState;
	public boolean showSite, showZones, showFootprints, showBases, showTowers, showFloors, showRooms;

	// Is there a specific view mode?
	public String viewModel;

	/**
	 * Make the Space Building Environment
	 */
	public Builder() {
		initModel();
		initViewState();
	}
	
	public void setTileWidth(float tileW) {
		this.tileW = tileW;
	}
	
	public void setTileHeight(float tileH) {
		this.tileH = tileH;
	}
	
	public void setTileRotation(float tile_rotation) {
		this.tile_rotation = tile_rotation;
	}
	
	public void setTileTranslation(float x, float y) {
		this.tile_translation = new Point(x,y);
	}
	
	public void setTileUnits(String units) {
		this.units = units;
	}

	/**
	 * Initial Build State
	 */
	public void initViewState() {
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
	 * Initialize the Model
	 */
	public void initModel() {

		// Init Vector Site Polygon
		site_boundary = new Polygon();

		// Init Raster-like Site Voxels
		dev_name = "New Development";
		dev = new Development(dev_name);
		site_name = "Property";
		setTileUnits("pixels");
		setTileWidth(30);
		setTileHeight(10);
		setTileTranslation(0,0);
		setTileRotation(0);

		// Init Control Points
		control = new Control();
		new_control_type = "zone";
		editVertices = false;
		editPlots = false;
		editVoids = false;
	}

	

	/**
	 * Generates a randomly configured model at x, y
	 * @param x
	 * @param y
	 */
	public void loadRandomModel(float x, float y) {
		// Init Random Model and Control Points
		site_boundary.randomShape(x, y, 5, 200, 300);
		initVertexControl(site_boundary);
		initSites();
		initPlotControl();
		initZones();
		initVoidControl();
		initFootprints();
		initBases();
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

		switch (change) {
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

	/**
	 * Initialize Site Model
	 */
	public void initSites() {

		// Define new Space Type
		String type = "site";
		dev.clearType(type);
		TileArray site = new TileArray(site_name, type);
		site.setParent(dev_name);

		// Update Polygon according to control points
		site_boundary.clear();
		ArrayList<ControlPoint> vertex_control = control.points("Vertex");
		for (ControlPoint p : vertex_control)
			site_boundary.addVertex(p);

		// Create new Site from polygon
		site.makeTiles(site_boundary, tileW, tileH, units, tile_rotation, tile_translation);

		// Add new spaces to Development
		dev.addSpace(site);
	}

	/**
	 * Subdivide the site into Zones
	 */
	public void initZones() {

		// Define new Space Type
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
		for (TileArray zone : new_zones)
			dev.addSpace(zone);
	}

	/**
	 * Subdivide Zones into Footprints
	 */
	public void initFootprints() {

		// Define new Space Type
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
						// Subtract other voids from current to prevent overlap
						for (TileArray prev : voidSpace)
							t.subtract(prev);
						voidSpace.add(t);
					}
				}

				// Building Footprint
				TileArray building = space.getDiff(setback);
				for (TileArray v : voidSpace)
					building.subtract(v);
				building.setName("Building");
				building.setType(type);

				new_foot.add(setback);
				new_foot.add(building);
				for (TileArray v : voidSpace)
					new_foot.add(v);
			}
		}

		// Add new Spaces to Development
		for (TileArray foot : new_foot)
			dev.addSpace(foot);
	}

	/**
	 * A Base is a building component that rests on a Footprint
	 */
	public void initBases() {

		// Define new Space Type
		String type = "base";
		dev.clearType(type);
		ArrayList<TileArray> new_bases = new ArrayList<TileArray>();

		// Create new Bases from Footprints
		for (TileArray space : dev.spaceList()) {

			// Building
			if (space.name.equals("Building") && space.type.equals("footprint")) {
				TileArray base = space.getExtrusion(0, space.toExtrude());
				base.setName("Podium");
				base.setType(type);
				new_bases.add(base);
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
		for (TileArray base : new_bases)
			dev.addSpace(base);
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
		for (TileArray space : dev.spaceList()) {
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
		for (TileArray space : dev.spaceList()) {
			// Add Control Point to Zone
			if (space.type.equals("zone")) {
				String point_prefix = "Void";
				void_counter++;
				control.addPoint(point_prefix + " " + void_counter, point_prefix, space);
			}
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
			initModel();
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
			for(ControlPoint c : control.points()) System.out.println(c);
			System.out.println("--Other Grid Attributes");
			System.out.println("Grid Size: " + tileW);
			System.out.println("Grid Rotation: " + tile_rotation);
			System.out.println("Grid Pan: " + tile_translation);
			break;
		}

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

	/**
	 * Trigger once when any mouse button is released
	 */
	public void mouseReleased() {
		selected = null;
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
}