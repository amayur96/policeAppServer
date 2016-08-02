
package policePatrolServlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
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
		     System.out.println("Creating statement...");
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
				JSONArray data = obj.getJSONArray("police");
				JSONObject dataArrival = data.getJSONObject(0);
				String date = dataArrival.getString("Datetime");
				for (int i = 1; i < data.length(); i++) {
					JSONObject policeCar = data.getJSONObject(i);
					String id = policeCar.getString("CarID");
					double lat = policeCar.getDouble("Lat");
					double lng = policeCar.getDouble("Long");
					sql = "INSERT INTO `AVLData`(Datetime,CarID,Lat,Long) VALUES ('" + date + "','" + id + "','" + lat
							+ "','" + lng + "')";
					stmt = (Statement) c.createStatement();
					stmt.executeUpdate(sql);
				}
				stmt.close();
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	public static void createTable()
	{
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = (Connection) DriverManager.getConnection(DATABASE_LOCATION);
			System.out.println("Opened database successfully");
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
		System.out.println("Table created successfully");
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
		System.out.println("Opened database successfully");
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
		//doGet(request, response);
		createDB();
		createTable();
		try {
			insertJSONData(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
