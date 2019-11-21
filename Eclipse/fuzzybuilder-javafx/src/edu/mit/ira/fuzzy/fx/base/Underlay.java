package edu.mit.ira.fuzzy.fx.base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Transform;

/**
 * A Raster Underlay Image to Display in a 3D Environment
 * 
 * @author Ira Winder
 *
 */
public class Underlay implements Cloneable {

	private final static double DEFAULT_SCALER_VALUE = 1.0;
	private final static double DEFAULT_OPACITY_VALUE = 0.75;
	private final static boolean DEFAULT_VISIBILITY = true;
	
	private Image underlay;
	private ImageView underlayView;
	private double scaler, opacity;
	private boolean isVisible;
	
	
	/**
	 * Initialize an empty Underlay
	 */
	public Underlay() {
		scaler = DEFAULT_SCALER_VALUE;
		opacity = DEFAULT_OPACITY_VALUE;
		isVisible = DEFAULT_VISIBILITY;
		underlayView = new ImageView();
	}
	
	/**
	 * Construct and Underlay Image for JavaFX
	 * 
	 * @param file_path relative path location of file
	 */
	public Underlay(String file_path) {
		this();
		setImage(file_path);
	}
	
	/**
	 * Construct and Underlay Image for JavaFX
	 * 
	 * @param file_path relative path location of file
	 * @param scaler scale the image up or down from its native resolution
	 * @param opacity opacity value of underlay (0 - 1)
	 */
	public Underlay(String file_path, double scaler, double opacity) {
		this();
		this.scaler = scaler;
		this.opacity = opacity;
		setImage(file_path);
	}
	
	/**
	 * Load Image from File
	 * 
	 * @param file_path relative file path from root directory
	 */
	public void setImage(String file_path) {
		InputStream is;
		try {
			is = new FileInputStream(file_path);
			underlay = new Image(is);
			setImageView();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Scale the ImageView up or down 
	 * 
	 * @param scaler < 1 makes image smaller; > 1 makes image bigger
	 */
	public void setScale(double scaler) {
		double w = scaler * underlay.getWidth();
		double h = scaler * underlay.getHeight();
		underlayView.setFitWidth(w);
		underlayView.setFitHeight(h);
	}
	
	/**
	 * Get the base scaler for the view image 
	 * 
	 * @return scaler
	 */
	public double getScaler() {
		return scaler;
	}
	
	/**
	 * Set image opacity
	 * @param opacity 0 is completely transparent; 1 is completely opaque
	 */
	public void setOpacity(double opacity) {
		underlayView.setOpacity(opacity);
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
	 * Set the Image as ImageView Node
	 * 
	 * @return ImageView node of underlay
	 */
	public void setImageView() {
		underlayView = new ImageView();
		underlayView.setImage(underlay);
		setScale(scaler);
		setOpacity(opacity);
	}
	
	/**
	 * Get the image as Image Node
	 * 
	 * @return Image node of underlay
	 */
	public Image getImage() {
		return underlay;
	}
	
	/**
	 * Check if the underlay is visible
	 * 
	 * @return
	 */
	public boolean isVisible() {
		return isVisible;
	}
	
	/**
	 * Set visibility of the underlay
	 * 
	 * @param isVisible
	 */
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
		underlayView.setVisible(isVisible);
	}
	
	/**
	 * Transform the image in 3D space
	 * 
	 * @param t
	 */
	public void applyTransform(Transform t) {
		
	}
	
	/**
	 * copies the object to a new in-memory object
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}