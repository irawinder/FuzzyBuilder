// The smallest "particle" of our sketch analysis
//
class Tile {
  
  // Center Point of Tile in real "geospatial" coordinates
  Point location;
  
  // Integer coordinates of tile
  int u, v;
  
  // How many units a tile represents 
  // i.e. [units/tile]
  float scale;
  String scale_unit;
  
  // Unique Tile ID, composite of integer coordinates
  String id;
  
  // Type of Tile
  String type;
  
  // Adjacent Tiles' names
  ArrayList<String> adjacent;
  
  // Construct Tile
  Tile(int u, int v, Point p) {
    this.location = p;
    this.u = u;
    this.v = v;
    id = u + "," + v;
    type = null;
    scale = 1.0;
    scale_unit = "";
  }
  
  // Define the scale of the tile
  public void setScale(float scale, String unit) {
    this.scale = scale;
    this.scale_unit = unit;
  }
  
  // Adds name and elevation of neighbor to adjacency map
  void addAdjacent(String _name) {
    adjacent.add(_name);
  }
  
  @Override
  public String toString() {
      return type + " Tile[" + u + "," + v +"] at " + location;
  }
}
