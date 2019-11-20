package edu.mit.ira.fuzzy.fx.scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class Toolbar extends Container2D {
	
	public Toolbar() {
		super();
		setFill(Color.hsb(0, 0, 0.70));
	}

	public void init() {
		nodes2D.getChildren().clear();
		StackPane content  = new StackPane(new Label("Toolbar"));
		nodes2D.getChildren().add(content);
	}
	
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
	}
}
