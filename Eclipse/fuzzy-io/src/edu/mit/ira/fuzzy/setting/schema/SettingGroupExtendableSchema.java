package edu.mit.ira.fuzzy.setting.schema;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A SettingGroupSchema defines the nature of group input needed from the GUI
 * 
 * @author Ira Winder
 *
 */
public class SettingGroupExtendableSchema extends SettingGroupSchema {
	public SettingSchema template;

	public SettingGroupExtendableSchema(String label) {
		super(label);
		this.type = SchemaType.group_extendable.toString();
	}
	
	public void extend() {
		
	}

	public JSONObject serialize() {
		
		// Even though the template is a single SettingSchema object,
        // Unity's JSONUtility will only serialize it if it's in a List<> (ffs!)
		JSONArray templateJSON = new JSONArray();
		if (template instanceof SettingValueSchema) {
			templateJSON.put(0, ((SettingValueSchema) template).serialize());
		} else if (template instanceof SettingGroupSchema) {
			templateJSON.put(0, ((SettingGroupSchema) template).serialize());
		} else if (template instanceof SettingGroupExtendableSchema) {
			templateJSON.put(0, ((SettingGroupExtendableSchema) template).serialize());
		}
		
		// Add any existing/default settings to the schema
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
		schema.put("template", templateJSON);
		return schema;
	}
}