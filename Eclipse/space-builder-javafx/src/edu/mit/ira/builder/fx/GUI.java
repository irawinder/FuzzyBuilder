package edu.mit.ira.builder.fx;
import javafx.application.Application;
import javafx.stage.Stage;

public class GUI extends Application {
	
	TestScene boxes = new TestScene();

    @Override
    public void start(Stage stage) {
        
    	boxes.createContent();
    	boxes.handleMouseEvents();
    	
        stage.setTitle("Space Builder FX");
        stage.setScene(boxes.getScene());
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
	
}