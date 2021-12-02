package edu.mit.ira.fuzzy.io;

public class FuzzyIO {
	private static FuzzyServer server;

	public static void main(String[] args) throws Exception {

		server = new FuzzyServer(8080);

	}
}