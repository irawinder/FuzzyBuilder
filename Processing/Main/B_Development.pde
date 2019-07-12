import java.util.Map;

// An entire development project
//
class Development {
  
  String name;
  
  // Dictionaries for collection of TileArrays that compose development
  //
  private HashMap<String, TileArray> spaceMap;
  private ArrayList<TileArray> spaceList;
  
  Development(String name) {
    spaceMap = new HashMap<String, TileArray>();
    spaceList = new ArrayList<TileArray>();
    this.name = name;
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
  public void clearSpace() {
    spaceMap.clear();
    spaceList.clear();
  }
  
  // Adds TileArray to Map and List dictionaries
  public void addSpace(TileArray space) {
    spaceMap.put(space.name, space);
    spaceList.add(space);
  }
  
  // Adds Empty TileArray to Map and List dictionaries
  public void addSpace(String name, String type) {
    TileArray space = new TileArray(name, type);
    spaceMap.put(space.name, space);
    spaceList.add(space);
  }
  
  // Return TileArray of certain name
  public TileArray getSpace(String spaceKey) {
    return spaceMap.get(spaceKey);
  }
}
