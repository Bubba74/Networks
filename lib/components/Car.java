package components;

import components.Track;

public class Car {

	private double controlSpeed;
	private double controlTurn;

	//(x,y) pair representing car's location
	private double x,y;
	//Angle towards which the car is 'looking' (0 == East)
	private double z; //radians
	
	//Forward Velocity
	private double startingVel;
	private double vel;
	private double acc;
	
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
		
		vel += controlSpeed*acc*dt/10;
		
		z += controlTurn*turnVel*dt/10;
		
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
		
		if (count > 3) collision = true;
		
		return collision;
	}//didCollide

	public void inputs (double controlValue){
		controlValue = limit(controlValue);
		
		controlTurn = controlValue;
		controlSpeed = 0;
	}//inputs
	public void inputs (double turn, double speed){
		turn = limit(turn);
		speed = limit(speed);
		controlTurn = turn;
		controlSpeed = speed;
	}//inputs
	private double limit (double value){
		//Limits value between -1 and 1
		if (value < -1) return -1;
		if (value > 1) return 1;
		return value;
	}//limit

	//-----------Basic Methods------------//
	
	public Car (double x, double y, double z, double vel, double acc, double turningSpeed, double greatestRayAngle, int rayNum){
		this.controlTurn = 0;
		
		this.x = x;
		this.y = y;
		this.z = z;

		this.vel = vel;
		startingVel = vel;
		this.acc = acc;
		this.turnVel = turningSpeed;
		
		rayCount = rayNum;
		rayAngles = new double[rayCount];
		fillAngles(greatestRayAngle);
		
		this.rayDistances = new double[rayCount];
	}//Complex Constructor
	public Car (){
		this.controlTurn = 0;
		
		x = 0;
		y = 0;
		z = 0;
		
		vel = 0;
		startingVel = 0;
		acc = 0;
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
	public double getCurAcc (){
		return controlSpeed*acc;
	}
	public double getMaxAcc (){
		return acc;
	}
	public void setMaxAcceleration (double acceleration){
		acc = acceleration;
	}
	
	public void setVelocities (double forwardVelocity, double forwardAcceleration, double turn){
		vel = forwardVelocity;
		startingVel = vel;
		acc = forwardAcceleration;
		
		turnVel = turn;
	}//setVelocities
	public void accelerate(double acc){
		vel += acc;
	}//acclerate
	public void stop(){
		vel = 0.0;
	}//stop
	public void rotate(double angle, double cos, double sin){
		z += angle;
		
		double new_x = x*cos+y*sin;
		y = x*sin-y*cos;
		x = new_x;
		
	}//rotate
	
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
	public double getControlTurn (){
		return controlTurn;
	}//getControlTurn
	public double getControlSpeed (){
		return controlSpeed;
	}//getControlSpeed
	
	public void resetTo (double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
		
//		vel = startingVel;
	}//resetTo
	public void resetTo (double kX, double kY, double kZ, double kVel, double kTurnVel){
		x = kX;
		y = kY;
		z = kZ;
		
		vel = kVel;
		turnVel = kTurnVel;
	}//resetTo
}//Car
