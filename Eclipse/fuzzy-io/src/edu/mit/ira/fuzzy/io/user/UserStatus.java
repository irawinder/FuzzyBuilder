package edu.mit.ira.fuzzy.io.user;

public enum UserStatus {
	ACTIVE("active"),
	INACTIVE("inactive");
	
	private String lc;
	
	private UserStatus(String lc) {
		this.lc = lc;
	}
	
	/**
	 * Get Lower Case Version of Enum
	 */
	public String lc() {
		return this.lc;
	}
}