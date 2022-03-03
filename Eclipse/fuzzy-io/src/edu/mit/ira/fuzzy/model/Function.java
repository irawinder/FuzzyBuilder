package edu.mit.ira.fuzzy.model;

/**
 * A collection of common use types in real estate
 *
 * @author Ira Winder
 *
 */
public enum Function {
	
	// LBCS Standard (https://www.planning.org/lbcs/standards/)
	
//	Residential("#CCCC00"),
//	Commercial("#CC0000"),
//	Manufacturing("#A020F0"),
//	Transportation("#BEBEBE"),
//	Entertainment("#90EE90"),
//	Institutional("#0000CC"),
//	Agriculture("#228B22"),
	
	// RED STUDIO 2016 Land & Floor Space Colors
	
	Residential("#fdde37"),
	//Water("#d1e7e8"),
	//Open_Space("#4f8554"),
	Institutional("#db499f"),
	Industrial("#9646c8"),
	Retail("#bf1921"),
	Office("#17629c"),
	Hotel("#3ca171"),
	Parking("#757575"),
	
	Unspecified("#CCCCCC");
	
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