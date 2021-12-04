package edu.mit.ira.fuzzy.data;

/**
 * A SettingSchema defines the nature of input needed from the GUI
 * 
 * @author Ira Winder
 *
 */
public class SettingSchema {
	protected String type, label;
	protected boolean extendable;
	
	public SettingSchema(String type, String label, boolean extendable) {
		this.type = type;
		this.label = label;
		this.extendable = extendable;
	}
}