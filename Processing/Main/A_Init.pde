import java.util.Random;

// Initialize Front End:

    // Point that is currently selected or hovering;
    ControlPoint selected;
    ControlPoint hovering;
    
    // Add or remove point via mouse click
    boolean editModel, addPoint, removePoint;
    
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
      
      editModel = true;
      addPoint = false;
      removePoint = false;
    }

// Initialize Backend:
    
    Development dev;
    String dev_name;
    
    Polygon site_boundary;
    String site_name;
    
    Control control;
    
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
      tileW = 15.0;
      tileH = 5.0;
      units = "pixels";
      tile_translation = new Point(0,0);
      tile_rotation = 0;
      
      control = new Control();
      
      initSite();
      initSiteControl();
      initZones();
      initZoneControl();
      initFootprints();
      initBase();
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
          initSite();
        case 'z':
          initZones();
        case 'f':
          initFootprints();
          initBase();
          break;
      }
    }
    
    // Initialize Site
    //
    void initSite() {
      
      //Define new Space Type
      String type = "site";
      dev.clearType(type);
      TileArray site = new TileArray(site_name, type);
      site.setParent(dev_name);
      
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
          ArrayList<TileArray> zones = space.getVoronoi(control.points(type));
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
          
          // Find a courtyard
          float yard_area = 2700;
          ArrayList<TileArray> courtyard = new ArrayList<TileArray>();
          ArrayList<ControlPoint> points = control.points(type);
          for (ControlPoint p : points) {
            if (space.pointInArray(p.x, p.y)) {
              TileArray t = space.getClosestN(p, yard_area);
              t.setName(p.getTag());
              t.setType(type);
              
              //Subtract other courtyards from current to prevent overlap
              for(TileArray prev : courtyard) t.subtract(prev);
              
              courtyard.add(t);
            }
          }
          
          
          // Building Footprint
          TileArray building = space.getDifference(setback);
          for(TileArray cy : courtyard) building.subtract(cy);
          building.setName("Building");
          building.setType(type);
          
          new_foot.add(setback);
          new_foot.add(building);
          for(TileArray cy : courtyard) new_foot.add(cy);
        }
      }
      
      // Add new Spaces to Development
      for (TileArray foot : new_foot) dev.addSpace(foot);
    }
    
    // A Base is a building component that rests on a Footprint
    //
    void initBase() {
      
      //Define new Space Type
      String type = "base";
      dev.clearType(type);
      ArrayList<TileArray> new_bases = new ArrayList<TileArray>();
      int i = 0;
      
      // Create new Bases from Footprints
      for (TileArray space : dev.spaceList()) {
        if (space.name.equals("Building") && space.type.equals("footprint")) {
          TileArray base = space.getExtrusion(min(0, -4 + i), i);
          base.setName("Podium");
          base.setType(type);
          new_bases.add(base);
          i++;
        }
      }
      
      // Add new Spaces to Development
      for (TileArray base : new_bases) dev.addSpace(base);
    }
    
    // Initialize Control Points
    //
    void initSiteControl() {
      for (TileArray space : dev.spaceList) {
        if (space.type.equals("site")) {
          String point_prefix = "Plot";
          for (int i=0; i<4; i++) control.addPoint(point_prefix, "zone", space);
        }
      }
    }
    
    // Initialize Control Points
    //
    void initZoneControl() {
      for (TileArray space : dev.spaceList) {
        // Add Control Point to Zone
        if (space.type.equals("zone")) {
          String point_prefix = "Courtyard";
          control.addPoint(point_prefix, "footprint", space);
        }
      }
    }
