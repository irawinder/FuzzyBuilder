/**
 * FuzzyMorph is a class deicated to functional transformations of geometry
 */
class FuzzyMorph {
  
  // geographic height of ground
  private float GROUND_Z = 0;
  
  // local w (vertical) coordinate of ground
  private int GROUND_W = 0;
  
  /**
   * Inherit the Voxels from a parent VoxelArray (child voxels are NOT cloned)
   * 
   * @param parent A parent VoxelArray
   */
  public VoxelArray cloneVoxelArray(VoxelArray parent) {
    VoxelArray arrayCopy = new VoxelArray();
    for (Voxel t : parent.voxelList) {
      arrayCopy.addVoxel(t);
    }
    return arrayCopy;
  }
  
  /**
   * Clone a voxel with a new UUID
   * 
   * @param parent A parent Voxel
   */
  public Voxel cloneVoxel(Voxel parent) {
    Voxel copy = new Voxel();
    copy.setLocation(parent.location.x, parent.location.y, parent.location.z);
    copy.setRotation(parent.rotation);
    copy.setSize(parent.width, parent.height);
    copy.setType(parent.type);
    return copy;
  }
  
  /**
   * Populate a grid of voxels that fits within an exact vector boundary that
   * defines site, but projected to ground plane;
   * 
   * @param boundary    Polygon that defines boundary of site
   * @param voxelWidth    Width of a square voxel
   * @param voxelHeight     Height of a voxel
   * @param units       Friendly units of a voxel (e.g. "meters")
   * @param rotation    Rotation of Voxel Grid
   * @param translation Translation Vector of Entire Grid
   */
  public VoxelArray make(Polygon boundary, float voxelWidth, float voxelHeight, float rotation, Point translation) {

    VoxelArray result = new VoxelArray();
    
    // Create a field of grid points that is certain
    // to uniformly saturate polygon boundary

    // Polygon origin and rectangular bounding box extents
    float origin_x = (float) (0.5 * (boundary.xMax() + boundary.xMin()));
    float origin_y = (float) (0.5 * (boundary.yMax() + boundary.yMin()));
    float boundary_w = boundary.xMax() - boundary.xMin();
    float boundary_h = boundary.yMax() - boundary.yMin();
    float bounds = max(boundary_w, boundary_h);
    
    // maximum additional bounding box dimensions if polygon is rotated 45 degrees
    float easement = (float) (Math.max(boundary_w, boundary_h) * (Math.sqrt(2) - 1));
    bounds += easement;

    int U = (int) ((bounds / voxelWidth) + 1);
    int V = U;
    float t_x = translation.x % voxelWidth;
    float t_y = translation.y % voxelWidth;

    for (int u = 0; u < U; u++) {
      for (int v = 0; v < V; v++) {

        // grid coordinates before rotation is applied
        float x_0 = (float) (origin_x - 0.5 * bounds + u * voxelWidth);
        float y_0 = (float) (origin_y - 0.5 * bounds + v * voxelWidth);

        // translate origin, rotate, shift back, then translate
        float sin = (float) Math.sin(rotation);
        float cos = (float) Math.cos(rotation);
        float x_f = +(x_0 - origin_x) * cos - (y_0 - origin_y) * sin + origin_x + t_x;
        float y_f = +(x_0 - origin_x) * sin + (y_0 - origin_y) * cos + origin_y + t_y;

        Point location = new Point(x_f, y_f);

        // Test which points are in the polygon boundary
        // and add them to voxel set
        //
        if (boundary.containsPoint(location)) {
          Voxel t = new Voxel();
          t.setLocation(location.x, location.y, GROUND_Z);
          t.setCoordinates(u, v, GROUND_W);
          t.setRotation(rotation);
          t.setSize(voxelWidth, voxelHeight);
          result.addVoxel(t);
        }
      }
    }
    
    return result;
  }
  
  /**
   * Given an input VoxelArray, returns a new VoxelArray with outer ring of voxels removed
   * 
   * @param parent voxel array to apply setback to
   * @return new VoxelArray with outer ring of voxels removed removed
   */
  private VoxelArray setback(VoxelArray parent) {
    VoxelArray result = new VoxelArray();

    // Add tiles that are at edge of parent TileArray
    for (Voxel t : parent.voxelList) {
      // Tile surrounded on all sides has 8 neighbors
      if (this.getNeighborsUV(t, parent).size() >= 7) {
        result.addVoxel(t);
      }
    }
    return result;
  }
  
