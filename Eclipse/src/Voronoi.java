import java.util.ArrayList;
import processing.core.PApplet;

public class Voronoi extends PApplet{

	ArrayList<Node> nodes;
	
	public static void main(String[] args) {
		PApplet.main("Voronoi");
	}
	
	public void settings(){
		size(400, 400);
    }

    public void setup(){
    	nodes = new ArrayList<Node>();
    	for(int i=0; i<10; i++) {
    		float x = random(width);
    		float y = random(height);
    		nodes.add(new Node(x,y));
    	}
    }

    public void draw(){
    	background(0);
    	int col = color(0, 255, 0);
    	fill(col);
    	for(Node n : nodes) ellipse(n.x, n.y, 5, 5);
    }
    
    public void mousePressed() {
    	nodes.add(new Node(mouseX, mouseY));
    }
    
    public void keyPressed() {
    	switch(key) {
    		case 'c':
    			nodes.clear();
    			break;
    	}
    }

}