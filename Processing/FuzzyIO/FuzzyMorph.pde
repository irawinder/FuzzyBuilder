/**
 * FuzzyMorph is a class deicated to functional transformations of geometry
 */
class FuzzyMorph {
  
  // geographic height of ground
  private float GROUND_Z = 0;
  
  // local w (vertical) coordinate of ground
  private int GROUND_W = 0;
  
  /**
   * Inherit the Voxels from a input VoxelArray (child voxels are NOT cloned)
   * 
   * @param input A parent VoxelArray
   */
  public VoxelArray cloneVoxelArray(VoxelArray input) {
    VoxelArray arrayCopy = new VoxelArray();
    for (Voxel t : input.voxelList) {
      arrayCopy.addVoxel(t);
    }
    return arrayCopy;
  }
  
  /**
   * Inherit the Voxels from a input VoxelArray (child voxels ARE cloned)
   * 
   * @param input A parent VoxelArray
   */
  public VoxelArray hardCloneVoxelArray(VoxelArray input) {
    VoxelArray arrayCopy = new VoxelArray();
    for (Voxel t : input.voxelList) {
      Voxel clone = this.cloneVoxel(t);
      arrayCopy.addVoxel(clone);
    }
    return arrayCopy;
  }
  
  /**
   * Clone a voxel with a new UUID
   * 
   * @param input A parent Voxel
   */
  public Voxel cloneVoxel(Voxel input) {
    Voxel copy = new Voxel();
    copy.setLocation(input.location.x, input.location.y, input.location.z);
    copy.setCoordinates(input.u, input.v, input.w);
    copy.setRotation(input.rotation);
    copy.setSize(input.width, input.height);
    copy.setType(input.type);
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

        // rotate, then translate
        Point location = rotateXY(new Point(x_0, y_0), new Point(origin_x, origin_y), rotation);
        location.x += t_x;
        location.y += t_y;

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
   * Generate a vertical extrusion of an existing Voxel Array
   *
   * @param input VoxelArray to extrude
   * @param levels number of vertical voxel layers to extrude upward
   * @return extruded VoxelArray
   * 
   */
  public VoxelArray extrude(VoxelArray input, int levels) {
    VoxelArray topLayer = new VoxelArray();
    
    // Collect all non-covered voxels
    for(Voxel t : input.voxelList) {
      if(this.getNeighborTop(t, input) == null) {
        topLayer.addVoxel(t);
      }
    }
    
    // Create the extruded voxels
    VoxelArray extruded = new VoxelArray();
    for(int i=1; i<=levels; i++) {
      for(Voxel t : topLayer.voxelList) {
        Voxel verticalCopy = this.cloneVoxel(t);
        verticalCopy.setCoordinates(t.u, t.v, t.w + i);
        verticalCopy.setLocation(t.location.x, t.location.y, t.location.z + i * t.height);
        extruded.addVoxel(verticalCopy);
      }
    }
    
    // Add extruded voxels to input and return
    return this.add(input, extruded);
  }
  
  /**
   * Given an input VoxelArray, returns a new VoxelArray with outer ring of voxels removed
   * 
   * @param input voxel array to apply setback to
   * @return new VoxelArray with outer ring of voxels removed removed
   */
  private VoxelArray setback(VoxelArray input) {
    VoxelArray result = new VoxelArray();

    // Add tiles that are at edge of input TileArray
    for (Voxel t : input.voxelList) {
      // Tile surrounded on all sides has 8 neighbors
      if (this.getNeighborsUV(t, input).size() >= 7) {
        result.addVoxel(t);
      }
    }
    return result;
  }
  
  /**
   * Given an input VoxelArray, returns a new VoxelArray with outer ring of voxels removed
   * 
   * @param input voxel array to apply setback to
   * @param setbackDistance distance to apply setback ffrom edge
   * @return new VoxelArray with outer ring of voxels removed
   */
  public VoxelArray setback(VoxelArray input, float setbackDistance) {
    VoxelArray result = input;
    
    // Get the width of the first voxel in the array
    float voxelWidth;
    if (input.voxelList.size() > 0) {
      voxelWidth = input.voxelList.get(0).width;
    } else {
      return result;
    }
    
    // repeat offset as necessary to achieve desired distance
    int numSetbacks = int(0.5 + setbackDistance / voxelWidth);
    for(int i=0; i<numSetbacks; i++) result = this.setback(result);
    
    return result;
  }
  
  /**
   * Returns a new VoxelArray with child voxels subtracted from input
   * 
   * @param input VoxelArray to be subtracted from
   * @param child VoxelArray to subtract from input
   * @return New VoxelArray with child subtracted from this VoxelArray
   */
  public VoxelArray sub(VoxelArray input, VoxelArray child) {
    VoxelArray result = new VoxelArray();
    for (Voxel t : input.voxelList) {
      if (!child.voxelList.contains(t)) {
        result.addVoxel(t);
      }
    }
    return result;
  }

  /**
   * Returns a new VoxelArray with child voxels added to input
   * 
   * @param input VoxelArray to be added to
   * @param child VoxelArray to add to input
   * @return New VoxelArray with child subtracted from this VoxelArray
   */
  public VoxelArray add(VoxelArray input, VoxelArray toAdd) {
    VoxelArray result = this.cloneVoxelArray(input);
    for (Voxel t : toAdd.voxelList) {
      if (!input.voxelList.contains(t)) {
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
  public Voxel getNeighborBottom(Voxel t, VoxelArray tArray) { //<>//
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
  
  /**
   * Generate a rectangular polygon on the ground plane
   *
   * @param location center of rectangle
   * @param width
   * @param length
   * @param rotation
   * @return resulting rectangle
   */
  public Polygon rectangle(Point location, float width, float height, float rotation) {
    ArrayList<Point> corners = new ArrayList<Point>();
    corners.add(new Point(- 0.5, - 0.5));
    corners.add(new Point(+ 0.5, - 0.5));
    corners.add(new Point(+ 0.5, + 0.5));
    corners.add(new Point(- 0.5, + 0.5));
    
    Polygon rectangle = new Polygon();
    for(Point corner : corners) {
      corner.x = location.x + corner.x * width;
      corner.y = location.y + corner.y * height;
      corner = this.rotateXY(corner, location, rotation);
      rectangle.addVertex(corner);
    }
    return rectangle;
  }
  
  /**
   * Rotate a 2D point about a specified origin
   *
   * @param input point to rotate
   * @param origin frame of reference to rotate about
   * @param amount to rotate in radians
   * @return rotated point
   */
  private Point rotateXY(Point input, Point origin, float rotation) {
    float sin = (float) Math.sin(rotation);
    float cos = (float) Math.cos(rotation);
    float x_f = +(input.x - origin.x) * cos - (input.y - origin.y) * sin + origin.x;
    float y_f = +(input.x - origin.x) * sin + (input.y - origin.y) * cos + origin.y;
    return new Point(x_f, y_f);
  }
}
