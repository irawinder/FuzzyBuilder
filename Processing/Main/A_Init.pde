// Initialize or Updated Backend

ArrayList<TaggedPoint> control_points;
Polygon site_boundary;
Site site_test;

// Update model state?
boolean change_detected;

void init() {
  
  // Init Vector Site Polygon
  site_boundary = new Polygon();
  site_boundary.randomShape(400, 200, 5, 100, 200);
  
  // Init Raster-like Site Voxels
  site_test = new Site("Site_Test");
  site_test.makeTiles(site_boundary, 10, "pixels");
  
  // Init Control Points
  control_points = new ArrayList<TaggedPoint>();
  int i = 0;
  while (i<4) {
    float randomX = random(site_boundary.xMin(), site_boundary.xMax());
    float randomY = random(site_boundary.yMin(), site_boundary.yMax());
    TaggedPoint random = new TaggedPoint(randomX, randomY);
    random.setTag("Zone " + (i+1));
    if (site_boundary.containsPoint(random)) {
      control_points.add(random);
      i++;
    }
  }
  
  // Init Voronoi Zones
  site_test.makeZones(control_points);
  
  // Update model state?
  change_detected = false;
}

void update() {
  if(change_detected) {
    // Init Voronoi Zones
    site_test.makeZones(control_points);
  }
  change_detected = false;
}
