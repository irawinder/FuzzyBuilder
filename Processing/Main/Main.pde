ArrayList<Point> control_points;
Polygon site;

void init() {
  site = new Polygon();
  site.randomShape(200, 200, 5, 50, 190);
  
  control_points = new ArrayList<Point>();
  int i = 0;
  while (i<3) {
    Point random = new Point(random(10, 390), random(10, 390));
    if (site.containsPoint(random)) {
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
  
  fill(50); stroke(100); strokeWeight(3);
  beginShape();
  for(Point p : site.vertex) vertex(p.x, p.y);
  endShape(CLOSE);
  
  for(Line l : site.edge) {
    stroke(#00FF00); 
    line(l.o.x, l.o.y, l.f.x, l.f.y);
  }
  
  for (Point p : control_points) {
    fill(0); stroke(100); ellipse(p.x, p.y, 10, 10);
  }
}

void keyPressed() {
  init();
  render();
}
