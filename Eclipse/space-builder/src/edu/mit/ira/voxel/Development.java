package edu.mit.ira.voxel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * An entire development project; (i.e. a dictionary of TileArray spaces and
 * their ControlPoints)
 * 
 * @author ira
 *
 */
public class Development {

	public String name;

	// space and point dictionaries share the same key from TileArray.hashKey()
	// Dictionaries for collection of TileArrays that compose development
	private HashMap<String, TileArray> spaceMap;
	private ArrayList<TileArray> spaceList;

	/**
	 * Constructor for new Development
	 * 
	 * @param name Name of Development
	 */
	public Development(String name) {
		this.name = name;
		spaceMap = new HashMap<String, TileArray>();
		spaceList = new ArrayList<TileArray>();
	}

	/**
	 * Constructor for new Development
	 */
	public Development() {
		this("New Development");
	}

	/**
	 * Set Name of Development
	 * 
	 * @param name name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Set Name of Development
	 * 
	 * @param name name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return Space Map
	 * 
	 * @return HashMap of TileArray spaces within development
	 */
	public HashMap<String, TileArray> spaceMap() {
		return spaceMap;
	}

	/**
	 * Return Space List
	 * 
	 * @return ArrayList of TileArray spaces within development
	 */
	public ArrayList<TileArray> spaceList() {
		return spaceList;
	}

	/**
	 * Returns a subset of spaces according to their type
	 * 
	 * @param type Type of spaces to Return
	 * @return TileArray spaces of 'type'
	 */
	public ArrayList<TileArray> spaceList(String type) {
		ArrayList<TileArray> subset = new ArrayList<TileArray>();
		for (TileArray space : spaceList) {
			if (space.type.equals(type)) {
				subset.add(space);
			}
		}
		return subset;
	}

	/**
	 * Get Specific Space
	 * 
	 * @param hashKey hash key value of a TileArray space, same as
	 *                TileArray.hashKey()
	 * @return matching Tile Array Space
	 */
	public TileArray getSpace(String hashKey) {
		return spaceMap.get(hashKey);
	}

	/**
	 * Clear All Spaces from Development
	 */
	public void clearSpaces() {
		spaceMap.clear();
		spaceList.clear();
	}

	/**
	 * Remove all spaces of a certain type from development
	 * 
	 * @param type Type of space to remove
	 */
	public void clearType(String type) {
		ArrayList<TileArray> toClear = new ArrayList<TileArray>();

		// Populate list of spaces to clear, based on type
		for (TileArray space : spaceList) {
			if (space.type.equals(type)) {
				toClear.add(space);
			}
		}

		// Clear all spaces from Map and List dictionaries
		for (TileArray space : toClear) {
			spaceMap.remove(space.hashKey());
			spaceList.remove(space);
		}
	}

	/**
	 * Adds TileArray to Map and List dictionaries
	 * 
	 * @param space TileArray space to add
	 */
	public void addSpace(TileArray space) {
		spaceMap.put(space.hashKey(), space);
		spaceList.add(space);
	}

	@Override
	public String toString() {
		return this.name;
	}
}