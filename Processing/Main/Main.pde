// Demonstration of low-fidelity zone creation with Voronoi Algorithm
// Ira Winder, jiw@mit.edu

// To Do:
// - Implement Variable Setback
// [Check] Implement Courtyard
// - Implement Tower
// - Implement Use Types
// [Check] Integrate Control Points into own class
// [Check] Flatten NestedTileArray?
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
