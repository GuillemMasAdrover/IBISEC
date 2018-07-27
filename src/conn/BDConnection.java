package conn;
import java.sql.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class BDConnection {	
	
	//static final String hostName = "10.215.26.93";
	//static final String dbName = "proves";  
	
	//static final String hostName =  "10.215.26.87";
	//static final String dbName = "IBISEC";
	
	
	public static Connection getPostgreConnection() throws ClassNotFoundException, SQLException, NamingException 
	  {
	    Connection conn = null;
	    String userName = "postgres";
	    String password = "guillem";
	    // connect to the database
	    
	    // Get the base naming context
	    Context env = (Context)new InitialContext().lookup("java:comp/env");
	    // Get a single value
	    String hostName = (String)env.lookup("hostName");
	    String dbName = (String)env.lookup("dbName");
	    conn = getPostgreConnection(hostName, dbName, userName, password);
	    return conn;	    
	  }   

	  public static Connection getPostgreConnection(String hostName, String dbName,
		        String userName, String password) throws SQLException,
      ClassNotFoundException
	  {
	    Connection conn = null;
	   	Class.forName("org.postgresql.Driver");
	   	String url = "jdbc:postgresql://" + hostName + ":5432/" + dbName;
	   	conn = DriverManager.getConnection(url,userName, password);    	    
	    return conn;
	  }

}
