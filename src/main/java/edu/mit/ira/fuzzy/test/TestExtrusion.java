package edu.mit.ira.fuzzy.test;

import edu.mit.ira.fuzzy.model.Extrusion;
import edu.mit.ira.fuzzy.model.Point;
import edu.mit.ira.fuzzy.model.Polygon;

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
		
		// Test Extrusions
		
		Polygon empty = new Polygon();
		
		Polygon pointOnly = new Polygon();
		pointOnly.addVertex(new Point(1, 1, 1));
		
		Polygon lineOnly = new Polygon();
		lineOnly.addVertex(new Point(0, 0, 0));
		lineOnly.addVertex(new Point(1, 1, 1));
		
		Extrusion extrusion0 = new Extrusion(empty, 10.0f);
		System.out.println("Extrusion 0 Serialized:\n" + extrusion0.serialize().toString(4));
		
		Extrusion extrusion1 = new Extrusion(pointOnly, 10f);
		System.out.println("Extrusion 1 Serialized:\n" + extrusion1.serialize().toString(4));
		
		Extrusion extrusion2 = new Extrusion(lineOnly, 10f);
		System.out.println("Extrusion 2 Serialized:\n" + extrusion2.serialize().toString(4));
		
		Extrusion extrusion3 = new Extrusion(base1, 10f);
		System.out.println("Extrusion 3 Serialized:\n" + extrusion3.serialize().toString(4));
		
		Extrusion extrusion4 = new Extrusion(base1, 0f);
		System.out.println("Extrusion 4 Serialized:\n" + extrusion4.serialize().toString(4));
	}
}