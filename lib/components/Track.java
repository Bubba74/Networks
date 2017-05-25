package components;

import java.awt.geom.Line2D;

import main.Main;

public class Track {

	private Path center;
	private Path left, right;
	
	double start_a;
	private double x, y, w, h;

	public double calcRay (double x, double y, double angle){

		double minimum = Main.maxDistance; //Greater than 0

		boolean verticalRay = false;
		
		double dx = 0, dy = 0, m = 0, b = 0;
		
		dx = Math.cos(angle);
		dy = Math.sin(angle);

		//Honestly not sure how often this will trip, but oh well.
		if (dx == 0){
			verticalRay = true;
		} else {
			m = dy/dx;
			b = y-m*x;
		}

		Path[] paths = {left, right};
		for (Path p: paths){

			//Left Path
			for (int i=0; i<center.getFilled(); i++){
				double x1, y1, x2, y2;
			
				
				x1 = p.getX(i);
				y1 = p.getY(i);
				x2 = p.getX((i+1<center.getFilled()?i+1:0));
				y2 = p.getY((i+1<center.getFilled()?i+1:0));
	
				if (x2-x1 == 0){
					if (verticalRay){
						//Ignore vertical ray contacting vertical line segments
						continue;
					}
					//Check intersection of ray with vertical line segment
					double a = x2*m+b; //Vertical intersection
	
					if (Math.min(y1,y2) <= a && a <= Math.max(y1,y2))
							if ((x2-x)/dx < minimum && (x2-x)/dx >= 0)
								minimum = (x2-x)/dx;
				} else {
					double m1 = (y2-y1)/(x2-x1);
					double b1 = y1-m1*x1;
					
					if (verticalRay){
						if (Math.min(x1, x2) <= x && x <= Math.max(x1, x2)){
							//Contact
							double distance = x*m1+b1-y;
							if (0 <= distance && distance < minimum)
								minimum = distance;
						}
						continue;
					}
	
					//Ray = m*x + b;
					//Line Segment = m1*x + b1;
					double xintercept = (b1-b)/(m-m1);

//					double intercept = (y1-(y2-y1)/(x2-x1)*x1-b)/(m-(y2-y1)/(x2-x1));
//					if (intercept != xintercept) System.out.println("AHFDAHRAHGAR");
					
					if (Math.min(x1,x2) <= xintercept && xintercept <= Math.max(x1,x2)){
						double distance = (xintercept-x)/dx;
						if ( 0 <= distance && distance < minimum) minimum = distance;
					}
				}//vertical line or not
			}//loop over paths
			
		}//loop over vertices
		return minimum;
	}//calcRay

				
	public Track (Path center_path, int radius){
		
		/*
		 * This constructor accepts a Path and a path radius;
		 * It creates two paths, on either side of the center
		 * path, spaced radius away from the center.
		 */
		this.center = center_path;
		left = new Path (center.getSize());
		right = new Path (center.getSize());
		
		start_a = Math.atan2(center.getY(1)-center.getY(0), center.getX(1)-center.getX(0));
		
		//Center path values
		double prev_x, prev_y, this_x, this_y, next_x, next_y;
		
		//The +- offsets from (this_x, this_y)
		double dx = 0, dy = 0;
		
		//For some reason, starting at i=0 doesn't work. So instead, we
		//  start with a filler and set it to the last point at the end.
		
		left.addPoint(0,0);
		right.addPoint(0,0);
		
		for (int i=1; i<center.getFilled(); i++){
			//Get x,y coords for last, this, and next points on center path.
			if (i == 0){
				prev_x = center.lastX();
				prev_y = center.lastY();
			} else {
				prev_x = center.getX(i-1);
				prev_y = center.getY(i-1);
			}
			this_x = center.getX(i);
			this_y = center.getY(i);
			
			if (i+1 == center.getFilled()){
				next_x = center.getX(0);
				next_y = center.getY(0);
			} else {
				next_x = center.getX(i+1);
				next_y = center.getY(i+1);
			}

			double leftDx = prev_x-this_x;
			double leftDy = prev_y-this_y;
			double rightDx = next_x-this_x;
			double rightDy = next_y-this_y;
			
			double left_angle, right_angle;
			double angle;
			
			left_angle = Math.atan2(leftDy, leftDx);
			right_angle = Math.atan2(rightDy, rightDx);
			
			if (left_angle == 0) left_angle = 2*Math.PI;
			
			double avg_x = (Math.cos(left_angle)+Math.cos(right_angle))/2;
			double avg_y = (Math.sin(left_angle)+Math.sin(right_angle))/2;

			angle = Math.atan2(avg_y, avg_x);
			
			dx = (int)(radius*Math.cos(angle));
			dy = (int)(radius*Math.sin(angle));
			
			/*
			 * An Attempt to figure out if dx,dy should switch, based off of the direction
			 * from next_x,next_y back to prev_x,prev_y
			 * 
				if (next_x-prev_x >= 0){
				if (next_y-prev_y >= 0){
					
				} else {
					dx *= -1;
					dy *= -1;
				}
				} else {
					if (next_y-prev_y > 0){
					} else {
						dx *= -1;
						dy *= -1;
					}
				}
			 * 
			 */
			
			/* 
			 * An attempt to figure out if dx,dy should switch, based off of the intersection
			 * of these two points. (The tracks *should* be parallel)
			 * 

			double scale = 0.5;
			dx /= scale;
			dy /= scale;
			
			if (Line2D.linesIntersect(left.lastX(), left.lastY(), this_x-dx, this_y-dy,
					right.lastX(), right.lastY(), this_x+dx, this_y+dy)){
				dx *= -1;
				dy *= -1;
			}
			
			dx *= scale;
			dy *= scale;
			
			 */
			

			/*
			 * An attempt to figure out if dx,dy should switch, based off of the correlation
			 * between crossing slopes from left_x,left_y and right_x,right_y to the extensions.
			 * More crossing lines are bad.
			 * 
			
			 */

			double dx1 = (this_x-dx - left.lastX()),  dy1 = (this_y-dy  - left.lastY());
			double dx2 = (this_x+dx - right.lastX()),  dy2 = (this_y+dy  - right.lastY());
			double dx3 = (this_x+dx - left.lastX()),  dy3 = (this_y+dy  - left.lastY());
			double dx4 = (this_x-dx - right.lastX()),  dy4 = (this_y-dy  - right.lastY());
			
//			if (Math.abs(Math.atan2(dy1,dx1)-Math.atan2(dy2, dx2)) > Math.abs(Math.atan2(dy3,dx3) - Math.atan2(dy4,dx4)) ){

			double da1 = Math.atan2(dy1, dx1)-Math.atan2(dy2,dx2);
			da1 = Math.abs(da1);
			if (da1 >= Math.PI) da1 -= 2*Math.PI;
			da1 = Math.abs(da1);
			
			double da2 = Math.atan2(dy3, dx3)-Math.atan2(dy4,dx4);
			da2 = Math.abs(da2);
			if (da2 >= Math.PI) da2 -= 2*Math.PI;
			da2 = Math.abs(da2);
			
			if (da1 > da2){
				dx *= -1;
				dy *= -1;
				System.out.printf("i: %d, Dx1: %f,  Dy1: %f,  Dx2: %f,  Dy2: %f,    Dx3: %f,  Dy3: %f,  Dxr: %f,  Dy4: %f\n",
						i, dx1, dy1, dx2, dy2, dx3, dy3, dx4, dy4);
			}
			else {
			}
			
			/*
			 * An attempt to figure out if dx,dy should switch, based off of the
			 */
			
			
			
			left.addPoint(this_x - dx, this_y - dy);
			right.addPoint(this_x + dx, this_y + dy);
			
//			System.out.printf("Avgx: %f\tAvgy: %f\t\tDx: %f\tDy: %f\n", avg_x, avg_y, dx, dy);
			
		}//for loop
		
		left.setPoint(0, left.lastX(), left.lastY());
		right.setPoint(0, right.lastX(), right.lastY());
		
		calcBounds();
		
	}//Track
	
