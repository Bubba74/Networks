package multiplayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server {
	
	public static void main (String[] args){
		
		ServerSocket server = null;
		PrintWriter print = null;
		BufferedReader reader = null;
		
		try {
			server = new ServerSocket(9999);
			server.setReuseAddress(true);
			
			System.out.println("Established Server at: "+server.getInetAddress());
			
			Socket client = server.accept();
			
			print = new PrintWriter(client.getOutputStream(), true);
			reader = new BufferedReader(
					new InputStreamReader(client.getInputStream()));
			
			String fromClient = "";
			while (fromClient != null){
				fromClient = reader.readLine();
				
				System.out.println("echo:->"+fromClient);
				print.println("Return -- "+fromClient);
			}
			
			print.close();
			reader.close();
			client.close();
			
			System.out.println("Client disconnected");
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}//main
}//Client
