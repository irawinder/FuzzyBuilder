package edu.mit.ira.builder.processing;

import edu.mit.ira.fuzzy.base.ControlPoint;
import edu.mit.ira.fuzzy.base.Tile;
import edu.mit.ira.fuzzy.base.TileArray;
import edu.mit.ira.fuzzy.builder.DevelopmentEditor;
import processing.core.PImage;

public class Canvas {
	
	private static GUI_Processing applet;
	
	// Is camera 3D? Otherwise it's 2D;
	//
	public boolean cam3D;
	
	public Canvas(GUI_Processing applet) {
		this.applet = applet;
		cam3D = true;
	}
	
	// Front-End Methods that rely heavily on Processing Library Functions
	//
	public void render(Underlay map, DevelopmentEditor editor) {
		applet.hint(applet.ENABLE_DEPTH_TEST);
		applet.background(255);
		
		if(cam3D) {
			cam3D();
		} else {
			cam2D();
		}
		
		// Draw Underlay
		renderUnderlay(map);

		// Draw Tiles and Voxels
		if (editor.showTiles) {

			for (TileArray space : editor.spaceList()) {
				if (editor.showSpace(space)) {

					// Draw Sites
					//
					if (space.isType("site")) {
						int col = applet.color(0, 50);
						for (Tile t : space.tileList())
							renderTile(t, col, -1);
					}

					// Draw Zones
					//
					if (space.isType("zone")) {
						applet.colorMode(applet.HSB);
						int col = applet.color(space.hue, 100, 225);
						for (Tile t : space.tileList())
							renderTile(t, col, -1);
					}

					// Draw Footprints
					//
					if (space.isType("footprint")) {
						applet.colorMode(applet.HSB);
						int col;
						if (space.name.equals("Building")) {
							col = applet.color(space.hue, 150, 200);
						} else if (space.name.equals("Setback")) {
							col = applet.color(space.hue, 50, 225);
						} else {
							col = applet.color(space.hue, 150, 200);
						}
						for (Tile t : space.tileList()) {
							renderTile(t, col, -1);
							if (space.name.equals("Building")) {
								renderVoxel(t, col, (float) -0.5 * t.scale_w);
							}
						}
					}

					// Draw Bases
					//
					if (space.isType("base")) {
						applet.colorMode(applet.HSB);
						int col = applet.color(space.hue, 150, 200);
						for (Tile t : space.tileList()) {
							// Only draws ground plane if in 2D view mode
							if (t.location.z == 0 || cam3D) {
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
		applet.fill(245, 50);
		applet.noStroke();
		if (editor.showPolygons) {
			applet.stroke(0, 100);
			applet.strokeWeight(1);
		}
		applet.pushMatrix();
		applet.translate(0, 0, -2);
		applet.beginShape();
		// for(Point p : editor.site_boundary.getCorners()) vertex(p.x, p.y);
		applet.endShape(applet.CLOSE);
		applet.popMatrix();

		applet.hint(applet.DISABLE_DEPTH_TEST);

		// Draw Tagged Control Points
		//
		for (ControlPoint p : editor.control.points()) {
			applet.fill(150, 100);
			applet.stroke(0, 150);
			applet.strokeWeight(1);
			applet.pushMatrix();
			applet.translate(0, 0, 1);
			if (p.active())
				applet.ellipse(p.x, p.y, 10, 10);
			int size = 4;
			if (!p.active()) {
				applet.stroke(0, 75);
				size = 2;
			}
			applet.line(p.x - size, p.y - size, p.x + size, p.y + size);
			applet.line(p.x - size, p.y + size, p.x + size, p.y - size);
			applet.popMatrix();
		}

		// Draw Tagged Control Points Labels
		//
		for (ControlPoint p : editor.control.points()) {
			if (p.active()) {
				int x, y;
				if (cam3D) {
					cam3D();
					x = (int) applet.screenX(p.x, p.y);
					y = (int) applet.screenY(p.x, p.y);
				} else {
					x = (int) p.x;
					y = (int) p.y;
				}
				if (cam3D)
					cam2D(); // sets temporarily to 2D camera, if in 3D
				applet.fill(255, 150);
				applet.stroke(200, 150);
				applet.strokeWeight(1);
				int textWidth = 7 * p.getTag().length();
				applet.rectMode(applet.CORNER);
				applet.rect(x + 10, y - 7, textWidth, 15, 5);
				applet.fill(50);
				applet.textAlign(applet.CENTER, applet.CENTER);
				applet.text(p.getTag(), x + 10 + (int) textWidth / 2, y - 1);
				if (cam3D)
					cam3D(); // sets back to 3D camera, if in 3D mode
			}
		}

		// Draw Hovering Control Point
		//
		if (editor.hovering != null && editor.hovering.active()) {
			int col = applet.color(50);
			if (editor.removePoint) {
				applet.colorMode(applet.RGB);
				col = applet.color(255, 0, 0);
			} else if (editor.addPoint) {
				applet.colorMode(applet.RGB);
				col = applet.color(0, 255, 00);
			}
			renderCross(editor.hovering.x, editor.hovering.y, 4, col, 2, 1);
		}

		if (cam3D)
			cam2D(); // sets temporarily to 2D camera, if in 3D

		// Draw Info Text
		//
		applet.fill(100);
		applet.textAlign(applet.LEFT, applet.TOP);
		String info = "";
		info += "Click and drag control points";
		info += "\n";
		info += "\n" + "Press 'a' to add control point";
		if (editor.addPoint)
			info += " <--";
		info += "\n" + "Press 'x' to remove control point";
		if (editor.removePoint)
			info += " <--";
		info += "\n" + "Press 'i' to edit Site";
		if (editor.editVertices)
			info += " <--";
		info += "\n" + "Press 'p' to edit Plots";
		if (editor.editPlots)
			info += " <--";
		info += "\n" + "Press 'o' to edit Voids";
		if (editor.editVoids)
			info += " <--";
		info += "\n" + "Press 'c' clear all control points";
		info += "\n";
		info += "\n" + "Press '-' or '+' to resize tiles";
		info += "\n" + "Press '[', '{', ']', or '}' to rotate tiles";
		info += "\n" + "Press 'r' to generate random site";
		info += "\n" + "Press 'm' to toggle 2D/3D view";
		info += "\n" + "Press 'v' to toggle View Model";
		info += "\n" + "Press 't' to hide/show Tiles";
		if (editor.showTiles)
			info += " <--";
		info += "\n" + "Press 'l' to hide/show PolyLines";
		if (editor.showPolygons)
			info += " <--";
		info += "\n";
		info += "\n" + "Press '1' to show Site";
		if (editor.viewState == 1)
			info += " <--";
		info += "\n" + "Press '2' to show Zones";
		if (editor.viewState == 2)
			info += " <--";
		info += "\n" + "Press '3' to show Footprints";
		if (editor.viewState == 3)
			info += " <--";
		info += "\n" + "Press '4' to show Zones + Buildings";
		if (editor.viewState == 4)
			info += " <--";
		info += "\n" + "Press '5' to show Buildings Only";
		if (editor.viewState == 5)
			info += " <--";
		// info += "\n" + "Press '6' to show Floors";
		// if(viewState == 6) info += " <--";
		// info += "\n" + "Press '7' to show Rooms";
		// if(viewState == 7) info += " <--";
		if (editor.showText)
			applet.text(info, 10, 10);
		// text("Framerate: " + int(frameRate), 10, height - 20);

		// Draw Summary
		//
		if (editor.showTiles) {
			applet.fill(100);
			applet.textAlign(applet.LEFT, applet.TOP);
			String summary = "";
			summary += "View Model: " + editor.viewModel;
			summary += "\n" + "Tile Dimensions:";
			summary += "\n" + editor.tileW + " x " + editor.tileW + " x " + editor.tileH + " units";
			summary += "\n";
			summary += "\n" + editor + "/...";
			for (TileArray space : editor.spaceList()) {
				if (editor.showSpace(space)) {
					summary += "\n~/" + space;
					// summary += "\n" + space.parent_name + "/" + space;
				}
			}
			if (editor.showText)
				applet.text(summary, applet.width - 175, 10);
		}

		// Mouse Cursor Info
		//
		applet.fill(50);
		applet.textAlign(applet.LEFT, applet.TOP);
		if (editor.addPoint) {
			applet.text("NEW (" + editor.new_control_type + ")", applet.mouseX + 10, applet.mouseY - 20);
		} else if (editor.removePoint) {
			applet.text("REMOVE", applet.mouseX + 10, applet.mouseY - 20);
		} else if (editor.hovering != null && editor.hovering.active()) {
			applet.text("MOVE", applet.mouseX + 10, applet.mouseY - 20);
		}

		if (cam3D)
			cam3D(); // sets back to 3D camera, if in 3D mode
	}

	private void renderTile(Tile t, int col, float z_offset) {

		float scaler = (float) 0.85;

		applet.fill(col);
		applet.noStroke();
		applet.pushMatrix();
		applet.translate(t.location.x, t.location.y, t.location.z + z_offset);

		if (applet.editor.viewModel.equals("DOT")) {
			applet.ellipse(0, 0, scaler * t.scale_uv, scaler * t.scale_uv);
		} else if (applet.editor.viewModel.equals("VOXEL")) {
			applet.rotate(applet.editor.tile_rotation);
			applet.rectMode(applet.CENTER);
			applet.rect(0, 0, scaler * t.scale_uv, scaler * t.scale_uv);
		} else {
			applet.ellipse(0, 0, scaler * t.scale_uv, scaler * t.scale_uv);
		}

		applet.popMatrix();
	}

	private void renderVoxel(Tile t, int col, float z_offset) {

		float scaler_uv = (float) 0.9;
		float scaler_w = (float) 0.6;

		applet.fill(col);
		applet.stroke(0, 50);
		applet.strokeWeight(1);
		applet.pushMatrix();
		applet.translate(t.location.x, t.location.y, t.location.z + z_offset);
		applet.rotate(applet.editor.tile_rotation);
		applet.box(scaler_uv * t.scale_uv, scaler_uv * t.scale_uv, scaler_w * t.scale_w);
		applet.popMatrix();
	}

	private void renderCross(float x, float y, float size, int col, float stroke,
			float z_offset) {
		applet.stroke(col);
		applet.strokeWeight(stroke);
		applet.pushMatrix();
		applet.translate(0, 0, z_offset);
		applet.line(x - 5, y - 5, x + 5, y + 5);
		applet.line(x - 5, y + 5, x + 5, y - 5);
		applet.popMatrix();
	}

	public void cam3D() {
		applet.camera(200, 400, 200, 400, 200, 0, 0, 0, -1);
		applet.lights();
		applet.colorMode(applet.HSB);
		applet.pointLight(0, 0, 100, 50, 50, 50);
	}

	public void cam2D() {
		applet.camera();
		applet.noLights();
		applet.perspective();
	}

	/**
	 * Renders a Raster Image Underlay
	 */
	private void renderUnderlay(Underlay map) {
		applet.pushMatrix();
		applet.translate(0, 0, -5);
		if (map.show())
			applet.image(map.getImg(), 0, 0);
		applet.popMatrix();
	}
	
	public void keyPressed(char key) {
		switch(key) {
		case 'm':
			cam3D = !cam3D;
			break;
		}
	}
}
