import java.util.Random;
import java.util.Collections;

// Either JAVA2D (default), P2D or P3D
final String RENDERER = P3D;

final float GRID_WIDTH = 10000;
final float GRID_HEIGHT = 1000;
final int GRID_UNITS = 100;

final int NUM_VERTICES = 4;
final float PLOT_X = 0;
final float PLOT_Y = -1000;
final float PLOT_MIN_RADIUS = 200;
final float PLOT_MAX_RADIUS = 1000;
final float VOXEL_WIDTH = 25;
final float VOXEL_HEIGHT = 10;
final float VOXEL_ROTATION = 0.25 * PI;
final Point VOXEL_TRANSLATE = new Point(5, 5);
final float SETBACK_DISTANCE = 50;
final int LEVELS_TO_EXTRUDE = 5;
final float TOWER_X = 0;
final float TOWER_Y = -1000;
final float TOWER_WIDTH = 100;
final float TOWER_DEPTH = 200;
final float TOWER_ROTATION = 0.25 * PI;

final int DEFAULT_COLOR = #999999;
final int BACKGROUND_COLOR = #222222;

Camera cam;

FuzzyRandom random;
FuzzyMorph morph;
Polygon plotShape, towerShape;
VoxelArray plot, podium, tower;

void setup() {
  
  size(1280, 800, RENDERER);
  
  cam = new Camera(RENDERER);
  cam.eye.y = - 0.50 * PLOT_MAX_RADIUS;
  cam.angleYZ = 0.20 * PI;
  
  this.random = new FuzzyRandom();
  this.morph = new FuzzyMorph();
  this.initGeometry();
}

void initGeometry() {
  this.plotShape = this.random.polygon(PLOT_X, PLOT_Y, NUM_VERTICES, PLOT_MIN_RADIUS, PLOT_MAX_RADIUS);
  this.plot = this.morph.make(this.plotShape, VOXEL_WIDTH, 0, VOXEL_ROTATION, VOXEL_TRANSLATE);
  this.podium = this.morph.hardCloneVoxelArray(this.plot);
  this.podium.setHeight(VOXEL_HEIGHT);
  this.podium = this.morph.setback(this.podium, SETBACK_DISTANCE);
  this.podium = this.morph.extrude(this.podium, LEVELS_TO_EXTRUDE);
  this.towerShape = this.morph.rectangle(new Point(TOWER_X, TOWER_Y), TOWER_WIDTH, TOWER_DEPTH, TOWER_ROTATION);
}

void draw() {
  // Update Camera Movement
  if (keyPressed) cam.move();
  
  background(BACKGROUND_COLOR);
  
  // 3D Objects
  cam.pov();
  this.drawGrids(GRID_WIDTH, GRID_UNITS, GRID_HEIGHT);
  this.drawShape(this.plotShape);
  this.drawShape(this.towerShape);
  //this.drawTiles(this.plot);
  //this.drawVoxels(this.podium);
  
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
      this.initGeometry();
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
