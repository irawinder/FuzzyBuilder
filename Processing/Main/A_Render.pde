// Front-End Methods that rely heavily on Processing Library Functions

void cam3D() {
  camera(200, 400, 200, 400, 200, 0, 0, 0, -1); 
  lights();
} 

void cam2D() {
  camera(); noLights(); perspective();
}

void render() {
  
  hint(ENABLE_DEPTH_TEST);
  
  background(255);
  
  // Draw Vector Polygon
  //
  fill(245); stroke(235); strokeWeight(1);
  pushMatrix(); translate(0, 0, -2);
  beginShape();
  for(Point p : site_boundary.vertex) vertex(p.x, p.y);
  endShape(CLOSE);
  popMatrix();
  
  // Draw Site Voxels
  //
  for(Map.Entry e : site_test.getTiles().entrySet()) {
    Tile t = (Tile)e.getValue();
    noFill(); fill(150); noStroke();
    pushMatrix(); translate(0, 0, -1);
    ellipse(t.location.x, t.location.y, 0.75*t.scale, 0.75*t.scale);
    popMatrix();
  }
  
  // Draw Zone Voxels
  //
  float hue = 0;
  for(Map.Entry e_z : site_test.getZones().entrySet()) {
    Zone z = (Zone)e_z.getValue();
    for(Map.Entry e_t : z.getTiles().entrySet()) {
      Tile t = (Tile)e_t.getValue();
      colorMode(HSB); color col = color(hue%255, 150, 200);
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
  
  hint(DISABLE_DEPTH_TEST);
  
  // Draw Tagged Control Points Labels
  //
  for (TaggedPoint p : control_points) {
    float x, y;
    if (cam3D) {
      cam3D();
      x = screenX(p.x, p.y);
      y = screenY(p.x, p.y);
    } else {
      x = p.x;
      y = p.y;
    }
    if(cam3D) cam2D(); // sets temporarily to 2D camera, if in 3D
    fill(255, 2000); stroke(200, 150); strokeWeight(1);
    rectMode(CENTER); rect(x + 40, y, 50, 15, 5);
    fill(50); textSize(12); textAlign(CENTER, CENTER);
    text(p.tag, x + 40, y - 1);
    if(cam3D) cam3D(); // sets back to 3D camera, if in 3D mode
  }
  
  // Draw Hovering Control Point
  //
  if (hovering != null) {
    color col = color(50);
    if (removePoint) {
      colorMode(RGB); 
      col = color(255, 0, 0);
    } else if (addPoint) {
      colorMode(RGB); 
      col = color(0, 255, 00);
    }
    stroke(col); strokeWeight(3);
    pushMatrix(); translate(0, 0, 1);
    line(hovering.x-5, hovering.y, hovering.x+5, hovering.y);
    line(hovering.x, hovering.y-5, hovering.x, hovering.y+5);
    popMatrix();
  }
  
  // Draw Info Text
  //
  fill(0);
  if(cam3D) cam2D(); // sets temporarily to 2D camera, if in 3D
  textAlign(LEFT, TOP); textSize(10);
  String info = "";
  info += "Click and drag zone nodes";
  info += "\n" + "Press 'a' to add zone node";
  info += "\n" + "Press 'x' to remove zone node";
  info += "\n" + "Press 'c' clear all zone nodes";
  info += "\n" + "Press 's' to generate random site";
  info += "\n" + "Press 'm' to toggle 2D/3D view";
  info += "\n" + "Press '-/+' to change tile size";
  text(info, 10, 10);
  text("Framerate: " + int(frameRate), 10, height - 20);
  
  // Mouse Cursor Info
  //
  fill(50);
  if (addPoint) {
    text("NEW", mouseX + 10, mouseY - 20);
  } else if (removePoint) {
    text("REMOVE", mouseX + 10, mouseY - 20);
  } else if (hovering != null) {
    text("MOVE", mouseX + 10, mouseY - 20);
  }
  
  if(cam3D) cam3D(); // sets back to 3D camera, if in 3D mode
}
