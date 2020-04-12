package IoTSimulation;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import org.eclipse.paho.client.mqttv3.*;
import java.util.concurrent.*;

import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * @author Augustas Valaitis Informatika 1 grupë
 */

public class Phone extends Appliance{
	
	public Phone()
	{
		setTopic("Augi User");
		setID("Augi_Phone");
		try
		{
			MemoryPersistence persistence = new MemoryPersistence();
			setClient(new MqttClient(getHost(), getID(), persistence));
			getClient().connect(getConnectOptions());
			getClient().setCallback(getCallback());
			getClient().subscribe(getTopic(), 2);
		}catch(MqttException me)
		{
			printException(me);
		}
	}
	
	
	public MqttCallback getCallback()
	{
		return new MqttCallback() {
             public void messageArrived(String topic, MqttMessage message) throws Exception {
                 String time = new Timestamp(System.currentTimeMillis()).toString();
                 String str = new String(message.getPayload());
                 
                 writeIntoFile(str);
                 
                 if(decide()) 
                 {
                	 getClient().publish("Augi Orders", prepareMessage("Sprinkle"));
                 }
             }

             public void connectionLost(Throwable cause) {
            	 System.out.println("Connection to broker messaging lost! " + cause.getMessage());
            	 printException((MqttException)cause);
             }

             public void deliveryComplete(IMqttDeliveryToken token) {
             }
         };
	}
	
	//simulates user getting a notification on the phone
	public void writeIntoFile(String str)
	{
		try
		{
			FileWriter fileWriter = new FileWriter(".\\phone.txt", true);
	        fileWriter.write(new String(str + "\r\n"));
	        fileWriter.close();
		}catch(IOException ex)
		{
			ex.printStackTrace();
		}
		
	}
	
	//simulates user deciding if to turn on the sprinklers
	public boolean decide()
	{
		int decision = generateRandomNumber(2);
		
		
		if(decision == 1)
		{
			return true;
		}else return false;
	}
}
