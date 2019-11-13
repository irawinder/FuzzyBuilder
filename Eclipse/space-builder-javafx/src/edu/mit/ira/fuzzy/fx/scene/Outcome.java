package edu.mit.ira.fuzzy.fx.scene;

import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

public class Outcome extends SubScene implements ContentContainer { 
	
	public Outcome() {
		super(EMPTY_GROUP, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		makeContent();
	}

	@Override
	public void makeContent() {
		StackPane toolbarContent  = new StackPane(new Label("Performance Graph"));
        setRoot(toolbarContent);
		
	}

	@Override
	public void refreshContent() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
