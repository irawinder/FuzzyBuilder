import java.util.Random;
import java.util.Collections;
import processing.net.*;
  
// Server Settings
private FuzzyServer server;
private final int PORT = 8080;
private final boolean RUN_SERVER = true;

// Test Scripts (Set to false for production)
private final boolean RUN_TESTS = true;
private FuzzyRandom random;

// Main global object for holding Fuzzy State as Voxels
private Development fuzzy;

// Superficial Draw Stuff
Camera cam;
final float GRID_WIDTH = 10000;
final float GRID_HEIGHT = 1000;
final int GRID_UNITS = 100;
final int DEFAULT_COLOR = #999999;
final int BACKGROUND_COLOR = #222222;
private boolean drawVoxels = true;

// Either JAVA2D (default), P2D or P3D
final String RENDERER = P3D;

void setup() {
  
  size(1280, 800, RENDERER);
  
  this.cam = new Camera(RENDERER);
  this.cam.eye.y = - 0.50 * GRID_HEIGHT;
  this.cam.angleYZ = 0.20 * PI; 
    
  this.random = new FuzzyRandom();
  
  if(RUN_SERVER) {
    this.server = new FuzzyServer(PORT);
  } 
  
  if(RUN_TESTS) {
    this.fuzzy = random.development();
  }
}

void draw() {
  
  // Listen for client requests
  if(server != null) {
    server.listenForRequest();
  }
  
  // Update Camera Movement
  if (keyPressed) cam.move();
  
  background(BACKGROUND_COLOR);
  
  // 3D Objects
  cam.pov();
    
  // World Grid
  this.drawGrids(GRID_WIDTH, GRID_UNITS, GRID_HEIGHT);
  
  // Polygons
  if(this.fuzzy != null) {
    for(Polygon plotShape : this.fuzzy.plotShapes) {
      
      // Plot Polygons
      this.drawShape(plotShape);
      
      // Tower Polygons
      if(this.fuzzy.towerShapes.get(plotShape) != null) {
        for(Polygon towerShape : this.fuzzy.towerShapes.get(plotShape)) {
          this.drawShape(towerShape);
        }
      }
    }
    
    // Voxels
    if (drawVoxels) {
      this.drawTiles(this.fuzzy.site);
      this.drawVoxels(this.fuzzy.massing);
    }
  }
  
  // 2D Overlay
  cam.overlay();
  // TBD
  
  if (mousePressed) {
    if (!cam.isPaused()) {
      
      // Update Camera Angle relative to cursor
      cam.updateCursorAngle();
      
      // reset mouse position
      cam.setMouseToCenter();
    }
  }
}

void keyPressed() {
  switch(key) {
    case 'c':
      cam.init();
      break;
    case 'r':
      this.fuzzy = random.development();
      break;
    case ' ':
      cam.pause();
      break;
    case 'v':
      drawVoxels = !drawVoxels;
      break;
  }
}

void mouseMoved() {
  if (!cam.isPaused()) {
    
    // Update Camera Angle relative to cursor
    cam.updateCursorAngle();
    
    // reset mouse position
    cam.setMouseToCenter();
  }
}
      
void mouseWheel(MouseEvent event) {
  if (!cam.isPaused()) {
    
    // Fly Up or Down using mouse wheel
    float e = event.getCount();
    cam.fly(e);
  }
}

// This method runs when a client first connects to the Server
void serverEvent(Server s, Client c) {
  //println("Server: We have a new client: " + c.ip());
}
