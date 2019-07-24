import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.HashMap;

// Demonstration of low-fidelity zone creation with Voronoi Algorithm
// Ira Winder, jiw@mit.edu

// To Do:
// - Implement Tower
// - Implement Variable Setback
// - Implement Use Types
// - Implement Variable Void size
// - Implement ControlPoint weight interface
// - Fix Bug : When two vertex points have same y coordinate
// [Check] Make ControlPoint types independent from Space types
// [Check] Implement Void/Courtyard
// [Check] Add ControlPoints for Site Polygon
// [Check] Layered and selectable ControlPoints
// [Check] Create Control() class to manage multiple ControlPoints()
// [Check] Create Development() class to manage multple TileArray() Spaces
// [Check] Create ArrayList Version for TileArray
// [Check] Implement native Java library for random (not Processing)

/* JR Site Settings:

  Site PNG Dimensions: 1438 x 820
  
  --Site Vertices
  --Zone Points
  Vertex 1; Point[1056.0, 509.0, 0.0]
  Vertex 2; Point[950.0, 509.0, 0.0]
  Vertex 3; Point[887.0, 504.0, 0.0]
  Vertex 4; Point[794.0, 488.0, 0.0]
  Vertex 5; Point[725.0, 468.0, 0.0]
  Plot 1; Point[350.0, 276.0, 0.0]
  Plot 2; Point[406.0, 297.0, 0.0]
  Plot 3; Point[454.0, 327.0, 0.0]
  Void 2; Point[760.0, 577.0, 0.0]
  Void 3; Point[565.0, 538.0, 0.0]
  Void 4; Point[650.0, 574.0, 0.0]
  Vertex 6; Point[589.0, 425.0, 0.0]
  Vertex 7; Point[505.0, 387.0, 0.0]
  Vertex 8; Point[518.0, 368.0, 0.0]
  Vertex 9; Point[434.0, 331.0, 0.0]
  Vertex 10; Point[405.0, 323.0, 0.0]
  Vertex 11; Point[303.0, 260.0, 0.0]
  Vertex 12; Point[307.0, 242.0, 0.0]
  Vertex 13; Point[407.0, 280.0, 0.0]
  Vertex 14; Point[471.0, 294.0, 0.0]
  Vertex 15; Point[567.0, 321.0, 0.0]
  Vertex 16; Point[673.0, 357.0, 0.0]
  Vertex 17; Point[746.0, 382.0, 0.0]
  Vertex 18; Point[888.0, 435.0, 0.0]
  Vertex 19; Point[970.0, 463.0, 0.0]
  Vertex 20; Point[1053.0, 480.0, 0.0]
  Plot 4; Point[596.0, 388.0, 0.0]
  Plot 5; Point[633.0, 401.0, 0.0]
  Plot 6; Point[788.0, 442.0, 0.0]
  Plot 7; Point[843.0, 465.0, 0.0]
  Plot 8; Point[703.0, 347.0, 0.0]
  Plot 9; Point[945.0, 484.0, 0.0]
  Plot 10; Point[1010.0, 498.0, 0.0]
  --Other Grid Attributes
  Grid Size: 11.0
  Grid Rotation: 0.34000006
  Grid Pan: Point[0.0, 0.0, 0.0]

*/

Builder builder;
Underlay map;

/**
 * Runs before everything else in PApplet
 */
public void settings(){
  
  map = new Underlay("20190724_takanawa.png", 0.5);
  
  // Init Application canvas size to match site_map
  size(map.getWidth(), map.getHeight(), P3D);
  
  // Set size of canvas to (X, Y) pixels
  //size(800, 400, P3D);
}

/**
 * Runs once upon initialization of PApplet class, but after settings() finishes
 */
public void setup(){
  
  frame.setTitle("Space Builder GUI");
  
  builder = new Builder();
}

/**
 * Runs every frame unless "noLoop()" is run
 */
boolean hasRun = false;
public void draw(){
  
  // listen for user inputs and mouse location
  builder.listen(mousePressed, mouseX, mouseY, pointAtMouse(), newPointAtMouse()); 

  // Update Model "Backend" with New State (if any)
  builder.updateModel();
  
  // Render the ViewModel "Front End" and GUI to canvas
  render();
  
  if(hasRun) {
    noLoop();
  } else {
    hasRun = true;
  }
}

/**
 * Runs once when key is pressed
 */
public void keyPressed() {
  builder.keyPressed(key, keyCode, CODED, LEFT, RIGHT, DOWN, UP);
  map.keyPressed(key);
  loop();
}

/**
 * Runs once when mouse is pressed down
 */
public void mousePressed() {
  builder.mousePressed(newPointAtMouse());
  loop();
}

/*
 * Runs once when mouse button is released
 */
public void mouseReleased() {
  builder.mouseReleased();
  loop();
}

/*
 * Runs when mouse has moved
 */
public void mouseMoved() {
  loop();
}

/**
 * Runs when mouse has moved while held down
 */
public void mouseDragged() {
  loop();
}

/**
 * 
 * @return new ControlPoint at mouse  (Requires processing.core)
 */
Point newPointAtMouse() {
  Point mousePoint = null;

  if(builder.cam3D) {
    cam3D();
    
    // generate a grid of points to search for nearest match
    // centered at (0,0)
    int breadth  = 1000;
    int interval = 5;

    float min_distance = Float.POSITIVE_INFINITY;
    for(int x=-breadth; x<breadth; x+=interval) {
      for(int y=-breadth; y<breadth; y+=interval) {
        float dist_x = mouseX - screenX(x, y);
        float dist_y = mouseY - screenY(x, y);
        float distance = (float) Math.sqrt( Math.pow(dist_x, 2) + Math.pow(dist_y, 2) );
        if (distance < 15) {
          if (distance < min_distance) {
            min_distance = distance;
            mousePoint = new Point(x,y);
          }
        }
      }
    } 
  } else {
    cam2D();
    mousePoint = new Point(mouseX, mouseY);
  }
  return mousePoint;
}

/**
 * Return Tagged Point Nearest to Mouse (Requires processing.core)
 * @return ControlPoint closest to mouse
 */
ControlPoint pointAtMouse() {
  ControlPoint closest = null;
  float min_distance = Float.POSITIVE_INFINITY;
  for (ControlPoint p : builder.control.points()) {
    float dist_x, dist_y;
    if(builder.cam3D) {
      cam3D();
      dist_x = mouseX - screenX(p.x, p.y);
      dist_y = mouseY - screenY(p.x, p.y);
    } else {
      cam2D();
      dist_x = mouseX - p.x;
      dist_y = mouseY - p.y;
    }
    float distance = (float) Math.sqrt( Math.pow(dist_x, 2) + Math.pow(dist_y, 2) );
    if (distance < 15) {
      if (distance < min_distance) {
        min_distance = distance;
        closest = p;
      }
    }
  }
  return closest;
}
