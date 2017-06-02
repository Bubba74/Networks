package multiplayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import main.Driver;

public class DriverServer extends Driver implements Runnable {

	private boolean disconnected = false;
	private Socket remote;
	private PrintWriter out;
	private BufferedReader in;

	private double rayScope;
	private int rayCount;
	private double[] rayDepths;

	private double turnControl, speedControl;
	private boolean first;
	
	private long start;//time


	private String getLine (){
		String ret = null;
		try {
			ret = in.readLine();
		} catch (IOException e){
			System.out.println("Client Disconnected!");
			disconnected = true;
			ret = "Definitely not Controls";
//			e.printStackTrace();
		}
		return ret;
	}//getLine

	public void run () {
		start = System.nanoTime();
		while (!disconnected){
			if (first){
				first = false;
				System.out.println("First");
	
				out.println("RayScope");
				out.println(""+rayScope);
				out.println("RayCount");
				out.println(""+rayCount);
				out.flush();
			}
			
			rayDepths = getRayDistances();
			sendRayDepths();
	
			out.println("Controls");
			out.flush();
	
			
			String input = getLine();

			if (input == null){
				input = "No command";

				System.out.println("End of stream, this means the client disconnected?!");
				disconnected = true;
			}
				
			if (input.equals("Controls")){
				long now = System.nanoTime();
//				System.out.println("New inputs: "+(now-start));
				start = now;
				
				turnControl = Double.parseDouble(getLine());
				speedControl = Double.parseDouble(getLine());
				inputs(turnControl, speedControl);
			}
		}//while loop
		
		System.out.println("Closing Resources");
		out.close();
		try {
			in.close();
			remote.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		setID(-1);
	}//run

	public DriverServer (Socket clientConnection, int id, double scope, int count) throws IOException{
		super(id);
		
		remote = clientConnection;
		out = new PrintWriter (remote.getOutputStream());
		in = new BufferedReader (
				new InputStreamReader (remote.getInputStream()));

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
	
	public void poll (){
		//Do nothing
	}
	
	public void disconnect (){
		disconnected = true;
	}
	
}//DriverServer
