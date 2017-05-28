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
		
		
		long time_start = System.currentTimeMillis();
		
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
			 * An attempt to figure out if dx,dy should switch, based off of whether the new
			 * lines intersect the center path (yes, the whole path...)
			 * 
			 */

			//Default Line to add to left side
			double default_lx = this_x - dx;
			double default_ly = this_y - dy;
			
			if (newLeftPointIntersectsCenterPath(default_lx, default_ly)){
				
				dx *= -1;
				dy *= -1;
			}
			
			left.addPoint(this_x - dx, this_y - dy);
			right.addPoint(this_x + dx, this_y + dy);
			
//			System.out.printf("Avgx: %f\tAvgy: %f\t\tDx: %f\tDy: %f\n", avg_x, avg_y, dx, dy);
			
		}//for loop
		
		left.setPoint(0, left.lastX(), left.lastY());
		right.setPoint(0, right.lastX(), right.lastY());
		
		long time_end = System.currentTimeMillis();
		
		System.out.printf("It took %.3f seconds to generate the track\n",(float)(time_end-time_start)/1000.0);
		
		
		calcBounds();
		
	}//Track
	
	private boolean newLeftPointIntersectsCenterPath (double x2, double y2){
		double x1 = left.lastX();
		double y1 = left.lastY();
		
		for (int i=0; i<center.getFilled(); i++){
			double x3 = center.getX(i-1), y3 = center.getY(i-1);
			double x4 = center.getX(i), y4 = center.getY(i);
			if (Line2D.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4)){
//				System.out.printf("X1: %f, Y1: %f,  X2: %f, Y2: %f,  X3: %f, Y3: %f,  X4: %f, Y4: %f\n",
//									x1,		y1,		x2,		y2,		x3,		y3,		x4,		y4);
				return true;
			}
		}
		
		return false;
	}//newLeftPointIntersectsCenterPath
	
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
