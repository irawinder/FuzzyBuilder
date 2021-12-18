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
	public Configuration parse(String data) {
		JSONObject configJSON = new JSONObject(data);
		try {
			String label = configJSON.getString("label");
			String apiVersion = configJSON.getString("apiVersion");
			String id = configJSON.getString("id");
			String author = configJSON.getString("author");
			String contact = configJSON.getString("contact");
			String sponsor = configJSON.getString("sponsor");
			Configuration config = new Configuration(label, apiVersion, id, author, sponsor, contact);
			JSONArray settingsJSON = configJSON.getJSONArray("settings");
			for(int i=0; i<settingsJSON.length(); i++) {
				JSONObject settingJSON = settingsJSON.getJSONObject(i);
				config.settings.add(parseSetting(settingJSON));
			}
			return config;
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
	private Setting parseSetting(JSONObject settingJSON) {
		String settingLabel = settingJSON.getString("label");
		String settingType = settingJSON.getString("type");
		Setting setting = new Setting(settingType, settingLabel);
		
		if (settingType.equals("group_extendable") || settingType.equals("group")) {
			
			// Parse Group
			JSONArray childrenSettings = settingJSON.getJSONArray("settings");
			for(int i=0; i<childrenSettings.length(); i++) {
				JSONObject childSettingJSON = childrenSettings.getJSONObject(i);
				Setting childSetting = parseSetting(childSettingJSON);
				setting.settings.add(childSetting);
	
				// Parse Template
				if (settingType.equals("group_extendable")) {
					// Even though the template is a single Setting object,
					// Unity's JSONUtility will only serialize it if it's in a List<> (ffs!)
					JSONObject templateJSON = settingJSON.getJSONArray("template").getJSONObject(0);
					setting.template = parseSetting(templateJSON);
				}
			}

		} else {

			// Parse Value
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
		}
		return setting;
	}
}