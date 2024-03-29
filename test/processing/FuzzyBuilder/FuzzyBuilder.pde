import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.HashMap;
import java.util.UUID;

// Fuzzy Builder v1.0-alpha.1

// Demonstration of low-fidelity zone creation with Voronoi Algorithm
// Ira Winder, jiw@mit.edu

// NOTE! AS OF 2019.10.01 THIS PROCESSING CODE IS DEPRECATED, 
// AS IT IS BEING MIGRATED FROM PROCESSING TO A JAVAFX GUI PLATFORM

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

Builder builder;
Underlay map;

/**
 * Runs before everything else in PApplet
 */
//public void settings(){
  
  
//}

/**
 * Runs once upon initialization of PApplet class, but after settings() finishes
 */
public void setup(){
  
  map = new Underlay("siteEW.png", 0.5);
  //map = new Underlay("euston_site.png", 0.5);
  
  // Init Application canvas size to match site_map
  //size(map.getWidth(), map.getHeight(), P3D);
  
  // Set size of canvas to (X, Y) pixels
  size(900, 500, P3D);
  
  surface.setTitle("Fuzzy Builder v1.0-alpha.1");
  
  builder = new Builder("random");
}

/**
 * Runs every frame unless "noLoop()" is run
 */
public void draw(){
  
  // listen for user inputs and mouse location
  builder.listen(mousePressed, mouseX, mouseY, pointAtMouse(), newPointAtMouse()); 

  // Update Model "Backend" with New State (if any)
  builder.updateModel();
  
  // Render the ViewModel "Front End" and GUI to canvas
  render();
  
  noLoop();
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
            mousePoint = new Point(x+random(0.01),y+random(0.01)); // Random jitter prevents bug where lines are perfectly horizontal or vertical
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
