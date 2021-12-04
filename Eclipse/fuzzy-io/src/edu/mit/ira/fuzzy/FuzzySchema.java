package edu.mit.ira.fuzzy;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.mit.ira.fuzzy.data.Schema;
import edu.mit.ira.fuzzy.data.SettingGroupSchema;
import edu.mit.ira.fuzzy.data.SettingValueSchema;

public class FuzzySchema {
	
	private final String API_VERSION = "0.1";
	private final String ID = "FuzzyIO";
	private final String universeName;
	
	public FuzzySchema() {
		this.universeName = "Site";
	}
	
	public JSONObject serialize() {
		JSONArray settings = this.settings();
		JSONObject schema = new JSONObject();
		schema.put("apiVersion", API_VERSION);
		schema.put("id", ID);
		schema.put("label", universeName);
		schema.put("settings", settings);
		return schema;
	}
	
	/**
	 * 
	 * @return
	 */
	public JSONArray settings() {
		
		SettingValueSchema floorHeight = new SettingValueSchema(Schema.slider, "Floor Height", false);
		floorHeight.values.add("10"); // default
		floorHeight.values.add("10"); // min
		floorHeight.values.add("20"); // max
		
		SettingValueSchema cantilever = new SettingValueSchema(Schema.slider, "Cantilever Allowance", false);
		cantilever.values.add("50");  // default
		cantilever.values.add("0");   // min
		cantilever.values.add("100"); // max
		
		SettingGroupSchema plot = new SettingGroupSchema("Plot", true);
		
		SettingValueSchema plotVertex = new SettingValueSchema(Schema.control_point, "Vertex", true);
		plotVertex.values.add("0");   // initial x
		plotVertex.values.add("0");   // initial y
		plotVertex.values.add("0");   // initial z
		plot.settings.add(plotVertex);
		
		SettingValueSchema gridSize = new SettingValueSchema(Schema.slider, "Grid Size", false);
		gridSize.values.add("10");    // default
		gridSize.values.add("10");    // min
		gridSize.values.add("50");    // max
		plot.settings.add(gridSize);
		
		SettingValueSchema gridRot = new SettingValueSchema(Schema.slider, "Grid Rotation", false);
		gridRot.values.add("0");      // default
		gridRot.values.add("0");      // min
		gridRot.values.add("90");     // max
		plot.settings.add(gridRot);
		
		SettingGroupSchema podium = new SettingGroupSchema("Podium Volume", true);
		plot.settings.add(podium);
		
		SettingValueSchema setback = new SettingValueSchema(Schema.slider, "Setback", false);
		setback.values.add("0");      // default
		setback.values.add("0");      // min
		setback.values.add("200");    // max
		podium.settings.add(setback);
		
		SettingGroupSchema openArea = new SettingGroupSchema("Open Area", true);
		podium.settings.add(openArea);
		
		SettingValueSchema openVertex = new SettingValueSchema(Schema.control_point, "Vertex", true);
		openVertex.values.add("0");   // initial x
		openVertex.values.add("0");   // initial y
		openVertex.values.add("0");   // initial z
		openArea.settings.add(openVertex);
		
		SettingGroupSchema pZone = new SettingGroupSchema("Zone", true);
		podium.settings.add(pZone);
		
		SettingValueSchema pFloors = new SettingValueSchema(Schema.slider, "Floors", false);
		pFloors.values.add("1");  	  // default
		pFloors.values.add("1");      // min
		pFloors.values.add("6");     // max
		pZone.settings.add(pFloors);
		
		SettingValueSchema pUse = new SettingValueSchema(Schema.dropdown, "Use Type", false);
		for (Use use : Use.values()) pUse.values.add(use.toString());
		pZone.settings.add(pUse);
		
		SettingGroupSchema tower = new SettingGroupSchema("Tower Volume", true);
		plot.settings.add(tower);
		
		SettingValueSchema tVertex = new SettingValueSchema(Schema.control_point, "Location", false);
		tVertex.values.add("0");      // initial x
		tVertex.values.add("0");      // initial y
		tVertex.values.add("0");      // initial z
		tower.settings.add(tVertex);
		
		SettingValueSchema tRot = new SettingValueSchema(Schema.slider, "Rotation", false);
		tRot.values.add("0");  	      // default
		tRot.values.add("0");         // min
		tRot.values.add("180");       // max
		tower.settings.add(tRot);
		
		SettingValueSchema tWidth = new SettingValueSchema(Schema.slider, "Width", false);
		tWidth.values.add("100");  	  // default
		tWidth.values.add("100");     // min
		tWidth.values.add("1000");    // max
		tower.settings.add(tWidth);
		
		SettingValueSchema tDepth = new SettingValueSchema(Schema.slider, "Depth", false);
		tDepth.values.add("50");  	  // default
		tDepth.values.add("50");      // min
		tDepth.values.add("200");     // max
		tower.settings.add(tDepth);
		
		SettingGroupSchema tZone = new SettingGroupSchema("Zone", true);
		tower.settings.add(tZone);
		
		SettingValueSchema tFloors = new SettingValueSchema(Schema.slider, "Floors", false);
		tFloors.values.add("1");  	  // default
		tFloors.values.add("1");      // min
		tFloors.values.add("40");     // max
		tZone.settings.add(tFloors);
		
		SettingValueSchema tUse = new SettingValueSchema(Schema.dropdown, "Use Type", false);
		for (Use use : Use.values()) pUse.values.add(use.toString());
		tZone.settings.add(tUse);
		
		JSONArray settings = new JSONArray();
		settings.put(0, floorHeight.serialize());
		settings.put(1, cantilever.serialize());
		settings.put(2, plot.serialize());
		return settings;
	}
}