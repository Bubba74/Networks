
import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class Main {
	
	private static final int WIDTH = 800;
	private static final int HEIGHT = 500;

	static CarToDraw car;
	static PathToDraw path;
	static TrackToDraw track;
	
	public static void main(String[] args) {
		initGL();

		car = new CarToDraw("Test",50,50,1,0.1,0.05);
		path = new PathToDraw(100);

		path.addPoint(200, 100);

		path.addLine(0, 200);
		path.addArc(Math.PI/16, 16, 20);
		
		path.addLine(Math.PI, 200);
		path.addArc(Math.PI/16, 16, 20);
		
		car.resetTo(path.getX(0), path.getY(0), 0);
		
		track = new TrackToDraw(path, 120, Color.red);
				
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
			Display.sync(15);
		}
		
		Display.destroy();
	
	}//main
	
	public static void poll (){
	
		car.inputs(Keyboard.isKeyDown(Keyboard.KEY_LEFT), Keyboard.isKeyDown(Keyboard.KEY_RIGHT));
		
	}//poll
	
	public static void update (long dt){
		
		car.update(dt);
		
		
	}//update
	
	public static void render (){
		glPushMatrix();
		glTranslated(-car.getX()+WIDTH/2, -car.getY()+HEIGHT/2, 0);
		car.render();
		track.render();
		
		glColor3f(1,1,1);
		path.render();
		glPopMatrix();
	}
	
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
