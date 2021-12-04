package edu.mit.ira.fuzzy.test;

import edu.mit.ira.fuzzy.FuzzySchema;

public class TestSchema {

	public static void main(String[] args) throws Exception {

		FuzzySchema testSchema = new FuzzySchema("0.1", "Test");
		System.out.println(testSchema.serialize().toString(4));
	}
}