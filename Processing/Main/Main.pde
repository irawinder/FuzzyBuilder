ArrayList<Point> control_points;
Polygon site_boundary;
Site test;

void init() {
  site_boundary = new Polygon();
  site_boundary.randomShape(200, 200, 5, 50, 190);
  
  test = new Site("Test");
  test.makeTilesFromPolygon(site_boundary, 10, "pixels");
  
  control_points = new ArrayList<Point>();
  int i = 0;
  while (i<10) {
    Point random = new Point(random(10, 390), random(10, 390));
    if (site_boundary.containsPoint(random)) {
      control_points.add(random);
      i++;
    }
  }
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
  background(0);
  
  fill(50); noStroke();
  beginShape();
  for(Point p : site_boundary.vertex) vertex(p.x, p.y);
  endShape(CLOSE);
  
  for (Point p : control_points) {
    fill(0); stroke(200); strokeWeight(1);
    line(p.x-5, p.y, p.x+5, p.y);
    line(p.x, p.y-5, p.x, p.y+5);
  }
  
  for(Map.Entry e : test.getTiles().entrySet()) {
    Tile t = (Tile)e.getValue();
    noFill(); fill(255, 100); noStroke();
    ellipse(t.location.x, t.location.y, 0.75*t.scale, 0.75*t.scale);
  }
}

void keyPressed() {
  init();
  render();
}
