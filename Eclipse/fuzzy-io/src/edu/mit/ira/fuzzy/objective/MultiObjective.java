package edu.mit.ira.fuzzy.objective;

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
			primaryObjectivesJSON.put(i, objective);
		}
		JSONArray secondaryObjectivesJSON = new JSONArray();
		for(int i=0; i<this.secondaryObjectives.size(); i++) {
			Objective objective = this.secondaryObjectives.get(i);
			secondaryObjectivesJSON.put(i, objective);
		}
		JSONObject objectivesJSON = new JSONObject();
		objectivesJSON.put("primary", primaryObjectives);
		objectivesJSON.put("secondary", secondaryObjectives);
		return objectivesJSON;
	}
}