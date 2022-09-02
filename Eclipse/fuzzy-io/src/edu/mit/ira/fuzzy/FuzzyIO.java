package edu.mit.ira.fuzzy;

import edu.mit.ira.fuzzy.io.Server;

public class FuzzyIO {
	
	private static final String NAME = "FuzzyIO";
	private static final String VERSION = "v1.3.16";
	private static final String AUTHOR = "Ira Winder, Daniel Fink, and Max Walker";
	private static final String SPONSOR = "MIT Center for Real Estate";
	private static final String CONTACT = "fuzzy-io@mit.edu";
	
	private static final int PORT = 8080;
	public static final String RELATIVE_DATA_PATH = "data";
	
	public static void main(String[] args) throws Exception {
		new Server(NAME, VERSION, AUTHOR, SPONSOR, CONTACT, PORT);
	}
}