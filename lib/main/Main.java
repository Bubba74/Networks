package main;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import multiplayer.DriverServer;

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
//	public static final Dimension screen = new Dimension(800, 500);
	
	public static final int WIDTH = (int) screen.getWidth()-10;
	public static final int HEIGHT = (int) screen.getHeight()-100;

	static double vel = 0.4, acc = 0.001, da = 0.02, rayScope = Math.PI/2;
	static int rays = 32;

	static List<Driver> cars = new ArrayList<Driver>();
	static RacewayServer server;
	
	static int raceIndex = -1;
	static PathToDraw path;
	static PathToDraw[] paths;
	static TrackToDraw track;
	static TrackToDraw[] tracks;
	
	static Spotlight spotlight;
	static View view;
	static View trackCamera;
	
	public static void main(String[] args) {
		
		paths = new PathToDraw[10];
		paths[0] = PathToDraw.convertPath(Path.importPath("BigTrack"));
		paths[1] = PathToDraw.convertPath(Path.importPath("Twists"));
		paths[2] = PathToDraw.convertPath(Path.importPath("Swirl"));
		paths[3] = PathToDraw.convertPath(Path.importPath("Complex2"));
		paths[4] = PathToDraw.convertPath(Path.importPath("Massive"));
		paths[5] = PathToDraw.convertPath(Path.importPath("Square_400"));
		paths[6] = PathToDraw.convertPath(Path.importPath("Switchbacks"));
		paths[7] = PathToDraw.convertPath(Path.importPath("Biguette"));
		paths[8] = PathToDraw.convertPath(Path.importPath("Testing"));
		paths[9] = PathToDraw.convertPath(Path.importPath("Wonky"));
		
		tracks = new TrackToDraw[10];
		for (int i=0; i<10; i++) tracks[i] = new TrackToDraw(paths[i], 40, Color.red);
		
		trackCamera = new View ();
		view = new View();
		
		switchToRaceTrack(0);
		
		int num = 5;
		for (int i=0; i<num; i++){
			cars.add( newCar(i, num, 1));
		}
		
		spotlight = new Spotlight(cars.get(4));
		spotlight.setLocation(0, 0);
		
		try {
			server = new RacewayServer(cars, 9999, rayScope, rays);
			server.start();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
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
		
		initGL();
		
		long lastTime = System.currentTimeMillis();
		long dt;
		
		while (!Display.isCloseRequested()){
			if (controllerIn) Controllers.poll();
			
			if (!Keyboard.isKeyDown(Keyboard.KEY_C)) glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
			dt = System.currentTimeMillis()-lastTime;
			lastTime = System.currentTimeMillis();
			
			poll();
			update(dt);
			render();
		
			Display.update();
			Display.sync(20);
		}
		
		Display.destroy();
		Controllers.destroy();
		
		System.out.println("Attempting to close server");
		server.close();
	
	}//main
	
	public static void poll (){
	
		spotlight.poll(cars);

		for (Driver car: cars){
			car.poll();
		}
		
		for (Driver car: cars){
			if (Keyboard.isKeyDown(Keyboard.KEY_EQUALS)) {
				car.accelerate(0.001);
				vel += 0.001;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_MINUS)) {
				car.accelerate(-0.001);
				vel -= 0.001;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) car.stop();
			if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
				car.resetTo(track.getStartX(), track.getStartY(), track.getStartA());
				car.setLapsCompleted(0);
			}
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_1)) switchToRaceTrack(1);
		if (Keyboard.isKeyDown(Keyboard.KEY_2)) switchToRaceTrack(2);
		if (Keyboard.isKeyDown(Keyboard.KEY_3)) switchToRaceTrack(3);
		if (Keyboard.isKeyDown(Keyboard.KEY_4)) switchToRaceTrack(4);
		if (Keyboard.isKeyDown(Keyboard.KEY_5)) switchToRaceTrack(5);
		if (Keyboard.isKeyDown(Keyboard.KEY_6)) switchToRaceTrack(6);
		if (Keyboard.isKeyDown(Keyboard.KEY_7)) switchToRaceTrack(7);
		if (Keyboard.isKeyDown(Keyboard.KEY_8)) switchToRaceTrack(8);
		if (Keyboard.isKeyDown(Keyboard.KEY_9)) switchToRaceTrack(9);
		if (Keyboard.isKeyDown(Keyboard.KEY_0)) switchToRaceTrack(0);
		
		while (Keyboard.next()){
			if (Keyboard.getEventKeyState() && Keyboard.getEventKey() == Keyboard.KEY_F3){
				System.out.println("Rescan");
				checkForControllers();
			}
		}
		
	}//poll	
	public static void update (long dt){
		for (int i=0; i<cars.size();){
			if (cars.get(i).getID() == -1){
				if (cars.get(i) == spotlight.getCar()){
					spotlight.setCar(cars.get(0));
				}
				cars.remove(i);
			} else
				i++;
		}
		
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
		
		if (!Keyboard.isKeyDown(Keyboard.KEY_C))
			track.fill();
		
		track.render();

		for (Driver car: cars){
			car.renderRays();
		}
		for (Driver car: cars){
			car.renderCar();
		}
		for (Driver car: cars){
			car.renderLapCounter();
		}
		
		glColor3f(1,1,1);
		path.render();
		
		glPopMatrix();
		spotlight.render();
		
	}//render

	public static void switchToRaceTrack (int index){
		if (index == raceIndex) return;
		
		raceIndex = index;
		
		path = paths[index];
		track = tracks[index];
		
		updateTrackView();
		for (Driver car: cars){
			car.resetTo(track.getStartX(), track.getStartY(), track.getStartA());
			car.setLapsCompleted(0);
		}
	}//switchToRaceTrack
	public static View getView (){
		return view;
	}
	public static Driver newCar(int i, double x, int id){
		
		Driver driver = new Driver(id);
		
		driver.resetRays(rayScope, rays);
		driver.setVelocities((i+1)*vel/x, acc, da);
		driver.setColor(1-i/x, i/x, 0);
		driver.drawRays(false);
		driver.setPID(10, 0, 0);
		
		return driver;
	}//newCar
	public static void prepCar (Driver driver){
		driver.resetRays(rayScope, rays);
		driver.setVelocities(vel, acc, da);
		driver.drawRays(false);
		driver.setPID(10, 0, 0);
		driver.resetTo(track.getStartX(), track.getStartY(), track.getStartA());
		
	}//prepCar
	
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
