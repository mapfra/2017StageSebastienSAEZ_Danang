package gemoc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
//Cette classe va gerer les differents scénarios
public class MonitoringController {

	//Liste des ports qu'elle a en entrées
	private ArrayList<Port> alp = new ArrayList<Port>();
	
	public void addPort(Port p){
		this.alp.add(p);
	}
	public ArrayList<Port> getAL(){
		return this.alp;
	}
	//Scénario de la douche
	public void executeBathroomWorkflow() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		//Connexion a la base de donnée local & Création de la SSDB
		HSQL databaseSmartSensor = new HSQL("SSDB","slego","slego123");
		databaseSmartSensor.createTableSmartData();
		//Connexion a la base de donnée en ligne
		String host = "mysql.nextwab.com/";
		 String port = "3306";
		 String nomBDD = "usr_sae7087793d21187";
		 String login = "sae7087793d21187";
		 String passwd = "slego123";
		 SQL sql = new SQL(host,nomBDD,login,passwd);
		 
		 HSQL databaseActivityTrackingResult = new HSQL("ATRDB","slego","slego123");
		databaseActivityTrackingResult.createTableActivityTrackingResult();
		 //On recupere la derniere valeur des capteurs pour pouvoir ensuite faire les verifications
		 String exValuePIR = databaseSmartSensor.getSmartDataPIR();
		 String exValueLight = databaseSmartSensor.getSmartDataLight() ; 
		 String exValueHumidity = databaseSmartSensor.getSmartDataHumidity(); 
		//On read chaque port pour voir quelle valeur a été envoyé
		for(int i = 0; i<alp.size();i++){
			if(((Port) alp.get(i)).readValue().equals("")){
				//System.out.println("Erreur");
			}else{
			String value = ((Port) alp.get(i)).readValue();
			String[] valueSplited = value.split(";");
			//System.out.println(((Port) alp.get(i)).readValue());
			
			//Affichage des differents valeurs
			System.out.println("Personne : " + valueSplited[0]);
			System.out.println("Action : " + valueSplited[1]);
			System.out.println("Date : " + valueSplited[2]);
			System.out.println("UTC : " + valueSplited[4]);
			System.out.println("Seuil : " + valueSplited[5]);
			System.out.println("Value : " + valueSplited[6]);
			System.out.println("Type : " + valueSplited[7]);
			int hours = Integer.parseInt(valueSplited[3]) / 3600;
        	int minutes = (Integer.parseInt(valueSplited[3]) % 3600) / 60;
        	int seconds = Integer.parseInt(valueSplited[3]) % 60;
			System.out.println("Heure : " + hours+":"+minutes+":"+seconds);

			System.out.println("---------");
			//On regarde si le comportement de la personne, on en déduit l'activité et on stocke le resultat dans la base de donnée
			if(exValuePIR.matches("(.*)is in the bathroom(.*)") && exValueHumidity.matches("(.*)low(.*)") && valueSplited[1].matches("(.*)high(.*)") || exValuePIR.matches("(.*)is in the bathroom(.*)") && exValueHumidity.matches("") && valueSplited[1].matches("(.*)high(.*)") ){
				databaseActivityTrackingResult.insertRowATR(valueSplited[0], "UTC "+ valueSplited[4], valueSplited[2], hours+":"+minutes+":"+seconds,"Start of shower");
				sql.insertRow("INSERT INTO ATRDB VALUES (NULL,'"+valueSplited[0]+"','"+"UTC " + valueSplited[4]+"','"+valueSplited[2]+"','"+ hours+":"+minutes+":"+seconds+"','Start of shower')");
				
			} else if(exValuePIR.matches("(.*)is in the bathroom(.*)") && exValueHumidity.matches("(.*)high(.*)") && valueSplited[1].matches("(.*)high(.*)")){
				databaseActivityTrackingResult.insertRowATR(valueSplited[0], "UTC "+ valueSplited[4], valueSplited[2], hours+":"+minutes+":"+seconds,"The person takes his shower");
				sql.insertRow("INSERT INTO ATRDB VALUES (NULL,'"+valueSplited[0]+"','"+"UTC " + valueSplited[4]+"','"+valueSplited[2]+"','"+ hours+":"+minutes+":"+seconds+"','The person takes his shower')");
				
			}else if(exValuePIR.matches("(.*)is in the bathroom(.*)") && exValueHumidity.matches("(.*)high(.*)") && valueSplited[1].matches("(.*)low(.*)")){
				databaseActivityTrackingResult.insertRowATR(valueSplited[0], "UTC "+ valueSplited[4], valueSplited[2], hours+":"+minutes+":"+seconds,"End of shower");
				sql.insertRow("INSERT INTO ATRDB VALUES (NULL,'"+valueSplited[0]+"','"+"UTC " + valueSplited[4]+"','"+valueSplited[2]+"','"+ hours+":"+minutes+":"+seconds+"','End of shower')");
				
			}
			
			databaseSmartSensor.insertRowSD(valueSplited[0], "UTC "+ valueSplited[4], valueSplited[2], hours+":"+minutes+":"+seconds,valueSplited[1],valueSplited[4],valueSplited[5],valueSplited[6]);
			sql.insertRow("INSERT INTO SSDB VALUES (NULL,'"+valueSplited[0]+"','"+"UTC " + valueSplited[4]+"','"+valueSplited[2]+"','"+ hours+":"+minutes+":"+seconds+"','"+valueSplited[1]+"','"+valueSplited[4]+"','"+valueSplited[5]+"','"+valueSplited[6]+"')");
			
			showData();
			System.out.println("------------");
			this.showdataFromATR();
			}
		}
		
		
		
	}
	//Similaire a la méthode du worflow de la douche
	public void executeActivityTracking() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		String host = "mysql.nextwab.com/";
		 String port = "3306";
		 String nomBDD = "usr_sae7087793d21187";
		 String login = "sae7087793d21187";
		 String passwd = "slego123";
		 
		 String url = "jdbc:mysql://"+host+":"+ port +"/";
		 SQL sql = new SQL(host,nomBDD,login,passwd);
		 
		 HSQL databaseSmartSensor = new HSQL("SSDB","slego","slego123");
			databaseSmartSensor.createTableSmartData();
			
		for(int i = 0; i<alp.size();i++){
			if(((Port) alp.get(i)).readValue() == ""){
				//System.out.println("Erreur");
			}else{
				String value = ((Port) alp.get(i)).readValue();
				String[] valueSplited = value.split(";");
				//System.out.println(((Port) alp.get(i)).readValue());
				System.out.println("Personne : " + valueSplited[0]);
				System.out.println("Action : " + valueSplited[1]);
				System.out.println("Date : " + valueSplited[2]);
				System.out.println("UTC : " + valueSplited[4]);
				int hours = Integer.parseInt(valueSplited[3]) / 3600;
	        	int minutes = (Integer.parseInt(valueSplited[3]) % 3600) / 60;
	        	int seconds = Integer.parseInt(valueSplited[3]) % 60;
				System.out.println("Heure : " + hours+":"+minutes+":"+seconds);
	
				System.out.println("---------");
				HSQL databaseActivityTrackingResult = new HSQL("ATRDB","awsom","slego123");
				databaseActivityTrackingResult.createTableActivityTrackingResult();
				if(valueSplited[1].matches("The person is in (.*)")){
					databaseActivityTrackingResult.insertRowATR(valueSplited[0], "UTC " + valueSplited[4], valueSplited[2], hours+":"+minutes+":"+seconds,valueSplited[1]);
					sql.insertRow("INSERT INTO ATRDB VALUES (NULL,'"+valueSplited[0]+"','"+"UTC " + valueSplited[4]+"','"+valueSplited[2]+"','"+ hours+":"+minutes+":"+seconds+"','"+valueSplited[1]+"')");
				}
				
				databaseSmartSensor.insertRowSD(valueSplited[0], "UTC "+ valueSplited[4], valueSplited[2], hours+":"+minutes+":"+seconds,valueSplited[1],valueSplited[4],valueSplited[5],valueSplited[6]);
				sql.insertRow("INSERT INTO SSDB VALUES (NULL,'"+valueSplited[0]+"','"+"UTC " + valueSplited[4]+"','"+valueSplited[2]+"','"+ hours+":"+minutes+":"+seconds+"','"+valueSplited[1]+"','"+valueSplited[4]+"','"+valueSplited[5]+"','"+valueSplited[6]+"')");
				
				
				 this.showdataFromATR();
			}
		}
		
	}
	//Affiche la base de donnée local
	public void showData() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		
		 //Partie HSQL
		 String login = "slego";
		 String passwd = "slego123";
         HSQL hsql = new HSQL("RAWDATA",login,passwd);
         System.out.println("RAW DATA");
         hsql.displayRowsRawData();
         
         System.out.println("");
         System.out.println("SMART DATA");
         HSQL databaseSmartSensor = new HSQL("SSDB","selgo","slego123");
		databaseSmartSensor.displayRowsSD();
 		
	}
	// affiche la table ATR de la base de donnée local
	public void showdataFromATR() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		 System.out.println("");
         System.out.println("ACTIVITYRESULT DATA");
         HSQL databaseSmartSensor = new HSQL("ATRDB","slego","slego123");
		databaseSmartSensor.displayRowsATR();	
	}

}
