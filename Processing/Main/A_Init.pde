ArrayList<TaggedPoint> control_points;
Polygon site_boundary;
Site site_test;

void init() {
  // Init Vector Site Polygon
  site_boundary = new Polygon();
  site_boundary.randomShape(200, 200, 5, 50, 190);
  
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
  
  site_test.makeZones(control_points);
}
