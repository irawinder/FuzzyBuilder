import java.util.Random;

// Initialize Front End:

    // Point that is currently selected or hovering;
    TaggedPoint selected;
    TaggedPoint hovering;
    
    // Add or remove point via mouse click
    boolean editZones, addPoint, removePoint;
    
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
      
      editZones = true;
      addPoint = false;
      removePoint = false;
    }

// Initialize Backend:
    
    NestedTileArray site_test;
    
    Development dev;
    String dev_name;
    
    Polygon site_boundary;
    String site_name;
    
    ArrayList<TaggedPoint> control_points;
    int control_point_counter;
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
      
      // Init Control Points
      control_point_counter = 0;
      control_points = new ArrayList<TaggedPoint>();
      Random rand = new Random();
      int i = 0;
      while (i<4) {
        float randomX = rand.nextFloat() * (site_boundary.xMax() - site_boundary.xMin()) + site_boundary.xMin();
        float randomY = rand.nextFloat() * (site_boundary.yMax() - site_boundary.yMin()) + site_boundary.yMin();
        TaggedPoint random = new TaggedPoint(randomX, randomY);
        if (site_boundary.containsPoint(random)) {
          control_point_counter++;
          random.setTag("Plot " + control_point_counter);
          control_points.add(random);
          i++;
        }
      }
      
      // Init Raster-like Site Voxels
      site_test = new NestedTileArray("Site_Test", "site");
      dev_name = "New Development";
      dev = new Development(dev_name);
      site_name = "New Property";
      tileW = 15.0;
      tileH = 5.0;
      units = "pixels";
      tile_translation = new Point(0,0);
      tile_rotation = 0;
      
      // Update model state?
      site_change_detected = true;
      
      updateModel();
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
    
    void initSite() {
      // Init Site from Polygon
      site_test.makeSite(site_boundary, tileW, tileH, units, tile_rotation, tile_translation);
      
      dev.addSpace(site_name, "site");
      dev.getSpace(site_name).makeTiles(site_boundary, tileW, tileH, units, tile_rotation, tile_translation);
    }
    
    void initZones() {
      // Init Voronoi Zones
      site_test.makeZones(control_points);
    }
    
    void initFootprints() {
      // Init Footprints
      for(NestedTileArray zone : site_test.childList()) {
        zone.makeFootprints();
      }
    }
    
    void initBase() {
      // Init Base
      int i = 0;
      for(NestedTileArray zone : site_test.childList()) {
        for(NestedTileArray foot : zone.childList()) {
          if(foot.name.equals("Building")) {
            foot.makeBase(min(0, -4 + i), i);
            i++;
          }
        }
      }
    }
