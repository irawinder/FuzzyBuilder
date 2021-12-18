package edu.mit.ira.fuzzy.setting;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A Utility Class to deserialize setting data
 *
 * @author Ira
 *
 */
public class Deserializer {

	/**
	 * Convert a JSON string of model settings to the SettingGroup class
	 *
	 * @param data settings formatted as json string
	 * @return settings formatted as java object
	 */
	public Setting parse(String data) {
		JSONObject configJSON = new JSONObject(data);
		try {
			return this.adapt(configJSON);
		} catch (Exception e) {
			System.out.println("JSON data is not formatted correctly");
			return null;
		}
	}

	/**
	 * serialize settings to Model
	 *
	 * @param config data formatted as JSON
	 * @return data formatted as SettingGroup class
	 */
	private Setting adapt(JSONObject rootJSON) {
		
		String rootLabel = rootJSON.getString("label");
		String rootType = rootJSON.getString("type");
		Setting root = new Setting(rootType, rootLabel);
		
		// Add Children Settings
		JSONArray settings = rootJSON.getJSONArray("settings");
		for (int i = 0; i < settings.length(); i++) {
			JSONObject settingJSON = settings.getJSONObject(i);
			String settingLabel = settingJSON.getString("label");
			String settingType = settingJSON.getString("type");
			
			if (settingType.equals("group_extendable") || settingType.equals("group")) {
				
				// Parse Group
				Setting setting = adapt(settingJSON);
				root.settings.add(setting);
				
				// Parse Template
				if(settingType.equals("group_extendable")) {
					// Even though the template is a single Setting object,
			        // Unity's JSONUtility will only serialize it if it's in a List<> (ffs!)
					JSONObject templateJSON = settingJSON.getJSONArray("template").getJSONObject(0);
					Setting template = adapt(templateJSON);
					root.template = template;
				}

			
			} else {
				
				// Parse Value
				Setting setting = new Setting(settingType, settingLabel);
				setting.value = new ArrayList<String>();
				setting.bounds = new ArrayList<String>();
				JSONArray values = settingJSON.getJSONArray("value");
				for (int j = 0; j < values.length(); j++) {
					setting.value.add(values.getString(j));
				}
				JSONArray bounds = settingJSON.getJSONArray("bounds");
				for (int j = 0; j < bounds.length(); j++) {
					setting.bounds.add(bounds.getString(j));
				}
				root.settings.add(setting);
			}
		}
		return root;
	}
}