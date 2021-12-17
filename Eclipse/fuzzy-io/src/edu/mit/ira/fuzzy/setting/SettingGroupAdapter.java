package edu.mit.ira.fuzzy.setting;

import org.json.JSONArray;
import org.json.JSONObject;

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
	 * @return settings formatted as SettingGRoup class
	 */
	public SettingGroup parse(String data) {
		// JSONObject settingGroupJSON = parseJSONObject(data);
		JSONObject settingGroupJSON = new JSONObject(data);
		try {
			return this.adapt(settingGroupJSON);
		} catch (Exception e) {
			System.out.println("JSON data is not formatted correctly");
			return new SettingGroup();
		}
	}

	/**
	 * serialize a JSONObject to SettingGroup class
	 *
	 * @param settingGroup data formatted as JSON
	 * @return data formatted as SettingGroup class
	 */
	private SettingGroup adapt(JSONObject settingGroup) {
		SettingGroup group = new SettingGroup();

		// Check for type group
		if (settingGroup.getString("type").equals("group")) {
			group.type = settingGroup.getString("type");
			group.label = settingGroup.getString("name");

			// Add SettingValues Associated with group
			JSONArray settingValues = settingGroup.getJSONArray("settingValues");
			for (int i = 0; i < settingValues.length(); i++) {
				JSONObject settingValue = settingValues.getJSONObject(i);
				SettingValue value = new SettingValue();
				value.label = settingValue.getString("name");
				value.type = settingValue.getString("type");
				value.value = settingValue.getString("value");
				group.settingValues.add(value);
			}

			// Add Child SettingGroups attached to Group
			JSONArray settingGroups = settingGroup.getJSONArray("settingGroups");
			for (int i = 0; i < settingGroups.length(); i++) {
				JSONObject childSettingGroup = settingGroups.getJSONObject(i);
				SettingGroup childGroup = adapt(childSettingGroup);
				group.settingGroups.add(childGroup);
			}
		} else {
			System.out.println("type must be group");
		}
		return group;
	}
}