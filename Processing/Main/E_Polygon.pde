import java.util.Random; 
import java.lang.Math.*;

class Polygon {
  private ArrayList<Point> vertex;
  private ArrayList<Line> edge;
  private float xMin, xMax, yMin, yMax;
  
  Polygon() {
    vertex = new ArrayList<Point>();
    edge = new ArrayList<Line>();
  }
  
  public ArrayList<Point> getCorners() {
    return vertex;
  }
  
  // Add a vertex to the polygon
  void addVertex(Point p) {
    vertex.add(p);
    
    // Generate Edges for Polygon once it has more than 3 vertices
    //
    if(vertex.size() > 2) {
      createEdges();
    }
    
    // Generate Min-Max Values
    //
    calcMinMax();
  }
  
  // Create Polygon Edge Objects, Composed of vertices
  void createEdges() {
    edge.clear();
    int n = vertex.size();
    for (int i=0; i<n-1; i++) {
      Line l = new Line( vertex.get(i), vertex.get(i+1) );
      edge.add(l);
    }
    Line l = new Line( vertex.get(n-1), vertex.get(0) );
    edge.add(l);
  }
  
  // Calculate minimum and maximum coordinate values
  private void calcMinMax() {
    xMin = Float.POSITIVE_INFINITY;
    xMax = Float.NEGATIVE_INFINITY;
    yMin = Float.POSITIVE_INFINITY;
    yMax = Float.NEGATIVE_INFINITY;
    
    for (Point p : vertex) {
      xMin = min(xMin, p.x);
      xMax = max(xMax, p.x);
      yMin = min(yMin, p.y);
      yMax = max(yMax, p.y);
    }
  }
  
  float xMin() {
    return this.xMin;
  }
  
  float xMax() {
    return this.xMax;
  }
  
  float yMin() {
    return this.yMin;
  }
  
  float yMax() {
    return this.yMax;
  }
  
  void clear() {
    vertex.clear();
    edge.clear();
  }
  
  void randomShape(int num_pts) {
    this.randomShape(0, 0, num_pts);
  }
  
  void randomShape(float x_center, float y_center, int num_pts) {
    this.randomShape(x_center, y_center, num_pts, 50, 100);
  }
  
  void randomShape(float x_center, float y_center, int num_pts, float min_radius, float max_radius) {
    
    ArrayList<Float> angle, radius;
    Random rand;
    float total;
    
    if (num_pts > 2) {
      
      // Initialize
      angle = new ArrayList<Float>();
      radius = new ArrayList<Float>();
      rand = new Random();
      total = 0.0;
      this.clear();
      
      for(int i=0; i<num_pts; i++) {
        
        // Generate random numbers relatively proportional to angle size
        float random_number = rand.nextFloat();
        total += random_number;
        angle.add(random_number);
        
        // Generate random radius values
        float variance = (max_radius - min_radius) * rand.nextFloat();
        float random_radius = min_radius + variance;
        radius.add(random_radius);
      }
      
      // Fit angle size to radian value
      for (int i=0; i<angle.size(); i++) {
        float mag = angle.get(i);
        angle.set(i, mag * 2 * (float) Math.PI / total); 
      }
      
      // generate each point around a circle
      float a = 0;
      for (int i=0; i<num_pts; i++) {
        a += angle.get(i);
        float r = radius.get(i);
        float x = r*cos(a);
        float y = r*sin(a);
        Point p = new Point(x, y);
        addVertex(p);
      }
      
      // shift polygon's coordinate system
      this.translate(x_center, y_center);
      
    } else {
      System.out.print("Not enough points to make polygon");
    }
  }
  
  void translate(float x, float y) {
    this.translate(x, y, 0);
  }
  
  void translate(float x, float y, float z) {
    for (Point p : vertex) {
      p.x += x;
      p.y += y;
      p.z += z;
    }
    xMin += x;
    xMax += x;
    yMin += y;
    yMax += y;
  }
  
  // Returns 'true' if Point p is inside of polygon
  //
  boolean containsPoint(Point p) {
    int num_nodes = vertex.size();
    
    // Make a horizonal line to cut through geometries
    //
    Point o = new Point(-1000000, p.y);
    Point f = new Point(+1000000, p.y);
    Line horizontal = new Line(o, f);
    
    // If polygon has 3 or more vertecies, count how many times a 
    // horizonal line drawn from negative infinity to point p
    // intersects with the polygon.  If off, the Point p is inside
    // the polygon - https://en.wikipedia.org/wiki/Point_in_polygon
    //
    if (num_nodes > 2) {
      int num_intersect = 0;
      for (Line l : edge) {
        Point intersect = horizontal.lineIntersect(l);
        if (intersect != null && intersect.x < p.x) {
          num_intersect++;
        }
      }
      if (num_intersect % 2 == 1) { // check for odd
        return true;
      } else {
        return false;
      }
      
    } else {
      return false;
    }
  }
}
