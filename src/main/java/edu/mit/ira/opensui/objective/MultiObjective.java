package edu.mit.ira.opensui.objective;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A class for holding the objective performance of a solution
 *
 * @author Ira
 *
 */
public class MultiObjective {
	public ArrayList<Objective> primaryObjectives; // always exist from one model to the next
	public ArrayList<Objective> secondaryObjectives; // fluctuate from one model to the next
	
	/**
	 * Instantiate a list of objectives
	 */
	public MultiObjective() {
		this.primaryObjectives = new ArrayList<Objective>();
		this.secondaryObjectives = new ArrayList<Objective>();
	}
	
	/**
	 * Serialize this object to JSON
	 * @return
	 */
	public JSONObject serialize() {
		JSONArray primaryObjectivesJSON = new JSONArray();
		for(int i=0; i<this.primaryObjectives.size(); i++) {
			Objective objective = this.primaryObjectives.get(i);
			primaryObjectivesJSON.put(i, objective.serialize());
		}
		JSONArray secondaryObjectivesJSON = new JSONArray();
		for(int i=0; i<this.secondaryObjectives.size(); i++) {
			Objective objective = this.secondaryObjectives.get(i);
			secondaryObjectivesJSON.put(i, objective.serialize());
		}
		JSONObject objectivesJSON = new JSONObject();
		objectivesJSON.put("primary", primaryObjectivesJSON);
		objectivesJSON.put("secondary", secondaryObjectivesJSON);
		return objectivesJSON;
	}
	
	/**
	 * Serialize this object to CSV
	 * @return
	 */
	public String toCSV() {
		String result = "Name, Description, Value, Units\n";
		for(int i=0; i<this.primaryObjectives.size(); i++) {
			Objective objective = this.primaryObjectives.get(i);
			result += objective.toCSV() + "\n";
		}
		for(int i=0; i<this.secondaryObjectives.size(); i++) {
			Objective objective = this.secondaryObjectives.get(i);
			result += objective.toCSV() + "\n";
		}
		return result;
	}
	
	public String getLogHeader() {
		String headers = "";
		for(int i=0; i<this.primaryObjectives.size(); i++) {
			Objective objective = this.primaryObjectives.get(i);
			headers += objective.name + " [" + objective.units +  "]\t";
		}
		for(int i=0; i<this.secondaryObjectives.size(); i++) {
			Objective objective = this.secondaryObjectives.get(i);
			headers += objective.name + " [" + objective.units +  "]\t";
		}
		headers = headers.substring(0, headers.length()-1);
		return headers;
	}
	
	/**
	 * Serialize this object to line in log file
	 * @return
	 */
	public String getLogRow() {
		String result = "";
		for(int i=0; i<this.primaryObjectives.size(); i++) {
			Objective objective = this.primaryObjectives.get(i);
			result += objective.value + "\t";
		}
		for(int i=0; i<this.secondaryObjectives.size(); i++) {
			Objective objective = this.secondaryObjectives.get(i);
			result += objective.value + "\t";
		}
		return result;
	}
}