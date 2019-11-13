package edu.mit.ira.fuzzy.processing;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * A raster Underlay to super-impose on View Model
 */
public class Underlay {

	private PImage underlay;
	private boolean show;
	int w, h;

	public Underlay(PApplet applet, String file_path, float scaler) {
		show = false;
		underlay = applet.loadImage(file_path);
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
