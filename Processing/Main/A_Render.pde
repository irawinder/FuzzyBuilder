// Front-End Methods that rely heavily on Processing Library Functions

void cam3D() {
  camera(200, 400, 200, 400, 200, 0, 0, 0, -1); 
  lights(); colorMode(HSB); pointLight(0, 0, 100, 150, 150, 150);
} 

void cam2D() {
  camera(); noLights(); perspective();
}

void siteState() {
  // Site Layer State
  offState();
  showTiles = true;
  showPolygons = true;
  showSite = true;
  viewState = 1;
}

void zoneState() {
  // Zone Layer State
  offState();
  showTiles = true;
  showSite = true;
  showZones = true;
  viewState = 2;
  editZones = true;
}

void footprintState() {
  // Footprint Layer State
  offState();
  showTiles = true;
  showFootprints = true;
  viewState = 3;
  editZones = true;
}

void buildingZoneState() {
  // Building + Zone Layer State
  offState();
  showTiles = true;
  showSite = true;
  showFootprints = true;
  showBases = true;
  viewState = 4;
  editZones = true;
}

void buildingState() {
  // Building Layer State
  offState();
  showTiles = true;
  showPolygons = true;
  showBases = true;
  showTowers = true;
  viewState = 5;
}

void floorState() {
  // Floor Layer State
  offState();
  showTiles = true;
  showPolygons = true;
  showFloors = true;
  viewState = 6;
}

void roomState() {
  // Room Layer State
  offState();
  showTiles = true;
  showPolygons = true;
  showRooms = true;
  viewState = 7;
}

void offState() {
  showTiles = false;
  showPolygons = false;
  showSite = false;
  showZones = false;
  showFootprints = false;
  showBases = false;
  showTowers = false;
  showFloors = false;
  showRooms = false;
  editZones = false;
}

