
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
	static double z = 0, vel = 0.2, da = 0.02, rayScope = Math.PI/2;
	static int rays = 2000;

	static int maxDistance = 500;
	static Network ai;
	static double[] outputs = new double[3];//left - center- right
	static boolean left;
	static boolean right;
	static int iteration = 0;
	
	static CarToDraw car;
	static PathToDraw path;
	static TrackToDraw track;
	
	public static void main(String[] args) {
		initGL();
		
		ai = new Network (rays, 20, 10, 3);

		car = new CarToDraw("Test",x, y, z, vel, da, rayScope, rays);
		path = new PathToDraw(100);

		path.addPoint(0, 0);

		path.addLine(0, 2000);
		path.addArc(Math.PI/16,16, 60);
		path.addLine(Math.PI, 2000);
		path.addArc(Math.PI/16,16, 60);

//		path.addLine(Math.PI/2, 200);
//		path.addLine(Math.PI, 200);
//		path.addLine(3*Math.PI/2, 200);

//		path.addLine(0, 200);
//		path.addArc(Math.PI/16, 16, 20);
//		
//		path.addLine(Math.PI, 200);
//		path.addArc(Math.PI/16, 16, 20);
//		
		car.resetTo(path.getX(0), path.getY(0), 0);
		
		track = new TrackToDraw(path, 60, Color.red);
		
		long lastTime = System.currentTimeMillis();
		long dt;
		
		while (!Display.isCloseRequested()){
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			dt = System.currentTimeMillis()-lastTime;
			lastTime = System.currentTimeMillis();
			
			poll();
			update(dt);
			car.calcRays(track);
			render();
		
			iteration++;
			Display.update();
			Display.sync(25);
		}
		
		Display.destroy();
	
	}//main
	
	public static void poll (){
	
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) car.forward();
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) car.reverse();
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) car.stop();
		
		left = Keyboard.isKeyDown(Keyboard.KEY_LEFT);
		right = Keyboard.isKeyDown(Keyboard.KEY_RIGHT);
		
		if (left && !right) {
			outputs[0] = 1;
			outputs[1] = 0;
			outputs[2] = 0;
		} else if (right && !left){
			outputs[0] = 0;
			outputs[1] = 0;
			outputs[2] = 1;
		} else {
			outputs[0] = 0;
			outputs[1] = 1;
			outputs[2] = 0;
		}
		
		car.inputs(left, right);
		
	}//poll
	
	public static void update (long dt){
		
		double[] inputs = new double[rays];
		for (int i=0; i<rays; i++){
			inputs[i] = car.getRayDistances()[i]/Main.maxDistance;
			if (inputs[i] > 1) inputs[i] = 1;
		}
//		double[] inputs = new double[1];
//		inputs[0] = car.getRayDistances()[1]-car.getRayDistances()[0];
		ai.loadInputs(inputs);
		ai.train(outputs, false);
		if (iteration % 100 == 0) System.out.println("Iteration # "+iteration);
		
		car.update(dt);
		
	}//update
	
	public static void render (){
		glPushMatrix();
		glTranslated(-car.getX()+WIDTH/2, -car.getY()+HEIGHT/2, 0);
		car.render();
//		track.render();
		
		glColor3f(1,1,1);
//		path.render();
		glPopMatrix();
		
		//Render Network Outputs
		int[][] x_vals = {
				{10, 210, 210},
				{230, 330, 430},
				{450, 450, 650}
		};
		int[][] y_vals = {
				{35, 10, 60},
				{60, 10, 60},
				{10, 60, 35}
		};

		double[] networkOutputs = ai.getOutputs();
		
		for (int i=0; i<networkOutputs.length; i++){
			glColor3d(networkOutputs[i], 0, 0);
			glBegin(GL_TRIANGLES);
				glVertex2i(x_vals[i][0], y_vals[i][0]);
				glVertex2i(x_vals[i][1], y_vals[i][1]);
				glVertex2i(x_vals[i][2], y_vals[i][2]);
			glEnd();
		}
		
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
