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

	public SettingGroupSchema(String label) {
		super(SchemaType.group.toString(), label);
		settings = new ArrayList<SettingSchema>();
	}

	public JSONObject serialize() {

		JSONArray settingsJSON = new JSONArray();
		for (int i = 0; i < this.settings.size(); i++) {
			SettingSchema settingSchema = this.settings.get(i);
			JSONObject settingJSON = null;
			if(settingSchema instanceof SettingValueSchema) {
				settingJSON = ((SettingValueSchema) settingSchema).serialize();
			} else if (settingSchema instanceof SettingGroupSchema) {
				settingJSON = ((SettingGroupSchema) settingSchema).serialize();
			} else if (settingSchema instanceof SettingGroupExtendableSchema) {
				settingJSON = ((SettingGroupExtendableSchema) settingSchema).serialize();
				
			}
			if (settingJSON != null) {
				settingsJSON.put(i, settingJSON);
			}
		}

		JSONObject schema = new JSONObject();
		schema.put("type", type);
		schema.put("label", label);
		schema.put("settings", settingsJSON);
		return schema;
	}
}