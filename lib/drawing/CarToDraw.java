package drawing;

import static org.lwjgl.opengl.GL11.*;

import main.Car;
import main.Main;

public class CarToDraw extends Car {
	
	/*
		Maintains management of Car class, but adds a render method
		which uses opengl graphics through lwjgl.
	*/
	private boolean drawCar, drawRays;
	private double[] color;
	
	public CarToDraw (double x, double y, double z
		, double vel, double turningSpeed
		, double greatestRayAngle, int rayCount){

		super (x, y, z, vel, turningSpeed, greatestRayAngle, rayCount);
		
		drawCar = true;
		drawRays = true;
		
		color = new double[3];
		color[0] = 1;
		color[1] = 0;
		color[2] = 0;
	}//CarToDraw
	public CarToDraw (){
		super();
		
		drawCar = true;
		drawRays = true;
		
		color = new double[3];
		color[0] = 1;
		color[1] = 0;
		color[2] = 0;
	}//CarToDraw
	
	
	public void setColor(double red, double green, double blue){
		color[0] = red;
		color[1] = green;
		color[2] = blue;
	}//setColor
	public double[] getColor (){
		return color;
	}//getColor
	
	public void drawCar (boolean draw){
		drawCar = draw;
	}//drawCar
	public boolean getDrawCar(){
		return drawCar;
	}//getDrawCar
	public void drawRays (boolean draw){
		drawRays = draw;
	}//drawCar
	public boolean getDrawRays(){
		return drawRays;
	}//getDrawRays	
	
	public void renderRays (){
		if (drawRays){
			int maxDistance = Main.maxDistance;
			
			double[] angles = getRayAngles();
			double[] distances = getRayDistances();
			
			glBegin(GL_LINES);
			for (int i=0; i<angles.length; i++){
				//Set color relative to distance
				glColor3d(0, 1-distances[i]/maxDistance, 0);
				glVertex2d(getX(), getY());
				glVertex2d(getX()+Math.cos(getZ()+angles[i])*distances[i],
						getY()+Math.sin(getZ()+angles[i])*distances[i]);
			}
			glEnd();
		}
	}//renderRays
	
	public void renderCar (){
		
		if (drawCar){
			//Draw car
			glPushMatrix();
			
			glTranslated(getX(), getY(), 0);
			glRotated(180*getZ()/Math.PI,0,0,1);
			
			glColor3d(color[0], color[1], color[2]);
			
			glBegin(GL_POLYGON);
				glVertex2d(-5,-5);
				glVertex2d(5,-5);
				glVertex2d(13,0);
				glVertex2d(5,5);
				glVertex2d(-5,5);
			glEnd();
	
			glPopMatrix();
		}
		
	}//renderCar
	
	public void render (){
		renderRays();
		renderCar();
	}//render

}//CarToDraw
