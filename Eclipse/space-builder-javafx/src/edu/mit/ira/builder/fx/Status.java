package edu.mit.ira.builder.fx;

import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

public class Status extends SubScene implements ContentContainer { 
	
	public Status() {
		super(EMPTY_GROUP, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		makeContent();
	}

	@Override
	public void makeContent() {
		StackPane toolbarContent  = new StackPane(new Label("Status Bar"));
        setRoot(toolbarContent);
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
