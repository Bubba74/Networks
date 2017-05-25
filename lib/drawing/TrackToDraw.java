package drawing;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;

import components.Path;
import components.Track;


public class TrackToDraw extends Track{

	private Color color;
	private double r, g, b;
	
	public TrackToDraw (Path center, int radius, Color c){
		super (center, radius);
		
		color = c;
		r = color.getRed()/255.0;
		g = color.getGreen()/255.0;
		b = color.getBlue()/255.0;
	}
	
	public void render (){
		
		glColor3d(r,g,b);
		PathToDraw.renderPath(getLeftSide());
		glColor3d(g,r,b);
		PathToDraw.renderPath(getRightSide());
		
	}//render
	
	public void fill (double br, double bg, double bb){
		
		Path left = getLeftSide();
		Path right = getRightSide();
		
		glColor3f(0,0,0);
		glBegin(GL_QUAD_STRIP);
		for (int i=0; i<left.getFilled(); i++){
			glVertex2d(left.getX(i), left.getY(i));
			glVertex2d(right.getX(i), right.getY(i));
		}
		
		glEnd();
		
//		glColor3d(br,bg,bb);
//		glBegin(GL_POLYGON);
//		for (int i=0; i<right.getFilled(); i++)
//			glVertex2d(right.getX(i), right.getY(i));
//		glEnd();
		
	}//fill

}//TrackToDraw
