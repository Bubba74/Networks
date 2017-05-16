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

	static int x = 50, y = 50;
	static double z = 0, vel = 0.1, da = 0.02, rayScope = Math.PI/2;
	static int rays = 1600;

	static MiniPID pid = new MiniPID(1,0,0);
	static AnalogToPWM aToPwm = new AnalogToPWM(100);
	static boolean aiControlled = true;
	static boolean lShiftHeld = false;
	
	static boolean left;
	static boolean right;
	static double output = 0;//From PID Controller
	
	static CarToDraw car;
	static PathToDraw path;
	static TrackToDraw track;
	
	public static void main(String[] args) {
		initGL();
		
		pid.setOutputLimits(1);
		pid.setSetpoint(0);
		
		car = new CarToDraw("Test",x, y, z, vel, da, rayScope, rays);
		path = new PathToDraw(100);

		path.addPoint(0, 0);

//		path.addLine(0, 1000);
//		path.addArc(Math.PI/16,16, 60);
//		path.addLine(Math.PI, 1000);
//		path.addArc(Math.PI/16,16, 60);

		path.addLine(0, 200);
		path.addLine(Math.PI/2, 200);
		path.addLine(Math.PI, 200);
		path.addLine(3*Math.PI/2, 200);

//		path.addLine(0, 200);
//		path.addArc(Math.PI/16, 16, 20);
		
//		path.addLine(Math.PI, 200);
//		path.addArc(Math.PI/16, 16, 20);
		
		track = new TrackToDraw(path, 60, Color.red);
		
		car.resetTo(track.getStartX(), track.getStartY(), 0);
		
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
	
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			if (!lShiftHeld){
				lShiftHeld = true;
				aiControlled = !aiControlled;
			}
		} else {
			lShiftHeld = false;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) car.forward();
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) car.reverse();
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) car.stop();
		if (Keyboard.isKeyDown(Keyboard.KEY_R)) car.resetTo(path.getX(0), path.getY(0), 0);
		
		if (aiControlled){
			left = false;
			right = false;
			
			//Turn a -1 to 1 analog output signal from PID controller
			//into essentially a pwm signal
			double pwm = aToPwm.getPWM(Math.abs(output));
			if (output < 0) pwm *= -1;

			if (pwm < 0.2) left = true;
			if (pwm > 0.2) right = true;
			
		} else {
			left = Keyboard.isKeyDown(Keyboard.KEY_LEFT);
			right = Keyboard.isKeyDown(Keyboard.KEY_RIGHT);
		}
		
		car.inputs(left, right);
		
	}//poll
	
	public static void update (long dt){
		
		double[] inputs = new double[rays];
		for (int i=0; i<rays; i++){
			inputs[i] = car.getRayDistances()[i]/Main.maxDistance;
		}

		car.update(dt);
		car.calcRays(track);

		if (car.didCollide(5)){
			car.resetTo(track.getStartX(), track.getStartY(), 0);
		}
		
		//PID loop input
		double offset = 0;
		double[] dists = car.getRayDistances();
		for (int i=0; i<rays/2; i++)
			offset -= dists[i];
		for (int i=rays/2; i<rays; i++)
			offset += dists[i];
		
		output = pid.getOutput(-offset/10000, 0);
		
	}//update
	
	public static void render (){
		glPushMatrix();
		glTranslated(-car.getX()+WIDTH/2, -car.getY()+HEIGHT/2, 0);
		car.render();
		track.render();
		
		glColor3f(1,1,1);
		path.render();
		
		glPopMatrix();

		//PID Background
		glColor3f(1,1,1);
		glBegin(GL_QUADS);
			glVertex2f(10,10);
			glVertex2f(210,10);
			glVertex2f(210,110);
			glVertex2f(10,110);
		glEnd();
		
		//Draw PID output (-1 to 1) -> (10 --> 210) red quad
		double pidx = 110+100*output;
		glColor3f(1,0,0);
		glBegin(GL_QUADS);
			glVertex2d(pidx-2, 10);
			glVertex2d(pidx-2, 110);
			glVertex2d(pidx+2, 110);
			glVertex2d(pidx+2, 10);
		glEnd();
		
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
