package edu.mit.ira.fuzzy.fx.scene;

import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

public class Toolbar extends SubScene implements ContentContainer { 
	
	public Toolbar() {
		super(EMPTY_GROUP, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setContent();
	}

	@Override
	public void setContent() {
		StackPane toolbarContent  = new StackPane(new Label("Toolbar"));
        setRoot(toolbarContent);
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
