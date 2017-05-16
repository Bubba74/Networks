package main;

public class AnalogToPWM {

	private int dutyCycle;
	
	private int lastIndex;
	private int index;

	/*public int getPWM (double input){
		
		if (input < 1) return 1-getPWM(1/input);
		
		int output = 0;
		
//		if (input < 0) input = 0;
//		if (input > 1) input = 1;
		
//		output = (int)(100/input);
		output = (int)input;
		
		if (index % output == 0) output = 1;
		else output = 0;
		
		index++;
		if (index == dutyCycle) index = 0;
		
		return output;
	}//getPWM
*/
	
	public int getPWM (double analog){
		//Convert an analog 0,1 signal into
		//a LOW, HIGH frequency.
		
		if (analog > 0.5) return 1-getPWM(1-analog);
		
		int output = 0;
		
//		int freq = (int)(100/analog);
		double freq = 1/analog;
		
		if (index-lastIndex >= freq){
			output = 1;
			lastIndex = index;
		}
		
		index++;
		if (index == dutyCycle){
			index = 0;
			lastIndex = 0;
		}
		
		return output;
	}//getPWM
	
	
	public AnalogToPWM (int dutyCycle){
		this.dutyCycle = dutyCycle;
		this.lastIndex = 0;
		this.index = 0;
		
	}//AnalogToPWM
	
	public static void main (String[] args){
		//Test AnalogToPWM
		
		AnalogToPWM converter = new AnalogToPWM(100);
		
		for (int i=0; i<100; i++){
			if (converter.getPWM(0.9) == 1)
				System.out.print("*");
			else 
				System.out.print("3");
		}
		
		
	}//main test
}//AnalogToPWM
