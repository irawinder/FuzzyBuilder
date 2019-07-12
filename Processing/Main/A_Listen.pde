// Front-End Methods that rely heavily on Processing Library Functions

// Designed to run every frame to check mouse position
void listen() {
  
  if (cam3D) {
    cam3D();
  } else {
    cam2D();
  }
  
  if (editZones) {
    
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
    
    if(mousePressed && selected != null) {
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
      zone_change_detected = true;
    }
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
  for (ControlPoint p : control_points) {
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
      break;
    case 'a':
      addPoint = !addPoint;
      removePoint = false;
      break;
    case 'x':
      removePoint = !removePoint;
      addPoint = false;
      break;
    case 'c':
      control_points.clear();
      control_point_counter = 0;
      zone_change_detected = true;
      addPoint = true;
      removePoint = false;
      buildingZoneState();
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
    case 'p':
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
}  

// Triggered once when any mouse button is pressed
void mousePressed() {
  if(editZones) {
    
    if (addPoint) {
      Point atMouse = newPointAtMouse();
      addControlPoint(atMouse.x, atMouse.y);
    } else {
      selected = hovering;
      if (removePoint) {
        removeControlPoint(selected);
      }
    }
  }
}

// Triggered once when any mouse button is released
void mouseReleased() {
  if(editZones) selected = null;
}

void addControlPoint(float x, float y) {
  control_point_counter++;
  String name = "Plot " + control_point_counter;
  ControlPoint new_zone = new ControlPoint(x, y);
  new_zone.setTag(name);
  control_points.add(new_zone);
  zone_change_detected = true;
}

void removeControlPoint(ControlPoint point) {
  control_points.remove(point);
  zone_change_detected = true;
}
