import java.util.Collections;

class Point {
  float x, y, z;
  
  Point(float x, float y) {
    this(x, y, 0.0);
  }
  
  Point(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  @Override
  public String toString() {
      return "Point[" + x + ", " + y + ", " + z + "]";
  }
}

class TaggedPoint extends Point {
  
  // String tag of point
  String tag;
  
  TaggedPoint(float x, float y) {
    super(x,y);
    tag = "";
  }
  
  // Set the Tag Value of the Point
  public void setTag(String tag) {
    this.tag = tag;
  }
  
  @Override
  public String toString() {
      return tag + "; Point[" + x + ", " + y + ", " + z + "]";
  }
  
}