void render() {
  
  hint(ENABLE_DEPTH_TEST);
  
  background(255);
  
  if (showTiles) {
    
    // Draw Site Voxels
    //
    if (showSite) {
      for(Map.Entry e : site_test.getTiles().entrySet()) {
        Tile t = (Tile)e.getValue();
        color col = color(0, 50);
        renderTile(t, col, -1);
      }
    }
    
    float hue = 0;
    
    // Cycle Through Zones
    for(Map.Entry e_z : site_test.getChildren().entrySet()) {
      NestedTileArray zone = (NestedTileArray)e_z.getValue();
      
      // Draw Zone Voxels
      //
      if (showZones) {
        for(Map.Entry e : zone.getTiles().entrySet()) {
          Tile t = (Tile)e.getValue();
          colorMode(HSB); color col = color(hue%255, 150, 200, 255);
          renderTile(t, col, 0);
        }
      }
      
      // Cycle Through Footprints
      for(Map.Entry e_f : zone.getChildren().entrySet()) {
        NestedTileArray footprint = (NestedTileArray)e_f.getValue();
        
        // Draw Footprint Voxels
        //
        if (showFootprints) {
          for(Map.Entry e : footprint.getTiles().entrySet()) {
            Tile t = (Tile)e.getValue();
            colorMode(HSB); color col = color(hue%255, 25, 200, 255);
            if(footprint.name.equals("Building")) col = color(hue%255, 150, 200, 255);
            renderTile(t, col, 0.5);
          }
        }
        
        // Cycle Through Bases
        for(Map.Entry e_b : footprint.getChildren().entrySet()) {
          NestedTileArray base = (NestedTileArray)e_b.getValue();
          
          // Draw Base Voxels
          //
          if (showBases) {
            for(Map.Entry e : base.getTiles().entrySet()) {
              Tile t = (Tile)e.getValue();
              colorMode(HSB); color col = color(hue%255, 150, 200, 150);
              renderVoxel(t, col, 0);
            }
          }
        }
      }
      hue += 40;
    }
  
  }
  
  // Draw Vector Polygon
  //
  fill(225, 200); noStroke(); 
  if (showPolygons) {
    stroke(0, 50); 
    strokeWeight(1);
  }
  pushMatrix(); translate(0, 0, -2);
  beginShape();
  for(Point p : site_boundary.vertex) vertex(p.x, p.y);
  endShape(CLOSE);
  popMatrix();
  
  hint(DISABLE_DEPTH_TEST);
  
  if (editZones) {
  
    // Draw Tagged Control Points Labels
    //
    for (TaggedPoint p : control_points) {
      int x, y;
      if (cam3D) {
        cam3D();
        x = (int)screenX(p.x, p.y);
        y = (int)screenY(p.x, p.y);
      } else {
        x = (int)p.x;
        y = (int)p.y;
      }
      if(cam3D) cam2D(); // sets temporarily to 2D camera, if in 3D
      fill(255, 2000); stroke(200, 150); strokeWeight(1);
      rectMode(CENTER); rect(x + 40, y, 50, 15, 5);
      fill(50); textAlign(CENTER, CENTER);
      text(p.tag, x + 40, y - 1);
      if(cam3D) cam3D(); // sets back to 3D camera, if in 3D mode
    }
    
    // Draw Tagged Control Points
    //
    for (TaggedPoint p : control_points) {
      fill(150, 100); stroke(0, 150); strokeWeight(1);
      pushMatrix(); translate(0, 0, 1);
      ellipse(p.x, p.y, 10, 10);
      line(p.x-5, p.y, p.x+5, p.y);
      line(p.x, p.y-5, p.x, p.y+5);
      popMatrix();
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
      stroke(col); strokeWeight(2);
      pushMatrix(); translate(0, 0, 1);
      line(hovering.x-5, hovering.y, hovering.x+5, hovering.y);
      line(hovering.x, hovering.y-5, hovering.x, hovering.y+5);
      popMatrix();
    }
  }
  
  if(cam3D) cam2D(); // sets temporarily to 2D camera, if in 3D
  
  // Draw Info Text
  //
  fill(0); textAlign(LEFT, TOP);
  String info = "";
  info += "Click and drag zone nodes";
  info += "\n" + "Press 'a' to add zone node";
  info += "\n" + "Press 'x' to remove zone node";
  info += "\n" + "Press 'c' clear all zone nodes";
  info += "\n" + "Press '-' or '+' to resize tiles";
  info += "\n" + "Press '[', '{', ']', or '}' to rotate tiles";
  info += "\n" + "Press 'r' to generate random site";
  info += "\n" + "Press 'm' to toggle 2D/3D view";
  info += "\n" + "Press 'v' to toggle View Model";
  info += "\n" + "Press 't' to hide/show Tiles";
  info += "\n" + "Press 'p' to hide/show Polygons";
  info += "\n";
  info += "\n" + "Press '1' to show Site";
  if(viewState == 1) info += " <--";
  info += "\n" + "Press '2' to show Zones";
  if(viewState == 2) info += " <--";
  info += "\n" + "Press '3' to show Footprints";
  if(viewState == 3) info += " <--";
  info += "\n" + "Press '4' to show Zones + Buildings";
  if(viewState == 4) info += " <--";
  info += "\n" + "Press '5' to show Buildings Only";
  if(viewState == 5) info += " <--";
  //info += "\n" + "Press '6' to show Floors";
  //if(viewState == 6) info += " <--";
  //info += "\n" + "Press '7' to show Rooms";
  //if(viewState == 7) info += " <--";
  text(info, 10, 10);
  text("Framerate: " + int(frameRate), 10, height - 20);
  
  // Draw Summary
  //
  if (showTiles) {
    fill(100); textAlign(LEFT, TOP);
    String summary = "";
    summary += "View Model: " + viewModel;
    summary += "\n" + site_test;
    for(Map.Entry e_z : site_test.getChildren().entrySet()) {
      NestedTileArray zone = (NestedTileArray)e_z.getValue();
      summary += "\n-" + zone;
      for(Map.Entry e_f : zone.getChildren().entrySet()) {
        NestedTileArray footprint = (NestedTileArray)e_f.getValue();
        summary += "\n--" + footprint;
        for(Map.Entry e_b : footprint.getChildren().entrySet()) {
          NestedTileArray base = (NestedTileArray)e_b.getValue();
          summary += "\n---" + base;
        }
      }
    }
    text(summary, width - 175, 10);
  }
  
  // Mouse Cursor Info
  //
  if (editZones) {
    
    fill(50); textAlign(LEFT, TOP);
    if (addPoint) {
      text("NEW", mouseX + 10, mouseY - 20);
    } else if (removePoint) {
      text("REMOVE", mouseX + 10, mouseY - 20);
    } else if (hovering != null) {
      text("MOVE", mouseX + 10, mouseY - 20);
    }
    
  }
  
  if(cam3D) cam3D(); // sets back to 3D camera, if in 3D mode
}

void renderTile(Tile t, color col, float z_offset) {
  
  float scaler = 0.85;
  
  fill(col); noStroke();
  pushMatrix(); translate(t.location.x, t.location.y, t.location.z + z_offset);
  
  if (viewModel.equals("DOT")) {
    ellipse(0, 0, scaler*t.scale_uv, scaler*t.scale_uv);
  } else if (viewModel.equals("VOXEL")) {
    rotate(tile_rotation);
    rectMode(CENTER); rect(0, 0, scaler*t.scale_uv, scaler*t.scale_uv);
  } else {
    ellipse(0, 0, scaler*t.scale_uv, scaler*t.scale_uv);
  }
  
  popMatrix();
}

void renderVoxel(Tile t, color col, float z_offset) {
  
  float scaler = 0.85;
  
  fill(col); noStroke();
  pushMatrix(); translate(t.location.x, t.location.y, t.location.z + z_offset + 0.5*t.scale_w);
  rotate(tile_rotation);
  box(scaler*t.scale_uv, scaler*t.scale_uv, scaler*t.scale_w);
  popMatrix();
}
