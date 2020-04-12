package IoTSimulation;

import java.util.concurrent.TimeUnit;

/**
 * @author Augustas Valaitis Informatika 1 grupë
 */

public class Household 
{

	public static void main(String[] args) 
	{
		Thermometer  t = new Thermometer();
		Sprinklers s = new Sprinklers();
		Phone p = new Phone();
		s.checkForCommand();

		t.reportTemperature();
		
		System.out.println("Done");
		//System.exit(0);//must give a little bit of time for the messages to make their way across the server and client
	}

}
