package edu.mit.ira.fuzzy.io;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.mit.ira.fuzzy.model.Function;
import edu.mit.ira.fuzzy.setting.schema.SchemaType;
import edu.mit.ira.fuzzy.setting.schema.SettingGroupExtendableSchema;
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

		SettingValueSchema floorHeight = new SettingValueSchema(SchemaType.slider, "Floor Height [ft]");
		floorHeight.values.add("10"); // default
		floorHeight.values.add("10"); // min
		floorHeight.values.add("20"); // max

		SettingValueSchema cantilever = new SettingValueSchema(SchemaType.slider, "Cantilever Allowance [%]");
		cantilever.values.add("50"); // default
		cantilever.values.add("0"); // min
		cantilever.values.add("100"); // max
		
		SettingGroupExtendableSchema plots = new SettingGroupExtendableSchema("Parcels");
		SettingGroupSchema plot = new SettingGroupSchema("Parcel");
		plots.template = plot;
		
		SettingGroupExtendableSchema plotVertices = new SettingGroupExtendableSchema("Vertices");
		SettingValueSchema plotVertex = new SettingValueSchema(SchemaType.control_point, "Vertex");
		plotVertices.template = plotVertex;
		plotVertex.values.add("0"); // initial x
		plotVertex.values.add("0"); // initial y
		plotVertex.values.add("0"); // initial z
		plot.settings.add(plotVertices);
		
		SettingValueSchema gridSize = new SettingValueSchema(SchemaType.slider, "Grid Size [ft]");
		gridSize.values.add("10"); // default
		gridSize.values.add("10"); // min
		gridSize.values.add("50"); // max
		plot.settings.add(gridSize);

		SettingValueSchema gridRot = new SettingValueSchema(SchemaType.slider, "Grid Rotation [degrees]");
		gridRot.values.add("0"); // default
		gridRot.values.add("0"); // min
		gridRot.values.add("90"); // max
		plot.settings.add(gridRot);
		
		SettingGroupExtendableSchema podiums = new SettingGroupExtendableSchema("Podium Volumes");
		SettingGroupSchema podium = new SettingGroupSchema("Podium Volume");
		podiums.template = podium;
		plot.settings.add(podiums);

		SettingValueSchema setback = new SettingValueSchema(SchemaType.slider, "Setback [ft]");
		setback.values.add("0"); // default
		setback.values.add("0"); // min
		setback.values.add("200"); // max
		podium.settings.add(setback);
		
		SettingGroupExtendableSchema pZones = new SettingGroupExtendableSchema("Zones");
		SettingGroupSchema pZone = new SettingGroupSchema("Zone");
		pZones.template = pZone;
		podium.settings.add(pZones);

		SettingValueSchema pFloors = new SettingValueSchema(SchemaType.slider, "Floors [#]");
		pFloors.values.add("1"); // default
		pFloors.values.add("1"); // min
		pFloors.values.add("6"); // max
		pZone.settings.add(pFloors);

		SettingValueSchema pFunction = new SettingValueSchema(SchemaType.dropdown, "Function");
		for (Function function : Function.values())
			pFunction.values.add(function.toString());
		pZone.settings.add(pFunction);
		
		SettingGroupExtendableSchema towers = new SettingGroupExtendableSchema("Tower Volumes");
		SettingGroupSchema tower = new SettingGroupSchema("Tower Volume");
		towers.template = tower;

		SettingValueSchema tVertex = new SettingValueSchema(SchemaType.control_point, "Location");
		tVertex.values.add("0"); // initial x
		tVertex.values.add("0"); // initial y
		tVertex.values.add("0"); // initial z
		tower.settings.add(tVertex);

		SettingValueSchema tRot = new SettingValueSchema(SchemaType.slider, "Rotation [degrees]");
		tRot.values.add("0"); // default
		tRot.values.add("0"); // min
		tRot.values.add("180"); // max
		tower.settings.add(tRot);

		SettingValueSchema tWidth = new SettingValueSchema(SchemaType.slider, "Width [ft]");
		tWidth.values.add("100"); // default
		tWidth.values.add("100"); // min
		tWidth.values.add("1000"); // max
		tower.settings.add(tWidth);

		SettingValueSchema tDepth = new SettingValueSchema(SchemaType.slider, "Depth [ft]");
		tDepth.values.add("50"); // default
		tDepth.values.add("50"); // min
		tDepth.values.add("200"); // max
		tower.settings.add(tDepth);
		
		SettingGroupExtendableSchema tZones = new SettingGroupExtendableSchema("Zones");
		SettingGroupSchema tZone = new SettingGroupSchema("Zone");
		tZones.template = tZone;
		tower.settings.add(tZones);

		SettingValueSchema tFloors = new SettingValueSchema(SchemaType.slider, "Floors [#]");
		tFloors.values.add("1"); // default
		tFloors.values.add("1"); // min
		tFloors.values.add("40"); // max
		tZone.settings.add(tFloors);

		SettingValueSchema tFunction = new SettingValueSchema(SchemaType.dropdown, "Function");
		for (Function function : Function.values())
			tFunction.values.add(function.toString());
		tZone.settings.add(tFunction);
		
		SettingGroupExtendableSchema openAreas = new SettingGroupExtendableSchema("Building Exclusion Areas");
		SettingGroupSchema openArea = new SettingGroupSchema("Area");
		openAreas.template = openArea;
		
		SettingGroupExtendableSchema openVertices = new SettingGroupExtendableSchema("Vertices");
		SettingValueSchema openVertex = new SettingValueSchema(SchemaType.control_point, "Vertex");
		openVertices.template = openVertex;
		openVertex.values.add("0"); // initial x
		openVertex.values.add("0"); // initial y
		openVertex.values.add("0"); // initial z
		openArea.settings.add(openVertices);
		
		JSONArray settings = new JSONArray();
		settings.put(0, floorHeight.serialize());
		settings.put(1, cantilever.serialize());
		settings.put(2, plots.serialize());
		settings.put(3, towers.serialize());
		settings.put(4, openAreas.serialize());
		
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