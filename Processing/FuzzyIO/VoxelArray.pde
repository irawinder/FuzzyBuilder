/**
 * A VoxelArray is a collection of voxels
 * 
 * @author ira
 * 
 */
public class VoxelArray {
  
  // Unique identifier for this voxel
  final private UUID uniqueID;

  // Collection of Voxels
  private HashMap<UUID, Voxel> voxelMap;
  private ArrayList<Voxel> voxelList;
  
  /**
   * Construct Empty VoxelArray
   * 
   * @param name Name of VoxelArray
   * @param type Type of VoxelArray
   */
  public VoxelArray() {
    this.uniqueID = UUID.randomUUID();
    this.voxelMap = new HashMap<UUID, Voxel>();
    this.voxelList = new ArrayList<Voxel>();
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
    voxelMap.put(t.uniqueID,t);
    voxelList.add(t);
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
   * Set the type of all voxels
   *
   * @param type
   */
  public void setType(String type) {
    for(Voxel voxel : voxelList) {
      voxel.setType(type);  
    }
  }

  @Override
  public String toString() {
    return "Voxel Array [" + voxelMap.size() + "]";
  }
}
