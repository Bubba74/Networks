package drawing;

import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2d;

import components.Path;


public class PathToDraw extends Path {

	public PathToDraw (int size){
		super(size);
	}

	public void render (){
		
		glBegin(GL_LINE_STRIP);
		
		for (int i=0; i<getFilled(); i++){
			glVertex2d(getX(i), getY(i));
		}

		glEnd();
		
	}//render
	
	public static void renderPath (Path path){
		
		glBegin(GL_LINE_STRIP);
		
		for (int i=0; i<path.getFilled(); i++){
			glVertex2d(path.getX(i), path.getY(i));
		}
		
		glEnd();
	}//renderPath
	
	public static PathToDraw convertPath (Path p){
		PathToDraw path = new PathToDraw(p.getFilled());
		
		for (int i=0; i<p.getFilled(); i++){
			path.addPoint(p.getX(i), p.getY(i));
		}
		
		return path;
	}

}//PathToDraw
