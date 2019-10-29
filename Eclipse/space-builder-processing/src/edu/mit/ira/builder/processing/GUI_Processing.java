package edu.mit.ira.builder.processing;

import edu.mit.ira.builder.Builder;
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

	Builder builder;
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

		builder = new Builder();
	}

	/**
	 * Runs every frame unless "noLoop()" is run
	 */
	public void draw() {

		// listen for user inputs and mouse location
		builder.listen(mousePressed, mouseX, mouseY, pointAtMouse(), newPointAtMouse());

		// Update Model "Backend" with New State (if any)
		builder.updateModel();

		// Render the ViewModel "Front End" and GUI to canvas
		render();

		noLoop();
	}

	/**
	 * Runs once when key is pressed
	 */
	public void keyPressed() {
		builder.keyPressed(key, keyCode, CODED, LEFT, RIGHT, DOWN, UP);
		map.keyPressed(key);
		loop();
	}

	/**
	 * Runs once when mouse is pressed down
	 */
	public void mousePressed() {
		builder.mousePressed(newPointAtMouse());
		loop();
	}

	/*
	 * Runs once when mouse button is released
	 */
	public void mouseReleased() {
		builder.mouseReleased();
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

		if (builder.cam3D) {
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
		for (ControlPoint p : builder.control.points()) {
			float dist_x, dist_y;
			if (builder.cam3D) {
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
		if (builder.showTiles) {

			for (TileArray space : builder.dev.spaceList()) {
				if (builder.showSpace(space)) {

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
							if(t.location.z == 0 || builder.cam3D) { 
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
		if (builder.showPolygons) {
			stroke(0, 100); 
			strokeWeight(1);
		}
		pushMatrix(); translate(0, 0, -2);
		beginShape();
		for(Point p : builder.site_boundary.getCorners()) vertex(p.x, p.y);
		endShape(CLOSE);
		popMatrix();

		hint(DISABLE_DEPTH_TEST);

		// Draw Tagged Control Points
		//
		for (ControlPoint p : builder.control.points()) {
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
		for (ControlPoint p : builder.control.points()) {
			if (p.active()) {
				int x, y;
				if (builder.cam3D) {
					cam3D();
					x = (int)screenX(p.x, p.y);
					y = (int)screenY(p.x, p.y);
				} else {
					x = (int)p.x;
					y = (int)p.y;
				}
				if(builder.cam3D) cam2D(); // sets temporarily to 2D camera, if in 3D
				fill(255, 150); stroke(200, 150); strokeWeight(1);
				int textWidth = 7*p.getTag().length();
				rectMode(CORNER); rect(x + 10, y - 7, textWidth, 15, 5);
				fill(50); textAlign(CENTER, CENTER);
				text(p.getTag(), x + 10 + (int)textWidth/2, y - 1);
				if(builder.cam3D) cam3D(); // sets back to 3D camera, if in 3D mode
			}
		}

		// Draw Hovering Control Point
		//
		if (builder.hovering != null && builder.hovering.active()) {
			int col = color(50);
			if (builder.removePoint) {
				colorMode(RGB); 
				col = color(255, 0, 0);
			} else if (builder.addPoint) {
				colorMode(RGB); 
				col = color(0, 255, 00);
			}
			renderCross(builder.hovering.x, builder.hovering.y, 4, col, 2, 1);
		}

		if(builder.cam3D) cam2D(); // sets temporarily to 2D camera, if in 3D

		// Draw Info Text
		//
		fill(100); textAlign(LEFT, TOP);
		String info = "";
		info += "Click and drag control points";
		info += "\n";
		info += "\n" + "Press 'a' to add control point";
		if(builder.addPoint) info += " <--";
		info += "\n" + "Press 'x' to remove control point";
		if(builder.removePoint) info += " <--";
		info += "\n" + "Press 'i' to edit Site";
		if(builder.editVertices) info += " <--";
		info += "\n" + "Press 'p' to edit Plots";
		if(builder.editPlots) info += " <--";
		info += "\n" + "Press 'o' to edit Voids";
		if(builder.editVoids) info += " <--";
		info += "\n" + "Press 'c' clear all control points";
		info += "\n";
		info += "\n" + "Press '-' or '+' to resize tiles";
		info += "\n" + "Press '[', '{', ']', or '}' to rotate tiles";
		info += "\n" + "Press 'r' to generate random site";
		info += "\n" + "Press 'm' to toggle 2D/3D view";
		info += "\n" + "Press 'v' to toggle View Model";
		info += "\n" + "Press 't' to hide/show Tiles";
		if(builder.showTiles) info += " <--";
		info += "\n" + "Press 'l' to hide/show PolyLines";
		if(builder.showPolygons) info += " <--";
		info += "\n";
		info += "\n" + "Press '1' to show Site";
		if(builder.viewState == 1) info += " <--";
		info += "\n" + "Press '2' to show Zones";
		if(builder.viewState == 2) info += " <--";
		info += "\n" + "Press '3' to show Footprints";
		if(builder.viewState == 3) info += " <--";
		info += "\n" + "Press '4' to show Zones + Buildings";
		if(builder.viewState == 4) info += " <--";
		info += "\n" + "Press '5' to show Buildings Only";
		if(builder.viewState == 5) info += " <--";
		//info += "\n" + "Press '6' to show Floors";
		//if(viewState == 6) info += " <--";
		//info += "\n" + "Press '7' to show Rooms";
		//if(viewState == 7) info += " <--";
		if (builder.showText) text(info, 10, 10);
		//text("Framerate: " + int(frameRate), 10, height - 20);

		// Draw Summary
		//
		if (builder.showTiles) {
			fill(100); textAlign(LEFT, TOP);
			String summary = "";
			summary += "View Model: " + builder.viewModel;
			summary += "\n" + "Tile Dimensions:";
			summary += "\n" + builder.tileW + " x " + builder.tileW + " x " + builder.tileH + " units";
			summary += "\n";
			summary += "\n" + builder.dev + "/...";
			for(TileArray space : builder.dev.spaceList()) {
				if (builder.showSpace(space)) {
					summary += "\n~/" + space;
					//summary += "\n" + space.parent_name + "/" + space;
				}
			}
			if (builder.showText) text(summary, width - 175, 10);
		}

		// Mouse Cursor Info
		//
		fill(50); textAlign(LEFT, TOP);
		if (builder.addPoint) {
			text("NEW (" + builder.new_control_type + ")", mouseX + 10, mouseY - 20);
		} else if (builder.removePoint) {
			text("REMOVE", mouseX + 10, mouseY - 20);
		} else if (builder.hovering != null && builder.hovering.active()) {
			text("MOVE", mouseX + 10, mouseY - 20);
		}

		if(builder.cam3D) cam3D(); // sets back to 3D camera, if in 3D mode
	}

	void renderTile(Tile t, int col, float z_offset) {

		float scaler = (float) 0.85;

		fill(col); noStroke();
		pushMatrix(); translate(t.location.x, t.location.y, t.location.z + z_offset);

		if (builder.viewModel.equals("DOT")) {
			ellipse(0, 0, scaler*t.scale_uv, scaler*t.scale_uv);
		} else if (builder.viewModel.equals("VOXEL")) {
			rotate(builder.tile_rotation);
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
		rotate(builder.tile_rotation);
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