
public class Main {
	
	public static double[] inputs;

	public static double[] weights1;
	public static double[] layerSums;
	public static double[] layerOutputs;

	public static double[] weights2;
	public static double finalSum;
	public static double output;

	public static double learningRate = 5;
	
	public static double cumulativeError = 0;
	public static int count = 0;

	public static void main (String[] args){

		long start = System.currentTimeMillis();
		int len = 1000;
		int period = 1;

		if (args.length > 0) len = Integer.parseInt(args[0]);
		if (args.length > 1) period = Integer.parseInt(args[1]);

		init();

		double x, y, ans;
		for (int i=0; i<len; i++){
			
			x = Math.random();
			y = Math.random();
			ans = ( 0.3 < x && x < 0.6 && 0.7 < y && y < 0.85 ? 0.9 : 0.1 );
			
			fill(x, y);

			cumulativeError += (ans-output);
			count++;
			if (i%period==0)
				System.out.printf("%.3f%%   Target: %.1f\tOutput: %f\t Error: %9f\tAvg. Error: %f\n",100*(float)i/len,ans, output, ans-output, cumulativeError / count);

			train (ans);
		}

		long end = System.currentTimeMillis();

		System.out.printf("Duration: %d\n",end-start);
	}//main method

	public static void train (double ans){
		//TODO Use the Delta rule (see wikipedia)

		//Train output
		for (int i=0;i<weights2.length;i++){
			double delta = (ans-output)*dsigma(finalSum)*layerOutputs[i];
			weights2[i] += learningRate*delta;
		}

		//Train mid-layer
		for (int i=0;i<layerOutputs.length;i++){
			for (int j=0;j<2;j++){
				double delta = (ans-layerOutputs[i])*dsigma(layerSums[i])*inputs[j];
				weights1[j*4+i] += learningRate*delta;
			}
		}
	}//training

	public static void fill (double x, double y){

		inputs[0] = x;
		inputs[1] = y;

		for (int s=0;s<layerSums.length;s++) layerSums[s] = 0;

		for (int i=0;i<2;i++)
			for (int w=0;w<4;w++)
				layerSums[w] += inputs[i]*weights1[i*w];

		for (int o=0;o<4;o++)
			layerOutputs[o] = sigma(layerSums[o]);

		finalSum = 0;
		for (int i=0;i<4;i++)
			finalSum += layerOutputs[i] * weights2[i];

		output = sigma(finalSum);

	}//fill

	public static void init (){
		//Initialized
		inputs = new double[2];
		weights1 = new double[2*4];
		
		layerSums = new double[4];
		layerOutputs = new double[4];
		weights2 = new double[4];

		finalSum = 0;
		output = 0;

		//Fill with beginning values
		for (int i=0;i<inputs.length;i++) inputs[i] = 0;
		for (int i=0;i<weights1.length;i++) weights1[i] = Math.random();

		for (int i=0;i<layerSums.length;i++) layerSums[i] = 0;
		for (int i=0;i<layerOutputs.length;i++) layerOutputs[i] = 0;
		for (int i=0;i<weights2.length;i++) weights2[i] = Math.random();

	}//init method

	public static double sigma (double x){
		return  (1/(1+Math.exp(-x)));
	}
	
	public static double dsigma (double x){
		return sigma(x) * (1-sigma(x));
	}
}//Network
