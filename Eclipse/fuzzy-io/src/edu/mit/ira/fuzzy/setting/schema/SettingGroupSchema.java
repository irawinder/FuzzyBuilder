package edu.mit.ira.fuzzy.setting.schema;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A SettingGroupSchema defines the nature of group input needed from the GUI
 * 
 * @author Ira Winder
 *
 */
public class SettingGroupSchema extends SettingSchema {
	public ArrayList<SettingSchema> settings;

	public SettingGroupSchema(String label, boolean extendable) {
		super(SchemaType.group.toString(), label, extendable);
		settings = new ArrayList<SettingSchema>();
	}

	public JSONObject serialize() {

		JSONArray settingsJSON = new JSONArray();
		for (int i = 0; i < this.settings.size(); i++) {
			SettingSchema settingSchema = this.settings.get(i);
			if(settingSchema instanceof SettingValueSchema) {
				JSONObject value = ((SettingValueSchema) settingSchema).serialize();
				settingsJSON.put(i, value);
			} else if (settingSchema instanceof SettingGroupSchema) {
				JSONObject group = ((SettingGroupSchema) settingSchema).serialize();
				settingsJSON.put(i, group);
			}
		}

		JSONObject schema = new JSONObject();
		schema.put("type", type);
		schema.put("label", label);
		schema.put("extendable", extendable);
		schema.put("settings", settingsJSON);
		return schema;
	}
}