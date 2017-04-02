
public class Weights {
	
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

		reset();
	}//Basic constructor

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
		

	public String toString (){
		String str = "";

		//Outputs
		for (int j=0; j<right; j++){
			//Inputs
			for (int i=0; i<left; i++){
				str += String.format("%15f  ", weights[i][j]);
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
				weights[i][j] = Math.random();
				deltas[i][j] = 0;
			}
		}
	}//reset method
}//Weights class
	
