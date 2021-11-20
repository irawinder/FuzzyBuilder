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

    VoxelArray voxels = new VoxelArray();
    
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
          voxels.addVoxel(t);
        }
      }
    }
    
    return voxels;
  }
  
  /**
   * Returns a new VoxelArray with child voxels subtracted from parent
   * 
   * @param parent VoxelArray to be subtracted from
   * @param child VoxelArray to subtract from parent
   * @return New VoxelArray with child subtracted from this VoxelArray
   */
  public VoxelArray sub(VoxelArray parent, VoxelArray child) {
    VoxelArray diff = new VoxelArray();
    for (Voxel t : parent.voxelList) {
      if (!child.voxelList.contains(t)) {
        diff.addVoxel(t);
      }
    }
    return diff;
  }

  /**
   * Returns a new VoxelArray with child voxels added to parent
   * 
   * @param parent VoxelArray to be added to
   * @param child VoxelArray to add to parent
   * @return New VoxelArray with child subtracted from this VoxelArray
   */
  public VoxelArray add(VoxelArray parent, VoxelArray child) {
    VoxelArray add = new VoxelArray();
    for (Voxel t : child.voxelList) {
      if (!parent.voxelList.contains(t)) {
        add.addVoxel(t);
      }
    }
    return add;
  }
  
  //public ArrayList<ArrayList<Voxel>> adjacencyList(VoxelArray
}
