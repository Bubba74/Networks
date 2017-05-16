package networks;

public class Layer {

	/*
		nets -- Sum of all outputs of previous layer times their respective weights.
		outs -- The outputs of this layer == activation (nets)
	*/	

	boolean isInputLayer;

	int size;
	double[] nets;
	double[] outs;

	double[] dnets;
	double[] douts;
	double[] deltas;

	/*
		num -- Size of layer
	*/
	public Layer (int num){

		isInputLayer = false;

		size = num;

		nets = new double[size];
		outs = new double[size];

		dnets = new double[size];
		douts = new double[size];
		deltas = new double[size];

		for (int i=0; i<size; i++){
			nets[i] = 0;
			outs[i] = 0;

			dnets[i] = 0;
			douts[i] = 0;

			deltas[i] = 0;
		}
	}//Layer constructor

	public void setInputLayer( boolean isInput){
		isInputLayer = isInput;
	}//setInputLayer

	//----------Interface for use-------------//

	public void inputAll (double[] newNets){
		clear();

		if (newNets.length != size){
			System.out.println("ERROR: Layer: Improper # of inputs");
			return;
		}

		for (int i=0; i<size; i++){
			nets[i] = newNets[i];
		}

		activate();

	}//input values

	public double[] getNets (){
		return nets;
	}//getNets
	public double[] getOuts (){
		return outs;
	}//getOutputs

	public double[] getDnets (){
		return dnets;
	}//getDnets
	public double[] getDOuts (){
		return douts;
	}//getDouts
	public double[] getDeltas (){
		return deltas;
	}//getDeltas

	public void calculateDeltas (boolean isOutputLayer, double[] data){
		if (isInputLayer){
			//No need for calculating deltas
			return;
		}
		if (isOutputLayer){
			//data[] = targets[]
			for (int i=0; i<size; i++){
				douts[i] = -(data[i]-outs[i]);
				dnets[i] = outs[i]*(1-outs[i]);
				deltas[i] = douts[i]*dnets[i];
			}
		} else {
			//data[] = nextLayerDeltas[]*connectionWeights
			for (int i=0; i<size; i++){
				douts[i] = data[i];
				dnets[i] = outs[i]*(1-outs[i]);
				deltas[i] = douts[i]*dnets[i];
			}
		}


	}//calculateDeltas

	public double[] getDerrors (double[] targets){
		double[] errors = new double[size];
		for (int i=0; i<size; i++){
			errors[i] = outs[i]- targets[i];
		}
		return errors;
	}//getDerrors

	public String toString (){
		String str = "";
		for (int i=0; i<size; i++){
			str += String.format("  [%4.4f ==> %4.4f]", nets[i], outs[i]);
		}
		str += "\n";

		return str;
	}//toString


	/*
		Called once at the start of each forward pass.

		As the net values, outputs, and thus effects change with each forward
			pass, all of these values are wiped by this call. The weights,
			maintained in a separate object, are not affected.
	*/

	public void clear (){
		for (int i=0; i<size; i++){
			nets[i] = 0;
			outs[i] = 0;
			deltas[i] = 0;
		}
	}//clear method


	// Called once to confine the net sums into a 0 to 1 output
	private void activate (){
		//If the layer is an input layer, no activation is applied to the inputs
		if (isInputLayer){
			for (int i=0; i<size; i++)
				outs[i] = nets[i];
		} else {
			for (int i=0; i<size; i++)
				outs[i] = activation (nets[i]);
		}
	}//activate method


	/*
		Sigmoid
		   1
		--------
		1 + e^-x
	*/
	private double activation (double net){
		return (1 / ( 1+Math.exp(-net) ));
	}//activation function

	private double dActivation (double net){
		return activation(net) * (1-activation(net));
	}//derivative of activation function 

}//Layer class
