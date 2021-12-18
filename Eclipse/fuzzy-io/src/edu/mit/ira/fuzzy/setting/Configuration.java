package edu.mit.ira.fuzzy.setting;

import org.json.JSONArray;
import org.json.JSONObject;

public class Configuration extends Setting {
	private String apiVersion, id, author, sponsor, contact;
	public Legend legend;
	
	/**
	 * An object that contains all settings and schema for a Scenario
	 * @param universe
	 * @param apiVersion
	 * @param id
	 * @param author
	 * @param sponsor
	 * @param contact
	 */
	public Configuration(String label, String apiVersion, String id, String author, String sponsor, String contact) {
		super(GUI.GROUP, label);
		this.apiVersion = apiVersion;
		this.id = id;
		this.author = author;
		this.sponsor = sponsor;
		this.contact = contact;
		this.legend = new Legend();
	}

	public JSONObject serialize() {
		JSONArray settingsJSON = new JSONArray();
		for(int i=0; i<settings.size(); i++) {
			Setting setting = settings.get(i);
			JSONObject settingJSON = setting.serialize();
			settingsJSON.put(i, settingJSON);
		}
		JSONObject configJSON = new JSONObject();
		configJSON.put("apiVersion", apiVersion);
		configJSON.put("id", id);
		configJSON.put("author", author);
		configJSON.put("sponsor", sponsor);
		configJSON.put("contact", contact);
		configJSON.put("label", label);
		configJSON.put("type", type);
		configJSON.put("settings", settingsJSON);
		configJSON.put("legend", legend.serialize());
		return configJSON;
	}
}