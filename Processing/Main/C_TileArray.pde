// Site -> Zone -> Footprint -> Podium -> Tower -> Floor -> Room
// all use NestedTileArray() class!

import java.util.Map;

// A TileArray is a collection of tiles
//
class TileArray {
  
  // Name and Type of TileArray
  public String name;
  public String type;
  
  // Collection of Tiles
  private HashMap<String, Tile> tileMap;
  private ArrayList<Tile> tileList;
  
  // Construct Empty TileArray
  public TileArray(String name, String type) {
    this.name = name;
    this.type = type;
    tileMap = new HashMap<String, Tile>();
    tileList = new ArrayList<Tile>();
  }
  
  // Return Tiles
  public HashMap<String, Tile> getTileMap() {
    return tileMap;
  }
  
  // Return Tiles
  public ArrayList<Tile> getTileList() {
    return tileList;
  }
  
  // Clear All Tiles
  public void clearTiles() {
    tileMap.clear();
    tileList.clear();
  }
  
  // Add Tile
  public void addTile(Tile t) {
    tileMap.put(t.id, t);
    tileList.add(t);
  }
  
  // Remove Tile
  public void removeTile(String tileKey) {
    Tile t = tileMap.get(tileKey);
    tileList.remove(t);
    tileMap.remove(tileKey);
  }
  
  // Remove Tile
  public void removeTile(Tile t) {
    tileList.remove(t);
    tileMap.remove(t.id);
  }
  
  // Return Adjacent Tiles from a TileArray
  public HashMap<String, Tile> getNeighbors(Tile t) {
    HashMap<String, Tile> adjacent = new HashMap<String, Tile>();
    for(int dU = -1; dU <= +1; dU++) {
      for(int dV = -1; dV <= +1; dV++) {
        if ( !(dU == 0 && dV == 0) ) { // tile skips itself
          String tileKey = (t.u + dU) + "," + (t.v + dV) + "," + t.w;
          Tile adj = tileMap.get(tileKey);
          if(adj != null) adjacent.put(tileKey, adj);
        }
      }
    }
    return adjacent;
  }
  
  @Override
  public String toString() {
      return this.name + " (" + this.type + "):" + tileMap.size() +  "t";
  }
  
  // Inherit the Tiles from another TileArray
  // so that they share the same location in memory
  public void inheritTiles(TileArray parent) {
    for(Map.Entry e : parent.getTileMap().entrySet()) {
      Tile t = (Tile)e.getValue();
      addTile(t);
    }
  }
}
