package edu.mit.ira.fuzzy.server.user;

public enum UserPrefixAdmin {
	SQUID("squid");
	
	private String lc;
	
	private UserPrefixAdmin(String lc) {
		this.lc = lc;
	}
	
	/**
	 * Get Lower Case Version of Enum
	 */
	public String lc() {
		return this.lc;
	}
}
