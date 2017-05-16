package drawing;

import static org.lwjgl.opengl.GL11.*;

import tracks.Path;

public class PathToDraw extends Path {

	public PathToDraw (int size){
		super(size);
	}

	public void render (){
		
		glBegin(GL_LINE_STRIP);
		
		for (int i=0; i<getFilled(); i++){
			glVertex2f(getX(i), getY(i));
		}

		glEnd();
		
	}//render
	
	public static void renderPath (Path path){
		
		glBegin(GL_LINE_STRIP);
		
		for (int i=0; i<path.getFilled(); i++){
			glVertex2f(path.getX(i), path.getY(i));
		}
		
		glEnd();
	}//renderPath

}//PathToDraw
