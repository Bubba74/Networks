package main;

import components.Path;
import components.Track;

public class LapCounter {

	static class Point {
		public double x, y;

		public Point (){
			x = 0; y = 0;
		}
		public Point (double x, double y){
			this.x = x;   this.y = y;
		}
	}//Point class

	public static final double minDistance = 100;
		//# of laps the car using this class has circled a path.
	private int laps;

		//# of points to look at
	private int definition;
		//The index (0<=state<definition) of the nearest point.
	private int state;

		//Does the LapCounter have a valid track/path to count against?
	private boolean ready;
		//The points to look at
	private Point[] points;


/////////////-------------------------------------------------------//////////////////
	public void update (double x, double y){
		if (!ready) return;

			//Get the index of the path point which is closest to the car
		int newState = getNewState(x, y);

//		System.out.printf("Laps: %d   State: %d   New: %d\n", laps, state, newState);
			//Updates state and sees if a new lap should be counted
		checkMovement (newState);
		
	}//update

	private int getNewState (double x, double y){
		//Unless the car is within minDistance of a new point,
		//keep the same state as before.
		int nearest = state;
		double nearestDistance = minDistance*minDistance;
		double dist2;

		for (int i=0; i<definition; i++){
			dist2 = (x-points[i].x)*(x-points[i].x)+
				(y-points[i].y)*(y-points[i].y);
			if (dist2 < nearestDistance) {
				nearestDistance = dist2;
				nearest = i;
			}
		}

		return nearest;
	}//getNewState
	
	private void checkMovement (int newState){
		if (newState == 0 && (state > 0 && state < definition-1)) {
//			System.out.printf("New Lap!!!  %d --> %d\n", state, newState);
			laps++;
		} else if (newState == (definition-1) && state == (definition-2)){
//			System.out.printf("Backwards!  %d --> %d\n", state, newState);
			laps--;
		}

		state = newState;
	}//checkMovement

	public LapCounter (){
		laps = 0;

		definition = 4;
		state = 0;
		
		ready = false;
		points = new Point[definition];
		for (int i=0; i<definition; i++) points[i] = new Point();
	}
	public LapCounter (Path path){
		this();

		ready = true;
		for (int i=0; i<definition; i++){
			points[i].x = path.getX(-i);
			points[i].y = path.getY(-i);
		}
	}//LapCounter
	public LapCounter (Track track){
		this(track.getCenterPath());
	}//LapCounter

	public void setPath (Path p){
		laps = 0;
		state = 0;

		ready = true;
		
//		System.out.println("New Path Set!!!\tDefinition: "+definition);
		
		for (int i=0; i<definition; i++){
			points[i].x = p.getX(-i);
			points[i].y = p.getY(-i);
		}
	}//setPath

	public int getLapsCompleted (){
		return laps;
	}//getLapsCompleted
	public void setLapsCompleted (int override){
		laps = override;
	}//setLapsCompleted
	
	public boolean isReady (){
		return ready;
	}//isReady
	public void reset (){
		laps = 0;
		state = 0;
	}//reset
	
}//LapCounter

