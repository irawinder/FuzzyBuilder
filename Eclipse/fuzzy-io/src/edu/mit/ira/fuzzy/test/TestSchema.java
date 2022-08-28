package edu.mit.ira.fuzzy.test;

import edu.mit.ira.fuzzy.io.Schema;
import edu.mit.ira.opensui.setting.Configuration;

public class TestSchema {

	public static void main(String[] args) throws Exception {

		Schema testSchema = new Schema();
		Configuration base = Schema.get("0.1", "Test", "Ira", "Mom", "no@email", true, true, true);
		System.out.println(base.serialize().toString(4));
	}
}