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
		floorHeight.value.add("10"); // default
		floorHeight.bounds.add("10"); // min
		floorHeight.bounds.add("20"); // max

		SettingValueSchema cantilever = new SettingValueSchema(SchemaType.slider, "Cantilever Allowance [%]");
		cantilever.value.add("50"); // default
		cantilever.bounds.add("0"); // min
		cantilever.bounds.add("100"); // max
		
		SettingGroupExtendableSchema plots = new SettingGroupExtendableSchema("Parcels");
		SettingGroupSchema plot = new SettingGroupSchema("Parcel");
		plots.template = plot;
		
		SettingGroupExtendableSchema plotVertices = new SettingGroupExtendableSchema("Vertices");
		SettingValueSchema plotVertex = new SettingValueSchema(SchemaType.control_point, "Vertex");
		plotVertices.template = plotVertex;
		plotVertex.value.add("0"); // initial x
		plotVertex.value.add("0"); // initial y
		plotVertex.value.add("0"); // initial z
		plot.settings.add(plotVertices);
		
		// test the addition of default vertices
		//for(int i=0; i<3; i++) plotVertices.settings.add(plotVertex);
		
		SettingValueSchema gridSize = new SettingValueSchema(SchemaType.slider, "Grid Size [ft]");
		gridSize.value.add("10"); // default
		gridSize.bounds.add("10"); // min
		gridSize.bounds.add("50"); // max
		plot.settings.add(gridSize);

		SettingValueSchema gridRot = new SettingValueSchema(SchemaType.slider, "Grid Rotation [degrees]");
		gridRot.value.add("0"); // default
		gridRot.bounds.add("0"); // min
		gridRot.bounds.add("90"); // max
		plot.settings.add(gridRot);
		
		SettingGroupExtendableSchema podiums = new SettingGroupExtendableSchema("Podium Volumes");
		SettingGroupSchema podium = new SettingGroupSchema("Podium Volume");
		podiums.template = podium;
		plot.settings.add(podiums);

		SettingValueSchema setback = new SettingValueSchema(SchemaType.slider, "Setback [ft]");
		setback.value.add("0"); // default
		setback.bounds.add("0"); // min
		setback.bounds.add("200"); // max
		podium.settings.add(setback);
		
		SettingGroupExtendableSchema pZones = new SettingGroupExtendableSchema("Zones");
		SettingGroupSchema pZone = new SettingGroupSchema("Zone");
		pZones.template = pZone;
		podium.settings.add(pZones);

		SettingValueSchema pFloors = new SettingValueSchema(SchemaType.slider, "Floors [#]");
		pFloors.value.add("1"); // default
		pFloors.bounds.add("1"); // min
		pFloors.bounds.add("6"); // max
		pZone.settings.add(pFloors);

		SettingValueSchema pFunction = new SettingValueSchema(SchemaType.dropdown, "Function");
		pFunction.value.add(Function.Commercial.toString());
		for (Function function : Function.values())
			pFunction.bounds.add(function.toString());
		pZone.settings.add(pFunction);
		
		SettingGroupExtendableSchema towers = new SettingGroupExtendableSchema("Tower Volumes");
		SettingGroupSchema tower = new SettingGroupSchema("Tower Volume");
		towers.template = tower;

		SettingValueSchema tVertex = new SettingValueSchema(SchemaType.control_point, "Location");
		tVertex.value.add("0"); // initial x
		tVertex.value.add("0"); // initial y
		tVertex.value.add("0"); // initial z
		tower.settings.add(tVertex);

		SettingValueSchema tRot = new SettingValueSchema(SchemaType.slider, "Rotation [degrees]");
		tRot.value.add("0"); // default
		tRot.bounds.add("0"); // min
		tRot.bounds.add("180"); // max
		tower.settings.add(tRot);

		SettingValueSchema tWidth = new SettingValueSchema(SchemaType.slider, "Width [ft]");
		tWidth.value.add("100"); // default
		tWidth.bounds.add("100"); // min
		tWidth.bounds.add("1000"); // max
		tower.settings.add(tWidth);

		SettingValueSchema tDepth = new SettingValueSchema(SchemaType.slider, "Depth [ft]");
		tDepth.value.add("50"); // default
		tDepth.bounds.add("50"); // min
		tDepth.bounds.add("200"); // max
		tower.settings.add(tDepth);
		
		SettingGroupExtendableSchema tZones = new SettingGroupExtendableSchema("Zones");
		SettingGroupSchema tZone = new SettingGroupSchema("Zone");
		tZones.template = tZone;
		tower.settings.add(tZones);

		SettingValueSchema tFloors = new SettingValueSchema(SchemaType.slider, "Floors [#]");
		tFloors.value.add("1"); // default
		tFloors.bounds.add("1"); // min
		tFloors.bounds.add("40"); // max
		tZone.settings.add(tFloors);

		SettingValueSchema tFunction = new SettingValueSchema(SchemaType.dropdown, "Function");
		tFunction.value.add(Function.Residential.toString());
		for (Function function : Function.values())
			tFunction.bounds.add(function.toString());
		tZone.settings.add(tFunction);
		
		SettingGroupExtendableSchema openAreas = new SettingGroupExtendableSchema("Building Exclusion Areas");
		SettingGroupSchema openArea = new SettingGroupSchema("Area");
		openAreas.template = openArea;
		
		SettingGroupExtendableSchema openVertices = new SettingGroupExtendableSchema("Vertices");
		SettingValueSchema openVertex = new SettingValueSchema(SchemaType.control_point, "Vertex");
		openVertices.template = openVertex;
		openVertex.value.add("0"); // initial x
		openVertex.value.add("0"); // initial y
		openVertex.value.add("0"); // initial z
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