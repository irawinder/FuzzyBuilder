package edu.mit.ira.builder.fx;

import java.util.ArrayList;
import java.util.Random;

import javafx.application.Application;
import javafx.scene.input.MouseEvent;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;


public class GUI extends Application {
	private ArrayList<Box> boxArray;
    private Scene scene;
    private Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);

    public Parent createContent() throws Exception {
        
        // Box Array
        Translate origin = new Translate(-5, 0, -5);
        boxArray = new ArrayList<Box>();
        for (int i=0; i<11; i++ ) {
        	for (int j=0; j<11; j++ ) {
        		
        		// Box Dimension and location
        		Random rand = new Random();
        		double h = rand.nextFloat();
        		Box b = new Box(0.85, h, 0.85);
        		Translate pos = new Translate(i, -0.5*h, j);
        		b.getTransforms().addAll(rotateY, origin, pos);
        		
        		// Box Color
        		double hue = 360 * rand.nextFloat();
        		PhongMaterial material = new PhongMaterial(Color.hsb(hue, 1.0, 1.0, 0.9));
        		b.setMaterial(material);
        		boxArray.add(b);
        	}
        }

        // Create and position camera
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.getTransforms().addAll(
        new Rotate(-20, Rotate.Y_AXIS), rotateX, new Translate(0, 0, -50));

        // Build the Scene Graph
        Group root = new Group();
        root.getChildren().add(camera);
        for (Box b : boxArray) root.getChildren().add(b);

        // Use a SubScene
        SubScene subScene = new SubScene(root, 1000, 600, true, 
             SceneAntialiasing.BALANCED);
        subScene.setFill(Color.TRANSPARENT);
        subScene.setCamera(camera);

        return new Group(subScene);
    }

    private double mousePosX, mousePosY = 0;
    private void handleMouseEvents() {
        scene.setOnMousePressed((MouseEvent me) -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
        });

        scene.setOnMouseDragged((MouseEvent me) -> {
            double dx = + (mousePosX - me.getSceneX());
            double dy = - (mousePosY - me.getSceneY());
            if (me.isPrimaryButtonDown()) {
            	
            	double angleX = rotateX.getAngle() - (dy / 10 * 360) * (Math.PI / 180);
            	double angleY = rotateY.getAngle() - (dx / 10 * -360) * (Math.PI / 180);
            	
            	// Can view above and below model, but
            	// Don't allow view to flip upside down
            	angleX = ensureRange(angleX, -90, 90);
            	
                rotateX.setAngle(angleX);
                rotateY.setAngle(angleY);
            }
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setResizable(false);
        scene = new Scene(createContent());
        handleMouseEvents();
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    double ensureRange(double value, double min, double max) {
    	return Math.min(Math.max(value, min), max);
    }
	
}

/* Example GUI Implementation to demonstration mouse event handling
 * 
public class GUI extends Application {
	
	private static final String OUTSIDE_TEXT = "Outside Label";

	public static void main(String[] args) { launch(args); }

	@Override public void start(final Stage stage) {
		final Label reporter = new Label(OUTSIDE_TEXT);
		Label monitored = createMonitoredLabel(reporter);

		VBox layout = new VBox(25);
		layout.setStyle("-fx-background-color: cornsilk; -fx-padding: 20px;");
		layout.getChildren().setAll(
				monitored,
				reporter
				);
		layout.setPrefWidth(600);

		stage.setScene(
				new Scene(layout)
				);

		stage.show();
	}

	private Label createMonitoredLabel(final Label reporter) {
		final Label monitored = new Label("Mouse Location Monitor");

		monitored.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 20px;");

		monitored.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent event) {
				String msg =
						"(x: "       + event.getX()      + ", y: "       + event.getY()       + ") -- " +
								"(sceneX: "  + event.getSceneX() + ", sceneY: "  + event.getSceneY()  + ") -- " +
								"(screenX: " + event.getScreenX()+ ", screenY: " + event.getScreenY() + ")";

				reporter.setText(msg);
			}
		});

		monitored.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent event) {
				reporter.setText(OUTSIDE_TEXT);
			}
		});

		return monitored;
	}
}
*/