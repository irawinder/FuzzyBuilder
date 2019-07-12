// Site -> Zone -> Footprint -> Podium -> Tower -> Floor -> Room
// all use NestedTileArray() class!

import java.util.Map;

// A TileArray is a collection of tiles
//
class TileArray {
  
  // Name and Type of TileArray
  public String name;
  public String type;
  public float hue;
  
  // Collection of Tiles
  private HashMap<String, Tile> tileMap;
  private ArrayList<Tile> tileList;
  
  // Construct Empty TileArray
  public TileArray(String name, String type) {
    this.name = name;
    this.type = type;
    tileMap = new HashMap<String, Tile>();
    tileList = new ArrayList<Tile>();
    hue = 0;
  }
  
  public void setHue(float hue) {
    this.hue = hue;
  }
  
  // Return Tiles
  public HashMap<String, Tile> tileMap() {
    return tileMap;
  }
  
  // Return Tiles
  public ArrayList<Tile> tileList() {
    return tileList;
  }
  
  // Returns true if TileArray contains Tile
  public boolean hasTile(Tile t) {
    return tileMap.get(t.id) != null;
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
  
  // Inherit the Tiles from another TileArray
  // so that they share the same location in memory
  //
  public void inheritTiles(TileArray parent) {
    for(Tile t : parent.tileList()) {
      addTile(t);
    }
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
  
  // Return Adjacent Tiles from a TileArray
  //
  public ArrayList<Tile> getNeighbors(Tile t) {
    ArrayList<Tile> adjacent = new ArrayList<Tile>();
    for(int dU = -1; dU <= +1; dU++) {
      for(int dV = -1; dV <= +1; dV++) {
        if ( !(dU == 0 && dV == 0) ) { // tile skips itself
          String tileKey = (t.u + dU) + "," + (t.v + dV) + "," + t.w;
          Tile adj = tileMap.get(tileKey);
          if(adj != null) adjacent.add(adj);
        }
      }
    }
    return adjacent;
  }
  
  // Given an input TileArray, returns a new TileArray with just the edges
  //
  public NestedTileArray getSetback(String name, String type) {
    NestedTileArray setback = new NestedTileArray(name, type);
    // Add tiles that are at edge of parent TileArray
    for (Tile t : tileList()) {
      // Tile is on edge of parent cluster (Tile surrounded on all sides has 8 neighbors)
      if (getNeighbors(t).size() < 8) {
        setback.addTile(t);
      }
    }
    return setback;
  }
  
  // Returns a new TileArray with child tiles subtracted from parent
  //
  public NestedTileArray getDifference(NestedTileArray child, String name, String type) {
    NestedTileArray subtract = new NestedTileArray(name, type);
    // If child tile doesn't exists in parent tile, add it to new TileArray
    for (Tile t : tileList()) {
      if (!child.hasTile(t)) {
        subtract.addTile(t);
      }
    }
    return subtract;
  }
  
  // Returns a new List of TileArrays generated according to Voronoi logic
  // Need input of Tagged control points, where points are the nodes of Voronoi Cells
  // https://en.wikipedia.org/wiki/Voronoi_diagram
  //
  public ArrayList<NestedTileArray> getVoronoi(ArrayList<TaggedPoint> points, String type) {
    HashMap<String, NestedTileArray> voronoiMap = new HashMap<String, NestedTileArray>();
    ArrayList<NestedTileArray> voronoiList = new ArrayList<NestedTileArray>();
    
    // Initialize Voronoi "Cells" Based Upon Tagged Point Collection
    for(TaggedPoint p : points) {
      String p_name = p.getTag();
      NestedTileArray cell = new NestedTileArray(p_name, type);
      voronoiMap.put(p_name, cell);
      voronoiList.add(cell);
    }
    
    // Fore Each Tile in Site, Check Which Control Point (i.e. Voronoi Site Point)
    // it is closested to. This resembles a Voronoi algorithm
    //
    if (points.size() > 0) {
      for(Tile t : tileList()) {
        float min_distance = Float.POSITIVE_INFINITY;
        String closest_cell_name = "";
        for(TaggedPoint p : points) {
          float distance = sqrt( sq( p.x - t.location.x ) + sq( p.y - t.location.y ) );
          if (distance < min_distance) {
            min_distance = distance;
            closest_cell_name = p.getTag();
          }
        }
        TileArray closest_cell = voronoiMap.get(closest_cell_name);
        closest_cell.addTile(t);
      }
    }
    
    return voronoiList;
  }
  
  // Return New 3D TileArray of Extruded Tiles
  //
  public NestedTileArray getExtrusion(int lowestFloor, int highestFloor, String name, String type) {
    NestedTileArray extrusion = new NestedTileArray(name, type);
    
    // Build Extrusion
    //
    for (Tile t : tileList()) {
      for(int i=lowestFloor; i<=highestFloor; i++) {
        if(i==0) {
          // Existing Ground-level tiles are referenced
          extrusion.addTile(t);
        } else {
          // New Tile must be created above and below ground
          Point newPoint = new Point(t.location.x, t.location.y, i*t.scale_w);
          Tile newTile = new Tile(t.u, t.v, i, newPoint);
          newTile.setScale(t.scale_uv, t.scale_w, t.scale_unit);
          extrusion.addTile(newTile);
        }
      }
    }
    return extrusion;
  }
  
  @Override
  public String toString() {
      return this.name + " (" + this.type + "):" + tileMap.size() +  "t";
  }
}
