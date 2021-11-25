import java.util.Random;
import java.util.Collections;
import processing.net.*;
  
// Server Settings
private final String HTTP_VERSION = "1.1";
private final String SERVER = "Processing Server (Java " + System.getProperty("java.version") + ")";
private final int PORT = 8080;
private final boolean RUN_SERVER = true;

private FuzzyServer fuzzyIO;
private FuzzyBuilder fuzzy;
private FuzzyRandom random;

// Either JAVA2D (default), P2D or P3D
final String RENDERER = P3D;

final float GRID_WIDTH = 10000;
final float GRID_HEIGHT = 1000;
final int GRID_UNITS = 100;
final int DEFAULT_COLOR = #999999;
final int BACKGROUND_COLOR = #222222;

boolean drawVoxels = true;

// Test Scripts (Set to false for production)
private final boolean RUN_TESTS = true;

Camera cam;
void setup() {
  
  size(1280, 800, RENDERER);
  
  if(RUN_SERVER) {
  
    // Initialize and Start Server
    fuzzyIO = new FuzzyServer(PORT);
  } 
  
  cam = new Camera(RENDERER);
  cam.eye.y = - 0.50 * GRID_HEIGHT;
  cam.angleYZ = 0.20 * PI;
  
  random = new FuzzyRandom();
  if(RUN_TESTS) {
    this.fuzzy = random.fuzzy();
  }
}

void draw() {
  
  // Listen for client requests
  if(fuzzyIO != null) {
    fuzzyIO.listenForRequest();
  }
  
  // Update Camera Movement
  if (keyPressed) cam.move();
  
  background(BACKGROUND_COLOR);
  
  // 3D Objects
  cam.pov();
    
  // World Grid
  this.drawGrids(GRID_WIDTH, GRID_UNITS, GRID_HEIGHT);
  
  if(fuzzy != null) {
    
    // Polygons
    for(Polygon plotShape : this.fuzzy.plotShapes) {
      this.drawShape(plotShape);
      
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
      this.fuzzy = random.fuzzy();
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
//
void serverEvent(Server s, Client c) {
  //println("Server: We have a new client: " + c.ip());
}
