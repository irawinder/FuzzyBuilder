import java.util.Map;
import java.lang.Math;

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
  public void makeTiles(Polygon boundary, float scale, String units, float rotation, Point translation) {
    
    clearTiles();
    
    // Create a field of grid points that is certain 
    // to uniformly saturate polygon boundary
    
    // Polygon origin and rectangular bounding box extents
    float origin_x = 0.5 * (boundary.xMax() + boundary.xMin());
    float origin_y = 0.5 * (boundary.yMax() + boundary.yMin());
    float boundary_w = boundary.xMax() - boundary.xMin();
    float boundary_h = boundary.yMax() - boundary.yMin();
    
    // maximum additional bounding box dimensions if polygon is rotated 90 degrees
    float easement = max(boundary_w, boundary_h) * (sqrt(2) - 1);
    boundary_w += easement;
    boundary_h += easement;
    
    int U = int(boundary_w / scale) + 1;
    int V = int(boundary_h / scale) + 1;
    float t_x = translation.x % scale;
    float t_y = translation.y % scale;
    
    for (int u=0; u<U; u++) {
      for (int v=0; v<V; v++) {
        
        // grid coordinates before rotation is applied
        float x_0 = boundary.xMin() - 0.5*easement + u*scale;
        float y_0 = boundary.yMin() - 0.5*easement + v*scale;
        
        // translate origin, rotate, shift back, then translate
        float sin = (float)Math.sin(rotation);
        float cos = (float)Math.cos(rotation);
        float x_f = + (x_0 - origin_x) * cos - (y_0 - origin_y) * sin + origin_x + t_x;
        float y_f = + (x_0 - origin_x) * sin + (y_0 - origin_y) * cos + origin_y + t_y;
        
        Point location = new Point(x_f, y_f);
        
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
