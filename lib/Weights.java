
import java.util.Random;

public class Weights {
	
	private Random rng;
	private float learningRate = 1f;
	Layer inputLayer, outputLayer;
	int left, right;

	double[][] weights;
	double[][] deltas;

	public Weights (Layer inputs, Layer outputs){
		
		inputLayer = inputs;
		outputLayer = outputs;

		left = inputs.size;
		right = outputs.size;

		weights = new double[left][right];
		deltas = new double[left][right];

		rng = new Random (System.currentTimeMillis());

		reset();
	}//Basic constructor

	public Weights (int rngSeed, Layer inputs, Layer outputs){
		this(inputs, outputs);
		rng = new Random (rngSeed);
		reset();
	}//Seeded weights

	public void feedForward (){
		double[] inputs = inputLayer.getOuts();
		double[] outputs = new double[outputLayer.size];

		for (int i=0; i<inputs.length; i++){
			for (int j=0; j<outputs.length; j++){
				outputs[j] += inputs[i]*weights[i][j];
			}
		}
		
		outputLayer.inputAll(outputs);
	
	}//update

	public void feedBackwards (){
		double[] outputsDeltas = outputLayer.getDeltas();
	
		//Since the deltas of the output layer are how much they affect the overall error,
		//the deltas of the input layer outputs will be simply the sum of the weights times
		//the next layers deltas.

		//In its own method, the input layer will then calculate the dnets and the overall deltas.

		double[] leftLayerDeltas = new double[left];
		for (int i=0; i<left; i++){
			leftLayerDeltas[i] = 0;
			for (int j=0; j<right; j++){
				leftLayerDeltas[i] += outputsDeltas[j] * weights[i][j];
			}
		}
		inputLayer.calculateDeltas(false, leftLayerDeltas);

		double[] inputs = inputLayer.getOuts();
		for (int i=0; i<left; i++){
			for (int j=0; j<right; j++){
				deltas[i][j]   = inputs[i]*outputsDeltas[j];
				weights[i][j] -= deltas[i][j]*learningRate;
			}
		}

	}//feedBackwards
		

	public String toString (){
		String str = "";

		//Outputs
		for (int j=0; j<right; j++){
			//Inputs
			for (int i=0; i<left; i++){
				str += String.format("%15.4f  ", weights[i][j]);
			}
			str += "\n";
		}
		return str;
	}//toString

	/*
		Completely resets the weight connections, destroying
			all information within the neural network.
	*/
	public void reset(){
		for (int i=0; i<left; i++){
			for (int j=0; j<right; j++){
				weights[i][j] = rng.nextDouble();
//				weights[i][j] = 0;
				deltas[i][j] = 0;
			}
		}
	}//reset method
}//Weights class
	
