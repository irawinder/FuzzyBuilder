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
final float PLOT_MIN_RADIUS = 200;
final float PLOT_MAX_RADIUS = 1000;
final float VOXEL_WIDTH = 25;
final float VOXEL_HEIGHT = 10;
final float VOXEL_ROTATION = 0.25 * PI;
final Point VOXEL_TRANSLATE = new Point(0, 0, 0);

final int DEFAULT_COLOR = #999999;
final int BACKGROUND_COLOR = #222222;

Camera cam;

RandomShape randomShape;
Morph morph;
Polygon plot;
VoxelArray volume;

void setup() {
  
  size(1280, 800, RENDERER);
  
  cam = new Camera(RENDERER);
  cam.eye.y = - 0.50 * PLOT_MAX_RADIUS;
  cam.angleYZ = 0.20 * PI;
  
  randomShape = new RandomShape();
  this.plot = randomShape.make(
    PLOT_X, 
    PLOT_Y, 
    NUM_VERTICES, 
    PLOT_MIN_RADIUS, 
    PLOT_MAX_RADIUS
  );
  
  this.morph = new Morph();
  this.volume = this.morph.make(
    this.plot, 
    VOXEL_WIDTH, 
    VOXEL_HEIGHT, 
    VOXEL_ROTATION, 
    VOXEL_TRANSLATE
  );
}

void draw() {
  // Update Camera Movement
  if (keyPressed) cam.move();
  
  background(BACKGROUND_COLOR);
  
  // 3D Objects
  cam.pov();
  this.drawGrids(GRID_WIDTH, GRID_UNITS, GRID_HEIGHT);
  this.drawShape(this.plot);
  this.drawVoxels(this.volume);
  
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

void drawVoxels(VoxelArray voxelArray) {
  fill(255, 100);
  noStroke();
  for(Voxel voxel : voxelArray.voxelList) {
    pushMatrix();
    translate(voxel.location.x, voxel.location.z, voxel.location.y);
    rotateY(voxel.rotation);
    box(0.9 * voxel.w, 0.9 * voxel.h, 0.9 * voxel.w);  
    popMatrix();
  }
}

void keyPressed() {
  switch(key) {
    case 'c':
      cam.init();
      break;
    case 'r':
      this.plot = randomShape.make(PLOT_X, PLOT_Y, NUM_VERTICES, PLOT_MIN_RADIUS, PLOT_MAX_RADIUS);
      this.volume = this.morph.make(this.plot, VOXEL_WIDTH, VOXEL_HEIGHT, VOXEL_ROTATION, VOXEL_TRANSLATE);
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
