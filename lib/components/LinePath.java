package components;

import java.util.ArrayList;
import java.util.List;

public class LinePath {

	private List<Line> lines;
	
	public LinePath (Sketch sketch){
		lines = new ArrayList<Line>();
		
		lines.add(new Line(sketch.lastX(),sketch.lastY(),
							sketch.getX(0), sketch.getY(0)
						));
		
		for (int i=1; i<sketch.getFilled()-1; i++){
			lines.add (new Line(sketch.getX(i-1), sketch.getY(i-1),
						sketch.getX(i), sketch.getY(i)
						));
		}
		
		lines.add(new Line(sketch.lastX(), sketch.lastY(),
							sketch.getX(0), sketch.getY(0)
							));

	}//LinePath
	
	public LinePath (){
		lines = new ArrayList<Line>();
	}//LinePath

	public void addLine (double x1, double y1, double x2, double y2){
		lines.add(new Line(x1,y1,x2,y2));
	}//addLine
	public void addLine (Line line){
		lines.add(line);
	}//addLine
	
	public Line getLine(int index){
		if (index < 0) index += size();
		if (index > size()-1) index -= size();
		
		return lines.get(index);
	}//getLine
	public Line lastLine(){
		return lines.get(lines.size()-1);
	}//lastLine
	
	public void rotate (double angle){
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		
		for (Line line: lines){
			line.x1 = line.x1*cos-line.y1*sin;
			line.y1 = line.x1*sin+line.y1*cos;
			line.x2 = line.x2*cos-line.y2*sin;
			line.y2 = line.x2*sin+line.y2*cos;
		}
	}//rotate
	
	public int size (){
		return lines.size();
	}//size
	
}//LinePath
