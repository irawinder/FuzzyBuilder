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
   * Add Voxel to VoxelArray
   * 
   * @param t Voxel to add to Array
   */
  public void addVoxel(Voxel t) {
    if (!voxelList.contains(t)) {
      voxelMap.put(t.uniqueID, t);
      voxelList.add(t);
    }
  }

  /**
   * Remove Voxel from VoxelArray
   * 
   * @param voxelKey Key value by which to look up Voxel in HashMap of Voxels in
   *                VoxelArray
   */
  public void removeVoxel(UUID id) {
    if (voxelMap.containsKey(id)) {
      Voxel t = voxelMap.get(id);
      voxelList.remove(t);
      voxelMap.remove(id);
    }
  }

  /**
   * Remove Voxel from VoxelArray
   * 
   * @param t The Voxel to Remove from VoxelArray
   */
  public void removeVoxel(Voxel t) {
    if (voxelList.contains(t)) {
      voxelList.remove(t);
      voxelMap.remove(t.uniqueID);
    }
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
