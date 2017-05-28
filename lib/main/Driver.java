package main;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Keyboard;

import components.Track;

import drawing.CarToDraw;

public class Driver extends CarToDraw {
	
	private int id;
	
	//Is the car controlled by the PID loop? Or by a human?
	private boolean aiControlled;
	//Keycode which will flip between ai and human control.
	private int controlKey;
	private int ejectKey;//Ai control
	//key pressed flag. Switch controls when (controlKey && !alreadySwitched)
	private boolean alreadySwitched;
	
	private double kP, kI, kD;
	private MiniPID pid;
	private double control;
	private int[] controlDisplay = {10, 10, 200, 100};
	
	private boolean crashed;

	public Driver (int id){
		super();
		
		this.id = id;
		
		aiControlled = true;
		controlKey = -1;//Don't EVER switch
		ejectKey = Keyboard.KEY_ESCAPE;
		alreadySwitched = false;
		
		kP = 1;
		kI = 0;
		kD = 0;
		pid = new MiniPID(kP, kI, kD);
		pid.setOutputLimits(1);
		pid.setSetpoint(0);
		
		crashed = false;
	}//Driver
	
	public Driver (int id, boolean aiControlled, int controlKey, int ejectKey){
		this(id);
		
		this.aiControlled = aiControlled;
		this.controlKey = controlKey;
		this.ejectKey = ejectKey;
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
		
		if (ejectKey > 0 && Keyboard.isKeyDown(ejectKey)){
			aiControlled = true;
		}
		
		if (!aiControlled){
			if (Main.controllerIn){
				control = Main.xbox.getRXAxisValue();
				
			} else {
				if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
					control = -1;
				else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
					control = 1;
				else
					control = 0;
			}
		}
		
		inputs(control);
		
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
		
		control = pid.getOutput(offset/10000, 0);
		
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
	
	
	public void rotate(double angle, double cos, double sin){
		super.rotate(angle, cos, sin);
	}//rotate
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
