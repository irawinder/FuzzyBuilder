package edu.mit.ira.fuzzy;

import edu.mit.ira.fuzzy.server.Server;

public class Main {
	
	private static final int PORT = 2222;
	
	public static void main(String[] args) throws Exception {
		
		new Server(PORT);
		
	}
}