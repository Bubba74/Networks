
public class Layer {

	/*
		nets -- Sum of all outputs of previous layer times their respective weights.
		outs -- The outputs of this layer == activation (nets)
	*/	

	int size;
	double[] nets;
	double[] outs;
	double[] deltas;

	/*
		num -- Size of layer
	*/
	public Layer (int num){
		size = num;

		nets = new double[size];
		outs = new double[size];
		deltas = new double[size];

		for (int i=0; i<size; i++){
			nets[i] = 0;
			outs[i] = 0;

			deltas[i] = 0;
		}
	}//Layer constructor

	
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

	/*
		Called once to confine the net sums into a 0 to 1 output
	*/
	public void activate (){
		for (int i=0; i<size; i++){
			outs[i] = activation (nets[i]);
		}
	}//activate method


	/*
		Sigmoid
		   1
		--------
		1 + e^-x
	*/
	public double activation (double net){
		return (1 / ( 1+Math.exp(-net) ));
	}//activation function

	public double dActivation (double net){
		return activation(net) * (1-activation(net));
	}//derivative of activation function 

}//Layer class
