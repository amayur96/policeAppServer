
package policePatrolServlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jdk.nashorn.internal.parser.JSONParser;

import java.util.Enumeration;

/**
 * Servlet implementation class updateJSON
 */
@WebServlet("/updateJSON")
public class updateJSON extends HttpServlet {
	// change this to your location.
	private static final String DATABASE_LOCATION = "jdbc:sqlite:/Users/genexli/Documents/policeAppServer/Databases/test.db";
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public updateJSON() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void init() throws ServletException {
		super.init();
		//TODO: We'll need multiple tables for the different categories in the JSON file.
		//check if tables are created.
		if(!checkTableExists()) {
			createTable();
		}
		System.out.println("init worked");
	}

	/**
	 * @throws IOException 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	
	protected void sendJSON(HttpServletResponse response) throws SQLException, JSONException, IOException
	{
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = (Connection) DriverManager.getConnection(DATABASE_LOCATION);
		     System.out.println("SEND JSON: Creating statement...");
		     stmt = c.createStatement();
		     String sql = "SELECT Datetime, CarID, Lat, Long FROM AVLData";
		      ResultSet rs = stmt.executeQuery(sql);
		      //STEP 5: Extract data from result set
		      JSONObject json = new JSONObject();
		      JsonArray policeCars = new JsonArray();
		      JSONObject policeData;
		      while(rs.next()){
		         //Retrieve by column name
		    	 policeData = new JSONObject();
		         String date  = rs.getString("Datetime");
		         String carID = rs.getString("CarID");
		         double lat = rs.getDouble("Lat");
		         double lng = rs.getDouble("Long");
		         //Display values
		         System.out.print("Date: " + date);
		         System.out.print(", CarID: " + carID);
		         System.out.print(", Lat: " + lat);
		         System.out.println(", Lng: " + lng);
		         policeData.put("Date:",date);
		         policeData.put("CarID", carID);
		         policeData.put("Latitude", lat);
		         policeData.put("Longitude", lng);
		         policeCars.add(policeData.toString());
		      }
		      json.put("Police", policeCars);
		      response.setContentType("application/json");
		      response.getWriter().write(json.toString());
		      stmt.close();
			  c.close();
		      rs.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	protected void insertJSONData(HttpServletRequest request) throws Exception{
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = (Connection) DriverManager.getConnection(DATABASE_LOCATION);
			String sql = "";
			try {
				StringBuilder buffer = new StringBuilder();
				BufferedReader reader = request.getReader();
				String line;
				while ((line = reader.readLine()) != null) {
					buffer.append(line);
				}
				String jsonData = buffer.toString();
				System.out.println(jsonData);
				JSONObject obj = new JSONObject(jsonData);
				//edit
				String date = obj.getString("Datetime");
				String id = obj.getString("ID");
				double lat = obj.getDouble("latitude");
				double lng = obj.getDouble("longitude");
				sql = "INSERT INTO `AVLData`(Datetime,CarID,Lat,Long) VALUES ('" + date + "','" + id + "','" + lat
						+ "','" + lng + "')";
				stmt = (Statement) c.createStatement();
				stmt.executeUpdate(sql);
				stmt.close();
				c.close();
				System.out.println("INSERT JSON: Finished");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			sendJSON(response);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//doPost(request, response);
		try {
			insertJSONData(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void createDB()
	{
		Connection c = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = (Connection) DriverManager.getConnection(DATABASE_LOCATION);
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		System.out.println("CREATE DB: Opened database successfully");
	}
	
	public boolean checkTableExists() {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			System.out.println("CHECK TABLE EXISTS: Creating a connection...");
			c = (Connection) DriverManager.getConnection(DATABASE_LOCATION);
			DatabaseMetaData md = c.getMetaData();
			ResultSet res = md.getTables(null, null, "AVLData", null);
			if(res.next()) {
				System.out.println("CHECK TABLE EXISTS: Table already exists");
				System.out.println(
				        "   "+res.getString("TABLE_CAT") 
				       + ", "+res.getString("TABLE_SCHEM")
				       + ", "+res.getString("TABLE_NAME")
				       + ", "+res.getString("TABLE_TYPE")
				       + ", "+res.getString("REMARKS")); 
				c.close();
				return true;
			}
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		System.out.println("CHECK TABLE EXISTS: Table does not exist");
		return false;
	}
	
	public static void createTable()
	{
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = (Connection) DriverManager.getConnection(DATABASE_LOCATION);
			System.out.println("CREATE TABLE: Opened database successfully");
			String sql = "CREATE TABLE AVLData " +
					"(Datetime TEXT NOT NULL, " +
					" CarID TEXT NOT NULL, " +
					" Lat REAL, " +
					" Long REAL)";
			stmt = (Statement) c.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		System.out.println("CREATE TABLE: Table created successfully");
	}

	
}
