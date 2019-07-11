// Classes: Site() -> Zone() -> Footprint() -> Podium() -> Tower() -> Floor() -> Room()
// all extend TileArray() class!

import java.util.Map;
import java.util.List;
import java.lang.Math;
import java.util.Random;

// A Site is a TileArray the represents piece of land that can be divided into zones
//
class Site extends TileArray {
  
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
        TileArray closest_zone = zone.get(closest_zone_name);
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

// A Zone is a type of compartment that we 
// can define with a Voronoi site point
//
class Zone extends TileArray {
  
  private HashMap<String, Footprint> footprint;
  
  Zone(String name) {
    super(name);
    footprint = new HashMap<String, Footprint>();
  }
  
  public void makeFootprints() {
    
    clearFootprints();
    
    // Initialize Footprints
    Footprint building = new Footprint("Building");
    Footprint setback = new Footprint("Setback");
    
    // Add tiles that are not at edge of zone
    for (Map.Entry e_z : getTiles().entrySet()) {
      Tile t = (Tile)e_z.getValue();
      if (getNeighbors(t).size() > 7) { // Tile is not an edge
        building.addTile(t);
      } else {
        setback.addTile(t);
      }
    }
    
    // Add footprints to HashMap
    footprint.put(building.name, building);
    footprint.put(setback.name, setback);
    
    println(coverageRatio());
  }
  
  // return ratio of built area footprint to zone area
  float coverageRatio() {
    float buildingTiles = (float)footprint.get("Building").getTiles().size();
    float zoneTiles = getTiles().size();
    return buildingTiles / zoneTiles;
  }
  
  // Return Footprints
  public HashMap<String, Footprint> getFootprints() {
    return footprint;
  }
  
  // Clear All Footprints
  public void clearFootprints() {
    footprint.clear();
  }
}

// A Footprint is a type of compartment that we 
// can define with a Voronoi site point
//
class Footprint extends TileArray {
  
  private HashMap<String, Base> base;
  
  Footprint(String name) {
    super(name);
    base = new HashMap<String, Base>();
  }
}

// A Base is a building component that rests on a Footprint
//
class Base extends TileArray {
  
  private HashMap<String, Floor> floor;
  private HashMap<String, Tower> tower;
  
  Base(String name) {
    super(name);
    floor = new HashMap<String, Floor>();
    tower = new HashMap<String, Tower>();
  }
}

// A Tower is a building component that rests on a Base
//
class Tower extends TileArray {
  
  private HashMap<String, Floor> floor;
  
  Tower(String name) {
    super(name);
    floor = new HashMap<String, Floor>();
  }
}

// A Floor is a subcompenent of a Base or Tower
//
class Floor extends TileArray {
  
  private HashMap<String, Room> room;
  
  Floor(String name) {
    super(name);
    room = new HashMap<String, Room>();
  }
}

// A Room is a subcompenent of a Floor
//
class Room extends TileArray {
  
  Room(String name) {
    super(name);
  }
}
