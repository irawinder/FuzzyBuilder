package edu.mit.ira.builder.processing;

import edu.mit.ira.builder.DevelopmentEditor;
import edu.mit.ira.voxel.ControlPoint;
import edu.mit.ira.voxel.Point;
import edu.mit.ira.voxel.Tile;
import edu.mit.ira.voxel.TileArray;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * A Processing GUI that opens a window and allow user interaction
 * 
 * @author ira winder
 * 
 */
public class GUI_Processing extends PApplet {
	
	// Is camera 3D? Otherwise it's 2D;
	public boolean cam3D;
	
	DevelopmentEditor editor;
	Underlay map;
	
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

		map = new Underlay("data/site.png", (float) 0.5);

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
		
		cam3D = true;
		editor = new DevelopmentEditor();
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
		render();

		noLoop();
	}

	/**
	 * Runs once when key is pressed
	 */
	public void keyPressed() {
		editor.keyPressed(key, keyCode, CODED, LEFT, RIGHT, DOWN, UP);
		map.keyPressed(key);
		
		switch (key) {
		
		case 'm':
			cam3D = !cam3D;
			break;
			
		}
			
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

		if (cam3D) {
			cam3D();

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
		} else {
			cam2D();
			mousePoint = new Point(mouseX, mouseY);
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
			if (cam3D) {
				cam3D();
				dist_x = mouseX - screenX(p.x, p.y);
				dist_y = mouseY - screenY(p.x, p.y);
			} else {
				cam2D();
				dist_x = mouseX - p.x;
				dist_y = mouseY - p.y;
			}
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

	// Front-End Methods that rely heavily on Processing Library Functions

	void cam3D() {
		camera(200, 400, 200, 400, 200, 0, 0, 0, -1); 
		lights(); colorMode(HSB); pointLight(0, 0, 100, 50, 50, 50);
	} 

	void cam2D() {
		camera(); noLights(); perspective();
	}

	void render() {
		hint(ENABLE_DEPTH_TEST);
		background(255);

		// Draw Underlay
		renderUnderlay();

		// Draw Tiles and Voxels
		if (editor.showTiles) {

			for (TileArray space : editor.spaceList()) {
				if (editor.showSpace(space)) {

					// Draw Sites
					//
					if (space.isType("site")) {
						int col = color(0, 50);
						for(Tile t : space.tileList()) renderTile(t, col, -1);
					}

					// Draw Zones
					//
					if (space.isType("zone")) {
						colorMode(HSB); int col = color(space.hue, 100, 225);
						for(Tile t : space.tileList()) renderTile(t, col, -1);
					}

					// Draw Footprints
					//
					if (space.isType("footprint")) {
						colorMode(HSB); int col;
						if(space.name.equals("Building")) {
							col = color(space.hue, 150, 200);
						} else if(space.name.equals("Setback")) {
							col = color(space.hue, 50, 225);
						} else {
							col = color(space.hue, 150, 200);
						}
						for (Tile t : space.tileList()) {
							renderTile(t, col, -1);
							if (space.name.equals("Building")) {
								renderVoxel(t, col, (float) -0.5*t.scale_w);
							}
						}
					}

					// Draw Bases
					//
					if (space.isType("base")) {
						colorMode(HSB); int col = color(space.hue, 150, 200);
						for(Tile t : space.tileList()) {
							// Only draws ground plane if in 2D view mode
							if(t.location.z == 0 || cam3D) { 
								if (space.name.substring(0, 3).equals("Cou")) {
									renderTile(t, col, 0);
								} else {
									renderVoxel(t, col, 0);
								}
							}
						}
					}
				}
			}
		}

		// Draw Vector Polygon
		//
		fill(245, 50); noStroke(); 
		if (editor.showPolygons) {
			stroke(0, 100); 
			strokeWeight(1);
		}
		pushMatrix(); translate(0, 0, -2);
		beginShape();
		//for(Point p : editor.site_boundary.getCorners()) vertex(p.x, p.y);
		endShape(CLOSE);
		popMatrix();

		hint(DISABLE_DEPTH_TEST);

		// Draw Tagged Control Points
		//
		for (ControlPoint p : editor.control.points()) {
			fill(150, 100); stroke(0, 150); strokeWeight(1);
			pushMatrix(); translate(0, 0, 1);
			if (p.active()) ellipse(p.x, p.y, 10, 10);
			int size = 4;
			if (!p.active()) {
				stroke(0, 75);
				size = 2;
			}
			line(p.x-size, p.y-size, p.x+size, p.y+size);
			line(p.x-size, p.y+size, p.x+size, p.y-size);
			popMatrix();
		}

		// Draw Tagged Control Points Labels
		//
		for (ControlPoint p : editor.control.points()) {
			if (p.active()) {
				int x, y;
				if (cam3D) {
					cam3D();
					x = (int)screenX(p.x, p.y);
					y = (int)screenY(p.x, p.y);
				} else {
					x = (int)p.x;
					y = (int)p.y;
				}
				if(cam3D) cam2D(); // sets temporarily to 2D camera, if in 3D
				fill(255, 150); stroke(200, 150); strokeWeight(1);
				int textWidth = 7*p.getTag().length();
				rectMode(CORNER); rect(x + 10, y - 7, textWidth, 15, 5);
				fill(50); textAlign(CENTER, CENTER);
				text(p.getTag(), x + 10 + (int)textWidth/2, y - 1);
				if(cam3D) cam3D(); // sets back to 3D camera, if in 3D mode
			}
		}

		// Draw Hovering Control Point
		//
		if (editor.hovering != null && editor.hovering.active()) {
			int col = color(50);
			if (editor.removePoint) {
				colorMode(RGB); 
				col = color(255, 0, 0);
			} else if (editor.addPoint) {
				colorMode(RGB); 
				col = color(0, 255, 00);
			}
			renderCross(editor.hovering.x, editor.hovering.y, 4, col, 2, 1);
		}

		if(cam3D) cam2D(); // sets temporarily to 2D camera, if in 3D

		// Draw Info Text
		//
		fill(100); textAlign(LEFT, TOP);
		String info = "";
		info += "Click and drag control points";
		info += "\n";
		info += "\n" + "Press 'a' to add control point";
		if(editor.addPoint) info += " <--";
		info += "\n" + "Press 'x' to remove control point";
		if(editor.removePoint) info += " <--";
		info += "\n" + "Press 'i' to edit Site";
		if(editor.editVertices) info += " <--";
		info += "\n" + "Press 'p' to edit Plots";
		if(editor.editPlots) info += " <--";
		info += "\n" + "Press 'o' to edit Voids";
		if(editor.editVoids) info += " <--";
		info += "\n" + "Press 'c' clear all control points";
		info += "\n";
		info += "\n" + "Press '-' or '+' to resize tiles";
		info += "\n" + "Press '[', '{', ']', or '}' to rotate tiles";
		info += "\n" + "Press 'r' to generate random site";
		info += "\n" + "Press 'm' to toggle 2D/3D view";
		info += "\n" + "Press 'v' to toggle View Model";
		info += "\n" + "Press 't' to hide/show Tiles";
		if(editor.showTiles) info += " <--";
		info += "\n" + "Press 'l' to hide/show PolyLines";
		if(editor.showPolygons) info += " <--";
		info += "\n";
		info += "\n" + "Press '1' to show Site";
		if(editor.viewState == 1) info += " <--";
		info += "\n" + "Press '2' to show Zones";
		if(editor.viewState == 2) info += " <--";
		info += "\n" + "Press '3' to show Footprints";
		if(editor.viewState == 3) info += " <--";
		info += "\n" + "Press '4' to show Zones + Buildings";
		if(editor.viewState == 4) info += " <--";
		info += "\n" + "Press '5' to show Buildings Only";
		if(editor.viewState == 5) info += " <--";
		//info += "\n" + "Press '6' to show Floors";
		//if(viewState == 6) info += " <--";
		//info += "\n" + "Press '7' to show Rooms";
		//if(viewState == 7) info += " <--";
		if (editor.showText) text(info, 10, 10);
		//text("Framerate: " + int(frameRate), 10, height - 20);

		// Draw Summary
		//
		if (editor.showTiles) {
			fill(100); textAlign(LEFT, TOP);
			String summary = "";
			summary += "View Model: " + editor.viewModel;
			summary += "\n" + "Tile Dimensions:";
			summary += "\n" + editor.tileW + " x " + editor.tileW + " x " + editor.tileH + " units";
			summary += "\n";
			summary += "\n" + editor + "/...";
			for(TileArray space : editor.spaceList()) {
				if (editor.showSpace(space)) {
					summary += "\n~/" + space;
					//summary += "\n" + space.parent_name + "/" + space;
				}
			}
			if (editor.showText) text(summary, width - 175, 10);
		}

		// Mouse Cursor Info
		//
		fill(50); textAlign(LEFT, TOP);
		if (editor.addPoint) {
			text("NEW (" + editor.new_control_type + ")", mouseX + 10, mouseY - 20);
		} else if (editor.removePoint) {
			text("REMOVE", mouseX + 10, mouseY - 20);
		} else if (editor.hovering != null && editor.hovering.active()) {
			text("MOVE", mouseX + 10, mouseY - 20);
		}

		if(cam3D) cam3D(); // sets back to 3D camera, if in 3D mode
	}

	void renderTile(Tile t, int col, float z_offset) {

		float scaler = (float) 0.85;

		fill(col); noStroke();
		pushMatrix(); translate(t.location.x, t.location.y, t.location.z + z_offset);

		if (editor.viewModel.equals("DOT")) {
			ellipse(0, 0, scaler*t.scale_uv, scaler*t.scale_uv);
		} else if (editor.viewModel.equals("VOXEL")) {
			rotate(editor.tile_rotation);
			rectMode(CENTER); rect(0, 0, scaler*t.scale_uv, scaler*t.scale_uv);
		} else {
			ellipse(0, 0, scaler*t.scale_uv, scaler*t.scale_uv);
		}

		popMatrix();
	}

	void renderVoxel(Tile t, int col, float z_offset) {

		float scaler_uv = (float) 0.9;
		float scaler_w  = (float) 0.6;

		fill(col); stroke(0, 50); strokeWeight(1);
		pushMatrix(); translate(t.location.x, t.location.y, t.location.z + z_offset);
		rotate(editor.tile_rotation);
		box(scaler_uv*t.scale_uv, scaler_uv*t.scale_uv, scaler_w*t.scale_w);
		popMatrix();
	}

	void renderCross(float x, float y, float size, int col, float stroke, float z_offset) {
		stroke(col); strokeWeight(stroke);
		pushMatrix(); translate(0, 0, z_offset);
		line(x-5, y-5, x+5, y+5);
		line(x-5, y+5, x+5, y-5);
		popMatrix();
	}

	/**
	 * Renders a Raster Image Underlay
	 */
	public void renderUnderlay() {
		pushMatrix();
		translate(0, 0, -5);
		if (map.show) image(map.getImg(), 0, 0);
		popMatrix();
	}

	/**
	 * A raster Underlay to super-impose on View Model
	 */
	public class Underlay {

		private PImage underlay;
		private boolean show;
		int w, h;

		public Underlay(String file_path, float scaler) {
			show = false;
			underlay = loadImage(file_path);
			w = (int) (scaler * underlay.width);
			h = (int) (scaler * underlay.height);
			underlay.resize(w, h);
		}

		public PImage getImg() {
			return underlay;
		}

		public int getWidth() {
			return w;
		}

		public int getHeight() {
			return h;
		}

		private void toggle() {
			show = !show;
		}

		public boolean show() {
			return show;
		}

		public void keyPressed(char key) {
			switch (key) {
			case 'M':
				toggle();
				break;
			}
		}
	}


}