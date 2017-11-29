
package gemoc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

public class main {

	public main(){
		
	}
	
	//NE PAS MODIFIER
	public static void main(String[] args)  throws MqttSecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, MqttException, SQLException{
		
	   String hostMqtt = args[0];
		//String hostMqtt = "localhost";
		//String nomActivteALancer = args[1];
		 
		 
		String topicActivite1 = "Home/Bathroom/#";
		String topicActivite2 = "Home/+/PIR";
		ArrayList<Integer> ListSensor = new ArrayList<Integer>();
		HashMap<Integer,String> AllSensor = new HashMap<Integer,String>(); // L'id sur senser et le type du sensor
		
		MqttClient mc = new MqttClient("tcp://"+hostMqtt, "Home");
		 MqttConnectOptions connOpts = new MqttConnectOptions();
		 connOpts.setCleanSession(true);
		 mc.connect(connOpts);
		 
		 
		//Activité Douche
		Activity a1 = new Activity("Douche",topicActivite1,mc);
		a1.runActivity();
		
		//Activité Douche
		Activity Track = new Activity("Track",topicActivite2,mc);
		Track.runActivity();
		
	}
}
