// The smallest "particle" of our sketch analysis
//
class Tile {
  
  // Center Point of Tile in real "geospatial" coordinates
  Point location;
  
  // Integer coordinates of tile
  int u, v, w;
  
  // How many units a tile represents 
  // i.e. [units/tile]
  float scale_uv, scale_w;
  String scale_unit;
  
  // Unique Tile ID, composite of integer coordinates
  String id;
  
  // Type of Tile
  String type;
  
  // Adjacent Tiles' names
  ArrayList<String> adjacent;
  
  // Construct Tile
  Tile(int u, int v, int w, Point p) {
    this.location = p;
    this.u = u;
    this.v = v;
    this.w = w;
    id = u + "," + v + "," + w;
    type = null;
    scale_uv = 1.0;
    scale_w = 0.5;
    scale_unit = "";
  }
  
  // Construct Tile
  Tile(int u, int v, Point p) {
    this(u, v, 0, p);
  }
  
  // Define the scale of the tile
  public void setScale(float scale_uv, float scale_w, String unit) {
    this.scale_uv = scale_uv;
    this.scale_w = scale_w;
    this.scale_unit = unit;
  }
  
  // Adds name and elevation of neighbor to adjacency map
  void addAdjacent(String _name) {
    adjacent.add(_name);
  }
  
  @Override
  public String toString() {
      return type + " Tile[" + u + "," + v + "," + w + "] at " + location;
  }
}
