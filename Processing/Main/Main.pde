// Demonstration of low-fidelity zone creation with Voronoi Algorithm
// Ira Winder, jiw@mit.edu

// Runs Once On Program Start
//
void setup() {
  
  // Set the canvas size to 800px by 400px
  size(800, 400, P3D);
  
  // Initialize ViewModel "Front End" Settings
  initRender();
  
  // Initialize Model "Backend"
  initModel();
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
}
