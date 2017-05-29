package multiplayer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	
	public static void main (String[] args){
		
		Socket client = null;
		PrintWriter print = null;
		
		try {
			client = new Socket("127.0.0.1", 9999);
			client.setReuseAddress(true);
			
			print = new PrintWriter(client.getOutputStream());
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
			
		
		
	}//main
}//Client
