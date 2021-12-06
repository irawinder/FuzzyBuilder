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
	public ArrayList<String> values;

	/**
	 * 
	 * @param type       usually refers to the type of GUI element needed (e.g.
	 *                   slider)
	 * @param label      the friendly name of the setting
	 * @param extendable whether or not this setting should be the element of an
	 *                   arbitrary list of such settings
	 */
	public SettingValueSchema(SchemaType type, String label, boolean extendable) {
		super(type.toString(), label, extendable);
		values = new ArrayList<String>();
	}

	public JSONObject serialize() {

		JSONArray valuesJSON = new JSONArray();
		for (int i = 0; i < this.values.size(); i++) {
			String value = this.values.get(i);
			valuesJSON.put(i, value);
		}

		JSONObject schema = new JSONObject();
		schema.put("type", type);
		schema.put("label", label);
		schema.put("extendable", extendable);
		schema.put("values", valuesJSON);
		return schema;
	}
}