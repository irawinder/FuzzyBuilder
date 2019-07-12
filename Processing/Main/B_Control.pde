// Control Class is for managing Control Points
//
class Control {
  
  private ArrayList<ControlPoint> cPoints;
  private int point_counter;
  
  Control() {
    cPoints = new ArrayList<ControlPoint>();
    point_counter = 1;
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
    p.setTag(prefix + " " + point_counter);
    p.setType(type);
    cPoints.add(p);
    point_counter++;
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
      random_point = new ControlPoint(random_tile.location.x, random_tile.location.y);
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
