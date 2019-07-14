// Control Class is for managing Control Points
//
class Control {
  
  private ArrayList<ControlPoint> cPoints;
  
  Control() {
    cPoints = new ArrayList<ControlPoint>();
  }
  
  public ArrayList<ControlPoint> points() {
    return cPoints;
  }
  
  // Returns a subset of control points according to their type
  //
  public ArrayList<ControlPoint> points(String type) {
    ArrayList<ControlPoint> subset = new ArrayList<ControlPoint>();
    for (ControlPoint p : cPoints) {
      if (p.getType().equals(type)) {
        subset.add(p);
      }
    }
    return subset;
  }
  
  // Turns all control points on()
  public void on() {
    for (ControlPoint p : cPoints) p.on();
  }
  
  // Turns all control points on()
  public void off() {
    for (ControlPoint p : cPoints) p.off();
  }
  
  // Turns all control points of a specific type on()
  public void on(String type) {
    for (ControlPoint p : cPoints) {
      if (p.type.equals(type)) p.on();
    }
  }
  
  // Turns all control points of a specific type off()
  public void off(String type) {
    for (ControlPoint p : cPoints) {
      if (p.type.equals(type)) p.off();
    }
  }
  
  // adds a control point randomly located inside a space
  //
  public void addPoint(String prefix, String type, TileArray space) {
    ControlPoint p = randomPoint(space);
    if (p == null) {
      addPoint(prefix, type, 0, 0);
    } else {
      addPoint(p, prefix, type);
    }
  }
  
  // adds a control point at the given xy coordinates
  //
  public void addPoint(String prefix, String type, float x, float y) {
    ControlPoint p = new ControlPoint(x, y);
    addPoint(p, prefix, type);
  }
  
  // adds a pre-made control point 
  //
  private void addPoint(ControlPoint p, String prefix, String type) {
    p.setTag(prefix);
    p.setType(type);
    cPoints.add(p);
  }
  
  // Returns a random control point with a coordinate
  // at an existing tile in the TileArray
  //
  private ControlPoint randomPoint(TileArray space) {
    ControlPoint random_point = null;
    if (space.tileList.size() > 0) {
      Random rand = new Random();
      int random_index = rand.nextInt(space.tileList.size());
      Tile random_tile = space.tileList.get(random_index);
      float jitter_x = rand.nextFloat() - 0.5;
      float jitter_y = rand.nextFloat() - 0.5;
      // jitter helps keep distance-based dictionary entries unique
      random_point = new ControlPoint(random_tile.location.x + jitter_x, random_tile.location.y + jitter_y);
    }
    return random_point;
  }
  
  public void removePoint(ControlPoint p) {
    cPoints.remove(p);
  }
  
  public void clearPoints() {
    cPoints.clear();
  }
  
  @Override
  public String toString() {
    String out = "";
    for (ControlPoint p : cPoints) out += p + "\n";
    return out;
  }
}

class ControlPoint extends Point {
  
  // String tag of point
  private String tag;
  
  // The type of control point
  private String type;
  
  // Numerical weight of point
  private Float weight;
  
  // Activate control point
  private boolean active;
  
  ControlPoint(float x, float y) {
    super(x,y);
    tag = "";
    weight = 1.0;
    active = true;
  }
  
  public void on() {
    active = true;
  }
  
  public void off() {
    active = false;
  }
  
  public boolean active() {
    return active;
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
