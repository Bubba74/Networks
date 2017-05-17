package drawing;

public class View {
	public double x, y, sf;
	
	public View (double viewX, double viewY, double viewScale){
		x = viewX;
		y = viewY;
		sf = viewScale;
	}
	public View (double[] view){
		x = view[0];
		y = view[1];
		sf = view[2];
	}
	public View (View otherView){
		x = otherView.x;
		y = otherView.y;
		sf = otherView.sf;
	}
	public View(){
		x = 0;
		y = 0;
		sf = 1;
	}
	
	public void setView (double viewX, double viewY, double viewScale){
		x = viewX;
		y = viewY;
		sf = viewScale;
	}
	public void setView (double[] view){
		x = view[0];
		y = view[1];
		sf = view[2];
	}
	public void setView (View otherView){
		x = otherView.x;
		y = otherView.y;
		sf = otherView.sf;
	}
}//View