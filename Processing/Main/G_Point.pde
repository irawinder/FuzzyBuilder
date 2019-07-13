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
