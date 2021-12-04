package edu.mit.ira.fuzzy.test;

import edu.mit.ira.fuzzy.Extrusion;
import edu.mit.ira.fuzzy.Point;
import edu.mit.ira.fuzzy.Polygon;

public class TestExtrusion {

	public static void main(String[] args) throws Exception {

		Polygon base1 = new Polygon();
		base1.addVertex(new Point(1, 0, 0));
		base1.addVertex(new Point(0, 1, 0));
		base1.addVertex(new Point(1, 1, 0));
		System.out.println("Pass Coplanar Test #1: " + base1.isCoplanar());

		Polygon base2 = new Polygon();
		base2.addVertex(new Point(1, 0, 0));
		base2.addVertex(new Point(0, 1, 0));
		base2.addVertex(new Point(1, 1, 0));
		base2.addVertex(new Point(2, 2, 0));
		System.out.println("Pass Coplanar Test #2: " + base2.isCoplanar());

		Polygon base3 = new Polygon();
		base3.addVertex(new Point(1, 0, 0));
		base3.addVertex(new Point(0, 1, 0));
		base3.addVertex(new Point(1, 1, 1));
		base3.addVertex(new Point(1, 1, 0));
		System.out.println("Pass Coplanar Test #3: " + !base3.isCoplanar());

		Polygon base4 = new Polygon();
		base4.addVertex(new Point(1, 0, 0));
		base4.addVertex(new Point(0, 1, 0));
		base4.addVertex(new Point(1, 1, 0));
		base4.addVertex(new Point(1, 1, 1));
		System.out.println("Pass Coplanar Test #4: " + !base4.isCoplanar());

		Polygon base5 = new Polygon();
		base5.addVertex(new Point(1, 0, 0));
		base5.addVertex(new Point(0, 1, 0));
		base5.addVertex(new Point(1, 1, 0));
		base5.addVertex(new Point(2, 2, 0.01f));
		System.out.println("Pass Coplanar Test #5: " + !base5.isCoplanar());

		Polygon base6 = new Polygon();
		base6.addVertex(new Point(1, 0, 0));
		base6.addVertex(new Point(0, 1, 0));
		base6.addVertex(new Point(1, 1, 0));
		base6.addVertex(new Point(2, 2, 0.001f));
		System.out.println("Pass Coplanar Test #6: " + base6.isCoplanar());

		Extrusion extrusion = new Extrusion(base1, 10.0f);
		System.out.println("Extrusion Serialized:\n" + extrusion.serialize().toString(4));
	}
}