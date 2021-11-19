/**
 * A VoxelArray is a collection of voxels
 * 
 * @author ira
 * 
 */
public class VoxelArray {
  
  // Unique identifier for this voxel
  final private UUID uniqueID;
  
  // Center Point of Voxel in real "geospatial" coordinates
  public Point location;

  // Collection of Voxels
  private HashMap<UUID, Voxel> voxelMap;
  private HashMap<String, Voxel> coordMap;
  private ArrayList<Voxel> voxelList;
  
  public float minX, maxX;
  public float minY, maxY;
  
  private float GROUND_Z = 0;
  
  /**
   * Construct Empty VoxelArray
   * 
   * @param name Name of VoxelArray
   * @param type Type of VoxelArray
   */
  public VoxelArray() {
    this.uniqueID = UUID.randomUUID();
    this.voxelMap = new HashMap<UUID, Voxel>();
    this.coordMap = new HashMap<String, Voxel>();
    this.voxelList = new ArrayList<Voxel>();
    
    this.minX = 0;
    this.maxX = 0;
    this.minY = 0;
    this.maxY = 0;
  }

  /**
   * Return Voxels
   * 
   * @return HashMap of all voxels in VoxelArray
   */
  public HashMap<UUID, Voxel> voxelMap() {
    return voxelMap;
  }

  /**
   * Return Voxels
   * 
   * @return ArrayList of all voxels in VoxelArray
   */
  public ArrayList<Voxel> voxelList() {
    return voxelList;
  }
  
  /**
   * Get minimum X value
   * 
   * @return minX
   */
  public float minX() {
    return minX;
  }
  
  /**
   * Get maximum X value
   * 
   * @return maxX
   */
  public float maxX() {
    return maxX;
  }
  
  /**
   * Get minimum Y value
   * 
   * @return minY
   */
  public float minY() {
    return minY;
  }
  
  /**
   * Get maximum Y value
   * 
   * @return maxY
   */
  public float maxY() {
    return maxY;
  }

  /**
   * Returns true if VoxelArray contains Voxel
   * 
   * @param t Voxel we want to check for
   * @return true if VoxelArray contains Voxel t
   */
  public boolean hasVoxel(Voxel t) {
    return voxelList.contains(t);
  }
  
