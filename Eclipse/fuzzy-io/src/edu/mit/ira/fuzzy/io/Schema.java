package edu.mit.ira.fuzzy.io;

import edu.mit.ira.fuzzy.model.Function;
import edu.mit.ira.opensui.setting.Configuration;
import edu.mit.ira.opensui.setting.GUI;
import edu.mit.ira.opensui.setting.Legend;
import edu.mit.ira.opensui.setting.Setting;
import edu.mit.ira.opensui.setting.Legend.Entry;

public class Schema {
	
	public final String UNIVERSE_NAME = "Site";
	
	public final String PARCELS = "Parcels";
	public final String PARCEL = "Parcel";
	public final String VERTICES = "Vertices";
	public final String VERTEX = "Vertex";
	public final String GRID_SIZE = "Grid Size [ft]";
	public final String GRID_ROTATION = "Grid Rotation [degrees]";
	public final String PODIUM_VOLUMES = "Podium Volumes";
	public final String PODIUM_VOLUME = "Podium Volume";
	public final String ORIENTATION = "Orientation";
	public final String SETBACK = "Setback [ft]";
	public final String ZONES = "Zones";
	public final String ZONE = "Zone";
	public final String FUNCTION = "Function";
	public final String FLOORS = "Floors [#]";
	public final String FLOOR_HEIGHT = "Floor Height [ft]";
	public final String TOWER_VOLUMES = "Tower Volumes";
	public final String TOWER_VOLUME = "Tower Volume";
	public final String LOCATION = "Location";
	public final String ROTATION = "Rotation [degrees]";
	public final String WIDTH = "Width [ft]";
	public final String DEPTH = "Depth [ft]";
	public final String AREAS = "Building Exclusion Areas";
	public final String AREA = "Area";
	public final String CANTILEVER = "Cantilever Allowance [%]";
	
