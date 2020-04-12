package IoTSimulation;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.concurrent.CountDownLatch;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * @author Augustas Valaitis Informatika 1 grupë
 */

public class Sprinklers extends Appliance{
	
	private boolean active;
	private String subTopic;
	
	public Sprinklers()
	{
		active = false;
		setID("Augi_Sprinklers");
		setTopic("Augi Temperature");
		subTopic = "Augi Orders";
		try
		{
			MemoryPersistence persistence = new MemoryPersistence();
			getClient().connect(getConnectOptions());
			getClient().subscribe(subTopic, 2);
		}catch(MqttException me)
		{
			printException(me);
		}
		
	}
	
	//subscribes to topic
	public void checkForCommand()
	{
		getClient().setCallback(getCallback());
		try
		{
			getClient().subscribe(getTopic(), 2);
		}catch(MqttException ex)
		{
			printException(ex);
		}
		
	}
	
	public boolean getActive()
	{
		return active;
	}
	
	private void setActive(boolean state)
	{
		active = state;
		if(getActive())
		{
			sprinkle();
		}
	}
	
	//callback that also looks for orders from the user
	public MqttCallback getCallback()
	{
		return new MqttCallback() {
             public void messageArrived(String topic, MqttMessage message) throws Exception {
            	 
                 
                 String command = new String(message.getPayload());
                 
                 if(command.contentEquals("Sprinkle"))
                 {
                	 setActive(true);
                 }else
                 {
                	 String time = new Timestamp(System.currentTimeMillis()).toString();
                	 writeIntoFile(time, message);
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
	
	private void sprinkle() //simulates the sprinklers being turned on
	{
		if(getActive())
		{
			try 
        	{
            FileWriter fileWriter = new FileWriter(".\\realWorldEvents.txt", true);

            fileWriter.write("Sprinklers started pouring water \r\n");
            fileWriter.close();
        	}catch(IOException io)
        	{
        		System.out.println("Sprinkler malfunction");
        	}
			setActive(false);
		}
	}
	
	
	public void writeIntoFile(String time, MqttMessage message)
	{
		try 
		{
			FileWriter fileWriter = new FileWriter(".\\log.txt", true);
            String str = new String (message.getPayload());
            fileWriter.write("\n" + time + " \n " + getTopic() + " \n " + str + " \r\n");
            fileWriter.close();
            
            /*Integer num = new Integer(str.substring(Math.max(str.length() - 2, 0)));
            
            if(num > 50)
            {
           	 //setActive(true);
            }*/
		}catch(IOException ex)
		{
			ex.printStackTrace();
		}
		
	}

}
