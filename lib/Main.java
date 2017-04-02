
public class Main {

	public static void main(String[] args){
		Network network = new Network(2, 1, 4, 1);

		double[] inputs = {1,0};
		network.load(inputs);
		
		System.out.println(network);
	}

}//Main class for testing Neural Networks
