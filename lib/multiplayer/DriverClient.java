package multiplayer;

import java.net.Socket;

import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.lang.Thread;

public class DriverClient extends Thread {

	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;

	private double turnControl, speedControl;
	private double start;
	
	private double rayScope;
	private int rayCount;
	private double[] rayDepths;

	public String getLine (){
		String ret = null;
		try {
			ret = in.readLine();
		} catch (IOException e){
			e.printStackTrace();
		}
		System.out.println(ret);
		return ret;
	}//getLine
			
	public void run(){
		while (true){
			System.out.println("Run");
			String command = getLine();
	
			if (command.equals("RayScope")){
				rayScope = Double.parseDouble(getLine());
			} else if (command.equals("RayCount")){
				rayCount = Integer.parseInt(getLine());
			} else if (command.equals("RayDepths")){
				if (rayDepths.length != rayCount) rayDepths = new double[rayCount];
				long start = System.currentTimeMillis();
				for (int i=0; i<rayCount; i++)
					rayDepths[i] = Double.parseDouble(getLine());
				System.out.printf("Reading %d rays took %d milliseconds.\n",rayCount,System.currentTimeMillis()-start);
			} else if (command.equals("Controls")){
				turnControl = start;
				speedControl = 1-start;
				start += 0.001;
				System.out.printf("Sending controls: Turn( %.3f )   Speed( %.3f )\n", turnControl, speedControl);
				out.println("Controls");
				out.println(""+turnControl);
				out.println(""+speedControl);
			}
		}
	}//run

	public DriverClient (Socket connectionToServer) throws IOException{

		socket = connectionToServer;

		out = new PrintWriter (socket.getOutputStream(), true);
		in = new BufferedReader (
				new InputStreamReader (socket.getInputStream()));

		turnControl = 0;
		speedControl = 0;

		rayScope = Math.PI/2;
		rayCount = 160;
		rayDepths = new double[160];

		start = 0;

	}//Constructor

	public void inputs (double turn, double speed){
		turnControl = turn;
		speedControl = speed;
	}//inputs

	public void inputTurnControl (double value){
		turnControl = value;
	}//inputTurnControl

	public void inputSpeedControl (double value){
		speedControl = value;
	}//inputSpeedControl

	public double getRayScope (){
		return rayScope;
	}//getRayScope
	public int getRayCount (){
		return rayCount;
	}//getRayCount
	public double[] getRayDepths (){
		return rayDepths;
	}//getRayDepths

}//DriverClient
