
public class AnalogToPWM {

	private int dutyCycle;
	private int index;

	private int min, max;

	public int getPWM (double input){
		int output = 0;
		
//		if ()
		
		
		index++;
		if (index == dutyCycle) index = 0;
		
		return output;
	}//getPWM

	public AnalogToPWM (int dutyCycle, int min, int max){
		this.dutyCycle = dutyCycle;
		this.index = 0;

		this.min = min;
		this.max = max;
	}//AnalogToPWM
}//AnalogToPWM
