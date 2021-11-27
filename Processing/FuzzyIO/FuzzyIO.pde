import java.util.Random;
import java.util.Collections;
import processing.net.*;
  
// Server Settings
private FuzzyServer server;
private final int PORT = 8080;
private final boolean RUN_SERVER = true;

// Visualization Scripts (Set to false for production)
private final boolean RUN_VIZ = true;
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
  
  this.fuzzy = new Development();
  
  if (RUN_SERVER) {
    this.server = new FuzzyServer(PORT);
    background(BACKGROUND_COLOR);
    textAlign(CENTER, CENTER); fill(255);
    text(server.info, width/2, height/2);
  } 
  
  if (RUN_VIZ) {
    this.cam = new Camera(RENDERER);
    this.cam.eye.y = - 0.50 * GRID_HEIGHT;
    this.cam.angleYZ = 0.20 * PI; 
    this.random = new FuzzyRandom();
    this.fuzzy = random.development();
  }
}

void draw() {
  
  // Listen for client requests
  if(server != null) {
    server.listenForRequest();
  }
  
  if (RUN_VIZ) {
  
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
        
        // Open Space Polygons
        if(this.fuzzy.openShapes.get(plotShape) != null) {
          for(Polygon openShape : this.fuzzy.openShapes.get(plotShape)) {
            this.drawShape(openShape);
          }
        }
        
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
}

void keyPressed() {
  if (RUN_VIZ) {
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
}

void mouseMoved() {
  if (RUN_VIZ) {
    if (!cam.isPaused()) {
    
      // Update Camera Angle relative to cursor
      cam.updateCursorAngle();
      
      // reset mouse position
      cam.setMouseToCenter();
    }
  }
}
      
void mouseWheel(MouseEvent event) {
  if (RUN_VIZ) {
    if (!cam.isPaused()) {
    
      // Fly Up or Down using mouse wheel
      float e = event.getCount();
      cam.fly(e);
    }
  }
}

// This method runs when a client first connects to the Server
void serverEvent(Server s, Client c) {
  // println("Server: We have a new client: " + c.ip());
}
