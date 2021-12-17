package edu.mit.ira.fuzzy.setting.schema;

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

		JSONObject templateJSON = new JSONObject();
		if (template instanceof SettingValueSchema) {
			templateJSON = ((SettingValueSchema) template).serialize();
		} else if (template instanceof SettingGroupSchema) {
			templateJSON = ((SettingGroupSchema) template).serialize();
		} else if (template instanceof SettingGroupExtendableSchema) {
			templateJSON = ((SettingGroupExtendableSchema) template).serialize();
		}

		JSONObject schema = new JSONObject();
		schema.put("type", type);
		schema.put("label", label);
		schema.put("template", templateJSON);
		return schema;
	}
}