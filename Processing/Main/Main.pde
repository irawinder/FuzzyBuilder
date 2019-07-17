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

// Runs Once On Program Start
//
void setup() {
  
  // Set the canvas size to 800px by 400px
  size(800, 400, P3D);
  
  // Initialize Model "Backend"
  initModel();
  
  // Initialize ViewModel "Front End" Settings
  initRender();
}

// Runs on Infinite Loop after setup() completes
//
void draw() {
  
  // listen for user inputs and mouse location
  listen(); 
  
  // Update Model "Backend" with New State (if any)
  updateModel();
  
  // Render the ViewModel "Front End" and GUI to canvas
  render();
  
  noLoop();
}
