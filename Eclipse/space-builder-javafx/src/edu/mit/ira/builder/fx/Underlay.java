package edu.mit.ira.builder.fx;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Transform;

/**
 * A raster Underlay to super-impose on View Model
 */
public class Underlay {

	private Image underlay;
	private ImageView underlayView;
	private boolean show;
	double w, h;

	public Underlay(String file_path, double scaler) {
		show = false;
		
		InputStream is;
		try {
			is = new FileInputStream(file_path);
			underlay = new Image(is);
			underlayView = new ImageView();
			underlayView.setImage(underlay);
			w = scaler * underlay.getWidth();
			h = scaler * underlay.getHeight();
			underlayView.setFitWidth(w);
			underlayView.setFitHeight(h);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public Image getImage() {
		return underlay;
	}
	
	public ImageView getImageView() {
		return underlayView;
	}

	public double getWidth() {
		return w;
	}

	public double getHeight() {
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
	
	public void applyTransform(Transform t) {
		
	}
}