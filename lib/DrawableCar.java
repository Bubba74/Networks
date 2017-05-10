
public class DrawableCar extends Car {
	
	/*
		Maintains management of Car class, but adds a render method
		which uses opengl graphics through lwjgl.
	*/

	public DrawableCar (String name, double x, double y, double z, double vel){
		super (name, x, y, z, vel);
	}

	public void render (){
		
	}
}//DrawableCar
