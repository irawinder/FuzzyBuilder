import java.util.Collections;

class Line {
  Point o, f;
  
  Line(Point o, Point f) {
    this.o = o;
    this.f = f;
  }
  
  // Checks to see if two line segments, definied by endpoints, intersect
  // if so, returns intersection point; otherwise returns null
  //
  Point lineIntersect(Line b) {
    Point intersect = null;
    
    // Line Slopes:
    //
    float m_A = (f.y - o.y) / (f.x - o.x);
    float m_B = (b.f.y - b.o.y) / (b.f.x - b.o.x);
    
    // Only continue if lines are NOT parallel
    //
    if (m_A != m_B) {
      
      // Line Y-Intercepts (at x = 0)
      //
      float i_A = o.y - m_A * o.x;
      float i_B = b.o.y - m_B * b.o.x;
      
      // Calculate line intersections, as if lines are infinite
      //
      float intersect_y = ((m_A * i_B) - (m_B * i_A)) / (m_A - m_B);
      float intersect_x;
      if (m_A == 0) {
        intersect_x = (intersect_y - i_B) / m_B;
      } else {
        intersect_x = (intersect_y - i_A) / m_A;
      }
      
      // Check if exists on line segments; if so set point intersect
      //
      boolean onLineA = inRange(intersect_x, o.x, f.x) && inRange(intersect_y, o.y, f.y);
      boolean onLineB = inRange(intersect_x, b.o.x, b.f.x) && inRange(intersect_y, b.o.y, b.f.y);
      if (onLineA && onLineB) intersect = new Point(intersect_x, intersect_y);
    }
    
    return intersect;
  }
  
  // Returns true if num is equal to or 
  // lies in an interval between r1 and r2
  //
  boolean inRange(float num, float r1, float r2) {
    
    // Create a list of our numbers to sort
    //
    ArrayList<Float> val = new ArrayList<Float>();
    val.add(num); val.add(r1); val.add(r2);
    
    // Sort the list from smallest to largest
    //
    Collections.sort(val);
    
    // Returns true if the number is the second value in the list
    // or if the number is equal to either range value.
    // Floating points aren't precise enough, so we allow for a
    // number that is "close enough"
    //
    float tolerance = 0.001;
    boolean isRange1 = abs(num - r1) <= tolerance;
    boolean isRange2 = abs(num - r2) <= tolerance;
    if(num == val.get(1) || isRange1 || isRange2) {
      return true;
    } else {
      return false;
    }
  }
  
  @Override
  public String toString() {
      return "Line[" + o.x + ", " + o.y + ", " + o.z + "][" + f.x + ", " + f.y + ", " + f.z + "]";
  }
}
