package main;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslated;

import java.awt.Color;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import drawing.PathToDraw;
import drawing.View;
import drawing.Spotlight;
import drawing.TrackToDraw;

public class Main {
	
	public static int maxDistance = 500;
	public static final int WIDTH = 1800;
	public static final int HEIGHT = 1000;

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
//			cars[i] = new Driver(1, true, (i==7?Keyboard.KEY_LSHIFT:-1));
			cars[i] = new Driver(1);
			cars[i].resetRays(rayScope, rays);
			cars[i].setVelocities(vel*(i+1)/20.0, da);
			cars[i].setColor(1-i/20.0, i/20.0, 0);
			cars[i].drawRays(false);
			cars[i].setPID(10, 0, 0);
		}
		spotlight = new Spotlight(cars[19]);
		
		path = new PathToDraw(100);

		path.addPoint(0, 0);

		//Small Track Field
//		path.addLine(0, 200);
//		path.addArc(Math.PI/16, 16, 20);
//		path.addLine(Math.PI, 200);
//		path.addArc(Math.PI/16, 16, 20);
		
		//Big Track Field
		path.addLine(0, 1000);
		path.addArc(Math.PI/32,32, 60);
		path.addLine(Math.PI, 1000);
		path.addArc(Math.PI/32,32, 60);

		//Square
//		path.addLine(0, 200);
//		path.addLine(Math.PI/2, 200);
//		path.addLine(Math.PI, 200);
//		path.addLine(3*Math.PI/2, 200);

//		path.rotate(4*Math.PI/2);
		
		trackCamera = new View ();
		view = new View();
		
		track = new TrackToDraw(path, 20, Color.red);

		updateTrackView();
		
		
		for (Driver car: cars){
			car.resetTo(track.getStartX(), track.getStartY(), track.getStartA());
		}
		
		spotlight.setLocation(WIDTH/2-100, HEIGHT/2-100);
		
		long lastTime = System.currentTimeMillis();
		long dt;
		
		while (!Display.isCloseRequested()){
			
			if (!Keyboard.isKeyDown(Keyboard.KEY_C)) glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			dt = System.currentTimeMillis()-lastTime;
			lastTime = System.currentTimeMillis();
			
			track.rotate(0.0001);
			updateTrackView();
			
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
		
		spotlight.poll(cars);
		
		if (spotlight.isFollowing()){
			view.setView(spotlight.getCarView());
		} else {
			view.setView(trackCamera);
		}
		
	}//update
	
	public static void render (){
		
		spotlight.render();
		
		glPushMatrix();

		glScaled(view.sf, view.sf, 1);
		glTranslated(view.x, view.y,0);
		
		for (Driver car: cars){
			car.renderRays();
		}
		for (Driver car: cars){
			car.renderCar();
		}
		
		track.render();
		
		glColor3f(1,1,1);
		path.render();
		
		glPopMatrix();
		
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
		
		if (temp[2] < WIDTH && temp[1] < HEIGHT){
			trackCamera.x = WIDTH/2-temp[0]-temp[2]/2;
			trackCamera.y = HEIGHT/2-temp[1]-temp[3]/2;
			trackCamera.sf = 1;
		} else {
			trackCamera.x = -temp[0];
			trackCamera.y = -temp[1];

			trackCamera.sf = temp[4];

			if (WIDTH/temp[2] > HEIGHT/temp[3]){
				trackCamera.x += WIDTH/2/trackCamera.sf;
				trackCamera.x -= temp[3]/2;
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

		glClearColor(0, 0, 0, 0);

	}//initGL
	

}//Main
