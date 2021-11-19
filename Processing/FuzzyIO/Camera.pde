import java.awt.Robot;

public class Camera {
  
  PVector eye;
  float angleXY, angleYZ;
  Robot robot;
  boolean mouseCentered;
  String renderer;
  boolean paused;
  
  private final float KEY_DISTANCE = 25;
  private final float WHEEL_DISTANCE = 10;
  private final float KEY_ANGLE = 0.05;
  private final float UP_X = 0;
  private final float UP_Y = 1;
  private final float UP_Z = 0;
  
  public Camera(String renderer) {
    
    this.renderer = renderer;
    this.paused = false;
    
    try { 
      robot = new Robot(); 
    } 
    catch (Exception e) {
    
    }
    
    surface.hideCursor();
    mouseCentered = false;
  
    this.init();
  }
  
  void init() {
    eye = new PVector(0, 0, 0);
    angleXY = PI;
    angleYZ = 0;
  }
  
  void overlay() {
    camera(); 
    perspective();
  }
  
  void pov() {
    
    // Center mouse on first frame
    if(!mouseCentered) {
      this.setMouseToCenter();
      mouseCentered = true;
    }
  
    PVector direction = new PVector();
    direction.x = eye.x + cos(angleYZ) * sin(angleXY);
    direction.y = eye.y + sin(angleYZ);
    direction.z = eye.z + cos(angleYZ) * cos(angleXY);
    camera(eye.x, eye.y, eye.z, direction.x, direction.y, direction.z, UP_X, UP_Y, UP_Z);
    lights();
  }
  
  // Run on mouseMoved
  void updateCursorAngle() {
    
    if(!this.paused) {
      
      float sensitivity = PI/4;
      
      int d_x = mouseX - width/2;
      int d_y = mouseY - height/2;
      
      angleXY += map(d_x, - width/2, width/2, sensitivity, -sensitivity);
      angleYZ += map(d_y, - height/2, height/2, -sensitivity, sensitivity);
      
      if(angleXY < 0 || angleXY > 2*PI) 
        angleXY = angleXY % (2*PI);
        
      float buffer = 0.1;
      if(angleYZ < -PI/2 + buffer || angleYZ > PI/2 - buffer)
        angleYZ = constrain(angleYZ, -PI/2 + buffer, PI/2 - buffer);
    }
  }
  
  // Run in draw if keyPressed == true
  void move() {
    
    if(key == CODED) {
      if(keyCode == UP) {
        this.flyUp(KEY_DISTANCE);
      } else if(keyCode == DOWN) {
        this.flyDown(KEY_DISTANCE);
      } else if(keyCode == RIGHT) {
        this.stepRight(KEY_DISTANCE);
      } else if(keyCode == LEFT) {
        this.stepLeft(KEY_DISTANCE);
      }
    }
    
    switch(key) {
      case '[':
        this.lookLeft(KEY_ANGLE);
        break;
      case ']':
        this.lookRight(KEY_ANGLE);
        break;
    }
  }
  
  // Update on mouseWheel
  void fly(float amount) {
    for(int i=0; i<abs(amount); i++) {
      if(amount < 0) {
        this.stepBackward(WHEEL_DISTANCE);
      } else {
        this.stepForward(WHEEL_DISTANCE);
      }
    }
  }
  
  void lookLeft(float angle) {
    angleXY += angle;
    if (angleXY > 2*PI) 
      angleXY -= 2*PI;
  }
  
  void lookRight(float angle) {
    angleXY -= angle;
    if (angleXY < 0) 
      angleXY += 2*PI;
  }
  
  void lookDown(float angle) {
    angleYZ = min(angleYZ + angle, PI/2);
  }
  
  void lookUp(float angle) {
    angleYZ = max(angleYZ - angle, -PI/2);
  }
  
  void stepLeft(float distance) {
    eye.x += distance * sin(angleXY + PI/2);
    eye.z += distance * cos(angleXY + PI/2);
  }
  
  void stepRight(float distance) {
    eye.x += distance * sin(angleXY - PI/2);
    eye.z += distance * cos(angleXY - PI/2);
  }
  
  void stepForward(float distance) {
    eye.x += distance * cos(angleYZ) * sin(angleXY);
    eye.y += distance * sin(angleYZ);
    eye.z += distance * cos(angleYZ) * cos(angleXY);
  }
  
  void stepBackward(float distance) {
    eye.x -= distance * cos(angleYZ) * sin(angleXY);
    eye.y -= distance * sin(angleYZ);
    eye.z -= distance * cos(angleYZ) * cos(angleXY);
  }
  
  void flyUp(float distance) {
    eye.y -= distance;
  }
  
  void flyDown(float distance) {
    eye.y += distance;
  }
  
  void setMouseToCenter() {
    if(!this.paused) {
      PVector windowLoc = getWindowLocation(renderer);
      robot.mouseMove((int) windowLoc.x + width/2, (int) windowLoc.y + height/2);
    }
  }
  
  PVector getWindowLocation(String renderer) {
    PVector l = new PVector();
    if (renderer == P2D || renderer == P3D) {
      com.jogamp.nativewindow.util.Point p = new com.jogamp.nativewindow.util.Point();
      ((com.jogamp.newt.opengl.GLWindow)surface.getNative()).getLocationOnScreen(p);
      l.x = p.getX();
      l.y = p.getY();
    } else if (renderer == JAVA2D) {
      java.awt.Frame f =  (java.awt.Frame) ((processing.awt.PSurfaceAWT.SmoothCanvas) surface.getNative()).getFrame();
      l.x = f.getX();
      l.y = f.getY();
    }
    return l;
  }
  
  void pause() {
    this.paused = !this.paused;
    if(this.paused) {
      surface.showCursor();
    } else {
      this.setMouseToCenter();
      surface.hideCursor();
    }
  }
  
  boolean isPaused() {
    return this.paused;
  }
}
