package main;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import drawing.CarToDraw;
import drawing.PathToDraw;

import drawing.TrackToDraw;

public class Main {
	
	public static int maxDistance = 500;
	private static final int WIDTH = 800;
	private static final int HEIGHT = 500;

	static double vel = 0.2, da = 0.02, rayScope = Math.PI/2;
	static int rays = 160;

	static Driver[] cars;
	static PathToDraw path;
	static TrackToDraw track;
	
	public static void main(String[] args) {
		initGL();
		
		cars = new Driver[10];
		for (int i=0; i<10; i++){
			cars[i] = new Driver(1, true, i==0?Keyboard.KEY_LSHIFT:-1);
			cars[i].resetRays(rayScope, rays);
			cars[i].setVelocities(vel*(i+1)/10.0, da);
			cars[i].setColor(1-i/10.0, i/10.0, 0);
			cars[i].drawRays(false);
		}
		
		path = new PathToDraw(100);

		path.addPoint(0, 0);

		//Small Track Field
//		path.addLine(0, 200);
//		path.addArc(Math.PI/16, 16, 20);
//		path.addLine(Math.PI, 200);
//		path.addArc(Math.PI/16, 16, 20);

		
		//Big Track Field
		path.addLine(0, 1000);
		path.addArc(Math.PI/16,16, 60);
		path.addLine(Math.PI, 1000);
		path.addArc(Math.PI/16,16, 60);
//		path.rotate(Math.PI);

		//Square
//		path.addLine(0, 200);
//		path.addLine(Math.PI/2, 200);
//		path.addLine(Math.PI, 200);
//		path.addLine(3*Math.PI/2, 200);

		
		track = new TrackToDraw(path, 60, Color.red);
		System.out.printf("Track -- (%d, %d, %d, %d)\n",track.getX(), track.getY(), track.getWidth(), track.getHeight());
		
		for (Driver car: cars){
			car.resetTo(track.getStartX(), track.getStartY(), track.getStartA());
		}
		
		long lastTime = System.currentTimeMillis();
		long dt;
		
		while (!Display.isCloseRequested()){
			
			if (!Keyboard.isKeyDown(Keyboard.KEY_C)) glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			dt = System.currentTimeMillis()-lastTime;
			lastTime = System.currentTimeMillis();
			
			poll();
			update(dt);
			render();
		
			Display.update();
			Display.sync(25);
		}
		
		Display.destroy();
	
	}//main
	
	public static void poll (){
	
		for (Driver car: cars){
			car.poll();
		}
		
		for (Driver car: cars){
			if (Keyboard.isKeyDown(Keyboard.KEY_UP)) car.accelerate(0.001);
			if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) car.accelerate(-0.001);
			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) car.stop();
			if (Keyboard.isKeyDown(Keyboard.KEY_R)) car.resetTo(path.getX(0), path.getY(0), 0);
		}
		
	}//poll
	
	public static void update (long dt){
		
		for (Driver car: cars){
			car.update(track, (int)dt);
	
			if (car.crashed()){
				car.resetTo(track.getStartX(), track.getStartY(), track.getStartA());
			}
		}
		
	}//update
	
	public static void render (){
		glPushMatrix();
//		glTranslated(-car.getX()+WIDTH/2, -car.getY()+HEIGHT/2, 0);

		double x = track.getX(), y = track.getY(), w = track.getWidth(), h = track.getHeight();
		if (w < WIDTH && h < HEIGHT){
			glTranslatef(WIDTH/2, HEIGHT/2, 0);
			glTranslated(-x, -y, 0);
			glTranslated(-w/2, -h/2, 0);
		} else {
			double scale = Math.min(WIDTH/w, HEIGHT/h);
			glScaled(scale, scale, 1);
			glTranslated(-x, -y, 0);

			if (WIDTH/w > HEIGHT/h){
				glTranslated(w/4, 0, 0);
			} else {
				glTranslated(0, h/4, 0);
			}
		
		}
		
		for (Driver car: cars){
			car.render();
		}
		
		track.render();
		
		glColor3f(1,1,1);
		path.render();
		
		glPopMatrix();
		
	}//render
	
	public static void initGL (){

		Display.setTitle("RaceCars");
		Display.setLocation(800, 500);

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

		glClearColor(0, 0, 1, 1);

	}//initGL
	

}//Main
