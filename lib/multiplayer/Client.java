package multiplayer;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	
	public static void main (String[] args){
		
		try {
			Socket client = new Socket("127.0.0.1", 9999);
			client.setReuseAddress(true);
			
			System.out.println("Connected to server at: "+client.getRemoteSocketAddress());

			DriverClient driver = new DriverClient(client);
			driver.start();

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("Done");
	}//main
}//Client
