import java.util.Map;

// An entire development project; (i.e. a dictionary of TileArray spaces)
//
class Development {
  
  String name;
  
  // Dictionaries for collection of TileArrays that compose development
  private HashMap<String, TileArray> spaceMap;
  private ArrayList<TileArray> spaceList;
  
  // Dictionary for collection of ControlPoints that compose development
  private HashMap<String, ArrayList<ControlPoint>> pointMap;
  
  Development(String name) {
    this.name = name;
    spaceMap = new HashMap<String, TileArray>();
    spaceList = new ArrayList<TileArray>();
    pointMap = new HashMap<String, ArrayList<ControlPoint>>();
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
  
  // Clear All Space
  public void clear() {
    spaceMap.clear();
    spaceList.clear();
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
    }
  }
  
  // Adds TileArray to Map and List dictionaries
  public void addSpace(TileArray space) {
    spaceMap.put(space.hashKey(), space);
    spaceList.add(space);
    pointMap.put(space.hashKey(), new ArrayList<ControlPoint>());
  }
  
  @Override
  public String toString() {
      return this.name;
  }
}
