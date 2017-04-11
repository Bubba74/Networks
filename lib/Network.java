
public class Network {
	
	private static int networksCreated = 0;

	private String name;

	private Weights[] weights;
	private Layer[] layers;

	public void loadInputs (double[] inputs){
		layers[0].inputAll(inputs);

		feedForward();
	}//loadInputs

	private void feedForward (){
		//For each web of weights:
			// The output layer = the activation of the sum
			// Of the input weights times their corresponding weights.

		for (int i=0; i<weights.length; i++){
			weights[i].feedForward();
		}

	}//feedForward

	public void train (double[] targets){
		//Input targets into output layer

		double[] errors = layers[layers.length-1].getErrors(targets);

		layers[layers.length-1].calculateDeltas(targets);	


	}//train



//------------------Construction of Network object-----------------------//

	public void initialize (String networkName, int inputSize, int hiddenNumber, int hiddenSize, int outputSize){

		//Name of network
		name = networkName;

		//Create right number of layers
		layers = new Layer[1 + hiddenNumber + 1];
		
		//Initialize Input Layer
		layers[0] = new Layer(inputSize);
		layers[0].setInputLayer(true);

		//Initialize Hidden Layers
		for (int i=1; i<layers.length-1; i++)
			layers[i] = new Layer(hiddenSize);

		//Initialize Output Layer
		layers[layers.length-1] = new Layer(outputSize);


		//Create weights for connections between the input layer, the hidden layers, and the output layer
		weights = new Weights[layers.length-1];

		//Connect the layers by a web of weights
		for (int i=0; i<layers.length-1; i++){
			weights[i] = new Weights(layers[i], layers[i+1]);
		}
	}//initialize

	public Network (int inputSize, int hiddenNumber, int hiddenSize, int outputSize){
		initialize ("Network: "+networksCreated++, inputSize, hiddenNumber, hiddenSize, outputSize);
	}//Network

	public Network (String networkName, int inputSize, int hiddenNumber, int hiddenSize, int outputSize){
		initialize (networkName, inputSize, hiddenNumber, hiddenSize, outputSize);
		networksCreated++;
	}//Network

//------------------Basic Functions----------------------------//

	public String toString (){
		String str = "";

		str += "------------" + name + "---------------\n";
		str += String.format("# of inputs: %d   # of outputs: %d\n", layers[0].size, layers[layers.length-1].size);

		//Hidden Layers
		str += String.format("\n# of hidden layers: %d\n", layers.length-2);
		for (int i=1; i<layers.length-1; i++)
			str += String.format("\tH%d has %d nodes\n", i, layers[i].size);
		
		str += "\n---------Connections------------\n";

		for (int i=0; i<layers.length-1; i++){
			str += layers[i].toString();
			str += weights[i].toString();
		}
		str += layers[layers.length-1].toString();


		return str;
	}//toString

}//Network class


