package edu.mit.ira.builder.fx;

import javafx.scene.Group;
import javafx.scene.input.KeyEvent;

/**
 * An interface for passing content to the main Application object
 * 
 * @author Ira Winder
 *
 */
public interface ContentContainer {
	
	// These values are designed to be externally overridden before rendered
	final static double DEFAULT_WIDTH = 100;
	final static double DEFAULT_HEIGHT = 100;
	final static Group EMPTY_GROUP = new Group();
	
	/**
	 * Populate the Master Container with Content
	 */
	public void makeContent();
	
	/**
	 * Trigger a key event
	 * @param e
	 */
	public void keyPressed(KeyEvent e);
	
}