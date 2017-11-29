package gemoc;

public class Port {
	
	private String nom;
	private String Value = "";
	
	public Port(String n){
		this.nom = n;
	}
	
	public String readValue(){
		return Value;
		
	}
	
	public void sendTo(Port p){
		p.writeValue(this.Value);
	}
	
	public void writeValue(String v){
		this.Value = v;
	}
}
