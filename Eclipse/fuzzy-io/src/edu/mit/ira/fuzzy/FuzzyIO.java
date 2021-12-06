package edu.mit.ira.fuzzy;

import edu.mit.ira.fuzzy.io.Server;

public class FuzzyIO {
	
	private static final int PORT = 8080;
	
	public static void main(String[] args) throws Exception {
		new Server(PORT);
	}
}