package edu.mit.ira.builder.fx;

import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

public class Version extends SubScene implements ContentContainer { 
	
	public Version() {
		super(EMPTY_GROUP, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		makeContent();
	}

	@Override
	public void makeContent() {
		StackPane toolbarContent  = new StackPane(new Label("Version Tree"));
        setRoot(toolbarContent);
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
