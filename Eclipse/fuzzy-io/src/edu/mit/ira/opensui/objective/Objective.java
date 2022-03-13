package edu.mit.ira.opensui.objective;

import org.json.JSONObject;

/**
 * A class for holding the objective performance of a solution
 *
 * @author Ira
 *
 */
public class Objective {
	public String name, description, units;
	public float value;
	
	/**
	 * Instantiate an objective
	 * @param name
	 * @param description
	 * @param value
	 * @param units
	 * @param decimals for example, a value of "1000d" will round the value to the nearest 3 decimal places
	 */
	public Objective(String name, String description, float value, String units) {
		this.name = name;
		this.description = description;
		this.value = value;
		this.units = units;
	}
	
	/**
	 * Serialize this object to JSON
	 * @return
	 */
	public JSONObject serialize() {
		double decimals = 100d;
		double _value = Math.round(decimals * this.value) / decimals;
		JSONObject objective = new JSONObject();
		objective.put("name", this.name);
		objective.put("description", this.description);
		objective.put("value", _value);
		objective.put("units", this.units);
		return objective;
	}
	
	/**
	 * Serialize this object to CSV
	 * @return
	 */
	public String toCSV() {
		double decimals = 100d;
		double _value = Math.round(decimals * this.value) / decimals;
		return this.name + "," + this.description + "," + _value + "," + this.units;
	}
}