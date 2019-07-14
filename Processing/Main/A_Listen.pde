// Front-End Methods that rely heavily on Processing Library Functions

// Designed to run every frame to check mouse position
void listen() {
  
  if (cam3D) {
    cam3D();
  } else {
    cam2D();
  }
  
  if (addPoint) {
    Point atMouse = newPointAtMouse();
    if (atMouse != null) {
      ControlPoint ghost = new ControlPoint(atMouse.x, atMouse.y);
      ghost.setTag("ghost");
      hovering = ghost;
    } else {
      hovering = null;
    }
  } else {
    hovering = pointAtMouse();
  }
  
  if(mousePressed && selected != null && selected.active()) {
    if(cam3D) {
      Point new_location = newPointAtMouse();
      if (new_location != null) {
        selected.x = new_location.x;
        selected.y = new_location.y;
      }
    } else {
      selected.x = mouseX;
      selected.y = mouseY;
    }
    detectChange(selected.type);
  }
  
}

Point newPointAtMouse() {
  Point mousePoint = null;
  
  if(cam3D) {
    // generate a grid of points to search for nearest match
    // centered at (0,0)
    int breadth  = 1000;
    int interval = 5;
    
    float min_distance = Float.POSITIVE_INFINITY;
    for(int x=-breadth; x<breadth; x+=interval) {
      for(int y=-breadth; y<breadth; y+=interval) {
        float dist_x = mouseX - screenX(x, y);
        float dist_y = mouseY - screenY(x, y);
        float distance = sqrt( sq(dist_x) + sq(dist_y) );
        if (distance < 15) {
          if (distance < min_distance) {
            min_distance = distance;
            mousePoint = new Point(x,y);
          }
        }
      }
    } 
  } else {
    mousePoint = new Point(mouseX, mouseY);
  }
  return mousePoint;
}

// Return Tagged Point Nearest to Mouse
//
ControlPoint pointAtMouse() {
  ControlPoint closest = null;
  float min_distance = Float.POSITIVE_INFINITY;
  for (ControlPoint p : control.points()) {
    float dist_x, dist_y;
    if(cam3D) {
      dist_x = mouseX - screenX(p.x, p.y);
      dist_y = mouseY - screenY(p.x, p.y);
    } else {
      dist_x = mouseX - p.x;
      dist_y = mouseY - p.y;
    }
    float distance = sqrt( sq(dist_x) + sq(dist_y) );
    if (distance < 15) {
      if (distance < min_distance) {
        min_distance = distance;
        closest = p;
      }
    }
  }
  return closest;
}

// Triggered when any key is pressed
void keyPressed() {
  
  switch(key) {
    case 'r':
      initModel();
      initRender();
      addPoint = false;
      removePoint = false;
      break;
    case 'a':
      addPoint = !addPoint;
      removePoint = false;
      if (addPoint) activateEditor();
      break;
    case 'x':
      removePoint = !removePoint;
      addPoint = false;
      break;
    case 'p':
      togglePlotEditing();
      break;
    case 'o':
      toggleVoidEditing();
      break;
    case 'i':
      toggleVertexEditing();
      break;
    case 'c':
      control.clearPoints();
      site_change_detected = true;
      removePoint = false;
      buildingZoneState();
      addPoint = true;
      toggleVertexEditing();
      break;
    case 'm':
      cam3D = !cam3D;
      break;
    case 'v':
      if (viewModel.equals("DOT")) {
        viewModel = "VOXEL";
      } else {
        viewModel = "DOT";
      }
      break;
    case '-':
      if (tileW > 1) tileW--;
      site_change_detected = true;;
      break;
    case '+':
      if (tileW < 50) tileW++;
      site_change_detected = true;
      break;
    case '[':
      tile_rotation -= 0.01;
      site_change_detected = true;;
      break;
    case ']':
      tile_rotation += 0.01;
      site_change_detected = true;
      break;
    case '}':
      tile_rotation += 0.1;
      site_change_detected = true;;
      break;
    case '{':
      tile_rotation -= 0.1;
      site_change_detected = true;
      break;
    case 't':
      showTiles = !showTiles;
      break;
    case 'l':
      showPolygons = !showPolygons;
      break;
    case '1':
      siteState();
      break;
    case '2':
      zoneState();
      break;
    case '3':
      footprintState();
      break;
    case '4':
      buildingZoneState();
      break;
    case '5':
      buildingState();
      break;
    //case '6':
    //  floorState();
    //  break;
    //case '7':
    //  roomState();
    //  break;
  }
  
  if (key == CODED) { 
    if (keyCode == LEFT) {
      tile_translation.x--;
      site_change_detected = true;
    }  
    if (keyCode == RIGHT) {
      tile_translation.x++;
      site_change_detected = true;
    }  
    if (keyCode == DOWN) {
      tile_translation.y++;
      site_change_detected = true;
    }  
    if (keyCode == UP) {
      tile_translation.y--;
      site_change_detected = true;
    }
  }
  
  loop();
}  

