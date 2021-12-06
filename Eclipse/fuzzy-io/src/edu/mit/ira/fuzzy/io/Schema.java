package edu.mit.ira.fuzzy.io;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.mit.ira.fuzzy.model.Function;
import edu.mit.ira.fuzzy.setting.schema.SchemaType;
import edu.mit.ira.fuzzy.setting.schema.SettingGroupSchema;
import edu.mit.ira.fuzzy.setting.schema.SettingValueSchema;

public class Schema {

	private String apiVersion;
	private String id;
	private String author;
	private String contact;
	private String sponsor;
	private String universeName;

	public Schema(String apiVersion, String id, String author, String sponsor, String contact) {
		this.apiVersion = apiVersion;
		this.id = id;
		this.author = author;
		this.sponsor = sponsor;
		this.contact = contact;
		this.universeName = "";
	}

	public JSONObject serialize() {
		JSONArray settings = this.settings();
		JSONObject legend = this.legend();
		JSONObject schema = new JSONObject();
		schema.put("apiVersion", apiVersion);
		schema.put("id", id);
		schema.put("label", universeName);
		schema.put("author", author);
		schema.put("sponsor", sponsor);
		schema.put("contact", contact);
		schema.put("settings", settings);
		schema.put("legend", legend);
		return schema;
	}

	/**
	 * make and return the setting schema for this model
	 * @return
	 */
	public JSONArray settings() {

		this.universeName = "Site";

		SettingValueSchema floorHeight = new SettingValueSchema(SchemaType.slider, "Floor Height", false);
		floorHeight.values.add("10"); // default
		floorHeight.values.add("10"); // min
		floorHeight.values.add("20"); // max

		SettingValueSchema cantilever = new SettingValueSchema(SchemaType.slider, "Cantilever Allowance", false);
		cantilever.values.add("50"); // default
		cantilever.values.add("0"); // min
		cantilever.values.add("100"); // max

		SettingGroupSchema plot = new SettingGroupSchema("Plot", true);

		SettingValueSchema plotVertex = new SettingValueSchema(SchemaType.control_point, "Vertex", true);
		plotVertex.values.add("0"); // initial x
		plotVertex.values.add("0"); // initial y
		plotVertex.values.add("0"); // initial z
		plot.settings.add(plotVertex);

		SettingValueSchema gridSize = new SettingValueSchema(SchemaType.slider, "Grid Size", false);
		gridSize.values.add("10"); // default
		gridSize.values.add("10"); // min
		gridSize.values.add("50"); // max
		plot.settings.add(gridSize);

		SettingValueSchema gridRot = new SettingValueSchema(SchemaType.slider, "Grid Rotation", false);
		gridRot.values.add("0"); // default
		gridRot.values.add("0"); // min
		gridRot.values.add("90"); // max
		plot.settings.add(gridRot);

		SettingGroupSchema podium = new SettingGroupSchema("Podium Volume", true);
		plot.settings.add(podium);

		SettingValueSchema setback = new SettingValueSchema(SchemaType.slider, "Setback", false);
		setback.values.add("0"); // default
		setback.values.add("0"); // min
		setback.values.add("200"); // max
		podium.settings.add(setback);

		SettingGroupSchema openArea = new SettingGroupSchema("Open Area", true);
		podium.settings.add(openArea);

		SettingValueSchema openVertex = new SettingValueSchema(SchemaType.control_point, "Vertex", true);
		openVertex.values.add("0"); // initial x
		openVertex.values.add("0"); // initial y
		openVertex.values.add("0"); // initial z
		openArea.settings.add(openVertex);

		SettingGroupSchema pZone = new SettingGroupSchema("Zone", true);
		podium.settings.add(pZone);

		SettingValueSchema pFloors = new SettingValueSchema(SchemaType.slider, "Floors", false);
		pFloors.values.add("1"); // default
		pFloors.values.add("1"); // min
		pFloors.values.add("6"); // max
		pZone.settings.add(pFloors);

		SettingValueSchema pFunction = new SettingValueSchema(SchemaType.dropdown, "Function", false);
		for (Function function : Function.values())
			pFunction.values.add(function.toString());
		pZone.settings.add(pFunction);

		SettingGroupSchema tower = new SettingGroupSchema("Tower Volume", true);
		plot.settings.add(tower);

		SettingValueSchema tVertex = new SettingValueSchema(SchemaType.control_point, "Location", false);
		tVertex.values.add("0"); // initial x
		tVertex.values.add("0"); // initial y
		tVertex.values.add("0"); // initial z
		tower.settings.add(tVertex);

		SettingValueSchema tRot = new SettingValueSchema(SchemaType.slider, "Rotation", false);
		tRot.values.add("0"); // default
		tRot.values.add("0"); // min
		tRot.values.add("180"); // max
		tower.settings.add(tRot);

		SettingValueSchema tWidth = new SettingValueSchema(SchemaType.slider, "Width", false);
		tWidth.values.add("100"); // default
		tWidth.values.add("100"); // min
		tWidth.values.add("1000"); // max
		tower.settings.add(tWidth);

		SettingValueSchema tDepth = new SettingValueSchema(SchemaType.slider, "Depth", false);
		tDepth.values.add("50"); // default
		tDepth.values.add("50"); // min
		tDepth.values.add("200"); // max
		tower.settings.add(tDepth);

		SettingGroupSchema tZone = new SettingGroupSchema("Zone", true);
		tower.settings.add(tZone);

		SettingValueSchema tFloors = new SettingValueSchema(SchemaType.slider, "Floors", false);
		tFloors.values.add("1"); // default
		tFloors.values.add("1"); // min
		tFloors.values.add("40"); // max
		tZone.settings.add(tFloors);

		SettingValueSchema tFunction = new SettingValueSchema(SchemaType.dropdown, "Function", false);
		for (Function function : Function.values())
			tFunction.values.add(function.toString());
		tZone.settings.add(tFunction);

		JSONArray settings = new JSONArray();
		settings.put(0, floorHeight.serialize());
		settings.put(1, cantilever.serialize());
		settings.put(2, plot.serialize());
		return settings;
	}

	/**
	 * Return the legend of colors for this model
	 * @return
	 */
	JSONObject legend() {
		JSONArray functionLegend = new JSONArray();
		int i = 0;
		for (Function function : Function.values()) {
			JSONObject entry = new JSONObject();
			entry.put("label", function.toString());
			entry.put("color", function.legendColor());
			functionLegend.put(i, entry);
			i++;
		}
		JSONObject legend = new JSONObject();
		legend.put("label", "Function");
		legend.put("entries", functionLegend);
		return legend;
	}
}