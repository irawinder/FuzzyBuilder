package edu.mit.ira.builder.processing;

import edu.mit.ira.fuzzy.base.ControlPoint;
import edu.mit.ira.fuzzy.base.Point;
import edu.mit.ira.fuzzy.builder.DevelopmentEditor;
import processing.core.PApplet;

/**
 * A Processing GUI that opens a window and allow user interaction
 * 
 * @author ira winder
 * 
 */
public class GUI_Processing extends PApplet {
	
	DevelopmentEditor editor;
	Underlay map;
	Canvas window;
	
	/**
	 * Initiate an Instance of the PApplet
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		PApplet.main("edu.mit.ira.builder.processing.GUI_Processing");
	}

	/**
	 * Runs before everything else in PApplet
	 */
	public void settings() {
		
		// Init Application canvas size to match site_map
		//size(map.getWidth(), map.getHeight(), P3D);

		// Set size of canvas to (X, Y) pixels
		size(900, 500, P3D);
	}

	/**
	 * Runs once upon initialization of PApplet class, but after settings() finishes
	 */
	public void setup() {

		surface.setTitle("Space Builder");
		window = new Canvas(this);
		
		editor = new DevelopmentEditor();
		map = new Underlay(this, "data/site.png", (float) 0.5);
		
	}

	/**
	 * Runs every frame unless "noLoop()" is run
	 */
	public void draw() {

		// listen for user inputs and mouse location
		editor.listen(mousePressed, mouseX, mouseY, pointAtMouse(), newPointAtMouse());

		// Update Model "Backend" with New State (if any)
		editor.updateModel();

		// Render the ViewModel "Front End" and GUI to canvas
		window.render(map, editor);
		
		//render();
		
		noLoop();
	}

	/**
	 * Runs once when key is pressed
	 */
	public void keyPressed() {
		editor.keyPressed(key, keyCode, CODED, LEFT, RIGHT, DOWN, UP);
		map.keyPressed(key);
		window.keyPressed(key);
		loop();
	}

	/**
	 * Runs once when mouse is pressed down
	 */
	public void mousePressed() {
		editor.mousePressed(newPointAtMouse());
		loop();
	}

	/*
	 * Runs once when mouse button is released
	 */
	public void mouseReleased() {
		editor.deselect();
		loop();
	}

	/*
	 * Runs when mouse has moved
	 */
	public void mouseMoved() {
		loop();
	}

	/**
	 * Runs when mouse has moved while held down
	 */
	public void mouseDragged() {
		loop();
	}

	/**
	 * 
	 * @return new ControlPoint at mouse (Requires processing.core)
	 */
	Point newPointAtMouse() {
		Point mousePoint = null;

		// generate a grid of points to search for nearest match
		// centered at (0,0)
		int breadth = 1000;
		int interval = 5;

		float min_distance = Float.POSITIVE_INFINITY;
		for (int x = -breadth; x < breadth; x += interval) {
			for (int y = -breadth; y < breadth; y += interval) {
				float dist_x = mouseX - screenX(x, y);
				float dist_y = mouseY - screenY(x, y);
				float distance = (float) Math.sqrt(Math.pow(dist_x, 2) + Math.pow(dist_y, 2));
				if (distance < 15) {
					if (distance < min_distance) {
						min_distance = distance;
						mousePoint = new Point(x, y);
					}
				}
			}
		}
		return mousePoint;
	}

	/**
	 * Return Tagged Point Nearest to Mouse (Requires processing.core)
	 * 
	 * @return ControlPoint closest to mouse
	 */
	ControlPoint pointAtMouse() {
		ControlPoint closest = null;
		float min_distance = Float.POSITIVE_INFINITY;
		for (ControlPoint p : editor.control.points()) {
			float dist_x, dist_y;
			dist_x = mouseX - screenX(p.x, p.y);
			dist_y = mouseY - screenY(p.x, p.y);

			float distance = (float) Math.sqrt(Math.pow(dist_x, 2) + Math.pow(dist_y, 2));
			if (distance < 15) {
				if (distance < min_distance) {
					min_distance = distance;
					closest = p;
				}
			}
		}
		return closest;
	}

}