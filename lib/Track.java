

public class Track {

	private Path center;
	private Path left, right;
	private int start_x, start_y;
	

	public double calcRay (double x, double y, double angle, int start_index){

		double minimum = 500; //Greater than 0


		double dx = Math.cos(angle);
		double dy = Math.sin(angle);

		double m = dy/dx;
		double b = y-m*x;

//		for (int i=start_index; i<center.getFilled()-1; i++){
		for (int i=0; i<center.getFilled()-1; i++){
			int j = i;
			if (j < 0) j += center.getFilled();
			
			int x1, y1, x2, y2;

			//Left Path
			Path[] paths = {left, right};
			
			for (Path p: paths){
				
				x1 = p.getX(j); y1 = p.getY(j);
				x2 = p.getX(j+1); y2 = p.getY(j+1);
	
				if (x2-x1 == 0){
					//Check intersection of ray with vertical line segment
					double a = x2*m+b; //Vertical intersection
	
					if (Math.min(y1,y2) <= a && a <= Math.max(y1,y2))
							if ((x2-x)/dx < minimum && (x2-x)/dx >= 0)
								minimum = (x2-x)/dx;
				} else {
					double m1 = (y2-y1)/(x2-x1);
					double b1 = y1-m1*x1;
	
					//Ray = m*x + b;
					//Line Segment = m1*x + b1;
	
					double xintercept = (b1-b)/(m-m1);
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
		
		start_x = center.getX(0);
		start_y = center.getY(0);
		
		//Center path values
		int prev_x, prev_y, this_x, this_y, next_x, next_y;
		
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

			int leftDx = prev_x-this_x;
			int leftDy = prev_y-this_y;
			int rightDx = next_x-this_x;
			int rightDy = next_y-this_y;
			
			double left_angle, right_angle;
			double angle;
			
			left_angle = Math.atan2(leftDy, leftDx);
			right_angle = Math.atan2(rightDy, rightDx);
			
			if (left_angle == 0) left_angle = 2*Math.PI;
			
			double avg_x = (Math.cos(left_angle)+Math.cos(right_angle))/2;
			double avg_y = (Math.sin(left_angle)+Math.sin(right_angle))/2;

			angle = Math.atan2(avg_y, avg_x);
			
			dx = Math.cos(angle);
			dy = Math.sin(angle);
			
			left.addPoint(this_x - (int)(dx*radius), this_y - (int)(dy*radius));
			right.addPoint(this_x + (int)(dx*radius), this_y + (int)(dy*radius));

//			System.out.printf("Avgx: %f\tAvgy: %f\t\tDx: %f\tDy: %f\n", avg_x, avg_y, dx, dy);
			
		}//for loop
		left.setPoint(0, left.lastX(), left.lastY());
		right.setPoint(0, right.lastX(), right.lastY());
		
	}//Track
	
	public Path getCenterPath (){
		return center;
	}
	public Path getLeftSide (){
		return left;
	}
	public Path getRightSide (){
		return right;
	}
	
	public int getStartX (){
		return start_x;
	}
	public int getStartY (){
		return start_y;
	}
	
}//Track