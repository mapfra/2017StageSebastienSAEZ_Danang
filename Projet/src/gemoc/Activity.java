package gemoc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
/*
 * Cette classe va permettre de créer des activité en relation 
 * avec le topic passé en parametre dans le constructeur
 * 
 */
public class Activity {
	private String nom = "";
	//private ArrayList<Integer> ListSensor = new ArrayList<Integer>();
	//private ArrayList<Integer> ListSmartSensor = new ArrayList<Integer>();
	private String topic = "";
	private ArrayList<Port> ListAllPortSensor = new ArrayList<Port>();
	private HashMap<Integer,SmartSensor> ListSmartSensor = new HashMap<Integer,SmartSensor>();
	private SmartSensor Humidity;
	private SmartSensor PIR;
	private SmartSensor Light;
	private MqttClient mqttc;
	
	//Constructeur
	public Activity(String n, String t, MqttClient m){
		this.nom = n;
		this.topic = t;
		this.mqttc = m;
	}
	//Cette méthode va initialiser les differents capteurs se trouvant dans la piece / maison 
	public void initActivity(int id, int type,String location){

		//TYPE DE CAPTEUR
		 // 0 = PIR
		 // 1 = Temperauture
		 // 2 = Humidity
		 // 3 = Light
		
		
		//On verifie le type du capteur et ensuite on ajoute les differentes propriétées selon son type
			if(type == 2){
				if(ListSmartSensor.containsKey(id) == false){
					this.Humidity.setId(id);
					this.Humidity.setType(type);
					this.Humidity.setLocation(location);
					this.Humidity.setSeuil(50.00);
					this.ListSmartSensor.put(id, this.Humidity);
				}
				
			}
			if(type == 0){
				if(ListSmartSensor.containsKey(id) == false){
					this.PIR.setId(id);
					this.PIR.setLocation(location);
					this.PIR.setSeuil(1.00);
					ListSmartSensor.put(id, this.PIR);
				}
			}
			if(type == 3){
				if(ListSmartSensor.containsKey("LIGHT") == false){
				
					this.Light.setId(id);
					this.Light.setLocation(location);
					this.Light.setSeuil(100.00);
					ListSmartSensor.put(id, this.Light);
				}
			}
			
			
		
		}
	
	//Cette méthode va permettre d'exectuer l'activité 
	public void runActivity() throws MqttSecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, MqttException, SQLException{
		
		// On Créer les ports qui seront utilisé dans le programme
		Port P1 = new Port("P1");// PORT MQTT
		Port P7 = new Port("P7");//PORT MQTT
		Port P4 = new Port("P4");//PORT MQTT
		
		Port P2 = new Port("P2"); // ENTRE DE SmartSensorHumidity
		Port P3 = new Port("P3"); // SORTIE DE SmartSensorHumidty
		
		Port P5 = new Port("P5");//ENTRE DE SmartSensorPIR
		Port P6 = new Port("P6");//SORTIE DE SmartSensorPIR
		
		Port P8 = new Port("P8");//ENTRE DE SmartSensorLight
		Port P9 = new Port("P9");//SORTIE DE SmartSensorLight
		
		Port P10 = new Port("P10"); //ENTRE DE MONITORING CONTROLLER
		Port P11 = new Port("P11"); //ENTRE DE MONITORING CONTROLLER
		Port P12 = new Port("P12"); //ENTRE DE MONITORING CONTROLLER
		
		//TYPE DE CAPTEUR
		 // 0 = PIR
		 // 1 = Temperauture
		 // 2 = Humidity
		 // 3 = Light
		
		// On créer les differents smartSensor
		this.Humidity = new SmartSensor("Humidity",2);
		this.PIR = new SmartSensor("PIR",0);
		this.Light = new SmartSensor("Light",3);
		
		//On affecte les ports au SmartSensor
		Humidity.addInPort(P2);
		Humidity.addOutPort(P3);
		
		PIR.addInPort(P5);
		PIR.addOutPort(P6);
		
		Light.addInPort(P8);
		Light.addOutPort(P9);
		
		ListAllPortSensor.add(P2); 
		ListAllPortSensor.add(P3);
		ListAllPortSensor.add(P5);
		ListAllPortSensor.add(P6);
		ListAllPortSensor.add(P8);
		ListAllPortSensor.add(P9);
		
		//MonitoringController va nous permttre de stocker dans la base de données et de gerer les workflows
		MonitoringController MC = new MonitoringController();
		MC.addPort(P10);
		MC.addPort(P11);
		MC.addPort(P12);
		Activity a = this;
		
		// Propriété de la base de donnée SQL
		 String host = "mysql.nextwab.com/";
		 String port = "3306";
		 String nomBDD = "usr_sae7087793d21187";
		 String login = "sae7087793d21187";
		 String passwd = "slego123";
		 
		 // On créer le dispatcher en relation avec l'activité
		Dispatcher D = new Dispatcher(host,port,nomBDD,login,passwd);
		D.Mqtt(this.mqttc,this.topic,a, this.ListSmartSensor, MC);
		
	}
	

	
}
