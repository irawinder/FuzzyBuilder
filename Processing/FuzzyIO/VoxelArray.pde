/**
 * A VoxelArray is a collection of voxels
 * 
 * @author ira
 * 
 */
public class VoxelArray {

  // Collection of Voxels
  private HashMap<String, Voxel> voxelMap;
  private ArrayList<Voxel> voxelList;
  
  /**
   * Construct Empty VoxelArray
   * 
   * @param name Name of VoxelArray
   * @param type Type of VoxelArray
   */
  public VoxelArray() {
    this.voxelMap = new HashMap<String, Voxel>();
    this.voxelList = new ArrayList<Voxel>();
  }

  /**
   * Add Voxel to VoxelArray
   * 
   * @param t Voxel to add to Array
   */
  public void addVoxel(Voxel t) {
    if (!voxelList.contains(t)) {
      voxelMap.put(t.coordKey(), t);
      voxelList.add(t);
    }
  }

  /**
   * Remove Voxel from VoxelArray
   * 
   * @param voxelKey Key value by which to look up Voxel in HashMap of Voxels in
   *                VoxelArray
   */
  public void removeVoxel(String coordKey) {
    if (voxelMap.containsKey(coordKey)) {
      Voxel t = voxelMap.get(coordKey);
      voxelList.remove(t);
      voxelMap.remove(t.coordKey());
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
      voxelMap.remove(t.coordKey());
    }
  }
  
  /**
   * Set the type of all voxels
   *
   * @param type
   */
  public void setVoxelUse(Use type) {
    for(Voxel voxel : voxelList) {
      voxel.setUse(type);  
    }
  }
  
  /**
   * Set the height of all voxels
   *
   * @param type
   */
  public void setVoxelHeight(float height) {
    for(Voxel voxel : voxelList) {
      voxel.setSize(voxel.width, height);  
    }
  }
  
  /**
   * Get the maximum height coordinate, w,  of any Voxel
   */
  public int maxW() {
    int maxW = 0;
    for(Voxel t : this.voxelList) {
      if (maxW < t.w) {
        maxW = t.w;
      }
    }
    return maxW;
  }
  
  /**
   * Get the minimum height coordinate, w,  of any Voxel
   */
  public int minW() {
    int minW = +1000000;
    for(Voxel t : this.voxelList) {
      if (minW > t.w) {
        minW = t.w;
      }
    }
    return minW;
  }

  @Override
  public String toString() {
    return "Voxel Array [" + voxelMap.size() + "]";
  }
}
