package gemoc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
// Cette classe va recupérer la trame MQTT pour ensuite envoyer les valeurs aux smartsensor
public class Dispatcher {
		
	private String SubscribedTopic ="";
	private MqttClient mqttClient;
	private String host = "mysql-awsom.alwaysdata.net/";
	private String port = "3306";
	private String nomBDD = "awsom_slego";
	private String login = "awsom";
	private String passwd = "slego123";
	 
	 public Dispatcher(String h, String p, String nBDD, String l, String pw){
		 this.host = h;
		 this.port = p;
		 this.nomBDD = nBDD;
		 this.login = l;
		 this.passwd = pw;
	 }
	
	public void Mqtt(MqttClient mc, String topic, Activity a, HashMap<Integer,SmartSensor> ListSmartSensor, MonitoringController MC)  throws MqttSecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, MqttException, SQLException{
		
		 SQL sql = new SQL(host,nomBDD,login,passwd);
		// On spécifie le topic au qu'elle on veut s'abonner
		 this.SubscribedTopic = topic;
		 //On créer notre mqttclient qui va nous permettre de recevoir le message
		 this.mqttClient = mc;
		 mqttClient.setCallback(new MqttCallback() {
			 
			 //EXEMPLE DE LA TRAME MQTT
			 // 0 = ITERRUP
			 // 1 = PERIODIQUE
			 //TYPE DE CAPTEUR
			 // 0 = PIR
			 // 1 = Temperauture
			 // 2 = Humidity
			 // 3 = Light
			 //Version userid timezone date heure samplekind idsensor namesensor datasensor unitsensor typesensor localisation
			 //String TrameMqtt = "1;SAEZ;6;04-05-2017;57600;1;45;Humidity;30;%;1;Bathroom";
			 
			 //Methode qui recupere le message lorsqu'il arrive
			    public void messageArrived(String topic, MqttMessage message) throws Exception {
			    	
					Port P1 = new Port("P1");// PORT MQTT
					Port P7 = new Port("P7");//PORT MQTT
					Port P4 = new Port("P4");//PORT MQTT
					
			    	   ArrayList<Port> alp = new ArrayList<Port>();
			    	   alp.add(P1);
			    	   alp.add(P7);
			    	   alp.add(P4);

					//message.getPayload() contient le message recu
			     String TrameMqtt = new String(message.getPayload());
			     
			     //On créer le fichier CSV en rapport avec le topic
			       if(topic.matches("(.*)/Bathroom/(.*)")){ 
				      //Création et ecriture dans le fichier .csv
				      try{
				    	  String nomFile = "TrameMqttBathroomWorkflow.csv";
				    	  String teee = "./"+nomFile;
				    	  PrintWriter fich;
				    	  fich = new PrintWriter(new BufferedWriter(new FileWriter(teee, true))); 
				    	  fich.println(TrameMqtt);
				    	  fich.close();
				      }
			    	  catch (Exception e) {
			    	  // TODO: handle exception
			    	  }	    
			       }
			       if(topic.matches("(.*)/PIR")){ 
					      //Création et ecriture dans le fichier .csv
					      try{
		
					    	  String nomFile = "TrameMqttActivtyTracking.csv";
					    	  String teee = "./"+nomFile;
					    	  PrintWriter fich;
					    	  fich = new PrintWriter(new BufferedWriter(new FileWriter(teee, true))); 
					    	  fich.println(TrameMqtt);
					    	  fich.close();
		
					      }
				    	  catch (Exception e) {
				    	  // TODO: handle exception
				    	  }	    
				       }
			       
			       
			       //On sépare les differents valeur de la trame Mqtt pour envoyé seulement celle necessaire au smartsensor
			        String[] MqttSplited = TrameMqtt.split(";");
			        int Type_sensor = Integer.parseInt(MqttSplited[10]);
			        
			        //Version userid timezone date heure samplekind idsensor namesensor datasensor unitsensor typesensor

			        //On convertit la chaine de caractere qui correspond a l'id en int
			        int idCapteur = Integer.parseInt(MqttSplited[6]);
			       
			        //On convertit le chaine de caractere qui correspond au type en String en fonction de sa valeur
			       	String typeCapteur = "";
					if(Integer.parseInt(MqttSplited[10]) == 0){
		        	 typeCapteur = "PIR";
		          }
		        	if(Integer.parseInt(MqttSplited[10]) == 1){
		        		typeCapteur = "Temperature";	        	
		          }
		        	if(Integer.parseInt(MqttSplited[10]) == 2){
		        		typeCapteur = "Humidity";	        	
		          }
		        	if(Integer.parseInt(MqttSplited[10]) == 3){
		        		typeCapteur = "Light";	        	
		          }
		        	
		        	//On appelle la méthode pour initaliser les SmartSensor
			        a.initActivity(idCapteur, Type_sensor, MqttSplited[11]);

			        
			        //Partie HSQL;
			        HSQL hsql = new HSQL("RAWDATA","slego","slego123");
			        hsql.createTableRawData();
			        String samplekind = null;
					if(Integer.parseInt(MqttSplited[5]) == 0){
			          	samplekind = "INTERRUPTION";
			        }
			        if(Integer.parseInt(MqttSplited[5]) == 1){
			        		samplekind = "PEDIODIQUE";	        	
			        }

			        //On convertie la valeur qui est donnée en secondes en Heures,minutes,secondes
			        int hours = Integer.parseInt(MqttSplited[4]) / 3600;
			        int minutes = (Integer.parseInt(MqttSplited[4]) % 3600) / 60;
			        int seconds = Integer.parseInt(MqttSplited[4]) % 60;
			        
			
			        
			        if(Type_sensor == 2){  
			        	//On ecrit sur le port de sortie les valeurs que l'on veut envoyer
			        	alp.get(0).writeValue(MqttSplited[1]+";"+MqttSplited[3]+";"+MqttSplited[4]+";"+MqttSplited[8]+";"+MqttSplited[2]);
			        	//On envoie les valeurs via les ports d'entrées/sorties
			        	alp.get(0).sendTo(ListSmartSensor.get(idCapteur).getPortIn());
				        try {
				        	//On appelle la méthode pour créer la smartData en fonction de l'id
				        	ListSmartSensor.get(idCapteur).creatSmartData(ListSmartSensor);
				        	//On ecrite la valeur sur le port de sortie du smartData
				        	ListSmartSensor.get(idCapteur).getPortOut().writeValue(ListSmartSensor.get(idCapteur).getSmartData());
				        	//On envoie cette valeur
				        	ListSmartSensor.get(idCapteur).getPortOut().sendTo(MC.getAL().get(0));
				        	//On stock la valeur brute dans la table local et la table sur la base de donnée du serveur externe
							  hsql.insertRowRawData(MqttSplited[1],"UTC "+MqttSplited[2],MqttSplited[3],hours+":"+minutes+":"+seconds,samplekind,Integer.parseInt(MqttSplited[6]),MqttSplited[7],Double.parseDouble(MqttSplited[8]),MqttSplited[9],typeCapteur,MqttSplited[11]);
							  sql.insertRow("INSERT INTO RAWDATA VALUES (NULL, '"+MqttSplited[1]+"', '"+"UTC" + MqttSplited[2]+"', '"+MqttSplited[3]+"', '"+ hours+":"+minutes+":"+seconds+"', '"+samplekind+"', '"+Integer.parseInt(MqttSplited[6])+"', '"+MqttSplited[7]+"', '"+Double.parseDouble(MqttSplited[8])+"', '"+MqttSplited[9]+"', '"+typeCapteur+"', '"+MqttSplited[11]+"');");
							  //Meme procéder on ecrit sur le port en on envoie a l'autre port
							 ListSmartSensor.get(idCapteur).getPortOut().writeValue("");
					        	ListSmartSensor.get(idCapteur).getPortOut().sendTo(MC.getAL().get(1));
					        	ListSmartSensor.get(idCapteur).getPortOut().writeValue("");
					        	ListSmartSensor.get(idCapteur).getPortOut().sendTo(MC.getAL().get(2));
						} catch (Throwable e) {
							e.printStackTrace();
						}
			        }else if(Type_sensor == 0){  
			        	  
			      
			        	alp.get(1).writeValue(MqttSplited[1]+";"+MqttSplited[3]+";"+MqttSplited[4]+";"+MqttSplited[8]+";"+MqttSplited[2]);
			        	alp.get(1).sendTo(ListSmartSensor.get(idCapteur).getPortIn());
				        try {
				        	
				        	ListSmartSensor.get(idCapteur).creatSmartData(ListSmartSensor);
				        	ListSmartSensor.get(idCapteur).getPortOut().writeValue(ListSmartSensor.get(idCapteur).getSmartData());
				        	ListSmartSensor.get(idCapteur).getPortOut().sendTo(MC.getAL().get(1));
							  hsql.insertRowRawData(MqttSplited[1],"UTC "+MqttSplited[2],MqttSplited[3],hours+":"+minutes+":"+seconds,samplekind,Integer.parseInt(MqttSplited[6]),MqttSplited[7],Double.parseDouble(MqttSplited[8]),MqttSplited[9],typeCapteur,MqttSplited[11]); 
							  sql.insertRow("INSERT INTO RAWDATA VALUES (NULL, '"+MqttSplited[1]+"', '"+"UTC" + MqttSplited[2]+"', '"+MqttSplited[3]+"', '"+ hours+":"+minutes+":"+seconds+"', '"+samplekind+"', '"+Integer.parseInt(MqttSplited[6])+"', '"+MqttSplited[7]+"', '"+Double.parseDouble(MqttSplited[8])+"', '"+MqttSplited[9]+"', '"+typeCapteur+"', '"+MqttSplited[11]+"');");
							  ListSmartSensor.get(idCapteur).getPortOut().writeValue("");
					        	ListSmartSensor.get(idCapteur).getPortOut().sendTo(MC.getAL().get(0));
					        	ListSmartSensor.get(idCapteur).getPortOut().writeValue("");
					        	ListSmartSensor.get(idCapteur).getPortOut().sendTo(MC.getAL().get(2));
						} catch (Throwable e) {
							e.printStackTrace();
						}
			        }else if(Type_sensor == 3){  
			        	  
			        	
			        	alp.get(2).writeValue(MqttSplited[1]+";"+MqttSplited[3]+";"+MqttSplited[4]+";"+MqttSplited[8]+";"+MqttSplited[2]);
			        	alp.get(2).sendTo(ListSmartSensor.get(idCapteur).getPortIn());
				        try {
				        	
				        	ListSmartSensor.get(idCapteur).creatSmartData(ListSmartSensor);
				        	ListSmartSensor.get(idCapteur).getPortOut().writeValue(ListSmartSensor.get(idCapteur).getSmartData());
				        	ListSmartSensor.get(idCapteur).getPortOut().sendTo(MC.getAL().get(2));
							  hsql.insertRowRawData(MqttSplited[1],"UTC "+MqttSplited[2],MqttSplited[3],hours+":"+minutes+":"+seconds,samplekind,Integer.parseInt(MqttSplited[6]),MqttSplited[7],Double.parseDouble(MqttSplited[8]),MqttSplited[9],typeCapteur,MqttSplited[11]);
							  sql.insertRow("INSERT INTO RAWDATA VALUES (NULL, '"+MqttSplited[1]+"', '"+"UTC" + MqttSplited[2]+"', '"+MqttSplited[3]+"', '"+ hours+":"+minutes+":"+seconds+"', '"+samplekind+"', '"+Integer.parseInt(MqttSplited[6])+"', '"+MqttSplited[7]+"', '"+Double.parseDouble(MqttSplited[8])+"', '"+MqttSplited[9]+"', '"+typeCapteur+"', '"+MqttSplited[11]+"');");
							  ListSmartSensor.get(idCapteur).getPortOut().writeValue("");
					        	ListSmartSensor.get(idCapteur).getPortOut().sendTo(MC.getAL().get(0));
					        	ListSmartSensor.get(idCapteur).getPortOut().writeValue("");
					        	ListSmartSensor.get(idCapteur).getPortOut().sendTo(MC.getAL().get(1));
						} catch (Throwable e) {
							e.printStackTrace();
						}
			        }
			        //On execute le workflow en fonction du topic
			        if(topic.matches("(.*)/Bathroom/(.*)")){ 
			        	MC.executeBathroomWorkflow(); 
			        }
			        if(topic.matches("(.*)/PIR")){ 
			        	MC.executeActivityTracking();
			        }
					
					
			    }
			    //Cette methode genere une execption quand la connexion est perdu
			    public void connectionLost(Throwable cause) {
			        System.out.println(cause.getMessage());
			    }

			    public void deliveryComplete(IMqttDeliveryToken token) {}

			});
		 //Le client mqtt s'abonne au topic
		 mqttClient.subscribe(SubscribedTopic, 0);


	}
}
