package main;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;
import java.awt.Dimension;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import components.Path;

import drawing.PathToDraw;
import drawing.Spotlight;
import drawing.TrackToDraw;
import drawing.View;

public class Main {

	public static boolean controllerIn;
	public static Controller xbox;
	
	public static int maxDistance = 500;
	
	public static final Dimension screen = (Dimension) java.awt.Toolkit.getDefaultToolkit().getScreenSize();
//	public static final Dimension screen = new Dimension(1800, 1000);
	public static final int WIDTH = (int) screen.getWidth()-10;
	public static final int HEIGHT = (int) screen.getHeight()-100;

	static double vel = 0.3, da = 0.02, rayScope = Math.PI/2;
	static int rays = 160;

	static Driver[] cars;
	static PathToDraw path;
	static TrackToDraw track;
	
	static Spotlight spotlight;
	static View view;
	static View trackCamera;
	
	public static void main(String[] args) {
		initGL();
		
		cars = new Driver[20];
		for (int i=0; i<20; i++){
			cars[i] = new Driver(1);
			cars[i].resetRays(rayScope, rays);
			cars[i].setVelocities(vel*(i+1)/20.0, da);
			cars[i].setColor(1-i/20.0, i/20.0, 0);
			cars[i].drawRays(false);
			cars[i].setPID(10, 0, 0);
		}
		spotlight = new Spotlight(cars[19]);
		spotlight.setLocation(0, 0);
//		path = PathToDraw.convertPath(Path.importPath("Square_400"));
//		path = PathToDraw.convertPath(Path.importPath("Complex2"));
//		path = PathToDraw.convertPath(Path.importPath("Blob"));
		path = PathToDraw.convertPath(Path.importPath("BigTrack"));
		
		trackCamera = new View ();
		view = new View();
		
		track = new TrackToDraw(path, 40, Color.red);

		updateTrackView();
		
		for (Driver car: cars){
			car.resetTo(track.getStartX(), track.getStartY(), track.getStartA());
		}

		spotlight.setLocation(WIDTH/2-100, HEIGHT/2-100);
		
		long lastTime = System.currentTimeMillis();
		long dt;

		
		//Xbox Support
		try {
			Controllers.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		controllerIn = false;
		xbox = null;
		checkForControllers();
		/**/
		
				
		while (!Display.isCloseRequested()){
			Controllers.poll();
//			if (controllerIn)
//				checkForControllers();
			
			if (!Keyboard.isKeyDown(Keyboard.KEY_C)) glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
			dt = System.currentTimeMillis()-lastTime;
			lastTime = System.currentTimeMillis();
			
			track.rotate(0.001);
			updateTrackView();
			
			poll();
			update(dt);
			render();
		
			Display.update();
			Display.sync(25);
		}
		
		Display.destroy();
		Controllers.destroy();
	
	}//main
	
	public static void poll (){
	
		spotlight.poll(cars);

		for (Driver car: cars){
			car.poll();
		}
		
		for (Driver car: cars){
			if (Keyboard.isKeyDown(Keyboard.KEY_UP)) car.accelerate(0.001);
			if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) car.accelerate(-0.001);
			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) car.stop();
			if (Keyboard.isKeyDown(Keyboard.KEY_R)) car.resetTo(track.getStartX(), track.getStartY(), track.getStartA());
		}
		
		while (Keyboard.next()){
			if (Keyboard.getEventKeyState() && Keyboard.getEventKey() == Keyboard.KEY_F3){
				System.out.println("Rescan");
				checkForControllers();
			}
		}
		
	}//poll	
	public static void update (long dt){
		
		for (Driver car: cars){
			car.update(track, (int)dt);
	
			if (car.crashed()){
				car.resetTo(track.getStartX(), track.getStartY(), track.getStartA());
			}
		}
		
		if (spotlight.isFollowing()){
			view.setView(spotlight.getCarView());
		} else {
			view.setView(trackCamera);
		}
		
	}//update
	
	public static void render (){
		
		
		glPushMatrix();

		glScaled(view.sf, view.sf, 1);
		glTranslated(view.x, view.y,0);
		
		track.fill();
		track.render();

		for (Driver car: cars){
			car.renderRays();
		}
		for (Driver car: cars){
			car.renderCar();
		}
		
//		glColor3f(1,1,1);
//		path.render();
		
		glPopMatrix();
		spotlight.render();
		
	}//render
	
	public static View getView (){
		return view;
	}

	public static void updateTrackView (){
		double[] temp = new double[5];
		temp[0] = track.getX(); 
		temp[1] = track.getY(); 
		temp[2] = track.getWidth();
		temp[3] = track.getHeight();
		temp[4] = Math.min(WIDTH/temp[2], HEIGHT/temp[3]);
		
		if (temp[2] < WIDTH && temp[3] < HEIGHT){
			trackCamera.x = WIDTH/2-temp[0]-temp[2]/2;
			trackCamera.y = HEIGHT/2-temp[1]-temp[3]/2;
			trackCamera.sf = 1;
		} else {
			trackCamera.x = -temp[0];
			trackCamera.y = -temp[1];

			trackCamera.sf = temp[4];

			if (WIDTH/temp[2] > HEIGHT/temp[3]){
				trackCamera.x += WIDTH/2/trackCamera.sf;
				trackCamera.x -= temp[2]/2;
			} else {
				trackCamera.y += HEIGHT/2/trackCamera.sf;
				trackCamera.y -= temp[3]/2;
			}
		}
		view.setView(trackCamera);
	}//updateTrackView
	
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

		glClearColor(0, 0.2f, 0, 0);

	}//initGL
	
	private static void checkForControllers(){
		Controllers.poll();

		if (Controllers.getControllerCount() > 0){
			controllerIn = true;
			xbox = Controllers.getController(0);
		} else {
			controllerIn = false;
		}
	}//checkForControllers

}//Main
