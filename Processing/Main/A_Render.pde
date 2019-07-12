// Front-End Methods that rely heavily on Processing Library Functions

void cam3D() {
  camera(200, 400, 200, 400, 200, 0, 0, 0, -1); 
  lights(); colorMode(HSB); pointLight(0, 0, 100, 50, 50, 50);
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
  editModel = true;
}

void footprintState() {
  // Footprint Layer State
  offState();
  showTiles = true;
  showSite = true;
  showFootprints = true;
  viewState = 3;
  editModel = true;
}

void buildingZoneState() {
  // Building + Zone Layer State
  offState();
  showTiles = true;
  showSite = true;
  showFootprints = true;
  showBases = true;
  viewState = 4;
  editModel = true;
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
  editModel = false;
}

void render() {
  hint(ENABLE_DEPTH_TEST);
  background(255);
  
  if (showTiles) {
    for (TileArray space : dev.spaceList()) {
      
      // Draw Sites
      //
      if (showSite && space.type.equals("site")) {
        color col = color(0, 50);
        for(Tile t : space.tileList()) renderTile(t, col, -1);
      }
      
      // Draw Zones
      //
      if (showZones && space.type.equals("zone")) {
        colorMode(HSB); color col = color(space.hue, 100, 225);
        for(Tile t : space.tileList()) renderTile(t, col, -1);
      }
      
      // Draw Footprints
      //
      if (showFootprints && space.type.equals("footprint")) {
        colorMode(HSB); color col;
        if(space.name.equals("Building")) {
          col = color(space.hue, 50, 150);
        } else if(space.name.equals("Setback")) {
          col = color(space.hue, 50, 225);
        } else {
          col = color(space.hue, 50, 225);
        }
        for (Tile t : space.tileList()) {
          renderTile(t, col, -1);
          if (space.name.substring(0,3).equals("Cou")) {
            renderVoxel(t, col, -0.5*t.scale_w);
          }
        }
      }
      
      // Draw Bases
      //
      if (showBases && space.type.equals("base")) {
        colorMode(HSB); color col = color(space.hue, 150, 200);
        for(Tile t : space.tileList()) {
          // Only draws ground plane if in 2D view mode
          if(t.location.z == 0 || cam3D) { 
            renderVoxel(t, col, 0);
          }
        }
      }
    }
  }
  
  // Draw Vector Polygon
  //
  fill(245, 225); noStroke(); 
  if (showPolygons) {
    stroke(0, 100); 
    strokeWeight(1);
  }
  pushMatrix(); translate(0, 0, -2);
  beginShape();
  for(Point p : site_boundary.vertex) vertex(p.x, p.y);
  endShape(CLOSE);
  popMatrix();
  
  hint(DISABLE_DEPTH_TEST);
  
  if (editModel) {
    
    // Draw Tagged Control Points Labels
    //
    for (ControlPoint p : control.points()) {
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
      fill(255, 150); stroke(200, 150); strokeWeight(1);
      int textWidth = 7*p.tag.length();
      rectMode(CORNER); rect(x + 10, y - 7, textWidth, 15, 5);
      fill(50); textAlign(CENTER, CENTER);
      text(p.tag, x + 10 + (int)textWidth/2, y - 1);
      if(cam3D) cam3D(); // sets back to 3D camera, if in 3D mode
    }
    
    // Draw Tagged Control Points
    //
    for (ControlPoint p : control.points()) {
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
    summary += "\n" + "Tile Dimensions:";
    summary += "\n" + tileW + " x " + tileW + " x " + tileH + " units";
    summary += "\n";
    summary += "\n" + dev;
    for(TileArray space : dev.spaceList()) summary += "\n" + space;
    text(summary, width - 175, 10);
  }
  
  // Mouse Cursor Info
  //
  if (editModel) {
    
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
  
  float scaler_uv = 0.85;
  float scaler_w = 0.6;
  
  fill(col); stroke(0, 50); strokeWeight(0.5);
  pushMatrix(); translate(t.location.x, t.location.y, t.location.z + z_offset);
  rotate(tile_rotation);
  box(scaler_uv*t.scale_uv, scaler_uv*t.scale_uv, scaler_w*t.scale_w);
  popMatrix();
}
