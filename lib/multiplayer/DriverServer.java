package multiplayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import main.Driver;

public class DriverServer extends Thread {

	private Socket remote;
	private PrintWriter out;
	private BufferedReader in;

	private Driver driver;

	private double rayScope;
	private int rayCount;
	private double[] rayDepths;

	private double turnControl, speedControl;
	private boolean first;


	private String getLine (){
		String ret = null;
		try {
			ret = in.readLine();
		} catch (IOException e){
			e.printStackTrace();
		}
		System.out.println(ret);
		return ret;
	}//getLine

	public void run () {
		while (true){
			System.out.println("Run loop");
			if (first){
				first = false;
				System.out.println("First");
	
				out.println("RayScope");
				out.println(""+rayScope);
				out.println("RayCount");
				out.println(""+rayCount);
				out.flush();
			}
			
			rayDepths = driver.getRayDistances();
			sendRayDepths();
	
			out.println("Controls");
	
			if (getLine().equals("Controls")){
				turnControl = Double.parseDouble(getLine());
				speedControl = Double.parseDouble(getLine());
				driver.inputs(turnControl, speedControl);
			}
		}
	}//run

	public DriverServer (Socket clientConnection, Driver localCar, double scope, int count) throws IOException{

		remote = clientConnection;
		out = new PrintWriter (remote.getOutputStream(),true);
		in = new BufferedReader (
				new InputStreamReader (remote.getInputStream()));

		driver = localCar;

		rayScope = scope;
		rayCount = count;
		rayDepths = new double[rayCount];

		turnControl = 0;
		speedControl = 0;

		first = true;
	}//DriverServer

	private void sendRayDepths (){
		out.println("RayDepths");
		for (int i=0; i<rayCount; i++)
			out.println(""+rayDepths[i]);
	}//sendRayDepths
	
	public Driver getDriver(){
		return driver;
	}

}//DriverServer
