package edu.mit.ira.fuzzy.test;

import edu.mit.ira.fuzzy.io.Schema;
import edu.mit.ira.opensui.setting.Configuration;

public class TestSchema {

	public static void main(String[] args) throws Exception {

		Configuration base = Schema.get(true, true, true, true);
		System.out.println(base.serialize().toString(4));
	}
}