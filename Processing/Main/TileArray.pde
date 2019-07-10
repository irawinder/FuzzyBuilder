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
  
  @Override
  public String toString() {
      return "TileArray [" + this.name + ": " + tile.size() +  " tiles]";
  }
}
