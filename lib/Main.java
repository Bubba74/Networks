
import java.util.Random;

public class Main {
	static Random rng = new Random (702);

	public static void main(String[] args){
		long start, end; //Times

		Network network = new Network(2, 1, 4, 2);

		double[] inputs = {1,0};
		double[] outputs = {0.3,0};
		
		int duration = 1000;
		int step = 10;

		if (args.length > 0)
			duration = Integer.parseInt(args[0]);
		if (args.length > 1)
			step = Integer.parseInt(args[1]);

		start = System.currentTimeMillis();
		for (int i=0; i<duration; i++){
	/*
			inputs[0] = (Math.random() <0.5? 1: 0);
			inputs[0] = 0;
			outputs[0] = inputs[0]==1?0:1;
	
			inputs[0] = Math.random();
			inputs[1] = Math.random();
			
			outputs[0] = (0.2 < inputs[0] && inputs[0] < 0.6 && 0.2 < inputs[1] && inputs[1] < 0.6)? 0.9: 0.1;
	
			if (rng.nextDouble() < 0.5)
				inputs[0] = 0.9;
			else
				inputs[0] = 0.1;
			if (rng.nextDouble() < 0.5)
				inputs[1] = 0.9;
			else
				inputs[1] = 0.1;

			if (inputs[0] == 0.9 && inputs[1] == 0.9)
				outputs[0] = 0.9;
			else
				outputs[0] = 0.1;

	*/

	
			if (rng.nextDouble() < 0.5)
				inputs[0] = 1;
			else inputs[0] = 0;

			if (rng.nextDouble() < 0.5)
				inputs[1] = 1;
			else inputs[1] = 0;
	
	/*
			if (inputs[0] == 0.1 && inputs[1] == 0.1)
				//00
				outputs[0] = ;
			else if (inputs[0] == 0.1 && inputs[1] == 0.9)
				//01
				outputs[0] = ;
			else if (inputs[0] == 0.9 && inputs[1] == 0.1)
				//10
				outputs[0] = ;
			else if (inputs[0] == 0.9 && inputs[1] == 0.9)
				//11
				outputs[0] = ;
			else
				System.out.println("ERROOOOOOOOOOROROROROROROROOROROROR");
	*/
			outputs[0] = inputs[0];
			outputs[1] = inputs[1];

			network.loadInputs(inputs);

			if (i % step == 0)
				network.train(outputs, true);
			else
				network.train(outputs, false);

//			System.out.println(network);
		}

		end = System.currentTimeMillis();

		System.out.println(network);

		float time = (float)(end-start)/ 1000;
		System.out.printf("\n\nProgram Finished in %.3f seconds.\n",time);
		System.out.printf("%d iterations\n", duration);
		System.out.printf("%d iterations between each print\n", step);
	}

}//Main class for testing Neural Networks
