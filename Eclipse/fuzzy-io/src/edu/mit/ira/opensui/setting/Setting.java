package edu.mit.ira.opensui.setting;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A SettingSchema defines the nature of input needed from the GUI
 * 
 * @author Ira Winder
 *
 */
public class Setting {
	public String type, label;
	public ArrayList<String> value, bounds;
	public ArrayList<Setting> settings;
	public Setting template;
	
	public Setting(GUI type, String label) {
		this(type.toString().toLowerCase(), label);
	}
	
	public Setting(String type, String label) {
		this.type = type;
		this.label = label;
		this.value = new ArrayList<String>();
		this.bounds = new ArrayList<String>();
		this.settings = new ArrayList<Setting>();
	}
	
	/**
	 * returns first child setting with matching label
	 * @param label
	 * @return
	 */
	public Setting find(String label) {
		for (Setting setting : settings) {
			if (setting.label.equals(label)) {
				return setting;
			}
		}
		for (Setting setting : settings) {
			Setting match = setting.find(label);
			if (match != null) return match;
		}
		System.out.println("No such setting: " + label);
		return null;
	}
	
	public float getFloat() {
		return Float.parseFloat(this.value.get(0));
	}
	
	public int getInt() {
		return Integer.parseInt(this.value.get(0));
	}
	
	public String getString() {
		return this.value.get(0);
	}
	
	public float[] getVector() {
		float[] vector = new float[value.size()];
		for (int m = 0; m < value.size(); m++) {
			vector[m] = Float.parseFloat(value.get(m));
		}
		return vector;
	}

	public JSONObject serialize() {
		
		JSONArray valueJSON = new JSONArray();
		for (int i = 0; i < value.size(); i++) {
			String val = value.get(i);
			valueJSON.put(i, val);
		}
		JSONArray boundsJSON = new JSONArray();
		for (int i = 0; i < bounds.size(); i++) {
			String bound = bounds.get(i);
			boundsJSON.put(i, bound);
		}
		JSONArray settingsJSON = new JSONArray();
		for (int i = 0; i < settings.size(); i++) {
			Setting setting = settings.get(i);
			settingsJSON.put(setting.serialize());
		}
		// Even though the template is a single SettingSchema object,
        // Unity's JSONUtility will only serialize it if it's in a List<> (ffs!)
		JSONArray templateJSON = new JSONArray();
		if(template != null) {
			templateJSON.put(0, template.serialize());
		}

		JSONObject settingJSON = new JSONObject();
		settingJSON.put("type", type);
		settingJSON.put("label", label);
		settingJSON.put("value", valueJSON);
		settingJSON.put("bounds", boundsJSON);
		settingJSON.put("settings", settingsJSON);
		settingJSON.put("template", templateJSON);
		return settingJSON;
	}
}