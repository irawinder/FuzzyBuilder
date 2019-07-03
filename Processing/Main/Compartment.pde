import java.util.Map;

// A Site is the largest compartment for a set of Tiles
//
class Site extends Compartment {
  
  Site(String name) {
    super(name);
  }
  
  // Populate a grid of site tiles that fits within
  // an exact vector boundary that defines site
  //
  public void makeTilesFromPolygon(Polygon boundary, float scale, String units) {
    
    // Create a field of grid points that is certain 
    // to uniformly saturate polygon boundary
    //
    float boundary_w = boundary.xMax() - boundary.xMin();
    float boundary_h = boundary.yMax() - boundary.yMin();
    int U = int(boundary_w / scale);
    int V = int(boundary_h / scale);
    
    for (int u=0; u<U; u++) {
      for (int v=0; v<V; v++) {
        float x_0 = boundary.xMin() + u*scale;
        float y_0 = boundary.yMin() + v*scale;
        Point location = new Point(x_0, y_0);
        
        // Test which points are in the polygon boundary
        // and add them to tile set
        //
        if(boundary.containsPoint(location)) {
          Tile t = new Tile(u, v, location);
          t.setScale(scale, units);
          addTile(t);
        }
      }
    }
    
  }
  
}

// A Zone is a type of compartment that we 
// can define with a Voronoi site point
//
class Zone extends Compartment {
  
  // user defined "center" of zone
  // used to generate Voronoi inclusion
  //
  Point voronoi_site;
  
  Zone(String name) {
    super(name);
  }
}

// A Compartment is a collection of tiles
//
class Compartment {
  
  // Name and Type of Compartment
  public String name;
  
  // Collection of Tiles
  private HashMap<String, Tile> tile;
  
  // Construct Empty Compartment
  public Compartment(String name) {
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
      return "Compartment[" + this.name + ": " + tile.size() +  " tiles]";
  }
}

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
