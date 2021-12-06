package edu.mit.ira.fuzzy.test;

import edu.mit.ira.fuzzy.io.Schema;

public class TestSchema {

	public static void main(String[] args) throws Exception {

		Schema testSchema = new Schema("0.1", "Test", "Ira", "Mom", "no@email");
		System.out.println(testSchema.serialize().toString(4));
	}
}