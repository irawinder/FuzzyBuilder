package edu.mit.ira.fuzzy.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Development is a collection of Polygons and VoxelArrays, usually generated by
 * FuzzyBuilder.build(SettingGroup) or FuzzyRandom.development()
 *
 * @author Ira Winder
 *
 */
public class Development {
	public ArrayList<Polygon> plotShapes;
	public HashMap<Polygon, String> plotNames;
	public HashMap<Polygon, ArrayList<Polygon>> openShapes;
	public HashMap<Polygon, ArrayList<Polygon>> towerShapes;
	public ArrayList<Polygon> allShapes;
	public HashMap<Polygon, VoxelArray> plotSite, plotMassing;
	public VoxelArray site, massing, allVoxels, hollowed;
	public String error;
	
	/**
	 * A collection of polygons and VoxelArrays that compose a development
	 */
	public Development() {
		this.plotShapes = new ArrayList<Polygon>();
		this.plotNames = new HashMap<Polygon, String>();
		this.openShapes = new HashMap<Polygon, ArrayList<Polygon>>();
		this.towerShapes = new HashMap<Polygon, ArrayList<Polygon>>();
		this.allShapes = new ArrayList<Polygon>();
		this.plotSite = new HashMap<Polygon, VoxelArray>();
		this.plotMassing = new HashMap<Polygon, VoxelArray>();
		this.site = new VoxelArray();
		this.massing = new VoxelArray();
		this.allVoxels = new VoxelArray();
		this.hollowed = new VoxelArray();
		this.error = null;
	}

	/**
	 * Serialize this development for export
	 * 
	 * @return
	 */
	public JSONObject serialize() {

		JSONArray voxelsJSON = this.hollowed.serialize();
		JSONArray shapesJSON = new JSONArray();
		for (int i = 0; i < this.allShapes.size(); i++) {
			Polygon shape = this.allShapes.get(i);
			shapesJSON.put(i, shape.serialize());
		}

		JSONObject data = new JSONObject();
		data.put("voxels", voxelsJSON);
		data.put("shapes", shapesJSON);
		data.put("feedback", this.feedback());
		return data;
	}
	
	private String feedback() {
		if (error != null) {
			return error;
		}
		
		if(this.site.voxelList.size() == 0) {
			return "You must draw a parcel before any massing is generated.\nParcels must have at least 3 vertices.";
		} else if (this.massing.voxelList.size() == 0) {
			return "You must add zones to a podium volume or tower volume.\nTower volumes must be placed within a parcel.";
		}
		
		return this.allVoxels.voxelList.size() + " voxels generated by FuzzyIO";
	}
}