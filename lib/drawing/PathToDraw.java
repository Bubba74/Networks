package drawing;

import static org.lwjgl.opengl.GL11.*;

import components.LinePath;
import components.Sketch;


public class PathToDraw extends LinePath {

	public PathToDraw (){
		super();
	}

	public void render (){
		
		glBegin(GL_LINE_STRIP);
		
		for (int i=0; i<size(); i++){
			glVertex2d(getLine(i).x1,
						getLine(i).y1);
		}

		glEnd();
		
	}//render
	
	public static void renderPath (LinePath path){
		glBegin(GL_LINE_STRIP);
		
		for (int i=0; i<path.size(); i++){
			glVertex2d(path.getLine(i).x1,
						path.getLine(i).y1);
		}
		
		glEnd();
	}//renderPath
	
	public static PathToDraw convertPath (LinePath p){
		PathToDraw path = new PathToDraw();
		
		for (int i=0; i<p.size(); i++){
			path.addLine(p.getLine(i));
		}
		
		return path;
	}

}//PathToDraw
