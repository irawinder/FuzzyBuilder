import java.util.Map;

// An entire development project; (i.e. a dictionary of TileArray spaces and their ControlPoints)
//
class Development {
  
  String name;
  
  // space and point dictionaries share the same key from TileArray.hashKey()
  
  // Dictionaries for collection of TileArrays that compose development
  private HashMap<String, TileArray> spaceMap;
  private ArrayList<TileArray> spaceList;
  
  // Allows for Unique nameing of control points
  int control_point_counter;
  
  Development(String name) {
    this.name = name;
    spaceMap = new HashMap<String, TileArray>();
    spaceList = new ArrayList<TileArray>();
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
  
  // Clear All Space
  public void clearSpaces() {
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
    }
  }
  
  // Adds TileArray to Map and List dictionaries
  public void addSpace(TileArray space) {
    spaceMap.put(space.hashKey(), space);
    spaceList.add(space);
  }
  
  @Override
  public String toString() {
      return this.name;
  }
}
