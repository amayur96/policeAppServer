package dbConnection;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;


import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class DBConnect {
	private static final String USERNAME = "root";
	private static final String PASSWORD = "hang9194";
	private static final String CONN_STRING = "jdbc:mysql://localhost/crimePatrol";
	
	public static void main (String[]args)
	{
		addPoliceData("PCP12345",14.323,16.87);
	}
	public static void addPoliceData(String id, double latitude, double longitude)
	{
		Connection con = null;
		try
		{
			con = (Connection) DriverManager.getConnection(CONN_STRING,USERNAME,PASSWORD);//connects to database
			System.out.println("Connected");
			Statement stmnt = (Statement) con.createStatement();
			stmnt.executeUpdate("INSERT INTO policeData(id,Latitude,Longitude) " +
					"VALUES ('PCP1234', 14.56, 18.6985)");//the id must be a string
		    con.close();
		}
		catch(SQLException e)
		{
			System.err.println(e);
		}
	}
	

}
