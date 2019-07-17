package edu.mit.ira.voxel;
import processing.core.PApplet;

public class Builder extends PApplet{
	
	public static void main(String[] args) {
		PApplet.main("edu.mit.ira.voxel.Builder");
	}
	
	// Runs once before setup()
	public void settings(){
		size(800, 400);
    }

	// Runs once upon initialization of class, after settings()
    public void setup(){
    	background(0);
    }
    
    // Runs every frame unless "noLoop()" is run
    public void draw(){
    	
    	noLoop();
    }
    
    // Triggered once when any key is pressed
    public void keyPressed() {
    	
    	loop();
    }
    
    // Triggered once when any mouse button is moved while pressed
    public void mouseDragged() {
    	
    	loop();
    }
    
    // Triggered once when any mouse button is pressed and released without moving
    public void mouseClicked() {
    	
    	loop();
    }
    
    // Triggered once when any mouse button is pressed
    public void mousePressed() {
    	
    	loop();
    }
    
    // Triggered once when any mouse button is released
    public void mouseReleased() {
    	
    	loop();
    }
    
    // Triggered once when any mouse button is moved
    public void mouseMoved() {
    	
    	loop();
    }

}