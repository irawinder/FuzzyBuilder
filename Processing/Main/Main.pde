ArrayList<TaggedPoint> control_points;
Polygon site_boundary;
Site site_test;

void init() {
  // Init Vector Site Polygon
  site_boundary = new Polygon();
  site_boundary.randomShape(200, 200, 5, 50, 190);
  
  // Init Raster-like Site Voxels
  site_test = new Site("Site_Test");
  site_test.initTiles(site_boundary, 10, "pixels");
  
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
  
  site_test.initZones(control_points);
}

// Runs Once On Program Start
//
void setup() {
  size(400, 400);
  init();
  render();
}

// Runs on Infinite Loop after setup() completes
//
void draw() {
  
}

void render() {
  background(255);
  
  // Draw Vector Polygon
  fill(225); noStroke();
  beginShape();
  for(Point p : site_boundary.vertex) vertex(p.x, p.y);
  endShape(CLOSE);
  
  // Draw Site Voxels
  for(Map.Entry e : site_test.getTiles().entrySet()) {
    Tile t = (Tile)e.getValue();
    noFill(); fill(150); noStroke();
    ellipse(t.location.x, t.location.y, 0.75*t.scale, 0.75*t.scale);
  }
  
  // Draw Tagged Control Points
  for (TaggedPoint p : control_points) {
    fill(0); stroke(0); strokeWeight(1);
    line(p.x-5, p.y, p.x+5, p.y);
    line(p.x, p.y-5, p.x, p.y+5);
    fill(255); stroke(200);
    rectMode(CENTER); rect(p.x + 30, p.y, 40, 10, 5);
    fill(50); textSize(9); textAlign(CENTER, CENTER);
    text(p.tag, p.x + 30, p.y - 1);
  }
}

void keyPressed() {
  init();
  render();
}
