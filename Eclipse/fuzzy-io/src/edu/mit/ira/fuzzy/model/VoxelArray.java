package edu.mit.ira.fuzzy.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A VoxelArray is a collection of voxels
 * 
 * @author ira
 * 
 */
public class VoxelArray {

	// Collection of Voxels
	public HashMap<String, Voxel> voxelMap;
	public ArrayList<Voxel> voxelList;

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
	 *                 VoxelArray
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
	public void setVoxelUse(Function type) {
		for (Voxel voxel : voxelList) {
			voxel.setUse(type);
		}
	}

	/**
	 * Set the height of all voxels
	 *
	 * @param type
	 */
	public void setVoxelHeight(float height) {
		for (Voxel voxel : voxelList) {
			voxel.setSize(voxel.width, height);
		}
	}

	/**
	 * Get the maximum height coordinate, w, of any Voxel
	 */
	public int maxW() {
		int maxW = 0;
		for (Voxel t : this.voxelList) {
			if (maxW < t.w) {
				maxW = t.w;
			}
		}
		return maxW;
	}

	/**
	 * Get the minimum height coordinate, w, of any Voxel
	 */
	public int minW() {
		int minW = +1000000;
		for (Voxel t : this.voxelList) {
			if (minW > t.w) {
				minW = t.w;
			}
		}
		return minW;
	}
	
	/**
	 * Get the maximum height, z, of any Voxel
	 */
	public float maxZ() {
		float maxZ = 0;
		for (Voxel t : this.voxelList) {
			if (maxZ < t.location.z) {
				maxZ = t.location.z;
			}
		}
		return maxZ;
	}
	
	/**
	 * Get the peak height of this voxel array, including the height from the top-most voxel
	 */
	public float peakZ() {
		float peakZ = 0;
		for (Voxel t : this.voxelList) {
			if (peakZ < t.location.z + t.height) {
				peakZ = t.location.z + t.height;
			}
		}
		return peakZ;
	}

	/**
	 * Get the minimum height, z, of any Voxel
	 */
	public float minZ() {
		float minZ = +1000000;
		for (Voxel t : this.voxelList) {
			if (minZ > t.location.z) {
				minZ = t.location.z;
			}
		}
		return minZ;
	}

	/**
	 * Get the width of the first voxel. Zero if Empty
	 */
	public float voxelWidth() {
		if (this.voxelList.size() > 0) {
			return this.voxelList.get(0).width;
		} else {
			return 0;
		}
	}

	/**
	 * Get the height of the first voxel. Zero if Empty
	 */
	public float voxelHeight() {
		if (this.voxelList.size() > 0) {
			return this.voxelList.get(0).height;
		} else {
			return 0;
		}
	}

	@Override
	public String toString() {
		return "Voxel Array [" + voxelMap.size() + "]";
	}

	public JSONArray serialize() {
		JSONArray voxelArrayJSON = new JSONArray();
		for (int i = 0; i < this.voxelList.size(); i++) {
			JSONObject voxelJSON = this.voxelList.get(i).serialize();
			voxelArrayJSON.put(i, voxelJSON);
		}
		return voxelArrayJSON;
	}
}