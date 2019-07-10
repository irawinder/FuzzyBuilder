// Initialize Front End:

    // Point that is currently selected or hovering;
    TaggedPoint selected;
    TaggedPoint hovering;
    
    // Add or remove point via mouse click
    boolean addPoint, removePoint;
    
    // Is camera 3D? Otherwise it's 2D;
    boolean cam3D;
    
    // Is there a specific view mode?
    String viewModel;
    
    // Initialize the View Model
    void initRender() {
      cam3D = true;
      viewModel = "DOT";
      addPoint = false;
      removePoint = false;
    }

// Initialize Backend:
    
    Site site_test;
    
    Polygon site_boundary;
    ArrayList<TaggedPoint> control_points;
    int control_point_counter;
    float tile_size, tile_rotation;
    Point tile_translation;
    
    // Update model state?
    boolean site_change_detected;
    boolean zone_change_detected;
    
    void initModel() {
      
      // Init Vector Site Polygon
      site_boundary = new Polygon();
      site_boundary.randomShape(400, 200, 5, 100, 200);
      
      // Init Raster-like Site Voxels
      site_test = new Site("Site_Test");
      tile_size = 10;
      tile_translation = new Point(0,0);
      tile_rotation = 0;
      site_test.makeTiles(site_boundary, tile_size, "pixels", tile_rotation, tile_translation);
      
      // Init Control Points
      control_point_counter = 0;
      control_points = new ArrayList<TaggedPoint>();
      int i = 0;
      while (i<4) {
        float randomX = random(site_boundary.xMin(), site_boundary.xMax());
        float randomY = random(site_boundary.yMin(), site_boundary.yMax());
        TaggedPoint random = new TaggedPoint(randomX, randomY);
        if (site_boundary.containsPoint(random)) {
          control_point_counter++;
          random.setTag("Zone " + control_point_counter);
          control_points.add(random);
          i++;
        }
      }
      
      // Init Voronoi Zones
      site_test.makeZones(control_points);
      
      // Update model state?
      site_change_detected = false;
      zone_change_detected = false;
    }

// Update Backend:
    
    void updateModel() {
      
      if(site_change_detected) {
        site_test.makeTiles(site_boundary, tile_size, "pixels", tile_rotation, tile_translation);
        site_test.makeZones(control_points);
        site_change_detected = false;
      }
      
      if(zone_change_detected) {
        // Init Voronoi Zones
        site_test.makeZones(control_points);
        zone_change_detected = false;
      }
    }
