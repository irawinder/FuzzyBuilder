package edu.mit.ira.fuzzy.survey;

public enum SurveyType {
	ENTRY("entry"),
	EXIT("exit");
	
	private String lc;
	
	private SurveyType(String lc) {
		this.lc = lc;
	}
	
	/**
	 * Get Lower Case Version of Enum
	 */
	public String lc() {
		return this.lc;
	}
}
