package components;

import components.Track;

public class Car {

	public enum Control {
		kLeft, kStraight, kRight
	}
	
	private Control control;

	//(x,y) pair representing car's location
	private double x,y;
	//Angle towards which the car is 'looking' (0 == East)
	private double z; //radians
	
	//Forward Velocity
	private double vel;
	//delta angle for each loop
	private double turnVel;
	
	//Angles (relative to z) from which the car will read distance measurements
	private int rayCount;
	private double[] rayAngles;
	private double[] rayDistances;
	
	public void update (int dt){
		
		double d = vel* dt;
		x += Math.cos(z)*d;
		y += Math.sin(z)*d;
		
		switch (control){
		case kLeft:
			z -= turnVel*dt/10;
			break;
		case kRight:
			z += turnVel*dt/10;
			break;
		case kStraight:
			//Don't turn
			break;
		}
		
	}//update
	
	public void calcRays (Track track){
		//Run each ray through the track calcRay() method

		for (int i=0; i<rayCount; i++){
			double angle = z + rayAngles[i];
			rayDistances[i] = track.calcRay(x,y,angle);
		}
	}//calcRays
	
	public boolean didCollide (int tolerance){
		boolean collision = false;
		
		int count = 0;
		for (double d: rayDistances){
			if (d < tolerance){
				count++;
			}
		}
		
		if (count > 10) collision = true;
		
		return collision;
	}//didCollide

	public void inputs (boolean left, boolean right){
		int command = 0;//0 = straight
		if (left && !right) command = -1;
		if (!left && right) command = 1;

		switch (command){
		case -1:
			control = Control.kLeft;
			break;
		case 1:
			control = Control.kRight;
			break;
		default:
			control = Control.kStraight;
			break;
		}
	}//inputs

	//-----------Basic Methods------------//
	
	public Car (double x, double y, double z, double vel, double turningSpeed, double greatestRayAngle, int rayNum){
		this.control = Control.kStraight;

		this.x = x;
		this.y = y;
		this.z = z;

		this.vel = vel;
		this.turnVel = turningSpeed;
		
		rayCount = rayNum;
		rayAngles = new double[rayCount];
		fillAngles(greatestRayAngle);
		
		this.rayDistances = new double[rayCount];
	}//Complex Constructor
	public Car (){
		this.control = Control.kStraight;
		
		x = 0;
		y = 0;
		z = 0;
		
		vel = 0;
		turnVel = 0;
		
		rayCount = 0;
		rayAngles = null;
		rayDistances = null;
	}//Car

	public double getX (){
		return x;
	}
	public double getY (){
		return y;
	}
	public double getZ (){
		return z;
	}
	public double getVel (){
		return vel;
	}
	
	public void setVelocities (double forward, double turn){
		vel = forward;
		turnVel = turn;
	}//setVelocities
	public void accelerate(double acc){
		vel += acc;
	}//acclerate
	public void stop(){
		vel = 0.0;
	}//stop

	public int getRayNum(){
		return rayCount;
	}
	public double[] getRayAngles (){
		return rayAngles;
	}
	public double[] getRayDistances (){
		return rayDistances;
	}
	public void resetRays (double greatestAngle, int count){
		rayCount = count;
		
		rayAngles = new double[count];
		fillAngles(greatestAngle);
		
		rayDistances = new double[count];
	}//resetRays
	
	private void fillAngles (double greatestAngle){
		for (int i=0; i<rayCount; i++)
			rayAngles[i] = -greatestAngle+i*(2*greatestAngle/(rayCount-1));
	}//fillAngles
	
	public void resetTo (double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}//resetTo
	public void resetTo (double kX, double kY, double kZ, double kVel, double kTurnVel){
		x = kX;
		y = kY;
		z = kZ;
		
		vel = kVel;
		turnVel = kTurnVel;
	}//resetTo
}//Car