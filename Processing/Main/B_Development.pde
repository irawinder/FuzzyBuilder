import java.util.Map;

// An entire development project; (i.e. a dictionary of TileArray spaces)
//
class Development {
  
  String name;
  
  // space and point dictionaries share the same key from TileArray.hashKey()
  
  // Dictionaries for collection of TileArrays that compose development
  private HashMap<String, TileArray> spaceMap;
  private ArrayList<TileArray> spaceList;
  
  // Dictionary for collection of ControlPoints that compose development
  private HashMap<String, ArrayList<ControlPoint>> pointMap;
  private ArrayList<ArrayList<ControlPoint>> pointList;
  
  // Allows for Unique nameing of control points
  int control_point_counter;
  
  Development(String name) {
    this.name = name;
    spaceMap = new HashMap<String, TileArray>();
    spaceList = new ArrayList<TileArray>();
    pointMap = new HashMap<String, ArrayList<ControlPoint>>();
    pointList = new ArrayList<ArrayList<ControlPoint>>();
    control_point_counter = 0;
  }
  
  Development() {
    this("New Development");
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  // Return Space Map
  public HashMap<String, TileArray> spaceMap() {
    return spaceMap;
  }
  
  // Return Space List
  public ArrayList<TileArray> spaceList() {
    return spaceList;
  }
  
  // Get Specific Space
  public TileArray getSpace(String hashKey) {
    return spaceMap.get(hashKey);
  }
  
  // Return Point Map
  public HashMap<String, ArrayList<ControlPoint>> pointMap() {
    return pointMap;
  }
  
  // Return Point List
  public ArrayList<ArrayList<ControlPoint>> pointList() {
    return pointList;
  }
  
  // Return Points for a Space
  public ArrayList<ControlPoint> getControlPoints(TileArray space) {
    return pointMap.get(space.hashKey());
  }
  
  // Clear All Space
  public void clearSpaces() {
    spaceMap.clear();
    spaceList.clear();
    clearPoints();
  }
  
  public void clearPoints() {
    pointMap.clear();
    pointList.clear();
    control_point_counter = 0;
    
    for (TileArray space : spaceList) {
      initPoints(space);
    }
  }
  
  // Remove all spaces of a certain type
  public void clearType(String type) {
    ArrayList<TileArray> toClear = new ArrayList<TileArray>();
    
    // Populate list of spaces to clear, based on type
    for (TileArray space : spaceList) {
      if (space.type.equals(type)) {
        toClear.add(space);
      }
    }
    
    // Clear all spaces from Map and List dictionaries
    for (TileArray space : toClear) {
      spaceMap.remove(space);
      spaceList.remove(space);
      pointMap.remove(space.hashKey());
      pointList.remove(space.hashKey());
    }
  }
  
  // Adds TileArray to Map and List dictionaries
  public void addSpace(TileArray space) {
    spaceMap.put(space.hashKey(), space);
    spaceList.add(space);
    initPoints(space);
  }
  
  // Init Control Point Maps and List
  private void initPoints(TileArray space) {
    pointMap.put(space.hashKey(), new ArrayList<ControlPoint>());
    pointList.add(getControlPoints(space));
  }
  
  // add a random control points to a space
  public void addControlPoint(TileArray space, String prefix) {
    ControlPoint point = space.randomPoint();
    addPointToSpace(space, point, prefix);
  }
  
  // add specific control points to a space
  public void addControlPoint(TileArray space, String prefix, float x, float y) {
    ControlPoint point = new ControlPoint(x, y, space.hashKey());
    addPointToSpace(space, point, prefix);
    
  }
  
  // assign a known ControlPoint to a space
  private void addPointToSpace(TileArray space, ControlPoint point, String prefix) {
    // Retrieve the point list for the input space
    ArrayList<ControlPoint> cPoints = getControlPoints(space);
    control_point_counter++;
    point.setTag(prefix + " " + control_point_counter);
    cPoints.add(point);
  }
  
  // remove a control points from a space
  public void removeControlPoint(ControlPoint point) {
    ArrayList<ControlPoint> cPoints = pointMap.get(point.hashKey);
    cPoints.remove(point);
  }
  
  @Override
  public String toString() {
      return this.name;
  }
}
