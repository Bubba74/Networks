package main;

import static org.lwjgl.opengl.GL11.GL_POLYGON;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotated;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glVertex2d;
import static org.lwjgl.opengl.GL11.glVertex2f;

import org.lwjgl.input.Keyboard;

import components.Track;

import drawing.CarToDraw;

public class Driver extends CarToDraw {
	
	private int id;
	
	//Is the car controlled by the PID loop? Or by a human?
	private boolean aiControlled;
	//Keycode which will flip between ai and human control.
	private int controlKey;
	//key pressed flag. Switch controls when (controlKey && !alreadySwitched)
	private boolean alreadySwitched;
	
	private double kP, kI, kD;
	private MiniPID pid;
	private double analogControl;
	private int[] controlDisplay = {10, 10, 200, 100};
	
	private boolean drawPWM;
	private AnalogToPWM pwm;
	private int pwmControl;
	
	private boolean crashed;
	

	public Driver (int id){
		super();
		
		this.id = id;
		
		aiControlled = true;
		controlKey = -1;//Don't EVER switch
		alreadySwitched = false;
		
		kP = 1;
		kI = 0;
		kD = 0;
		pid = new MiniPID(kP, kI, kD);
		pid.setOutputLimits(1);
		pid.setSetpoint(0);
		
		drawPWM = false;
		pwm = new AnalogToPWM(100);
		pwmControl = 0;

		
		crashed = false;
	}//Driver
	
	public Driver (int id, boolean aiControlled, int controlKey){
		this(id);
		
		this.aiControlled = aiControlled;
		this.controlKey = controlKey;
		this.alreadySwitched = false;
	}//Driver
	
//------------      POLL     -----------     UPDATE         ---------        RENDER        ----------//
	public void poll (){
		
		if (controlKey > 0 && Keyboard.isKeyDown(controlKey)){
			if (!alreadySwitched){
				aiControlled = !aiControlled;
				alreadySwitched = true;
			}
		} else {
			alreadySwitched = false;
		}
		
		boolean left = false, right = false;
		
		if (aiControlled){
			//Turn a -1 to 1 analog output signal from PID controller
			//into essentially a pwm signal

			pwmControl = pwm.getPWM(Math.abs(analogControl));
			if (analogControl < 0) pwmControl *= -1;

			if (pwmControl < 0.2) left = true;
			else if (pwmControl > 0.2) right = true;

		} else {
			left = Keyboard.isKeyDown(Keyboard.KEY_LEFT);
			right = Keyboard.isKeyDown(Keyboard.KEY_RIGHT);
		}
		
		inputs(left, right);
		
	}//poll
	
	public void update (Track track, int dt){
		super.update(dt);
		
		calcRays(track);
		
		if (didCollide(5)){
			crashed = true;
		} else {
			calculatePID();
		}
	}//update

	public void renderCar (){
		if (!aiControlled){
			glPushMatrix();
			glTranslated(getX(), getY(), 0);
			glRotated(180*getZ()/Math.PI, 0, 0, 1);
			
			glColor3f(1,1,0);
			glBegin(GL_POLYGON);
				glVertex2f(-7, -7);
				glVertex2f(7, -7);
				glVertex2f(15, 0);
				glVertex2f(7, 7);
				glVertex2f(-7, 7);
			glEnd();
			
			glPopMatrix();
		}
		super.renderCar();
	}//renderCar
	public void renderRays (){
		super.renderRays();
	}//renderRays
	
	public void render (){
		//BROKEN
		if (drawPWM){
			//PID Background
//			glPushMatrix();
//			glTranslatef(-100, -50, 0);
			
				glColor3f(1,1,1);
				glBegin(GL_QUADS);
					glVertex2f(10,10);
					glVertex2f(210,10);
					glVertex2f(210,110);
					glVertex2f(10,110);
				glEnd();
				
				//Draw PID output (-1 to 1) -> (10 --> 210) red quad
				double pidx = 110+100*analogControl;
				glColor3f(1,0,0);
				glBegin(GL_QUADS);
					glVertex2d(pidx-2, 10);
					glVertex2d(pidx-2, 110);
					glVertex2d(pidx+2, 110);
					glVertex2d(pidx+2, 10);
				glEnd();
				
//			glPopMatrix();
		}
		
		//Draw the car and rays if so desired
		super.render();
		
	}//render

//-----------------------------------------------------------------------------------------------------//
		
	private void calculatePID(){
		/*
		 * PID Loop:
		 * - input: sum of the left rays minus the sum of the right rays
		 * - output: the variable control effort to adjust the car's direction
		 * 
		 */

		double offset = 0;
		double[] dists = getRayDistances();
		int len = getRayNum();
		
		for (int i=0; i<len/2; i++)
			offset += dists[i];
		
		for (int i=len/2; i<len; i++)
			offset -= dists[i];
		
		analogControl = pid.getOutput(offset/10000, 0);
		
		pwmControl = pwm.getPWM(analogControl);
		
	}//calculatePID
	
	
	//------------------Utility Functions--------------------//
	public int getID (){
		return id;
	}
	public boolean aiControlled(){
		return aiControlled;
	}//aiControlled
	public void forceCompControl (boolean compControl){
		aiControlled = !compControl;
	}//forceCompControl
	public void setControlKey (int key){
		controlKey = key;
	}//setControlKey
	public boolean crashed (){
		return crashed;
	}//crashed
	
	public void setControlDisplay (int x, int y, int w, int h){
		controlDisplay[0] = x;
		controlDisplay[1] = y;
		controlDisplay[2] = w;
		controlDisplay[3] = h;
	}//setControlDisplay
	
	public void resetTo (double newX, double newY, double newZ){
		super.resetTo (newX, newY, newZ);
		crashed = false;
	}//resetTo
	
	//------------------PID Controls------------------------//
	public void setPID(double p, double i, double d){
		kP = p;
		kI = i;
		kD = d;
		
		pid.setPID(kP, kI, kD);
	}
	public void setP (double p){
		kP = p;
		pid.setP(kP);
	}
	public void setI (double i){
		kI = i;
		pid.setI(kI);
	}
	public void setD (double d){
		kD = d;
		pid.setD(kD);
	}
	public double getP (){
		return kP;
	}
	public double getI (){
		return kI;
	}
	public double getD (){
		return kD;
	}
	
}//Driver
