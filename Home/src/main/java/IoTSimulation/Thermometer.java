package IoTSimulation;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * @author Augustas Valaitis Informatika 1 grupë
 */

public class Thermometer extends Appliance{
	
	private int temperature;
	
	public Thermometer()
	{
		setTopic("Augi Temperature");
		temperature = 0;
		setID("Augi_Thermometer");
		try
		{
			
			MemoryPersistence persistence = new MemoryPersistence();
			setClient(new MqttClient(getHost(), getID(), persistence));
			
	        getClient().connect(getConnectOptions());
		}catch(MqttException me)
		{
			printException(me);
		}
		
		//System.out.println(getClient().isConnected());
		
	}
	
	//publishes an update in regard to current temperature to the server
	public void reportTemperature()
	{	
		temperature = generateRandomNumber(80); //since input from a real world appliance is unavailable, I am generating a random temperature
		
		try 
		{
			if(temperature > 50) //50 chosen as a random number, meant to simulate quite high temperatures. 
			{
				getClient().publish(getTopic(), prepareMessage("The temperature is dangeriously high and stands at: " + Integer.toString(temperature)));
				informUser("Warning:The thermometer senses abnormally high temperatures");
			}else
			{
				getClient().publish(getTopic(), prepareMessage("The temperature is normal and stands at: " + Integer.toString(temperature)));
			}
				
		}catch(MqttException me)
		{
			printException(me);
		}
	}
}


