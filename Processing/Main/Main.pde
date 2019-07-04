// Runs Once On Program Start
//
void setup() {
  size(800, 400, P3D);
  initRender();
  initModel();
}

// Runs on Infinite Loop after setup() completes
//
void draw() {
  listen();
  updateModel();
  render();
}
