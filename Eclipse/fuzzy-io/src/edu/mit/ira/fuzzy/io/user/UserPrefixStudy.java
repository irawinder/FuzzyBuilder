package edu.mit.ira.fuzzy.io.user;

public enum UserPrefixStudy {
	ZEBRA("zebra"),
	COBRA("cobra"),
	PANDA("panda");
	
	private String lc;
	
	private UserPrefixStudy(String lc) {
		this.lc = lc;
	}
	
	/**
	 * Get Lower Case Version of Enum
	 */
	public String lc() {
		return this.lc;
	}
}
