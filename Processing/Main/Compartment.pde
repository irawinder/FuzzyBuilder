import java.util.Map;

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

// A Zone is a type of compartment that we 
// can define with a Voronoi site point
//
class Zone extends Compartment {
  
  Zone(String name) {
    super(name);
  }
}

// A Site is the largest compartment for a set of Tiles
//
class Site extends Compartment {
  
  private HashMap<String, Zone> zone;
  
  Site(String name) {
    super(name);
    zone = new HashMap<String, Zone>();
  }
  
  // Populate a grid of site tiles that fits within
  // an exact vector boundary that defines site
  //
  public void makeTiles(Polygon boundary, float scale, String units) {
    
    clearTiles();
    
    // Create a field of grid points that is certain 
    // to uniformly saturate polygon boundary
    //
    float boundary_w = boundary.xMax() - boundary.xMin();
    float boundary_h = boundary.yMax() - boundary.yMin();
    int U = int(boundary_w / scale) + 1;
    int V = int(boundary_h / scale) + 1;
    
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
  
  // Subdivide the site into Zones that are defined
  // by Voronoi-style nodes
  //
  public void makeZones(ArrayList<TaggedPoint> points) {
    
    clearZones();
    
    // Initialize Zones Based Upon Tagged Point Collection
    for(TaggedPoint p : points) {
      String zone_name = p.getTag();
      Zone z = new Zone(zone_name);
      zone.put(zone_name, z);
    }
    
    // Fore Each Tile in Site, Check Which Control Point (i.e. Zone Point)
    // it is closested to. This resembles a Voronoi algorithm
    //
    if (zone.size() > 0) {
      for(Map.Entry e : getTiles().entrySet()) {
        Tile t = (Tile)e.getValue();
        float min_distance = Float.POSITIVE_INFINITY;
        String closest_zone_name = "";
        for(TaggedPoint p : points) {
          float x_dist = p.x - t.location.x;
          float y_dist = p.y - t.location.y;
          float distance = sqrt( sq(x_dist) + sq(y_dist) );
          if (distance < min_distance) {
            min_distance = distance;
            closest_zone_name = p.getTag();
          }
        }
        Zone closest_zone = zone.get(closest_zone_name);
        closest_zone.addTile(t);
      }
    }
  }
  
  // Return Zones
  public HashMap<String, Zone> getZones() {
    return zone;
  }
  
  // Clear All Zones
  public void clearZones() {
    zone.clear();
  }
  
}
