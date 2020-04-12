package IoTSimulation;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import org.eclipse.paho.client.mqttv3.*;
import java.util.Random;
import java.util.concurrent.*;

/**
 * @author Augustas Valaitis Informatika 1 grupë
 */

import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Appliance
{
	private MqttClient client;
	private String ID;
	private String topic;
	private String host;
	
	public Appliance()
	{
		topic = "Augi Base";
		ID = "Augi_Base";
		//host = "tcp://mqtt.eclipse.org:1883";
	    host = "tcp://test.mosquitto.org:1883";
		try 
		{
			MemoryPersistence persistence = new MemoryPersistence();
			//client = new MqttClient("tcp://test.mosquitto.org:1883", ID, persistence);
			client = new MqttClient(getHost(), ID, persistence);
			client = new MqttClient(getHost(), ID, persistence);
		}catch(MqttException ex)
		{
			printException(ex);
		}
		
	}
	
	public String getHost()
	{
		return host;
	}
	
	//generates a random number for the purpose of simulating real world events
	public int generateRandomNumber(int max)
	{
		Random rand = new Random(); 
  
        int number = rand.nextInt(max); 
        
        return number;
	}
	
	public MqttClient getClient()
	{
		return client;
	}
	
	public void setClient(MqttClient newClient)
	{
		client = newClient;
	}
	
	public String getID()
	{
		return ID;
	}
	
	public void setID(String name)
	{
		ID = name;
	}
	
	
	public String getTopic()
	{
		return topic;
	}
	
	public void setTopic(String name)
	{
		topic = name;
	}
	
	//prints an informative message in the event of an exception being thrown
	public void printException(MqttException me)
	{
		System.out.println("reason " + me.getReasonCode());
        System.out.println("msg " + me.getMessage());
        System.out.println("loc " + me.getLocalizedMessage());
        System.out.println("cause " + me.getCause());
        System.out.println("excep " + me);
        me.printStackTrace();
	}
	
	//provides instructions what should client do in specific cases
	public MqttCallback getCallback()
	{
		return new MqttCallback() {
             public void messageArrived(String topic, MqttMessage message) throws Exception {
                 String time = new Timestamp(System.currentTimeMillis()).toString();
                 writeIntoFile(time, message);
                 
             }

             public void connectionLost(Throwable cause) {
                 System.out.println("Connection to broker messaging lost!3 " + cause.getMessage());
                 printException((MqttException)cause);
             }

             public void deliveryComplete(IMqttDeliveryToken token) {
             }
         };
	}
	
	//returns a message that is fit to be published
	public MqttMessage prepareMessage(String content)
	{
		MqttMessage message = new MqttMessage(content.getBytes());
		message.setQos(2);
		
		return message;
	}
	
	//simulates notifying user's phone
	public void informUser(String message)
	{
		MqttClient phoneClient;
		String phoneTopic = "Augi User";
		String phoneID = "Augi_Informer";
		try 
		{
			MemoryPersistence persistence = new MemoryPersistence();
			phoneClient = new MqttClient("tcp://test.mosquitto.org:1883", phoneID, persistence);
			
			MqttConnectOptions connOpts = new MqttConnectOptions();
	        connOpts.setCleanSession(true);
			phoneClient.connect(connOpts);
			
			phoneClient.publish(phoneTopic, prepareMessage(message));
			phoneClient.disconnect();
			phoneClient.close();
		}catch(MqttException ex)
		{
			printException(ex);
		}
		
	}
	
	//Simulates real world events by logging them in a txt file
	public void writeIntoFile(String time, MqttMessage message)
	{
		try 
		{
			FileWriter fileWriter = new FileWriter(".\\log.txt", true);
	        fileWriter.write(new String("\n" + time + "\n" + getTopic() + "\n" + message.getPayload() + "\r\n"));
	        fileWriter.close();
		}catch(IOException ex)
		{
			ex.printStackTrace();
		}
		
	}
	
	public MqttConnectOptions getConnectOptions()
	{
		MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setAutomaticReconnect(true);
        return connOpts;
	}
}
