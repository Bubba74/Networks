package drawing;

import static org.lwjgl.opengl.GL11.glColor3f;

import java.awt.Color;

import tracks.Path;
import tracks.Track;

public class TrackToDraw extends Track{

	private Color color;
	private int r, g, b;
	
	public TrackToDraw (Path center, int radius, Color c){
		super (center, radius);
		
		color = c;
		r = color.getRed();
		g = color.getGreen();
		b = color.getBlue();
	}
	
	public void render (){
		
		glColor3f(r,g,b);
		PathToDraw.renderPath(getLeftSide());
		glColor3f(g,r,b);
		PathToDraw.renderPath(getRightSide());
		
	}//render

}//TrackToDraw
