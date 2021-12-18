package edu.mit.ira.fuzzy.setting;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.mit.ira.fuzzy.setting.schema.SchemaType;
import edu.mit.ira.fuzzy.setting.schema.SettingGroupSchema;
import edu.mit.ira.fuzzy.setting.schema.SettingValueSchema;

/**
 * A Utility Class to deserialize setting data
 *
 * @author Ira
 *
 */
public class SettingGroupAdapter {

	/**
	 * Convert a JSON string of model settings to the SettingGroup class
	 *
	 * @param data settings formatted as json string
	 * @return settings formatted as java object
	 */
	public SettingGroupSchema parse(String data) {
		JSONObject configurationJSON = new JSONObject(data);
		try {
			return this.adapt(configurationJSON);
		} catch (Exception e) {
			System.out.println("JSON data is not formatted correctly");
			return new SettingGroupSchema("null");
		}
	}

	/**
	 * serialize a JSONObject to SettingGroup class
	 *
	 * @param config data formatted as JSON
	 * @return data formatted as SettingGroup class
	 */
	private SettingGroupSchema adapt(JSONObject config) {
		
		String rootLabel = config.getString("label");
		SettingGroupSchema root = new SettingGroupSchema(rootLabel);
		
		// Add Children Settings
		JSONArray settings = config.getJSONArray("settings");
		for (int i = 0; i < settings.length(); i++) {
			JSONObject setting = settings.getJSONObject(i);
			String settingType = setting.getString("type");
			String settingLabel = setting.getString("label");
			
			if (settingType.equals("group_extendable") || settingType.equals("group")) {
				
				// Parse Group
				SettingGroupSchema schema = adapt(setting);
				root.settings.add(schema);

			
			} else {
				
				// Parse Value
				SettingValueSchema schema = new SettingValueSchema(SchemaType.valueOf(settingType), settingLabel);
				schema.value = new ArrayList<String>();
				schema.bounds = new ArrayList<String>();
				JSONArray values = setting.getJSONArray("value");
				for (int j = 0; j < values.length(); j++) {
					schema.value.add(values.getString(j));
				}
				JSONArray bounds = setting.getJSONArray("bounds");
				for (int j = 0; j < bounds.length(); j++) {
					schema.bounds.add(bounds.getString(j));
				}
				root.settings.add(schema);
			}
		}
		return root;
	}
}