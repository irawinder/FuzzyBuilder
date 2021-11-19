/**
 * Polygon class: a series of connected, closed points
 * 
 * @author ira
 * 
 */
public class Polygon {
  private ArrayList<Point> vertex;
  private ArrayList<Line> edge;
  private float xMin, xMax, yMin, yMax;

  /**
   * Constructs an empty polygon
   */
  public Polygon() {
    vertex = new ArrayList<Point>();
    edge = new ArrayList<Line>();
  }
  
  /**
   * Constructs a closed polygon using a list of points. 
   * Points are connected in order with last point connected to first point
   *
   * @param vertices list of points that comprise polygon vertices
   */
  public Polygon(ArrayList<Point> vertices) {
    this();
    for(Point vertex : vertices) {
      this.addVertex(vertex);
    }
  }

  /**
   * Retrieve the list of polygon corners
   * 
   * @return corners of polygon
   */
  public ArrayList<Point> getCorners() {
    return vertex;
  }

  /**
   * Add a vertex to the polygon
   * 
   * @param p Point location of new vertex
   */
  public void addVertex(Point p) {
    vertex.add(p);

    // Generate Edges for Polygon once it has more than 3 vertices
    //
    if (vertex.size() > 2) {
      createEdges();
    }

    // Generate Min-Max Values
    //
    calcMinMax();
  }

  /**
   * Create Polygon Edge Objects, Composed of vertices
   */
  private void createEdges() {
    edge.clear();
    int n = vertex.size();
    for (int i = 0; i < n - 1; i++) {
      Line l = new Line(vertex.get(i), vertex.get(i + 1));
      edge.add(l);
    }
    Line l = new Line(vertex.get(n - 1), vertex.get(0));
    edge.add(l);
  }

  /**
   * Calculate minimum and maximum coordinate values
   */
  private void calcMinMax() {
    xMin = Float.POSITIVE_INFINITY;
    xMax = Float.NEGATIVE_INFINITY;
    yMin = Float.POSITIVE_INFINITY;
    yMax = Float.NEGATIVE_INFINITY;

    for (Point p : vertex) {
      xMin = Math.min(xMin, p.x);
      xMax = Math.max(xMax, p.x);
      yMin = Math.min(yMin, p.y);
      yMax = Math.max(yMax, p.y);
    }
  }

  /**
   * @return lowest-bound x coordinate of polygon
   */
  public float xMin() {
    return this.xMin;
  }

  /**
   * @return highest-bound x coordinate of polygon
   */
  public float xMax() {
    return this.xMax;
  }

  /**
   * @return lowest-bound y coordinate of polygon
   */
  public float yMin() {
    return this.yMin;
  }

  /**
   * @return highest-bound y coordinate of polygon
   */
  public float yMax() {
    return this.yMax;
  }

  /**
   * clear entire polygon of vertices and edges
   */
  public void clear() {
    vertex.clear();
    edge.clear();
  }

  /**
   * 
   * @param x x-coordinate
   * @param y y-coordinate
   */
  public void translate(float x, float y) {
    this.translate(x, y, 0);
  }

  /**
   * 
   * @param x x-coordinate
   * @param y y-coordinate
   * @param z z-coordinate
   */
  public void translate(float x, float y, float z) {
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

  /**
   * Check if a point is within the polygon
   * 
   * @param p Point we wish to know whether inside polygon or not
   * @return Returns 'true' if Point p is inside of polygon
   */
  public boolean containsPoint(Point p) {
    int num_nodes = vertex.size();

    // Make a horizontal line to cut through geometries
    //
    Point o = new Point(-1000000, p.y);
    Point f = new Point(+1000000, p.y);
    Line horizontal = new Line(o, f);

    // If polygon has 3 or more vertices, count how many times a
    // horizontal line drawn from negative infinity to point p
    // intersects with the polygon. If off, the Point p is inside
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
