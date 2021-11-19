/**
 * A Voxel is a primitive particle of space with a square base
 * 
 * @author ira
 * 
 */
public class Voxel {
  
  // Unique identifier for this voxel
  final private UUID uniqueID;
  
  // Center Point of Voxel in real "geospatial" coordinates
  public Point location;
  
  // Rotation of voxel along Z axis, in radians
  public float rotation;
  
  // Width and Height of voxel
  public float w, h;

  // Type of Voxel
  public String type;

  // Adjacent Voxels
  public ArrayList<Voxel> neighbors; // 0 - 8 horizontal neighbors
  public Voxel above, below;

  /**
   * Constructs a default Voxel
   */
  public Voxel() {
    this.uniqueID = UUID.randomUUID();
    this.location = new Point();
    this.rotation = 0;
    this.w = 1;
    this.h = 1;
    this.type = null;
    this.neighbors = new ArrayList<Voxel>();
    this.above = null;
    this.below = null;
  }
  
  public void setLocation(float x, float y, float z) {
    this.location = new Point(x, y, z);  
  }
  
  public void setRotation(float rotation) {
    this.rotation = rotation;
  }
  
  public void setSize(float w, float h) {
    this.w = w;
    this.h = h;
  }

  public void addNeighbor(Voxel neighbor) {
    neighbors.add(neighbor);
  }

  @Override
  public String toString() {
    return type + " Voxel[" + type + "] at " + location;
  }
}
