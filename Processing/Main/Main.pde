// Runs Once On Program Start
//
void setup() {
  size(400, 400, P3D);
  init();
}

// Runs on Infinite Loop after setup() completes
//
void draw() {
  listen3D();
  update();
  render();
}