	public void calcBounds(){
		double min_x = Integer.MAX_VALUE;
		double max_x = Integer.MIN_VALUE;
		double min_y = Integer.MAX_VALUE;
		double max_y = Integer.MIN_VALUE;
		
		Path[] paths = {left, right};
		
		for (Path path: paths)
			for (int i=0; i<path.getFilled(); i++){
				double x = path.getX(i);
				double y = path.getY(i);
				
				if (x < min_x) min_x = x;
				else if (x > max_x) max_x = x;

				if (y < min_y) min_y = y;
				else if (y > max_y) max_y = y;
			}

		x = min_x;
		y = min_y;
		w = max_x-min_x;
		h = max_y-min_y;
		
	}//calcBounds
	
	public Path getCenterPath (){
		return center;
	}
	public Path getLeftSide (){
		return left;
	}
	public Path getRightSide (){
		return right;
	}
	public void rotate (double angle){
		center.rotate(angle);
		left.rotate(angle);
		right.rotate(angle);
		
		start_a += angle;
		calcBounds();
	}//rotate
	
	
	public double getStartX (){
		return center.getX(0);
	}
	public double getStartY (){
		return center.getY(0);
	}
	public double getStartA (){
		return start_a;
	}
	
	public double getX(){
		return x;
	}//getX
	public double getY(){
		return y;
	}//getY
	public double getWidth(){
		return w;
	}//getWidth
	public double getHeight(){
		return h;
	}//getHeight
	
}//Track
