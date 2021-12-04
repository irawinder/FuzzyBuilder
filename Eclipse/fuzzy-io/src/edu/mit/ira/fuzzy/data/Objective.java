package edu.mit.ira.fuzzy.data;

import org.json.JSONObject;

/**
 * A class for holding the objective performance of a solution
 *
 * @author Ira
 *
 */
public class Objective {
	private final String name, description, units;
	private final double value;
	private static final double DEFAULT_DECIMALS = 1000d;
	
	/**
	 * Instantiate an objective with a specified rounding goal
	 * @param name
	 * @param description
	 * @param value
	 * @param units
	 * @param decimals for example, a value of "1000d" will round the value to the nearest 3 decimal places
	 */
	public Objective(String name, String description, double value, String units, double decimals) {
		this.name = name;
		this.description = description;
		this.value = Math.round(decimals * value) / decimals;
		this.units = units;
	}
	
	/**
	 * Instantiate an objective; Rounds values to 3 decimal places by default
	 * @param name
	 * @param description
	 * @param value
	 * @param units
	 */
	public Objective(String name, String description, double value, String units) {
		this(name, description, value, units, DEFAULT_DECIMALS);
	}
	
	/**
	 * Serialize this object to JSON
	 * @return
	 */
	public JSONObject serialize() {
		JSONObject objective = new JSONObject();
		objective.put("name", this.name);
		objective.put("description", this.description);
		objective.put("value", this.value);
		objective.put("units", this.units);
		return objective;
	}
}