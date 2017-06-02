package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import multiplayer.DriverServer;

public class RacewayServer extends Thread {

	private boolean running = true;
	private ServerSocket server;
	private List<Driver> carList;
	
	private double rayScope;
	private int rayCount;
	
	public RacewayServer (List<Driver> cars, int port, double scope, int count) throws IOException{
		carList = cars;
		
		server = new ServerSocket(port);
		server.setReuseAddress(true);
		
		rayScope = scope;
		rayCount = count;
	}
	
	public void run (){
		while (running){
			try {
				Socket client = server.accept();
				DriverServer newCar = new DriverServer(client, 4, rayScope, rayCount);
				new Thread(newCar).start();
				Main.prepCar(newCar);
				carList.add(newCar);
			} catch (IOException e) {
				if (running)
					e.printStackTrace();
			}
		}
		try {
			System.out.println("Closing server?");
			server.close();
			
			for (Driver car: carList){
				if (4 == car.getID()){
					((DriverServer)car).disconnect();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//run
	
	public void close (){
		running = false;
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}//RacewayServer
