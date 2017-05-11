
public class Path {

	/*
		A collection of consecutive xy coordinates which
		denote a series of line segments in a path.
	*/

	private int size;
	private int filled;

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

	public void addArc (Path p, double delta_angle, int line_num, int line_size){
		//Adds on an arc to an existing Path
		/*
			Path p -- The Path object to add an arc to
			double delta_angle -- The change in angle between each line
			int line_num -- The number of lines to draw in a curve
			int line_size -- Length of each line
		*/

		int start_x = p.lastX();
		int start_y = p.lastY();
		double start_angle = 0;

		if (p.getFilled() > 1){
			start_angle = Math.atan2(start_y-p.getY(p.getFilled()-2), start_x-p.getX(p.getFilled()-2));
		}

		for (int i=0; i<=line_num; i++){
			int dx = (int)(line_size*Math.cos(start_angle+i*delta_angle));
			int dy = (int)(line_size*Math.sin(start_angle+i*delta_angle));
			p.addPoint(last_x+=dx, last_y+=dy);
		}

	}//addArc
	
	public void addLine (Path p, double angle, int line_size){
		int start_x = p.lastX();
		int start_y = p.lastY();

		p.setPoint(start_index, start_x+line_size*Math.cos(angle)
					, start_y+line_size*Math.sin(angle));
	}//addLine

	//-----------Basic Methods------------//

	public Track (int number_of_tracks){
		this.size = number_of_tracks;
		this.filled = 0;

		arr_x = new int[size];
		arr_y = new int[size];
	}//Track

	public void addPoint (int x, int y){
		setPoint(filled, x, y);
		filled++;
	}//addPoint

	public void setPoint (int index, int x, int y){
		if (index == filled) filled++;
		arr_x[index] = x;
		arr_y[index] = y;
	}//setPoint
	public int getX (int index){
		return arr_x[index];
	}
	public int lastX (){
		if (filled == 0) return 0;
		return arr_x[filled-1];
	}
	public int getY (int index){
		return arr_y[index];
	}
	public int lastY(){
		if (filled == 0) return 0;
		return arr_y[filled-1];
	}

	public int getSize (){
		return size;
	}
	public int getFilled (){
		return filled;
	}

}//Path class
