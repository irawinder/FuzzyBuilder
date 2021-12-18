package edu.mit.ira.fuzzy.io;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.mit.ira.fuzzy.model.Function;
import edu.mit.ira.fuzzy.setting.Setting;

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
		schema.put("type", "group");
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

		Setting floorHeight = new Setting("slider", "Floor Height [ft]");
		floorHeight.value.add("10"); // default
		floorHeight.bounds.add("10"); // min
		floorHeight.bounds.add("20"); // max

		Setting cantilever = new Setting("slider", "Cantilever Allowance [%]");
		cantilever.value.add("50"); // default
		cantilever.bounds.add("0"); // min
		cantilever.bounds.add("100"); // max
		
		Setting plots = new Setting("group_extendable", "Parcels");
		Setting plot = new Setting("group", "Parcel");
		plots.template = plot;
		
		Setting plotVertices = new Setting("group_extendable", "Vertices");
		Setting plotVertex = new Setting("control_point", "Vertex");
		plotVertices.template = plotVertex;
		plotVertex.value.add("0"); // initial x
		plotVertex.value.add("0"); // initial y
		plotVertex.value.add("0"); // initial z
		plot.settings.add(plotVertices);
		
		// test the addition of default vertices
		//for(int i=0; i<3; i++) plotVertices.settings.add(plotVertex);
		
		Setting gridSize = new Setting("slider", "Grid Size [ft]");
		gridSize.value.add("10"); // default
		gridSize.bounds.add("10"); // min
		gridSize.bounds.add("50"); // max
		plot.settings.add(gridSize);

		Setting gridRot = new Setting("slider", "Grid Rotation [degrees]");
		gridRot.value.add("0"); // default
		gridRot.bounds.add("0"); // min
		gridRot.bounds.add("90"); // max
		plot.settings.add(gridRot);
		
		Setting podiums = new Setting("group_extendable", "Podium Volumes");
		Setting podium = new Setting("group", "Podium Volume");
		podiums.template = podium;
		plot.settings.add(podiums);

		Setting setback = new Setting("slider", "Setback [ft]");
		setback.value.add("0"); // default
		setback.bounds.add("0"); // min
		setback.bounds.add("200"); // max
		podium.settings.add(setback);
		
		Setting pZones = new Setting("group_extendable", "Zones");
		Setting pZone = new Setting("group", "Zone");
		pZones.template = pZone;
		podium.settings.add(pZones);

		Setting pFloors = new Setting("slider", "Floors [#]");
		pFloors.value.add("1"); // default
		pFloors.bounds.add("1"); // min
		pFloors.bounds.add("6"); // max
		pZone.settings.add(pFloors);

		Setting pFunction = new Setting("dropdown", "Function");
		pFunction.value.add(Function.Commercial.toString());
		for (Function function : Function.values())
			pFunction.bounds.add(function.toString());
		pZone.settings.add(pFunction);
		
		Setting towers = new Setting("group_extendable", "Tower Volumes");
		Setting tower = new Setting("group", "Tower Volume");
		towers.template = tower;

		Setting tVertex = new Setting("control_point", "Location");
		tVertex.value.add("0"); // initial x
		tVertex.value.add("0"); // initial y
		tVertex.value.add("0"); // initial z
		tower.settings.add(tVertex);

		Setting tRot = new Setting("slider", "Rotation [degrees]");
		tRot.value.add("0"); // default
		tRot.bounds.add("0"); // min
		tRot.bounds.add("180"); // max
		tower.settings.add(tRot);

		Setting tWidth = new Setting("slider", "Width [ft]");
		tWidth.value.add("100"); // default
		tWidth.bounds.add("100"); // min
		tWidth.bounds.add("1000"); // max
		tower.settings.add(tWidth);

		Setting tDepth = new Setting("slider", "Depth [ft]");
		tDepth.value.add("50"); // default
		tDepth.bounds.add("50"); // min
		tDepth.bounds.add("200"); // max
		tower.settings.add(tDepth);
		
		Setting tZones = new Setting("group_extendable", "Zones");
		Setting tZone = new Setting("group", "Zone");
		tZones.template = tZone;
		tower.settings.add(tZones);

		Setting tFloors = new Setting("slider", "Floors [#]");
		tFloors.value.add("1"); // default
		tFloors.bounds.add("1"); // min
		tFloors.bounds.add("40"); // max
		tZone.settings.add(tFloors);

		Setting tFunction = new Setting("dropdown", "Function");
		tFunction.value.add(Function.Residential.toString());
		for (Function function : Function.values())
			tFunction.bounds.add(function.toString());
		tZone.settings.add(tFunction);
		
		Setting openAreas = new Setting("group_extendable", "Building Exclusion Areas");
		Setting openArea = new Setting("group", "Area");
		openAreas.template = openArea;
		
		Setting openVertices = new Setting("group_extendable", "Vertices");
		Setting openVertex = new Setting("control_point", "Vertex");
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