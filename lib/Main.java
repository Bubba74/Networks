
import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class Main {
	
	private static final int WIDTH = 800;
	private static final int HEIGHT = 500;

	static int x = 50, y = 50;
	static double z = 0, vel = 0.2, da = 0.02, rayScope = Math.PI/6;
	static int rays = 7;

	static MiniPID pid = new MiniPID(0.9,0,0);
	static boolean aiControlled = true;
	static boolean lShiftHeld = false;
	static int checkpoint = 0;
	static int maxDistance = 500;
	
	static long start_time = System.currentTimeMillis();
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

//		path.addLine(0, 2000);
//		path.addArc(Math.PI/16,16, 60);
//		path.addLine(Math.PI, 2000);
//		path.addArc(Math.PI/16,16, 60);

//		path.addLine(Math.PI/2, 200);
//		path.addLine(Math.PI, 200);
//		path.addLine(3*Math.PI/2, 200);

		path.addLine(0, 200);
		path.addArc(Math.PI/16, 16, 20);
		
		path.addLine(Math.PI, 200);
		path.addArc(Math.PI/16, 16, 20);
		
		track = new TrackToDraw(path, 60, Color.red);
		
		car.resetTo(track.getStartX(), track.getStartY(), 0);
		
		long lastTime = System.currentTimeMillis();
		long dt;
		
		while (!Display.isCloseRequested()){
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
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
		
		if (aiControlled){
			left = false;
			right = false;
			
			//Turn a -1 to 1 analog output signal from PID controller
			//into essentially a pwm signal
			boolean turning = false;
			long currentTime = System.currentTimeMillis();
			if ((currentTime-start_time)%(101-Math.abs(100*output)) < 1) turning = true;
			
			if (output < 0) left = turning;
			else if (output > 0) right = turning;
			
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

		if (car.didCollide(5))
			car.resetTo(track.getStartX(), track.getStartY(), 0);
		
		double targetAngle = car.getZ();
		
		double px = path.getX(checkpoint), py = path.getY(checkpoint);

		double dx = px - car.getX();
		double dy = py - car.getY();
		
		targetAngle = Math.atan2(dy, dx);
		if (dy*dy + dx*dx < 25) checkpoint++;
		
		output = pid.getOutput(car.getZ(), targetAngle);
		
	}//update
	
	public static void render (){
		glPushMatrix();
		glTranslated(-car.getX()+WIDTH/2, -car.getY()+HEIGHT/2, 0);
		car.render();
		track.render();
		
		glColor3f(1,1,1);
		path.render();
		
		//Highlight Checkpoint
		int x = path.getX(checkpoint);
		int y = path.getY(checkpoint);
		glColor3f(1,1,0);
		glBegin(GL_QUADS);
			glVertex2f(x-5, y-5);
			glVertex2f(x+5, y-5);
			glVertex2f(x+5, y+5);
			glVertex2f(x-5, y+5);
		glEnd();
		
		glPopMatrix();

		glColor3f(1,1,1);
		glBegin(GL_QUADS);
			glVertex2f(10,10);
			glVertex2f(210,10);
			glVertex2f(210,110);
			glVertex2f(10,110);
		glEnd();
		
		double pidx = 10+200/((output+2)/2);
		glColor3f(1,0,0);
		glBegin(GL_LINES);
			glVertex2d(pidx, 10);
			glVertex2d(pidx, 110);
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
