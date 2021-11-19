import java.util.Random;
import java.util.Collections;
import java.util.UUID;

// Either JAVA2D (default), P2D or P3D
final String RENDERER = P3D;

final float GRID_WIDTH = 10000;
final float GRID_HEIGHT = 1000;
final int GRID_UNITS = 100;

final int NUM_VERTICES = 4;
final float PLOT_X = 0;
final float PLOT_Y = -1000;
final float PLOT_MIN = 200;
final float PLOT_MAX = 1000;

final int DEFAULT_COLOR = #999999;
final int BACKGROUND_COLOR = #222222;

Camera cam;

Test test = new Test();
Polygon plot;

void setup() {
  size(1280, 800, RENDERER);
  cam = new Camera(RENDERER);
  cam.eye.y = - 0.50 * PLOT_MAX;
  cam.angleYZ = 0.20 * PI;
  this.plot = test.randomShape(PLOT_X, PLOT_Y, NUM_VERTICES, PLOT_MIN, PLOT_MAX);
}

void draw() {
  // Update Camera Movement
  if (keyPressed) cam.move();
  
  background(BACKGROUND_COLOR);
  
  // 3D Objects
  cam.pov();
  this.drawGrids(GRID_WIDTH, GRID_UNITS, GRID_HEIGHT);
  this.drawShape(this.plot);
  
  // 2D Overlay
  cam.overlay();
  
  if (mousePressed) {
    
    // Update Camera Angle relative to cursor
    cam.updateCursorAngle();
    
    // reset mouse position
    cam.setMouseToCenter();
  }
}

void drawShape(Polygon p) {
  noFill();
  stroke(255);
  for(Line edge : p.edge) {
    line(edge.o.x, edge.o.z, edge.o.y, edge.f.x, edge.f.z, edge.f.y);
  }
}

void keyPressed() {
  switch(key) {
    case 'c':
      cam.init();
      break;
    case 'r':
      this.plot = test.randomShape(PLOT_X, PLOT_Y, NUM_VERTICES, PLOT_MIN, PLOT_MAX);
      break;
  }
}

void mouseMoved() {
    
  // Update Camera Angle relative to cursor
  cam.updateCursorAngle();
  
  // reset mouse position
  cam.setMouseToCenter();
}
      
void mouseWheel(MouseEvent event) {
  
  if (!cam.isPaused()) {
    
    // Fly Up or Down using mouse wheel
    float e = event.getCount();
    cam.fly(e);
  }
}

void drawGrids(float gridSize, int gridUnits, float z) {
  this.drawGrid(gridSize, gridUnits, -z);
  this.drawGrid(gridSize, gridUnits, +z);
}

void drawGrid(float gridSize, int gridUnits, float z) {
  stroke(DEFAULT_COLOR, 20);
  float gridWidth = gridSize / gridUnits;
  for (int u=0; u<gridUnits; u++) {
    line(-0.5 * gridSize, z, -0.5 * gridSize + u * gridWidth, 0.5 * gridSize, z, -0.5 * gridSize + u * gridWidth);
    line(-0.5 * gridSize + u * gridWidth, z, -0.5 * gridSize, -0.5 * gridSize + u * gridWidth, z, 0.5 * gridSize);
  }
}

void drawCursor() {
  stroke(DEFAULT_COLOR, 200);
  pushMatrix(); translate(width/2, height/2);
  line(0, -20, 0, 20);
  line(-20, 0, 20, 0);
  popMatrix();
}
