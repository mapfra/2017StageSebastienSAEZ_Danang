package gemoc;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSet;
import com.mysql.jdbc.Statement;

// Adresse IP de la base sql: 113.160.226.118 
// SHOW VARIABLES LIKE "%ssl%"
public class SQL {
	private String url = "";
	//private String NomBDD = "";
	private String login = "";
	private String 	passwd = "";
	Connection cnx = null;
	Statement st = null;
	public SQL(){
	
	}
	public SQL(String u, String nbdd, String l, String p){
		//this.url = "jdbc:mysql://localhost/"+u;
		this.url = "jdbc:mysql://" +u + nbdd;
		this.login = l;
		this.passwd = p;
	}
	
	public void insertRow(String rq) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		String requete;
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			cnx = (Connection) DriverManager.getConnection(this.url,this.login,this.passwd);
			st = (Statement) cnx.createStatement();
			
			requete = rq;
			st.execute(requete);
			System.out.println("Ligne ajoutée");
		} catch (SQLException e){
			System.out.println("Ligne deja existante dans la base de donnée");
			e.printStackTrace();	
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		} finally{
			try{
				cnx.close();
				st.close();
			} catch (SQLException e){
				e.printStackTrace();
				
			}
		}
	}
	
	
	public void displayRows(String rq) throws SQLException, ClassNotFoundException{
		ResultSet rs = null;
		Class.forName("com.mysql.jdbc.Driver");
		cnx = (Connection) DriverManager.getConnection(url,login,passwd);
		st = (Statement) cnx.createStatement();
		
		
		rs = (ResultSet) st.executeQuery(rq);
		while(rs.next()){
			//nom de la colonne en parametre dans le rs.getString()
			System.out.println(rs.getString("id") + " | " + rs.getString("Temperature"));
		}
	}




	
	public void LoginOrcreateDB(String u, String nbdd, String l, String p) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		this.url = u + nbdd;
		this.login = l;
		this.passwd = p;
	        try{
	            Class.forName("com.mysql.jdbc.Driver").newInstance();
	        }
	        catch(Exception e){
	        	e.printStackTrace();
	        }
	        try{
	           cnx = (Connection) DriverManager.getConnection( u + nbdd, login, passwd);
	        }
	        catch(SQLException ek){
	            try{
	            	cnx = (Connection) DriverManager.getConnection( u + nbdd, login, passwd);
	                System.out.println(" connexion à "+nbdd +" reussi");
	            }
	            catch(SQLException ef){
	              System.out.println("impossible de se connecter à " +nbdd);
	            } 
	        }
	        try{
	            cnx.close();
	        }
	        catch(SQLException v){}
	 
	 
	    }
	
		
}

