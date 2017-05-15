import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;

public class NetworkToDraw extends Network {

	private int x, y, width, height;
	
	public NetworkToDraw (int inputc, int hlayerc, int hlayersize, int outputc){
		super(inputc, hlayerc, hlayersize, outputc);
		
		x = 0;
		y = 0;
		width = Display.getWidth();
		height = Display.getHeight();
	}//NetworkToDraw
	
	public NetworkToDraw (int inputc, int hlayerc, int hlayersize, int outputc
						, int x, int y, int width, int height){
		super(inputc, hlayerc, hlayersize, outputc);
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}//NetworkToDraw
	
	public void render (){
		
		
		glColor3f(0,0,0);

		
		
		
	}//render
	
}//NetworkToDraw
