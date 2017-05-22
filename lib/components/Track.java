package components;

import main.Main;

public class Track {

	private LinePath center;
	private LinePath left, right;
	
	private double start_a;
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

		LinePath[] paths = {left, right};
		for (LinePath p: paths){

			for (int i=0; i<p.size(); i++){
				double x1, y1, x2, y2;
				Line line = p.getLine(i);
				
				x1 = line.x1;
				y1 = line.y1;
				x2 = line.x2;
				y2 = line.y2;
				
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
				
	public Track (LinePath center_path, int radius){
		
		/*
		 * This constructor accepts a Path and a path radius;
		 * It creates two paths, on either side of the center
		 * path, spaced radius away from the center.
		 */
		this.center = center_path;
		left = new LinePath ();
		right = new LinePath ();
		
		Line first = center.getLine(0);
		start_a = Math.atan2(first.y2-first.y1, first.x2-first.x1);
		
		//The +- offsets from (this_x, this_y)
		double dx = 0, dy = 0;

		left.addLine(0, 0, 0, 0);
		right.addLine(0, 0, 0, 0);
		for (int i=2; i<center.size(); i++){
			//Get x,y coords for last, this, and next points on center path.
			
			Line l1 = center.getLine(i-1), l2 = center.getLine(i);
			
			double leftDx = l1.x1-l2.x1;
			double leftDy = l1.y1-l2.y1;
			double rightDx = l2.x2-l2.x1;
			double rightDy = l2.y2-l2.y1;
			
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

			double left_x = l2.x1 - dx, left_y = l2.y1 - dy;
			double right_x = left_x + 2*dx, right_y = left_y + 2*dy;
			
			left.lastLine().x2 = left_x;
			left.lastLine().y2 = left_y;
			right.lastLine().x2 = right_x;
			right.lastLine().y2 = right_y;
			
			left.addLine(left_x, left_y, 0, 0);
			right.addLine(right_x, right_y, 0, 0);
 
		}//for loop
		
		left.getLine(0).y1 = left.getLine(left.size()-2).y2;
		left.getLine(0).x1 = left.getLine(left.size()-2).x2;
		
		right.getLine(0).y1 = right.getLine(right.size()-2).y2;
		right.getLine(0).x1 = right.getLine(right.size()-2).x2;
		
		
		calcBounds();
		
	}//Track
	public void calcBounds(){
		double min_x = Integer.MAX_VALUE;
		double max_x = Integer.MIN_VALUE;
		double min_y = Integer.MAX_VALUE;
		double max_y = Integer.MIN_VALUE;
		
		LinePath[] paths = {left, right};
		
		for (LinePath path: paths)
			for (int i=0; i<path.size(); i++){
				Line line = path.getLine(i);
				double x = line.x1;
				double y = line.y1;
				
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
	
	public LinePath getCenterPath (){
		return center;
	}
	public LinePath getLeftSide (){
		return left;
	}
	public LinePath getRightSide (){
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
		return center.getLine(0).x1;
	}
	public double getStartY (){
		return center.getLine(0).y1;
	}
	public double getStartAngle(){
		return start_a;
	}//getStartAngle
	
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
