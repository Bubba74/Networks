package drawing;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3d;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2d;
import static org.lwjgl.opengl.GL11.glVertex2f;
import main.Driver;
import main.Main;

import org.lwjgl.input.Mouse;

public class Spotlight {
	
	//Essentially a dummy car
	private static Driver ghost = new Driver(0);
	
	private Driver car;
	public int[] box;
	
	private boolean dragView;
	private boolean followCar;
	
	private View view;
	
	public void setLocation (int x, int y){
		box[0] = x;
		box[1] = y;
	}//setLocation
	public Spotlight (Driver car){
		this.car = car;

		box = new int[4];
		box[0] = 0;
		box[1] = 0;
		box[2] = 200;
		box[3] = 150;
		
		dragView = false;
		followCar = false;

		view = new View ();
	}//Spotlight
	
	public void poll (Driver[] cars){
		
		updateCarView();
		
		View cam = Main.getView();
		
		//Poll Mouse
		while (Mouse.next()){
			if (dragView){
				box[0] += Mouse.getEventDX();
				box[1] -= Mouse.getEventDY();
			}
			//Check Button 0
			if (Mouse.getEventButton() == 0){
				//If Pressed
				if (Mouse.getEventButtonState() == true){
					int mx = (int)(Mouse.getX());
					int my = (int)(Main.HEIGHT-Mouse.getY());
					
					if (box[0]<mx && mx < box[0]+box[2] && box[1]<my && my<box[1]+10){
						dragView = true;
					} else if (box[0]+10 < mx && mx < box[0]+box[2] && box[1] < my && my < box[1]+box[3]){
						int bx = (mx-box[0]-10) /50;
						int by = (my-box[1]-10) /50;
						int bi = bx*2+by;
						
						System.out.println(bi);
						switch (bi){
						case 0:
							followCar = true;
							break;
						case 1:
							followCar = false;
							break;
						case 2:
							car.drawRays(true);
							break;
						case 3:
							car.drawRays(false);
							break;
						case 4:
							car.forceCompControl(true);
							break;
						case 5:
							car.forceCompControl(false);
							break;
						}
					} else {
						mx /= cam.sf;
						mx -= cam.x;
						my /= cam.sf;
						my -= cam.y;
						int closest = 0;
						int d2 = Integer.MAX_VALUE;
						for (int i=0; i<cars.length; i++){
							int dist2 = (int)( (mx-cars[i].getX())*(mx-cars[i].getX()) + (my-cars[i].getY())*(my-cars[i].getY())  );
							if (dist2 < d2){
								closest = i;
								d2 = dist2;
							}
						}
						car = cars[closest];
					}//Button 0 Pressed
				}//Pressed
				else {
					dragView = false;
				}
			}//Button 0
		
		}//Scan Mouse events
			
	}//update
	
	public void render(){
		//--------Draw Driver's Summary----------------//

		//Draw Boundary (0,0, 200, 160) padding=10
//				           x,y,  w,  h, p
		glPushMatrix();
		glTranslatef(box[0], box[1], 0);
		
		glColor3d(0.5,0.5,0.5);
		glBegin(GL_QUADS);
			glVertex2f(0,0);
			glVertex2f(box[2],0);
			glVertex2f(box[2],box[3]);
			glVertex2f(0,box[3]);
		glEnd();
		
		glTranslatef(10,10,0);
		
		int[][] buttons = {
				{1,1,1}
				,{0,0,0}
				,{0,1,0}
				,{1,0,0}
				,{1,1,0}
				,{0,0,1}
			};
		//Draw Buttons
		glBegin(GL_QUADS);
		
		for (int i=0; i<buttons.length; i++){
			glColor3f(buttons[i][0],buttons[i][1],buttons[i][2]);
			int x = i/2*50;
			int y = i%2*50;
			glVertex2f(x	,y);
			glVertex2f(x+50 ,y);
			glVertex2f(x+50 ,y+50);
			glVertex2f(x	,y+50);
		}
		glEnd();
		
		//Draw Car @ 280
		ghost.resetTo(180, 80, car.getZ());
		double[] color = car.getColor();
		ghost.setColor(color[0], color[1], color[2]);
		ghost.renderCar();
		
		glPopMatrix();
	
		//Draw Line to where the spotlight car is
		glColor3f(1,1,1);
		glBegin(GL_LINES);
			glVertex2f(box[0]+190, box[1]+90);
			glVertex2d(gridToScreen(car.getX(), true), gridToScreen(car.getY(), false));
		glEnd();
		
		//Draw velocity line
		glColor3f(1,0,1);
		glBegin(GL_LINES);
			glVertex2f(box[0]+190, box[1]+90);
			glVertex2d(box[0]+190+300*car.getVel()*Math.cos(car.getZ()), box[1]+90+300*car.getVel()*Math.sin(car.getZ()));
		glEnd();
		
	}//render
	
	private double gridToScreen (double gridValue, boolean xValue){
		View cam = Main.getView();
		
		if (xValue)
			gridValue += cam.x;
		else
			gridValue += cam.y;
		
		gridValue *= cam.sf;
		return gridValue;
	}//gridToScreen
	
	public View getCarView(){
		return view;
	}//getPerspective
	public void trackCar(boolean doTrack){
		followCar = doTrack;
	}//trackCar
	public boolean isFollowing (){
		return followCar;
	}//isFollowing
	
	public void setCar (Driver newCar){
		car = newCar;
	}//setCar
	public Driver getCar (){
		return car;
	}
	private void updateCarView (){
		view.x = Main.WIDTH/2 - car.getX();
		view.y = Main.HEIGHT/2 - car.getY();
		view.sf = 1;
	}
	
}//Spotlight
