
public class Car {

	private String name;

	private double x,y;
	private double z; //View angle in radians
	
	private double vel;
	
	public void update (long dt){
		float d = vel* (int)dt;
		x += Math.cos(z)*d;
		y += Math.sin(z)*d;
	}//update

	public void inputs (boolean left, boolean right){
		int command = 0;//0 = straight
		if (left && !right) command = -1;
		if (!left && right) command = 1;

		switch (command){
		case -1:
			z += vel;
			break;
		case 1:
			z -= vel;
			break;
		}
	}//inputs

	//-----------Basic Methods------------//
	
	public Car (String name, double x, double y, double z, double vel){
		this.name = name;

		this.x = x;
		this.y = y;
		this.z = z;

		this.vel = vel;
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
	public double getX (){
		return z;
	}

	public void resetTo (double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
}//Car
