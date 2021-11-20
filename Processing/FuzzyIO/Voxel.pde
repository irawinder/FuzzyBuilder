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
  public float width, height;

  // Type of Voxel
  public String type;
  
  // Local integer coordinates of this voxel (for efficient adjacency analysis)
  public int u, v, w;

  /**
   * Constructs a default Voxel
   */
  public Voxel() {
    this.uniqueID = UUID.randomUUID();
    this.location = new Point();
    this.rotation = 0;
    this.width = 1;
    this.height = 1;
    this.type = null;
    this.u = 0;
    this.v = 0;
    this.w = 0;
  }
  
  public void setLocation(float x, float y, float z) {
    this.location = new Point(x, y, z);  
  }
  
  public void setCoordinates(int u, int v, int w) {
    this.u = u;
    this.v = v;
    this.w = w;
  }
  
  public void setRotation(float rotation) {
    this.rotation = rotation;
  }
  
  public void setSize(float width, float height) {
    this.width = width;
    this.height = height;
  }
  
  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return type + " Voxel [" + this.u + "," + this.v + "," + this.w + "] at " + location;
  }
}
