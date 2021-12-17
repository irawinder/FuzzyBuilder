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

		JSONObject schema = new JSONObject();
		schema.put("type", type);
		schema.put("label", label);
		schema.put("template", templateJSON);
		return schema;
	}
}