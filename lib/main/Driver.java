package main;

import drawing.CarToDraw;

public class Driver extends CarToDraw {
	
	private int id;
	
	private double kP, kI, kD;
	private MiniPID pid;
	

	public Driver (int id){
		super();
		
		this.id = id;
		
		kP = 0;
		kI = 0;
		kD = 0;
		pid = new MiniPID(0, 0, 0);
		
	}//Driver
	
	public void render (){
		super.render();
	
	}
	
	//PID Controls
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
