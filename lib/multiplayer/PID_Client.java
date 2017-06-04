package multiplayer;

import java.io.IOException;

import main.MiniPID;

public class PID_Client {

	private static MiniPID pid = new MiniPID (10, 0, 0);
	private static double pid_input = 0;
	private static double pid_output = 0;

	private static double rayScope = Math.PI/2;
	private static int rayCount = 160;
	private static double[] rayDepths = new double[rayCount];

	private static DriverClient driver;

	public static void main (String[] args){

		int port = 9999;
		if (args.length > 0) port = Integer.parseInt(args[0]);

		pid.setOutputLimits(1);

		try {
			driver = new DriverClient ("192.168.1.68", port);
			driver.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (true){
			rayScope = driver.getRayScope();
			if (driver.getRayCount() != rayCount){
				rayCount = driver.getRayCount();
				rayDepths = new double[rayCount];
			}

			rayDepths = driver.getRayDepths();

			double sum = 0;
			for (int i=0; i<rayCount/2; i++)
				sum += rayDepths[i];
			for (int i=rayCount/2; i<rayCount; i++)
				sum -= rayDepths[i];

			pid_output = pid.getOutput(sum, 0);
			driver.inputs(pid_output, 0);
		}
	}//main

}//PID_Client
