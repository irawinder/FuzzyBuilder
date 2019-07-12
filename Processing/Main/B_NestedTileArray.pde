import java.util.Map;
import java.lang.Math;

// NestedTileArray allows TileArrays within TileArrays, indefinitely
//
class NestedTileArray extends TileArray{
  
  private HashMap<String, NestedTileArray> childMap;
  private ArrayList<NestedTileArray> childList;
  
  NestedTileArray(String name, String type) {
    super(name, type);
    childMap = new HashMap<String, NestedTileArray>();
    childList = new ArrayList<NestedTileArray>();
  }
  
  // Return Children Map
  public HashMap<String, NestedTileArray> childMap() {
    return childMap;
  }
  
  // Return Children List
  public ArrayList<NestedTileArray> childList() {
    return childList;
  }
  
  // Clear All Children
  public void clearChildren() {
    childMap.clear();
    childList.clear();
  }
  
  public void addChild(NestedTileArray child) {
    childMap.put(child.name, child);
    childList.add(child);
  }
  
  public NestedTileArray getChild(String childKey) {
    return childMap.get(childKey);
  }
  
  // Subdivide the site into Zones that are defined
  // by Voronoi-style nodes
  //
  public void makeZones(ArrayList<TaggedPoint> points) {
    clearChildren();
    
    // Initialize Zones
    String type = "zone";
    ArrayList<NestedTileArray> zones = getVoronoi(points, type);
    
    // Add new TileArrays to children
    for(NestedTileArray z : zones) addChild(z);
  }
  
  // A Footprint is a type of TileArray that we
  // can derive from an existing TileArray (i.e. zone)
  //
  public void makeFootprints() {
    clearChildren();
    
    // Initialize Footprints
    String type = "footprint";
    NestedTileArray setback = getSetback("Setback", type);
    NestedTileArray building = getDifference(setback, "Building", type);
    
    // Add new Tilemaps to children
    addChild(setback);
    addChild(building);
  }
  
  // A Base is a building component that rests on a Footprint
  //
  public void makeBase(int lowestFloor, int highestFloor) {
    clearChildren();
    
    // Initialize Base
    String type = "base";
    NestedTileArray base = getExtrusion(lowestFloor, highestFloor, "Podium", type);
    
    // Add new Tilemaps to children
    addChild(base);
  }
  
  // A Tower is a building component that rests on a Base
  //
  public void makeTowers() {
    
    clearChildren();
    
  }
  
  // A Floor is a subcompenent of a Base or Tower
  //
  public void makeFloors() {
    
    clearChildren();
    
  }
  
  // A Room is a subcompenent of a Floor
  //
  public void makeRooms() {
    
    clearChildren();
    
  }
}
