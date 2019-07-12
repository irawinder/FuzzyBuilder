class Point {
  public float x, y, z;
  
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

class ControlPoint extends Point {
  
  // String tag of point
  String tag;
  
  // The type of control point
  String type;
  
  // Numerical weight of point
  Float weight;
  
  ControlPoint(float x, float y) {
    super(x,y);
    tag = "";
    weight = 1.0;
  }
  
  // Set the Tag Value of the Point
  public void setTag(String tag) {
    this.tag = tag;
  }
  
  // Get the Tag Value of the Point
  public String getTag() {
    return tag;
  }
  
  // Set the Type Value of the Point
  public void setType(String type) {
    this.type = type;
  }
  
  // Get the Type Value of the Point
  public String getType() {
    return type;
  }
  
  // Set the Weight of the Point
  public void setWeight(float weight) {
    this.weight = weight;
  }
  
  // Get the Weight of the Point
  public float getWeight() {
    return weight;
  }
  
  @Override
  public String toString() {
      return tag + "; Point[" + x + ", " + y + ", " + z + "]";
  }
  
}
