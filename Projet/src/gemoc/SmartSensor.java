package gemoc;

import java.util.HashMap;

public class SmartSensor{

	private String nom = "";
	private int id = 1;
	private int type;
	private Port PortIn = null;
	private Port PortOut = null;
	private Double Seuil;
	private String SmartData = "";
	private Double currentValue = null;
	private String Etat = "";
	private String location = "";
	
	
	public SmartSensor(String s, int t){
		this.nom = s;
		this.type = t;
		this.Etat = "Inactif";
		
	}
	
	//Getters / setters
	public void setLocation(String s){
		this.location = s;
	}
	public String getLocation(){
		return this.location;
	}
	public int getId(){
		return id;
	}
	
	public void setId(int i){
		this.id = i;
	}
	public void setType(int s){
		this.type = s;
	}
	public int getType(){
		return this.type;
	}
	public void addInPort(Port p){
		this.PortIn = p;
	}
	
	public void addOutPort(Port p){
		this.PortOut = p;
	}
	public void setSeuil(Double d){
		this.Seuil = d;
	}
	
	public void setEtat(String s){
		this.Etat = s;
	}
	// On recupere les ports de E/S
	public Port getPortIn(){
		return this.PortIn;
	}
	
	public Port getPortOut(){
		return this.PortOut;
	}
	
	/*ID DES CAPTEURS
	 * id 10 = capteur d'humidity dans la douche
	 * id 11 = capteur PIR dans la douche
	 * id 12 = capteur ligt dans la douche
	 * 
	 * id 21 = capteur PIR dans le salon
	 * 
	 * 
	 * id 31 = capteur PIR dans la chambre
	 */
	// On recupere la valeur convertie qui est appellé smartdata
	public String getSmartData(){
		return this.SmartData;
	}
	
	public void creatSmartData(HashMap<Integer,SmartSensor> ListSmartSensor) throws Throwable{
		String[] tabPortInValue = this.PortIn.readValue().split(";");
		this.currentValue = Double.parseDouble(tabPortInValue[3]);
		if(tabPortInValue[3] == "" || tabPortInValue[3] == null) throw new Throwable("-1");
		//this.currentValue = Double.parseDouble(this.PortIn.readValue());
		//type
		// 2 = humidity
		// 0 = PIR
		// 1 = Temperature
		// 3 = light
		String typeS ="";
		if(this.type == 2){
			typeS ="Humidity";
		}
		if(this.type == 0){
			typeS ="PIR";	
				}
		if(this.type == 1){
			typeS ="Temperature";
		}
		if(this.type == 3){
			typeS ="Light";
		}
		// on verifie que la validité du smartsensor
		if(ListSmartSensor.containsKey(this.id) && this.location.equals("Bathroom") && this.type == 2){
			//on verifie si sa valeur est supérieur au seuil
				//si elle est > alors on execute laction suivante
			if(this.currentValue > this.Seuil){
				this.SmartData = tabPortInValue[0]+";"+"The humidity is high"+";"+tabPortInValue[1]+";"+tabPortInValue[2]+";"+tabPortInValue[4]+";"+this.Seuil+";"+this.currentValue+";"+typeS;
			}
			//si elle est < alors on execute laction suivante
			if(this.currentValue < this.Seuil){
				this.SmartData = tabPortInValue[0]+";"+"The humidity is low"+";"+tabPortInValue[1]+";"+tabPortInValue[2]+";"+tabPortInValue[4]+";"+this.Seuil+";"+this.currentValue+";"+typeS;;
			}
			//...etc pour chaque condition
		}else if(ListSmartSensor.containsKey(this.id) && this.location.equals("Bathroom") && this.type == 0){
			Integer i = this.currentValue.intValue();
			if(i == 1){
				this.SmartData = tabPortInValue[0]+";"+"The person is in the bathroom"+";"+tabPortInValue[1]+";"+tabPortInValue[2]+";"+tabPortInValue[4]+";"+this.Seuil+";"+this.currentValue+";"+typeS;;
			}else{
				this.SmartData = tabPortInValue[0]+";"+"The person is not in the bathroom"+";"+tabPortInValue[1]+";"+tabPortInValue[2]+";"+tabPortInValue[4]+";"+this.Seuil+";"+this.currentValue+";"+typeS;;
			}
		}else if(ListSmartSensor.containsKey(this.id) && this.location.equals("Livingroom") && this.type == 0){
			Integer i = this.currentValue.intValue();
			if(i == 1){
				this.SmartData = tabPortInValue[0]+";"+"The person is in the living room"+";"+tabPortInValue[1]+";"+tabPortInValue[2]+";"+tabPortInValue[4]+";"+this.Seuil+";"+this.currentValue+";"+typeS;;
			}else{
				this.SmartData = tabPortInValue[0]+";"+"The person is not in the living room"+";"+tabPortInValue[1]+";"+tabPortInValue[2]+";"+tabPortInValue[4]+";"+this.Seuil+";"+this.currentValue+";"+typeS;;
			}
		}
		else if(ListSmartSensor.containsKey(this.id) && this.location.equals("Room") && this.type == 0){
			Integer i = this.currentValue.intValue();
			if(i == 1){
				this.SmartData = tabPortInValue[0]+";"+"The person is in the room"+";"+tabPortInValue[1]+";"+tabPortInValue[2]+";"+tabPortInValue[4]+";"+this.Seuil+";"+this.currentValue+";"+typeS;;
			}else{
				this.SmartData = tabPortInValue[0]+";"+"The person is not in the room"+";"+tabPortInValue[1]+";"+tabPortInValue[2]+";"+tabPortInValue[4]+";"+this.Seuil+";"+this.currentValue+";"+typeS;;
			}
		}
		else if(ListSmartSensor.containsKey(this.id) && this.location.equals("Bathroom") && this.type == 3){
			if(this.currentValue > this.Seuil){
				this.SmartData = tabPortInValue[0]+";"+"The light is on in bathroom"+";"+tabPortInValue[1]+";"+tabPortInValue[2]+";"+tabPortInValue[4]+";"+this.Seuil+";"+this.currentValue+";"+typeS;;
			}
			if(this.currentValue < this.Seuil){
				this.SmartData = tabPortInValue[0]+";"+"The light is off in bathroom"+";"+tabPortInValue[1]+";"+tabPortInValue[2]+";"+tabPortInValue[4]+";"+this.Seuil+";"+this.currentValue+";"+typeS;;
			}
		}else{
			 throw new Throwable("Error SensorId or TypeSensor");//Erreur sur l'id ou le type
		}
	      
}
	//Permet de mettre actif un capteur
	public void setEtatActif() {
		// TODO Auto-generated method stub
		this.Etat = "Actif";
		
	}
}