	/**
	 * make and return the setting schema for this model
	 * @return
	 */
	public Configuration baseConfiguration(String apiVersion, String id, String author, String sponsor, String contact) {
		
		Configuration base = new Configuration(UNIVERSE_NAME, apiVersion, id, author, sponsor, contact);
		
		Setting plots = new Setting(GUI.GROUP_EXTENDABLE, PARCELS);
		Setting plot = new Setting(GUI.GROUP, PARCEL);
		plots.template = plot;
		
		Setting plotVertices = new Setting(GUI.GROUP_EXTENDABLE, VERTICES);
		Setting plotVertex = new Setting(GUI.CONTROL_POINT, VERTEX);
		plotVertices.template = plotVertex;
		plotVertex.value.add("0"); // initial x
		plotVertex.value.add("0"); // initial y
		plotVertex.value.add("0"); // initial z
		plot.settings.add(plotVertices);
		
		Setting gridSize = new Setting(GUI.SLIDER, GRID_SIZE);
		gridSize.value.add("30"); // default
		gridSize.bounds.add("15"); // min
		gridSize.bounds.add("50"); // max
		plot.settings.add(gridSize);

		Setting gridRot = new Setting(GUI.SLIDER, GRID_ROTATION);
		gridRot.value.add("45"); // default
		gridRot.bounds.add("0"); // min
		gridRot.bounds.add("90"); // max
		plot.settings.add(gridRot);
		
		Setting podiums = new Setting(GUI.GROUP_EXTENDABLE, PODIUM_VOLUMES);
		Setting podium = new Setting(GUI.GROUP, PODIUM_VOLUME);
		podiums.template = podium;
		podiums.settings.add(podium); // Add a podium volume by default
		plot.settings.add(podiums);
		
		Setting orientation = new Setting(GUI.DROPDOWN, ORIENTATION);
		orientation.value.add("Above Ground"); // default
		orientation.bounds.add("Above Ground");
		orientation.bounds.add("Below Ground");
		podium.settings.add(orientation);
		
		Setting setback = new Setting(GUI.SLIDER, SETBACK);
		setback.value.add("20"); // default
		setback.bounds.add("0"); // min
		setback.bounds.add("200"); // max
		podium.settings.add(setback);
		
		Setting pZones = new Setting(GUI.GROUP_EXTENDABLE, ZONES);
		Setting pZone = new Setting(GUI.GROUP, ZONE);
		pZones.template = pZone;
		pZones.settings.add(pZone); // add 1 zone by default
		podium.settings.add(pZones);
		
		Setting pFunction = new Setting(GUI.DROPDOWN, FUNCTION);
		pFunction.value.add(Function.Retail.toString());
		for (Function function : Function.values())
			pFunction.bounds.add(function.toString());
		pZone.settings.add(pFunction);
		
		Setting pFloors = new Setting(GUI.SLIDER, FLOORS);
		pFloors.value.add("1"); // default
		pFloors.bounds.add("1"); // min
		pFloors.bounds.add("10"); // max
		pZone.settings.add(pFloors);
		
		Setting pFloorHeight = new Setting(GUI.SLIDER, FLOOR_HEIGHT);
		pFloorHeight.value.add("10"); // default
		pFloorHeight.bounds.add("1"); // min
		pFloorHeight.bounds.add("50"); // max
		pZone.settings.add(pFloorHeight);
		
		Setting towers = new Setting(GUI.GROUP_EXTENDABLE, TOWER_VOLUMES);
		Setting tower = new Setting(GUI.GROUP, TOWER_VOLUME);
		towers.template = tower;

		Setting tVertex = new Setting(GUI.CONTROL_POINT, LOCATION);
		tVertex.value.add("0"); // initial x
		tVertex.value.add("0"); // initial y
		tVertex.value.add("0"); // initial z
		tower.settings.add(tVertex);

		Setting tRot = new Setting(GUI.SLIDER, ROTATION);
		tRot.value.add("90"); // default
		tRot.bounds.add("0"); // min
		tRot.bounds.add("180"); // max
		tower.settings.add(tRot);

		Setting tWidth = new Setting(GUI.SLIDER, WIDTH);
		tWidth.value.add("100"); // default
		tWidth.bounds.add("100"); // min
		tWidth.bounds.add("1000"); // max
		tower.settings.add(tWidth);

		Setting tDepth = new Setting(GUI.SLIDER, DEPTH);
		tDepth.value.add("50"); // default
		tDepth.bounds.add("50"); // min
		tDepth.bounds.add("200"); // max
		tower.settings.add(tDepth);
		
		Setting tZones = new Setting(GUI.GROUP_EXTENDABLE, ZONES);
		Setting tZone = new Setting(GUI.GROUP, ZONE);
		tZones.template = tZone;
		tZones.settings.add(tZone); // add 1 zone by default
		tower.settings.add(tZones);
		
		Setting tFunction = new Setting(GUI.DROPDOWN, FUNCTION);
		tFunction.value.add(Function.Residential.toString());
		for (Function function : Function.values())
			tFunction.bounds.add(function.toString());
		tZone.settings.add(tFunction);
		
		Setting tFloors = new Setting(GUI.SLIDER, FLOORS);
		tFloors.value.add("3"); // default
		tFloors.bounds.add("1"); // min
		tFloors.bounds.add("40"); // max
		tZone.settings.add(tFloors);
		
		Setting tFloorHeight = new Setting(GUI.SLIDER, FLOOR_HEIGHT);
		tFloorHeight.value.add("10"); // default
		tFloorHeight.bounds.add("10"); // min
		tFloorHeight.bounds.add("20"); // max
		tZone.settings.add(tFloorHeight);
		
		Setting openAreas = new Setting(GUI.GROUP_EXTENDABLE, AREAS);
		Setting openArea = new Setting(GUI.GROUP, AREA);
		openAreas.template = openArea;
		
		Setting openVertices = new Setting(GUI.GROUP_EXTENDABLE, VERTICES);
		Setting openVertex = new Setting(GUI.CONTROL_POINT, VERTEX);
		openVertices.template = openVertex;
		openVertex.value.add("0"); // initial x
		openVertex.value.add("0"); // initial y
		openVertex.value.add("0"); // initial z
		openArea.settings.add(openVertices);
		
		Setting cantilever = new Setting(GUI.SLIDER, CANTILEVER);
		cantilever.value.add("50"); // default
		cantilever.bounds.add("0"); // min
		cantilever.bounds.add("100"); // max
		
		// Add top-level settings to base Configuration
		base.settings.add(plots);
		base.settings.add(towers);
		base.settings.add(openAreas);
		base.settings.add(cantilever);
		
		// Create Legend
		Legend legend = new Legend();
		legend.label = "Function";
		for (Function function : Function.values()) {
			Entry entry = legend.new Entry();
			entry.label = function.toString();
			entry.color = function.legendColor();
			legend.entries.add(entry);
		}
		
		Entry plotEntry = legend.new Entry();
		plotEntry.label = "Plot";
		plotEntry.color = "#000000";
		legend.entries.add(plotEntry);
		
		Entry towerEntry = legend.new Entry();
		towerEntry.label = "Tower";
		towerEntry.color = "#00FF00";
		legend.entries.add(towerEntry);
		
		Entry openEntry = legend.new Entry();
		openEntry.label = "Open";
		openEntry.color = "#FF0000";
		legend.entries.add(openEntry);
		
		base.legend = legend;
		
		return base;
	}
}