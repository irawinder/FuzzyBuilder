package edu.mit.ira.fuzzy.test;

import edu.mit.ira.fuzzy.Extrusion;
import edu.mit.ira.fuzzy.Point;
import edu.mit.ira.fuzzy.Polygon;

public class Test {

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
	}
}