  /**
   * Given an input VoxelArray, returns a new VoxelArray with outer ring of voxels removed
   * 
   * @param parent voxel array to apply setback to
   * @param setbackDistance distance to apply setback ffrom edge
   * @return new VoxelArray with outer ring of voxels removed
   */
  public VoxelArray setback(VoxelArray parent, float setbackDistance) {
    VoxelArray result = parent;
    
    // Get the width of the first voxel in the array
    float voxelWidth;
    if (parent.voxelList.size() > 0) {
      voxelWidth = parent.voxelList.get(0).width;
    } else {
      return result;
    }
    
    // repeat offset as necessary to achieve desired distance
    int numSetbacks = int(0.5 + setbackDistance / voxelWidth);
    for(int i=0; i<numSetbacks; i++) result = this.setback(result);
    
    return result;
  }
  
  /**
   * Returns a new VoxelArray with child voxels subtracted from parent
   * 
   * @param parent VoxelArray to be subtracted from
   * @param child VoxelArray to subtract from parent
   * @return New VoxelArray with child subtracted from this VoxelArray
   */
  public VoxelArray sub(VoxelArray parent, VoxelArray child) {
    VoxelArray result = new VoxelArray();
    for (Voxel t : parent.voxelList) {
      if (!child.voxelList.contains(t)) {
        result.addVoxel(t);
      }
    }
    return result;
  }

  /**
   * Returns a new VoxelArray with child voxels added to parent
   * 
   * @param parent VoxelArray to be added to
   * @param child VoxelArray to add to parent
   * @return New VoxelArray with child subtracted from this VoxelArray
   */
  public VoxelArray add(VoxelArray parent, VoxelArray child) {
    VoxelArray result = new VoxelArray();
    for (Voxel t : child.voxelList) {
      if (!parent.voxelList.contains(t)) {
        result.addVoxel(t);
      }
    }
    return result;
  }
  
  /**
   * Get the horizontal neighboring Voxels of a specific Voxel
   * 
   * @param t Voxel we wish to know the Neighbors of
   * @param tArray that encapsulates t
   * @return Adjacent Voxels that Exist within a VoxelArray
   */
  public ArrayList<Voxel> getNeighborsUV(Voxel t, VoxelArray tArray) {
    ArrayList<Voxel> neighbors = new ArrayList<Voxel>();
    for (int dU = -1; dU <= +1; dU++) {
      for (int dV = -1; dV <= +1; dV++) {
        if (!(dU == 0 && dV == 0)) { // tile skips itself
          String coordKey = this.coordKey(t.u + dU, t.v + dV, t.w);
          if (tArray.voxelMap.containsKey(coordKey)) {
            Voxel adj = tArray.voxelMap.get(coordKey);
            neighbors.add(adj);
          }
        }
      }
    }
    return neighbors;
  }
  
  /**
   * Get the Voxel directly above a specific Voxel; null if none
   * 
   * @param t Voxel we wish to know the Neighbor of
   * @param tArray that encapsulates t
   * @return Voxel directly above a specific Voxel; null if none
   */
  public Voxel getNeighborTop(Voxel t, VoxelArray tArray) {
    String coordKey = this.coordKey(t.u, t.v, t.w + 1);
    if (tArray.voxelMap.containsKey(coordKey)) {
      return tArray.voxelMap.get(coordKey);
    } else {
      return null;
    }
  }
  
  /**
   * Get the Voxel directly below a specific Voxel; null if none
   * 
   * @param t Voxel we wish to know the Neighbor of
   * @param tArray that encapsulates t
   * @return Voxel directly below a specific Voxel; null if none
   */
  public Voxel getNeighborBottom(Voxel t, VoxelArray tArray) {
    String coordKey = this.coordKey(t.u, t.v, t.w - 1);
    if (tArray.voxelMap.containsKey(coordKey)) {
      return tArray.voxelMap.get(coordKey);
    } else {
      return null;
    }
  }
  
  public String coordKey(int u, int v, int w) {
    return u + "," + v + "," + w;
  }
}
