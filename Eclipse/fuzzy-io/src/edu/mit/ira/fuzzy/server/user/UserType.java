package edu.mit.ira.fuzzy.server.user;

public enum UserType {
	ADMIN("admin"),
	STUDY("study");
	
	private String lc;
	
	private UserType(String lc) {
		this.lc = lc;
	}
	
	/**
	 * Get Lower Case Version of Enum
	 */
	public String lc() {
		return this.lc;
	}
}
