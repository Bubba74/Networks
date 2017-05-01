
public class Main {

	public static void main(String[] args){
		long start, end; //Times

		Network network = new Network(1, 1, 4, 1);

		double[] inputs = {1};
		double[] outputs = {0.72};
		
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
	*/

			network.loadInputs(inputs);

			if (i % step == 0)
				network.train(outputs, true);
			else
				network.train(outputs, false);

			System.out.println(network);
		}

		end = System.currentTimeMillis();

		System.out.println(network);

		float time = (float)(end-start)/ 1000;
		System.out.printf("\n\nProgram Finished in %.3f seconds.\n",time);
		System.out.printf("%d iterations\n", duration);
		System.out.printf("%d iterations between each print\n", step);
	}

}//Main class for testing Neural Networks
