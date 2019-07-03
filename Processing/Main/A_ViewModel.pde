void render() {
  background(255);
  
  // Draw Vector Polygon
  fill(245); noStroke();
  beginShape();
  for(Point p : site_boundary.vertex) vertex(p.x, p.y);
  endShape(CLOSE);
  
  //// Draw Site Voxels
  //for(Map.Entry e : site_test.getTiles().entrySet()) {
  //  Tile t = (Tile)e.getValue();
  //  noFill(); fill(150); noStroke();
  //  ellipse(t.location.x, t.location.y, 0.75*t.scale, 0.75*t.scale);
  //}
  
  // Draw Zone Voxels
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
  for (TaggedPoint p : control_points) {
    fill(0); stroke(0); strokeWeight(1);
    line(p.x-5, p.y, p.x+5, p.y);
    line(p.x, p.y-5, p.x, p.y+5);
    fill(255); stroke(200);
    rectMode(CENTER); rect(p.x + 30, p.y, 40, 10, 5);
    fill(50); textSize(9); textAlign(CENTER, CENTER);
    text(p.tag, p.x + 30, p.y - 1);
  }
}
