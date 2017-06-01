package multiplayer;

import java.awt.Dimension;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

public class HumanClient {

	public static Dimension screen = new Dimension (800, 500);
	public static final int WIDTH = (int)screen.getWidth();
	public static final int HEIGHT = (int)screen.getHeight();

	public static DriverClient driver;

	public static void main (String[] args){

		try {
			Socket client = new Socket("127.0.0.1", 9999);
		} catch (UnknownHostException e) {
		} catch (IOException e) {
		}
		
		initGL();


		while (!Display.isCloseRequested()){

			boolean left = Keyboard.isKeyDown(Keyboard.KEY_LEFT) ||
					Keyboard.isKeyDown(Keyboard.KEY_A);
			boolean right = Keyboard.isKeyDown(Keyboard.KEY_RIGHT) ||
					Keyboard.isKeyDown(Keyboard.KEY_D);

			boolean down = Keyboard.isKeyDown(Keyboard.KEY_DOWN) ||
					Keyboard.isKeyDown(Keyboard.KEY_S);
			boolean up = Keyboard.isKeyDown(Keyboard.KEY_UP) ||
					Keyboard.isKeyDown(Keyboard.KEY_W);

			double turn, speed;

			if (left && !right) turn = -1;
			else if (!left && right) turn = 1;
			else turn = 0;

			if (down && !up) speed = -1;
			else if (!down && up) speed = 1;
			else speed = 0;

			driver.inputs(turn, speed);
		}
		Display.destroy();
		Keyboard.destroy();

	}//main
	
	public static void initGL (){
		
	}

}//HumanClient
			
