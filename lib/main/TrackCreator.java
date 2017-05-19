package main;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import components.Sketch;

public class TrackCreator {
	
	public static final int WIDTH = 1800;
	public static final int HEIGHT = 1000;
	
	private static List<Point> points;
	private static boolean mousePressed;
	
	private static Scanner input = new Scanner(System.in);

	public static void main(String[] args) {
		initGL();
		
		points = new ArrayList<Point>();
		mousePressed = false;
		
		while (!Display.isCloseRequested()){
			
			if (!Keyboard.isKeyDown(Keyboard.KEY_C)) glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			poll();
			render();
		
			Display.update();
			Display.sync(25);
		}
		
		Display.destroy();
	
	}//main
	
	public static void addPoint(){
		Point p = new Point(Mouse.getX(), HEIGHT-Mouse.getY());
		points.add(p);
	}//addPoint
	
	private static void savePath(){
		
		System.out.print("What should your fantastic creation be called? ");
		String pathName = input.next();
		System.out.println("\nExcellent!!!");
		
		Sketch.exportPath(pathName, points);
		System.out.println("Track saved as "+pathName+"!");
		
	}//saveTrack
	
	private static void deletePath(){
		points.clear();
		mousePressed = false;
	}//deletePath
	
	public static void poll (){
		
		while (Mouse.next()){
			
			if (Mouse.getEventButton() == 0){
				if (Mouse.getEventButtonState()){
					mousePressed = true;

					addPoint();
				} else {
					mousePressed = false;
				}
				
			}//Check button 0
		}//Poll through Mouse events
		
		if (mousePressed){
			double lastX = points.get(points.size()-1).getX();
			double lastY = points.get(points.size()-1).getY();
			double mx = Mouse.getX();
			double my = HEIGHT-Mouse.getY();
			
			if (400 < (my-lastY)*(my-lastY)+(mx-lastX)*(mx-lastX)){
				addPoint();
			}
		}
		
		//Check for Control+S
		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
		while (Keyboard.next()){
			if (Keyboard.getEventKey() == Keyboard.KEY_S){
				if (Keyboard.getEventKeyState()){
					savePath();
				}
			}
			if (Keyboard.getEventKey() == Keyboard.KEY_D){
				if (Keyboard.getEventKeyState()){
					deletePath();
				}
			}
		}
		else
			while(Keyboard.next()){
				if (Keyboard.getEventKey() == Keyboard.KEY_BACK && Keyboard.getEventKeyState()){
					if (points.size() > 0) points.remove(points.size()-1);
				}
			}
		
	}//poll	
	
	public static void render (){
		
		glColor3f(1,1,1);
		glBegin(GL_LINE_STRIP);
		for (int i=0; i<points.size(); i++){
			glVertex2d(points.get(i).getX(), points.get(i).getY());
		}
		if (points.size() > 0){
			glVertex2d(points.get(0).getX(), points.get(0).getY());
		}
		glEnd();
		
	}//render
	
	public static void initGL (){

		Display.setTitle("Track Creator");
		Display.setLocation(0, 0);

		try {
			Display.setDisplayMode(new DisplayMode(WIDTH,HEIGHT));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0,WIDTH,HEIGHT,0,1,-1);
		glMatrixMode(GL_MODELVIEW);

		glClearColor(0, 0, 0, 0);

	}//initGL
	

}//TrackCreator
