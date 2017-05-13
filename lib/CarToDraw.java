
import static org.lwjgl.opengl.GL11.*;

public class CarToDraw extends Car {
	
	/*
		Maintains management of Car class, but adds a render method
		which uses opengl graphics through lwjgl.
	*/
	public CarToDraw (String name, double x, double y, double z, double vel, double turningSpeed){
		super (name, x, y, z, vel, turningSpeed);
	}

	public void render (){
		
		glPushMatrix();
		
		glTranslated(getX(), getY(), 0);
		glRotated(180*getZ()/Math.PI,0,0,1);
		
		glColor3f(1,0,0);
		
		glBegin(GL_POLYGON);
			glVertex2d(-5,-5);
			glVertex2d(5,-5);
			glVertex2d(13,0);
			glVertex2d(5,5);
			glVertex2d(-5,5);
		glEnd();

		glPopMatrix();


		glColor3f(0,1,0);
		
		double[] angles = getRayAngles();
		double[] distances = getRayDistances();

		glBegin(GL_LINES);
		for (int i=0; i<angles.length; i++){
			glVertex2d(getX(), getY());
			glVertex2d(getX()+Math.cos(getZ()+angles[i])*distances[i],
				   getY()+Math.sin(getZ()+angles[i])*distances[i]);
		}
		glEnd();
		
	}//render

}//CarToDraw
