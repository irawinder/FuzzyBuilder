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

	/**
	 * Construct and Underlay Image for JavaFX
	 * 
	 * @param file_path relative path location of file
	 * @param scaler scale the image up or down from its native resolution
	 * @param opacity opacity value of underlay (0 - 1)
	 */
	public Underlay(String file_path, double scaler, double opacity) {
		show = false;
		
		InputStream is;
		try {
			is = new FileInputStream(file_path);
			underlay = new Image(is);
			underlayView = new ImageView();
			underlayView.setImage(underlay);
			double w = scaler * underlay.getWidth();
			double h = scaler * underlay.getHeight();
			underlayView.setFitWidth(w);
			underlayView.setFitHeight(h);
			underlayView.setOpacity(opacity);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Get the Image as ImageView Node
	 * 
	 * @return ImageView node of underlay
	 */
	public ImageView getImageView() {
		return underlayView;
	}

	/**
	 * switches show true to false, or false to true
	 */
	private void toggle() {
		show = !show;
	}
	
	/**
	 * Show we show the map?
	 * 
	 * @return true or false
	 */
	public boolean show() {
		return show;
	}
	
	/**
	 * Execute methods based upon key that is passed
	 * 
	 * @param key
	 */
	public void keyPressed(char key) {
		switch (key) {
		case 'u': // set show to false
			toggle();
			break;
		}
	}
	
	/**
	 * Transform the image in 3D space
	 * 
	 * @param t
	 */
	public void applyTransform(Transform t) {
		
	}
}