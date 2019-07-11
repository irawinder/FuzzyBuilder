import java.util.Map;
import java.lang.Math;

// A TileArray is a collection of tiles
//
class TileArray {
  
  // Name and Type of TileArray
  public String name;
  
  // Collection of Tiles
  private HashMap<String, Tile> tile;
  
  // Construct Empty TileArray
  public TileArray(String name) {
    this.name = name;
    tile = new HashMap<String, Tile>();
  }
  
  // Add Tile
  public void addTile(Tile t) {
    tile.put(t.id, t);
  }
  
  // Remove Tile
  public void removeTile(String tileKey) {
    tile.remove(tileKey);
  }
  
  // Clear All Tiles
  public void clearTiles() {
    tile.clear();
  }
  
  // Return Tiles
  public HashMap<String, Tile> getTiles() {
    return tile;
  }
  
  // Return Adjacent Tiles from a TileArray
  public HashMap<String, Tile> getNeighbors(Tile t) {
    HashMap<String, Tile> adjacent = new HashMap<String, Tile>();
    for(int dU = -1; dU <= +1; dU++) {
      for(int dV = -1; dV <= +1; dV++) {
        if ( !(dU == 0 && dV == 0) ) { // tile skips itself
          String tileKey = (t.u + dU) + "," + (t.v + dV);
          Tile adj = tile.get(tileKey);
          if(adj != null) adjacent.put(tileKey, adj);
        }
      }
    }
    return adjacent;
  }
  
  @Override
  public String toString() {
      return "TileArray [" + this.name + ": " + tile.size() +  " tiles]";
  }
  
  // Inherit the Tiles from another TileArray
  // so that they share the same location in memory
  public void inheritTiles(TileArray parent) {
    for(Map.Entry e : parent.getTiles().entrySet()) {
      Tile t = (Tile)e.getValue();
      addTile(t);
    }
  }
}
