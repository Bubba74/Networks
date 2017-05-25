package components;

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
		
		System.out.println("\n\nCenter\n");
		for (int i=0; i<center.getFilled(); i++){
			System.out.printf("x is: %f,  y is: %f\n", center.getX(i), center.getY(i));
		}
		
		left = new Path (center.getSize());
		right = new Path (center.getSize());
		
		start_a = Math.atan2(center.getY(1)-center.getY(0), center.getX(1)-center.getX(0));
		
		//Path->Track Transformation
		
		for (int i=0; i<center.getFilled(); i++){
			//Get x,y coords for last, this, and next points on center path.

			double[] coords = new double[8];
			
			/*
			 * 0 -- x1 of previous line
			 * 1 -- y1 of previous line
			 * 2 -- x2 of previous line
			 * 3 -- y2 of previous line
			 * 
			 * 4 -- x1 of this line
			 * 5 -- y1 of this line
			 * 6 -- x2 of this line
			 * 7 -- y2 of this line
			 * 
			 */
			
			coords[0] = center.getX(i-1);
			coords[1] = center.getY(i-1);
			
			coords[2] = coords[4] = center.getX(i);
			coords[3] = coords[5] = center.getY(i);
			
			coords[6] = center.getX(i+1);
			coords[7] = center.getY(i+1);
			
			//Take two connected lines, and move them to the left.
			double[] newCoords = getLeftLines(coords, radius);
			
			if (i < 3){
				System.out.println("Coords: {");
				for (double d: coords)
					System.out.print("  "+d);

				System.out.println("  }\nCoord2: {");
				for (double d: newCoords)
					System.out.print("  "+d);
				System.out.println("  }\n");
			}
			
			//Calculate intersection of these two new lines
			double intercept = getIntersection (newCoords);
			
			//Take either line and find that (x,y) coordinate of intersection
				//This is the new (x,y) pair for the left path
			double dx = newCoords[2]-newCoords[0];
			double dy = newCoords[3]-newCoords[1];
			double r = Math.sqrt(dx*dx+dy*dy);
			dx /= r;
			dy /= r;
			
			double left_x = newCoords[0]+(intercept-newCoords[0])*dx;
			double left_y = newCoords[1]+(intercept-newCoords[0])*dy;
			left.addPoint(left_x, left_y);
			
			//Reflect this pair across the corresponding (x,y)
				//This is the new (x,y) pair for the right path
				right.addPoint(-left_x+2*coords[2], -left_y+2*coords[3]);
			
		}//for loop
		
		calcBounds();
		
	}//Track
	
	public double[] getLeftLines (double[] coords, int radius){
		double[] newCoords = new double[8];
		for (int i=0; i<coords.length; i++) newCoords[i] = coords[i];
		
		//First line
		double dx = coords[2]-coords[0];
		double dy = coords[3]-coords[1];
		
//		if (reflectDeltas(dx,dy)){
//			dx *= -1;
//			dy *= -1;
//		}
		
		double r = 1/Math.sqrt(dx*dx+dy*dy);
		dx *= r;
		dy *= r;
		
		newCoords[0] += radius*dy;
		newCoords[1] -= radius*dx;
		newCoords[2] += radius*dy;
		newCoords[3] -= radius*dx;

		//Second line
		dx = coords[6]-coords[4];
		dy = coords[7]-coords[5];
		
		r = 1/Math.sqrt(dx*dx+dy*dy);
		dx *= r;
		dy *= r;
		
//		if (reflectDeltas(dx,dy)){
//			dx *= -1;
//			dy *= -1;
//		}
		
		newCoords[4] += radius*dy;
		newCoords[5] -= radius*dx;
		newCoords[6] += radius*dy;
		newCoords[7] -= radius*dx;
		
		return newCoords;
	}//getLeftLines
	
	private boolean reflectDeltas(double dx, double dy){
		boolean reflect = false;
		
//		if ()
		
		
		
		return reflect;
	}//reflectDeltas
	
	private double getIntersection (double[] coords){

		/*
		(x-x1)*(y2-y1)/(x2-x1) + y1    ==  (x-x3)*(y4-y3)/(x4-x3) + y3;
		(x-x1)*(y2-y1)(x4-x3) + y1(x2-x1)(x4-x3)   == (x-x3)*(y4-y3)(x2-x1) + y3(x2-x1)(x4-x3);
		(x-x1)*(y2-y1)*(x4-x3) - (x-x3)*(y4-y3)*(x2-x1)   ====    y3*(x2-x1)*(x4-x3) - y1*(x2-x1)*(x4-x3)
		x*(y2-y1)*(x4-x3) - x1*(y2-y1)*(x4-x3) - x*(y4-y3)*(x2-x1) + x3*(y4-y3)*(x2-x1)   ==  y3*(x2-x1)*(x4-x3) - y1*(x2-x1)*(x4-x3)
		x*(   (y2-y1)*(x4-x3) - (y4-y3)*(x2-x1)  )  == y3*(x2-x1)*(x4-x3) - y1*(x2-x1)*(x4-x3)  +  x1*(y2-y1)*(x4-x3)  -  x3*(y4-y3)*(x2-x1)
		x ==  (        y3*(x2-x1)*(x4-x3) - y1*(x2-x1)*(x4-x3)  +  x1*(y2-y1)*(x4-x3)  -  x3*(y4-y3)*(x2-x1)          )   /  (  (y2-y1)*(x4-x3) - (y4-y3)*(x2-x1)   )
		*/
		
		double x1,y1,x2,y2;
		x1 = coords[0];
		y1 = coords[1];
		x2 = coords[2];
		y2 = coords[3];
	
		double x3,y3,x4,y4;
		x3 = coords[4];
		y3 = coords[5];
		x4 = coords[6];
		y4 = coords[7];
	
		//Single line solution, from comments above
		if (y2-y1 == 0 && y4-y3 == 0) return coords[2]/2 + coords[4]/2;
		double x =  (        y3*(x2-x1)*(x4-x3) - y1*(x2-x1)*(x4-x3)  +  x1*(y2-y1)*(x4-x3)  -  x3*(y4-y3)*(x2-x1)      )   /  (  (y2-y1)*(x4-x3) - (y4-y3)*(x2-x1)   );
		
		System.out.println("Intercept: "+x);
		
		return x;
	}//getIntersections
	
	
	public void calcBounds(){
		double min_x = Integer.MAX_VALUE;
		double max_x = Integer.MIN_VALUE;
		double min_y = Integer.MAX_VALUE;
		double max_y = Integer.MIN_VALUE;
		
		Path[] paths = {left, right};
		
		for (Path path: paths){
			for (int i=0; i<path.getFilled(); i++){
				double x = path.getX(i);
				double y = path.getY(i);

				if (x < min_x) {
					min_x = x;
				}
				else if (x > max_x) max_x = x;

				if (y < min_y) min_y = y;
				else if (y > max_y) max_y = y;
			}
		}

		x = min_x;
		y = min_y;
		w = max_x-min_x;
		h = max_y-min_y;
		
//		System.out.printf("X: %f,  Y: %f,  W: %f,  H: %f\n", x, y, w, h);
		
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