// Triggered once when any mouse button is pressed
void mousePressed() {
  if (addPoint) {
    Point atMouse = newPointAtMouse();
    addControlPoint(atMouse.x, atMouse.y);
  } else {
    selected = hovering;
    if (removePoint) {
      removeControlPoint(selected);
    }
  }
  
  loop();
}

// Triggered once when any mouse button is released
void mouseReleased() {
  selected = null;
  
  loop();
}

void mouseMoved() {
  loop();
}

void mouseDragged() {
  loop();
}

void addControlPoint(float x, float y) {
  if (new_control_type.equals("Vertex")) {
    control.addPoint(new_control_type + " " + vert_counter, new_control_type, x, y);
    vert_counter++;
  } else if (new_control_type.equals("Plot")) {
    control.addPoint(new_control_type + " " + plot_counter, new_control_type, x, y);
    plot_counter++;
  } else if (new_control_type.equals("Void")) {
    control.addPoint(new_control_type + " " + void_counter, new_control_type, x, y);
    void_counter++;
  }
  detectChange(new_control_type);
}

void removeControlPoint(ControlPoint point) {
  control.removePoint(point);
  detectChange(point.type);
}

// detect change based upon a type string
void detectChange(String type) {
  if (type.equals("Vertex")) {
    site_change_detected = true;
  } else if (type.equals("Plot")) {
    zone_change_detected = true;
  } else if (type.equals("Void")) {
    foot_change_detected = true;
  }
}

void toggleVertexEditing() {
  editVertices = !editVertices;
  editPlots = false;
  editVoids = false;
  new_control_type = "Vertex";
  control.off();
  if (editVertices) {
    control.on(new_control_type);
    // auto add points if list is empty
    if (control.points(new_control_type).size() == 0) addPoint = true;
    showPolygons = true;
  }
}

void togglePlotEditing() {
  editVertices = false;
  editPlots = !editPlots;
  editVoids = false;
  new_control_type = "Plot";
  control.off();
  if (editPlots) control.on(new_control_type);
  // auto add points if list is empty
  if (control.points(new_control_type).size() == 0) addPoint = true;
}

void toggleVoidEditing() {
  editVertices = false;
  editPlots = false;
  editVoids = !editVoids;
  new_control_type = "Void";
  control.off();
  if (editVoids) {
    control.on(new_control_type);
    // auto add points if list is empty
    if (control.points(new_control_type).size() == 0) addPoint = true;
  }
}

// Activate Editor
void activateEditor() {
  editVertices = false;
  editPlots = false;
  editVoids = false;
  if (new_control_type.equals("Vertex")) {
    editVertices = true;
  } else if (new_control_type.equals("Plot")) {
    editPlots = true;
  } else if (new_control_type.equals("Void")) {
    editVoids = true;
  } 
  control.off();
  control.on(new_control_type);
}
