
public class Path {

	/*
		A collection of consecutive xy coordinates which
		denote a series of line segments in a path.
	*/

	private int size;

	private int[] arr_x, arr_y;


	public Path getCircle (int center_x, int center_y, int radius, int poly_num){
		//Create a circle of $poly_num+1 line segments centered around the point
		// (center_x, center_y) with each vertex $radius units from the center
		// point.
		Path path = new Path(poly_num+1);

		double dAngle = 2*Math.pi/poly_num;
		for (int i=0; i<=poly_num; i++){
			int dx = (int)(Math.cos(dAngle*i)*radius);
			int dy = (int)(Math.sin(dAngle*i)*radius);
			path.setPoint (i, center_x+dx, center_y+dy);
		}
	}//getCircle

	public void addArc (Path p, double delta_angle, int line_num, int line_size, int start_index){
		//Adds on an arc to an existing Path

		int last_x, last_y;
		double start_angle;

		if (start_index == 0){
			last_x = 0;
			last_y = 0;
			start_angle = 0;
		} else if (start_index == 1){
			last_x = p.getX(start_index-1);
			last_y = p.getY(start_index-1);
			start_angle = 0;
		} else {
			last_x = p.getX(start_index-1);
			last_y = p.getY(start_index-1);
			start_angle = Math.atan2(last_y-p.getY(start_index-2), last_x-p.getX(start_index-2));
		}

		for (int i=0; i<=line_num; i++){
			int dx = (int)(line_size*Math.cos(start_angle+i*delta_angle));
			int dy = (int)(line_size*Math.sin(start_angle+i*delta_angle));
		}
	
		

	//-----------Basic Methods------------//

	public Track (int number_of_tracks){
		this.size = number_of_tracks;

		arr_x = new int[size];
		arr_y = new int[size];
	}//Track

	public void setPoint (int index, int x, int y){
		arr_x[index] = x;
		arr_y[index] = y;
	}//setPoint
	public int getX (int index){
		return arr_x[index];
	}
	public int getY (int index){
		return arr_y[index];
	}

}//Path class
