
public class Car {

	public enum Control {
		kLeft, kStraight, kRight
	}
	
	private String name;
	private Control control;
	
	private double x,y;
	private double z; //View angle in radians
	
	private double vel;
	private double turnVel;
	
	//Angles (relative to z) from which the car will read distance measurements
//	private double[] rayAngles = {
//			-Math.PI/2, -Math.PI/3, -Math.PI/6, 0, Math.PI/6, Math.PI/3, Math.PI/2
//		-6*Math.PI/12, -5*Math.PI/12, -4*Math.PI/12, -3*Math.PI/12, -2*Math.PI/12, -1*Math.PI/12, 0*Math.PI/12, 1*Math.PI/12, 2*Math.PI/12, 3*Math.PI/12, 4*Math.PI/12, 5*Math.PI/12, 6*Math.PI/12, 
//		};
	private double[] rayAngles;
	private double[] rayDistances;
	
	public void update (long dt){
		
		double d = vel* (int)dt;
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
		
		for (int i=0; i<rayAngles.length; i++){
			double angle = z + rayAngles[i];
			rayDistances[i] = track.calcRay(x,y,angle);
		}
		
	}//calcRays

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
	
	public Car (String name, double x, double y, double z, double vel, double turningSpeed, double greatestRayAngle, int rayCount){
		this.name = name;
		this.control = Control.kStraight;

		this.x = x;
		this.y = y;
		this.z = z;

		this.vel = vel;
		this.turnVel = turningSpeed;
		
		this.rayAngles = new double[rayCount];
		for (int i=0; i<rayCount; i++)
			rayAngles[i] = -greatestRayAngle+i*(2*greatestRayAngle/(rayCount-1));
		
		this.rayDistances = new double[rayCount];
	}

	public String getName (){
		return name;
	}
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
	
	public void forward(){
		vel += 0.001;
	}
	public void reverse(){
		vel -= 0.005;
	}
	public void stop(){
		vel = 0.0;
	}
	public double[] getRayAngles (){
		return rayAngles;
	}
	public double[] getRayDistances (){
		return rayDistances;
	}

	public void resetTo (double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
}//Car
