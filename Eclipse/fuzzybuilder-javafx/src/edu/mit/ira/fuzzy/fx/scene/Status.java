package edu.mit.ira.fuzzy.fx.scene;

import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class Status extends SubScene implements ContentContainer { 
	
private Group nodes;
	
	public Status() {
		super(EMPTY_GROUP, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		nodes = new Group();
		setRoot(nodes);
		setFill(Color.hsb(0, 0, 0.7));
	}

	public void render() {
		nodes.getChildren().clear();
		StackPane content  = new StackPane(new Label("Status Bar"));
		nodes.getChildren().add(content);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}