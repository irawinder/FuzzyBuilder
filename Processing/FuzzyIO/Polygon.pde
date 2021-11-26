/**
 * Polygon class: a series of connected, closed points
 * 
 * @author ira
 * 
 */
public class Polygon {
  private ArrayList<Point> vertices;
  private ArrayList<Line> edges;
  private float xMin, xMax, yMin, yMax;

  /**
   * Constructs an empty polygon
   */
  public Polygon() {
    vertices = new ArrayList<Point>();
    edges = new ArrayList<Line>();
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
    return vertices;
  }

  /**
   * Add a vertex to the polygon
   * 
   * @param p Point location of new vertex
   */
  public void addVertex(Point p) {
    vertices.add(p);

    // Generate Edges for Polygon once it has more than 3 vertices
    //
    if (vertices.size() > 2) {
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
    edges.clear();
    int n = vertices.size();
    for (int i = 0; i < n - 1; i++) {
      Line l = new Line(vertices.get(i), vertices.get(i + 1));
      edges.add(l);
    }
    Line l = new Line(vertices.get(n - 1), vertices.get(0));
    edges.add(l);
  }

  /**
   * Calculate minimum and maximum coordinate values
   */
  private void calcMinMax() {
    xMin = Float.POSITIVE_INFINITY;
    xMax = Float.NEGATIVE_INFINITY;
    yMin = Float.POSITIVE_INFINITY;
    yMax = Float.NEGATIVE_INFINITY;

    for (Point p : vertices) {
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
    vertices.clear();
    edges.clear();
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
    for (Point p : vertices) {
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
    int num_nodes = vertices.size();

    // Make a horizontal line to cut through geometries
    //
    Point o = new Point(this.xMin(), p.y);
    Point f = new Point(this.xMax(), p.y);
    Line horizontal = new Line(o, f);

    // If polygon has 3 or more vertices, count how many times a
    // horizontal line drawn from negative infinity to point p
    // intersects with the polygon. If odd, the Point p is inside
    // the polygon - https://en.wikipedia.org/wiki/Point_in_polygon
    //
    if (num_nodes > 2) {
      int num_intersect = 0;
      for (Line l : edges) {
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
  
  /**
   * Check if a Polygon is inside of this polygon
   *
   * @param polygon shape that we wish to know is contained
   * @return returns "true" if polygon paramter is inside of this polygon
   */
  public boolean containsPolygon(Polygon polygon) {
    for (Point p : polygon.getCorners()) {
      if (!this.containsPoint(p)) {
        return false;
      }
    }
    return true;
  }
  
  /**
   * Check if a Polygon intersects another polygon
   *
   * @param polygon shape that we wish to know is intersecting
   * @return returns "true" if polygons intersect polygon
   */
  public boolean intersectsPolygon(Polygon polygon) {
    if (this.containsPolygon(polygon)) {
      return true;
    } else if (polygon.containsPolygon(this)) {
      return true;
    } else {
      for (Line thisEdge : this.edges) {
        for (Line thatEdge : polygon.edges) {
          if( thisEdge.lineIntersect(thatEdge) != null) {
            return true;
          }
        }
      }
    }
    return false;
  }
  
  public JSONArray serialize() {
    JSONArray polygonJSON = new JSONArray();
    for (int i=0; i<this.vertices.size(); i++) {
      JSONObject vertex = vertices.get(i).serialize();
      polygonJSON.setJSONObject(i, vertex);
    }
    return polygonJSON;
  }
}
