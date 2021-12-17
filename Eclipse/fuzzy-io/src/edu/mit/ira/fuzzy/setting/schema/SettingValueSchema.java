package edu.mit.ira.fuzzy.setting.schema;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A SettingValueSchema defines the nature of an input value needed from the GUI
 * 
 * @author Ira Winder
 *
 */
public class SettingValueSchema extends SettingSchema {
	public ArrayList<String> value, bounds;
	
	public SettingValueSchema(SchemaType type, String label) {
		super(type.toString(), label);
		value = new ArrayList<String>();
		bounds = new ArrayList<String>();
	}

	public JSONObject serialize() {

		JSONArray valueJSON = new JSONArray();
		for (int i = 0; i < this.value.size(); i++) {
			String val = this.value.get(i);
			valueJSON.put(i, val);
		}
		JSONArray boundsJSON = new JSONArray();
		for (int i = 0; i < this.bounds.size(); i++) {
			String bound = this.bounds.get(i);
			boundsJSON.put(i, bound);
		}

		JSONObject schema = new JSONObject();
		schema.put("type", type);
		schema.put("label", label);
		schema.put("value", valueJSON);
		schema.put("bounds", boundsJSON);
		return schema;
	}
}