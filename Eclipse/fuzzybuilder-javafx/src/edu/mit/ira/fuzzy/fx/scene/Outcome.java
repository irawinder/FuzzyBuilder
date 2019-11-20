package edu.mit.ira.fuzzy.fx.scene;
import edu.mit.ira.fuzzy.fx.base.Container2D;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class Outcome extends Container2D {
	
	public Outcome() {
		super();
		setFill(Color.hsb(0, 0, 0.80));
	}

	public void init() {
		nodes2D.getChildren().clear();
		StackPane content  = new StackPane(new Label("Performance Graphs"));
		nodes2D.getChildren().add(content);
	}
	
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
	}
}
