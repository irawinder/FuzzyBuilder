// Front-End Methods that rely heavily on Processing Library Functions

// Point that is currently selected or hovering;
TaggedPoint selected;
TaggedPoint hovering;

// Designed to run every frame to check mouse position
void listen2D() {
  cam2D();
  hovering = pointHover2D();
  if(mousePressed && selected != null) {
    selected.x = mouseX;
    selected.y = mouseY;
    change_detected = true;
  }
}

// Designed to run every frame to check mouse position
void listen3D() {
  cam3D();
  hovering = pointHover3D();
  if(mousePressed && selected != null) {
    Point new_location = mouseToPoint();
    if (new_location != null) {
      selected.x = new_location.x;
      selected.y = new_location.y;
    }
    change_detected = true;
  }
}

Point mouseToPoint() {
  Point mousePoint = null;
  
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
  return mousePoint;
}

// Return Tagged Point Nearest to Mouse
//
TaggedPoint pointHover2D() {
  TaggedPoint closest = null;
  float min_distance = Float.POSITIVE_INFINITY;
  for (TaggedPoint p : control_points) {
    float dist_x = mouseX - p.x;
    float dist_y = mouseY - p.y;
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

// Return Tagged Point Nearest to Mouse
//
TaggedPoint pointHover3D() {
  TaggedPoint closest = null;
  float min_distance = Float.POSITIVE_INFINITY;
  for (TaggedPoint p : control_points) {
    float dist_x = mouseX - screenX(p.x, p.y);
    float dist_y = mouseY - screenY(p.x, p.y);
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
  init();
  render();
}

// Triggered once when any mouse button is pressed
void mousePressed() {
  selected = hovering;
}

// Triggered once when any mouse button is released
void mouseReleased() {
  selected = null;
}
