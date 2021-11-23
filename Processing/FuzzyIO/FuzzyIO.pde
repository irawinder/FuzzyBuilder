import java.util.Random;
import java.util.Collections;

// Either JAVA2D (default), P2D or P3D
final String RENDERER = P3D;

final float GRID_WIDTH = 10000;
final float GRID_HEIGHT = 1000;
final int GRID_UNITS = 100;
final int DEFAULT_COLOR = #999999;
final int BACKGROUND_COLOR = #222222;

Camera cam;

Test test;

void setup() {
  
  size(1280, 800, RENDERER);
  
  cam = new Camera(RENDERER);
  cam.eye.y = - 0.50 * GRID_HEIGHT;
  cam.angleYZ = 0.20 * PI;
  
  this.test = new Test();
}

void draw() {
  // Update Camera Movement
  if (keyPressed) cam.move();
  
  background(BACKGROUND_COLOR);
  
  // 3D Objects
  cam.pov();
  
  // World Grid
  this.drawGrids(GRID_WIDTH, GRID_UNITS, GRID_HEIGHT);
  
  // Polygons
  for(Polygon plotShape : this.test.plotShapes) {
    this.drawShape(plotShape);
    for(Polygon towerShape : this.test.towerShapes.get(plotShape)) {
      this.drawShape(towerShape);
    }
  }
  
  // Voxels
  this.drawTiles(this.test.site);
  this.drawVoxels(this.test.massing);
  
  // 2D Overlay
  cam.overlay();
  
  if (mousePressed) {
    
    // Update Camera Angle relative to cursor
    cam.updateCursorAngle();
    
    // reset mouse position
    cam.setMouseToCenter();
  }
}

void keyPressed() {
  switch(key) {
    case 'c':
      cam.init();
      break;
    case 'r':
      this.test = new Test();
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
