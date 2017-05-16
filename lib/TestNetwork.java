
import networks.Network;

public class TestNetwork {

	public static void main (String[] args){
		Network net = new Network (2, 0, 4, 2);

		double[] inputs = new double[2];
		double[] outputs = new double[2];


		int duration = 1000;
		int step = 10;

		if (args.length > 0){
			duration = Integer.parseInt(args[0]);
		}
		if (args.length > 1){
			step = Integer.parseInt(args[1]);
		}

//		inputs[2] = 1;//Bias
		for (int i=0; i<duration; i++){
	//		inputs[0] = (Math.random() < 0.5? 1: 0);
	//		inputs[1] = (Math.random() < 0.5? 1: 0);
			inputs[0] = Math.random();
			inputs[1] = Math.random();
			outputs[0] = inputs[0];
			outputs[1] = inputs[1];

			net.loadInputs(inputs);
			
			if (i % step == 0){
				System.out.printf("%d/%d --",i, duration);
				net.train(outputs, true);
			} else
				net.train(outputs, false);
		}
		System.out.println(net);
	}
}
