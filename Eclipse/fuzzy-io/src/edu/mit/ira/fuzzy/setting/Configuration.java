package edu.mit.ira.fuzzy.setting;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Configuration {
	private String apiVersion;
	private String id;
	private String author;
	private String contact;
	private String sponsor;
	private String universeName;
	public ArrayList<Setting> settings;
	public Legend legend;

	public Configuration(String universe, String apiVersion, String id, String author, String sponsor, String contact) {
		this.universeName = universe;
		this.apiVersion = apiVersion;
		this.id = id;
		this.author = author;
		this.sponsor = sponsor;
		this.contact = contact;
		this.universeName = "";
		this.settings = new ArrayList<Setting>();
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
		configJSON.put("label", universeName);
		configJSON.put("type", GUI.GROUP.toString().toLowerCase());
		configJSON.put("author", author);
		configJSON.put("sponsor", sponsor);
		configJSON.put("contact", contact);
		configJSON.put("settings", settingsJSON);
		configJSON.put("legend", legend.serialize());
		return configJSON;
	}
}