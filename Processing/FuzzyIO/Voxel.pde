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
  
  public void setType(String type) {
    this.type = type;
  }
  
  public Voxel copy() {
    Voxel copy = new Voxel();
    copy.setLocation(location.x, location.y, location.z);
    copy.setRotation(rotation);
    copy.setSize(w, h);
    copy.setType(type);
    return copy;
  }

  @Override
  public String toString() {
    return type + " Voxel[" + type + "] at " + location;
  }
}
