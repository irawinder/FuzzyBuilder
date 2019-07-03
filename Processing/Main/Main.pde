// Runs Once On Program Start
//
void setup() {
  size(800, 400, P3D);
  cam3D = true;
  init();
}

// Runs on Infinite Loop after setup() completes
//
void draw() {
  
  if(cam3D) {
    cam3D();
  } else {
    cam2D();
  }
  
  listen();
  update();
  render();
}
