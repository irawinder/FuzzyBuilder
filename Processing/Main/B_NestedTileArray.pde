import java.util.Map;
import java.lang.Math;

// Allows NestedTileArray allows TileArrays within TileArrays, indefinitely
//
class NestedTileArray extends TileArray{
  
  private HashMap<String, NestedTileArray> childrenMap;
  //private ArrayList<NestedTileArray> childrenList;
  
  NestedTileArray(String name, String type) {
    super(name, type);
    childrenMap = new HashMap<String, NestedTileArray>();
    //childrenList = new ArrayList<NestedTileArray>();
  }
  
  // Return Children
  public HashMap<String, NestedTileArray> getChildren() {
    return childrenMap;
  }
  
  // Clear All Children
  public void clearChildren() {
    childrenMap.clear();
  }
  
  // Populate a grid of site tiles that fits within
  // an exact vector boundary that defines site
  //
  public void makeTiles(Polygon boundary, float scale_uv, float scale_w, String units, float rotation, Point translation) {
    
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
    
    int U = int(boundary_w / scale_uv) + 1;
    int V = int(boundary_h / scale_uv) + 1;
    float t_x = translation.x % scale_uv;
    float t_y = translation.y % scale_uv;
    
    for (int u=0; u<U; u++) {
      for (int v=0; v<V; v++) {
        
        // grid coordinates before rotation is applied
        float x_0 = boundary.xMin() - 0.5*easement + u*scale_uv;
        float y_0 = boundary.yMin() - 0.5*easement + v*scale_uv;
        
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
          t.setScale(scale_uv, scale_w, units);
          addTile(t);
        }
      }
    }
  }
  
  // Subdivide the site into Zones that are defined
  // by Voronoi-style nodes
  //
  public void makeZones(ArrayList<TaggedPoint> points) {
    
    clearChildren();
    
    // Initialize Zones Based Upon Tagged Point Collection
    for(TaggedPoint p : points) {
      String zone_name = p.getTag();
      NestedTileArray z = new NestedTileArray(zone_name, "zone");
      childrenMap.put(zone_name, z);
    }
    
    // Fore Each Tile in Site, Check Which Control Point (i.e. Zone Point)
    // it is closested to. This resembles a Voronoi algorithm
    //
    if (childrenMap.size() > 0) {
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
        TileArray closest_zone = childrenMap.get(closest_zone_name);
        closest_zone.addTile(t);
      }
    }
  }
  
  // A Footprint is a type of TileArray that we 
  // can define with a Voronoi site point
  //
  public void makeFootprints() {
    
    clearChildren();
    
    // Initialize Footprints
    NestedTileArray building = new NestedTileArray(this.name + ": Building", "building");
    NestedTileArray setback = new NestedTileArray(this.name + ": Setback", "setback");
    
    // Add tiles that are not at edge of parent TileArray
    for (Map.Entry e : getTiles().entrySet()) {
      Tile t = (Tile)e.getValue();
      if (getNeighbors(t).size() > 7) { // Tile is not an edge
        building.addTile(t);
      } else {
        setback.addTile(t);
      }
    }
    
    // Add footprints to HashMap
    childrenMap.put(building.name, building);
    childrenMap.put(setback.name, setback);
  }
  
  // A Base is a building component that rests on a Footprint
  //
  public void makeBase(int lowestFloor, int highestFloor, float floorHeight) {
    
    clearChildren();
    
    // Initialize Base
    NestedTileArray base = new NestedTileArray(this.name + ": Base", "base");
    for(Map.Entry e : getTiles().entrySet()) {
      Tile t = (Tile)e.getValue();
      
      for(int i=lowestFloor; i<=highestFloor; i++) {
        if(i==0) {
          base.addTile(t);
        } else {
          Point newPoint = new Point(t.location.x, t.location.y, i*floorHeight);
          Tile newTile = new Tile(t.u, t.v, i, newPoint);
          newTile.setScale(t.scale_uv, t.scale_w, t.scale_unit);
          base.addTile(newTile);
        }
        
      }
    }
    childrenMap.put(base.name, base);
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
