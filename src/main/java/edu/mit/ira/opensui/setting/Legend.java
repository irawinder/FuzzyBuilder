package edu.mit.ira.opensui.setting;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Legend {
	
	public String label;
	public ArrayList<Entry> entries;
	
	public Legend() {
		this.label = "";
		this.entries = new ArrayList<Entry>();
	}
	
	public class Entry {
		
		public String label, color;
		
		public Entry() {
			this.label = "";
			this.color = "";
		}
		
		public JSONObject serialize() {
			JSONObject entryJSON = new JSONObject();
			entryJSON.put("label", this.label);
			entryJSON.put("color", this.color);
			return entryJSON;
		}
	}
	
	public JSONObject serialize() {
		JSONArray entriesJSON = new JSONArray();
		for (int i=0; i<entries.size(); i++) {
			Entry entry = entries.get(i);
			entriesJSON.put(i, entry.serialize());
		}
		JSONObject legend = new JSONObject();
		legend.put("label", label);
		legend.put("entries", entriesJSON);
		return legend;
	}
}