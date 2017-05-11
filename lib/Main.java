
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public class Main {

	public static void main(String[] args) {
t
		glfwInit();
		glfwWindowHint(GLFW_DECORATED, GLFW_TRUE);
		glfwWindowHint(GLFW_REFRESH_RATE, 10);
		
		long window = glfwCreateWindow(800, 600, "Test Window", 0, 0);
	
		if (window == 0){
			throw new RuntimeException("Failed to create window");
		}
		
		glfwMakeContextCurrent(window);
		
		
		GL.createCapabilities();
		glClearColor(0, 0, 1, 1);
		
		while (!glfwWindowShouldClose(window)){

			GL11.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			GL11.glColor3d(1.0, 0.0, 0.0);
			GL11.glBegin(GL_LINE);
			GL11.glVertex2d(0, 0);
			GL11.glVertex2d(100, 0);
			GL11.glVertex2d(100, 100);
			GL11.glVertex2d(0, 100);
			GL11.glVertex2d(50, 50);
			GL11.glEnd();
			
			glfwPollEvents();
			glfwSwapBuffers(window);
		}
		
		
		glfwDestroyWindow(window);
	
	}
	
	public static void initWindow (int width, int height){
		
		
		
	}
	

}//Main
