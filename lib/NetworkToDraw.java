import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;

public class NetworkToDraw extends Network {

	private int x, y, width, height;
	
	private int bufferSize;
	
	private int index;
	private double[] errors;
	
	
	public NetworkToDraw (int inputc, int hlayerc, int hlayersize, int outputc){
		super(inputc, hlayerc, hlayersize, outputc);
		
		x = 0;
		y = 0;
		width = Display.getWidth();
		height = Display.getHeight();
		
		bufferSize = 200;
		index = 0;
		errors = new double[bufferSize];
		for (int i=0; i<bufferSize; i++) errors[i] = 1;

	}//NetworkToDraw
	
	public NetworkToDraw (int inputc, int hlayerc, int hlayersize, int outputc
						, int x, int y, int width, int height){
		super(inputc, hlayerc, hlayersize, outputc);
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		bufferSize = 200;
		index = 0;
		errors = new double[bufferSize];
		for (int i=0; i<bufferSize; i++) errors[i] = 1;
		
	}//NetworkToDraw
	
	public void train (double[] targets, boolean draw){
		super.train(targets, draw);
		
		double[] outputs = super.getOutputs();
		
		double error = 0;
		for (int i=0; i<outputs.length; i++){
			error += (targets[i]-outputs[i])*(targets[i]-outputs[i])/2;
		}
		
		errors[index] = error;
		index++;
		if (index == bufferSize) index = 0;
		
	}//train
	
	public void render (){
		glColor3f(1,0,0);
		
		double dx = ((double)width)/bufferSize;
		double draw_x = x;
		
		glBegin(GL_LINE_STRIP);
		
		for (int i=0; i<errors.length; i++){
			glVertex2d(draw_x, y+(1-errors[i])*height);
			draw_x += dx;
		}
		
		glEnd();

		//Draw Green Update Marker
		glColor3f(0,1,0);
		glBegin(GL_LINES);
			glVertex2d(x+dx*index, y);
			glVertex2d(x+dx*index, y+height);
		glEnd();
		
	}//render
	
}//NetworkToDraw
