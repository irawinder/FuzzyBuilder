package edu.mit.ira.builder.fx;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GUI extends Application {
	
	static final int WINDOW_WIDTH = 1280;
    static final int WINDOW_HEIGHT = 720;
    static final Color TEXT_COLOR = Color.gray(0.8);
    
    @Override
    public void start(Stage stage) {
        
    	ViewModel site = new ViewModel("JR");
    	//TestScene boxes = new TestScene();
    	
    	stage.setTitle("Space Builder FX");
    	
    	final SubScene scene3D = new SubScene(site.getGroup(), 
        		WINDOW_WIDTH, WINDOW_HEIGHT, true, SceneAntialiasing.BALANCED);
//        final SubScene sceneUI = new SubScene(settings.getGroup(), WINDOW_WIDTH,
//                WINDOW_HEIGHT);

        scene3D.setFill(site.getBackground());
        scene3D.setCamera(site.getCamera());
        
        final Group root = new Group(scene3D);
        //final Group root = new Group(scene3D, sceneUI);
        final Scene master = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        site.handleMouseEvents(master);
    	
        master.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.R) {
                site.buildSite("random");
                site.renderSite();
                scene3D.setRoot(site.getGroup());
            } else if (e.getCode() == KeyCode.L) {
            	site.buildSite("JR");
                site.renderSite();
                scene3D.setRoot(site.getGroup());
            }
        }); 
        
        stage.setScene(master);
        stage.show();
        
    }

    public static void main(String[] args) {
        launch(args);
    }
	
}