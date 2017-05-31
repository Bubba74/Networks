package multiplayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	
	public static void main (String[] args){
		
		Socket client = null;
		PrintWriter print = null;
		BufferedReader reader = null;
		
		try {
			client = new Socket("127.0.0.1", 9999);
			client.setReuseAddress(true);
			
			print = new PrintWriter(client.getOutputStream(), true);
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			
			System.out.println("Connected to server at port: "+client.getPort());
			
			String text = "";
			Scanner scan = new Scanner(System.in);

			while (!text.equals("q")){
				System.out.print("Input -->");
				text = scan.nextLine();
				
				System.out.println("Sending: "+text);
				print.println(text);
				
				System.out.println("Waiting for server...");
				text = reader.readLine();
				
				System.out.println("Received from server: "+text);
			}
			scan.close();

			reader.close();
			print.close();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
			
		
		
	}//main
}//Client
