package edu.mit.ira.fuzzy.model;

/**
 * A collection of common use types in real estate
 *
 * @author Ira Winder
 *
 */
public enum Function {
	
	// LBCS Standard (https://www.planning.org/lbcs/standards/)
	
	Residential("#FFFF00"),
	Commercial("#FF0000"),
	Manufacturing("#A020F0"),
	Transportation("#BEBEBE"),
	Entertainment("#90EE90"),
	Institutional("#0000FF"),
	Agriculture("#228B22"),
	Unspecified("#FFFFFF");
	
	private String legendColor;
	
	private Function(String color) {
		this.legendColor = color;
	}
	
	/**
	 * Get the hexidecimal color associated with this use
	 * @return
	 */
	public String legendColor() {
		return this.legendColor;
	}
}