// Front-End Methods that rely heavily on Processing Library Functions

void cam3D() {
  camera(200, 400, 300, 400, 200, 0, 0, 0, -1); 
  lights();
  hint(ENABLE_DEPTH_TEST);
} 

void cam2D() {
  camera(); noLights(); perspective(); 
  hint(DISABLE_DEPTH_TEST);
}

void render() {
  cam3D();
  background(255);
  
  //// Draw Vector Polygon
  ////
  //fill(245); noStroke();
  //beginShape();
  //for(Point p : site_boundary.vertex) vertex(p.x, p.y);
  //endShape(CLOSE);
  
  //// Draw Site Voxels
  ////
  //for(Map.Entry e : site_test.getTiles().entrySet()) {
  //  Tile t = (Tile)e.getValue();
  //  noFill(); fill(150); noStroke();
  //  ellipse(t.location.x, t.location.y, 0.75*t.scale, 0.75*t.scale);
  //}
  
  // Draw Zone Voxels
  //
  float hue = 0;
  for(Map.Entry e_z : site_test.getZones().entrySet()) {
    Zone z = (Zone)e_z.getValue();
    for(Map.Entry e_t : z.getTiles().entrySet()) {
      Tile t = (Tile)e_t.getValue();
      colorMode(HSB); color col = color(hue, 150, 200);
      noFill(); fill(col); noStroke();
      ellipse(t.location.x, t.location.y, 0.75*t.scale, 0.75*t.scale);
    }
    hue += 40;
  }
  
  // Draw Tagged Control Points
  //
  for (TaggedPoint p : control_points) {
    fill(0); stroke(0); strokeWeight(1);
    line(p.x-5, p.y, p.x+5, p.y);
    line(p.x, p.y-5, p.x, p.y+5);
  }
  
  // Draw Hovering Control Point
  //
  if (hovering != null) {
    stroke(50); strokeWeight(3);
    line(hovering.x-5, hovering.y, hovering.x+5, hovering.y);
    line(hovering.x, hovering.y-5, hovering.x, hovering.y+5);
  }
 
  // Draw Tagged Control Points Labels
  //
  for (TaggedPoint p : control_points) {
    cam3D();
    float x = screenX(p.x, p.y);
    float y = screenY(p.x, p.y);
    cam2D();
    fill(255, 2000); stroke(200, 150); strokeWeight(1);
    rectMode(CENTER); rect(x + 40, y, 50, 15, 5);
    fill(50); textSize(12); textAlign(CENTER, CENTER);
    text(p.tag, x + 40, y - 1);
  }
  textAlign(LEFT, TOP);
  text(frameRate, 20, 20);
}