  /**
   * Does the VoxelArray have any voxels at all?
   * 
   * @return true if VoxelArray has any voxels
   */
  public boolean hasVoxels() {
    if (voxelList.size() > 0) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Returns true if a point is within the VoxelArray
   * 
   * @param x x-coordinate
   * @param y y-coordinate
   * @return true if (x, y) lies within one of the voxels in VoxelArray
   */
  public boolean pointInArray(float x, float y) {
    boolean inArray = false;
    for (Voxel t : voxelList) {
      float dX = Math.abs(t.location.x - x);
      float dY = Math.abs(t.location.y - y);
      if (dX < 0.51 * t.w && dY < 0.51 * t.w + 1) {
        inArray = true;
        break;
      }
    }
    return inArray;
  }

  /**
   * Clear All Voxels in VoxelArray
   */
  public void clearVoxels() {
    voxelMap.clear();
    voxelList.clear();
  }

  /**
   * Add Voxel to VoxelArray
   * 
   * @param t Voxel to add to Array
   */
  public void addVoxel(Voxel t) {
    Voxel copy = t.copy();
    voxelMap.put(copy.uniqueID, copy);
    voxelList.add(copy);
  }

  /**
   * Remove Voxel from VoxelArray
   * 
   * @param voxelKey Key value by which to look up Voxel in HashMap of Voxels in
   *                VoxelArray
   */
  public void removeVoxel(UUID id) {
    Voxel t = voxelMap.get(id);
    voxelList.remove(t);
    voxelMap.remove(id);
  }

  /**
   * Remove Voxel from VoxelArray
   * 
   * @param t The Voxel to Remove from VoxelArray
   */
  public void removeVoxel(Voxel t) {
    voxelList.remove(t);
    voxelMap.remove(t.uniqueID);
  }

  /**
   * Inherit the Voxels from a parent VoxelArray so that child voxels share the same
   * location in memory
   * 
   * @param parent A parent VoxelArray
   */
  public void inheritVoxels(VoxelArray parent) {
    for (Voxel t : parent.voxelList())
      addVoxel(t);
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
  public void makeVoxels(Polygon boundary, float voxelWidth, float voxelHeight, float rotation, Point translation, String type) {

    clearVoxels();

    // Create a field of grid points that is certain
    // to uniformly saturate polygon boundary

    // Polygon origin and rectangular bounding box extents
    float origin_x = (float) (0.5 * (boundary.xMax() + boundary.xMin()));
    float origin_y = (float) (0.5 * (boundary.yMax() + boundary.yMin()));
    float boundary_w = boundary.xMax() - boundary.xMin();
    float boundary_h = boundary.yMax() - boundary.yMin();
    
    // maximum additional bounding box dimensions if polygon is rotated 45 degrees
    float easement = (float) (Math.max(boundary_w, boundary_h) * (Math.sqrt(2) - 1));
    boundary_w += easement;
    boundary_h += easement;

    int U = (int) ((boundary_w / voxelWidth) + 1);
    int V = (int) ((boundary_h / voxelWidth) + 1);
    float t_x = translation.x % voxelWidth;
    float t_y = translation.y % voxelWidth;

    for (int u = 0; u < U; u++) {
      for (int v = 0; v < V; v++) {

        // grid coordinates before rotation is applied
        float x_0 = (float) (boundary.xMin() - 0.5 * easement + u * voxelWidth);
        float y_0 = (float) (boundary.yMin() - 0.5 * easement + v * voxelWidth);

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
          t.setRotation(rotation);
          t.setSize(voxelWidth, voxelHeight);
          t.setType(type);
          addVoxel(t);
        }
      }
    }
    calcMinMax();
  }

  /**
   * Returns a new VoxelArray with child voxels subtracted from parent
   * 
   * @param child VoxelArray to subtract from current VoxelArray
   * @return New VoxelArray with child subtracted from this VoxelArray
   */
  public VoxelArray getDiff(VoxelArray child) {
    VoxelArray diff = new VoxelArray();

    // Unless child voxel doesn't exists in parent voxel, add parent Voxel to new
    // VoxelArray
    diff.inheritVoxels(this);
    for (Voxel t : voxelList()) {
      if (child.hasVoxel(t)) {
        diff.removeVoxel(t);
      }
    }
    diff.calcMinMax();
    return diff;
  }

  /**
   * Returns a new VoxelArray with child voxels added to parent
   * 
   * @param child VoxelArray to add to current VoxelArray
   * @return New VoxelArray with child subtracted from this VoxelArray
   */
  public VoxelArray getSum(VoxelArray child) {
    VoxelArray add = new VoxelArray();

    // If parent voxel doesn't exists in child VoxelArray, add child Voxel to new
    // VoxelArray
    add.inheritVoxels(this);
    for (Voxel t : child.voxelList()) {
      if (!hasVoxel(t)) {
        add.addVoxel(t);
      }
    }
    add.calcMinMax();
    return add;
  }
  
  /**
   * update minimum and maximum extents of model
   */
  public void calcMinMax() {
    if (voxelList().size() > 0) {
      minX = Float.POSITIVE_INFINITY;
      maxX = Float.NEGATIVE_INFINITY;
      minY = Float.POSITIVE_INFINITY;
      maxY = Float.NEGATIVE_INFINITY;
      for (Voxel voxel : voxelList()) {
        minX = Math.min(minX, voxel.location.x);
        maxX = Math.max(maxX, voxel.location.x);
        minY = Math.min(minY, voxel.location.y);
        maxY = Math.max(maxY, voxel.location.y);
      }
    }
  }

  @Override
  public String toString() {
    return "Voxel Array [" + voxelMap.size() + "]";
  }
}
