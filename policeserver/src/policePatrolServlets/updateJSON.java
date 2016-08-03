
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
		      
		      //--------------------------------------------
		      //TODO: Fix this to return the correct thing.
		      //For right now just hardcode the response. 
		      JSONObject json = new JSONObject();
		      /*JsonArray policeCars = new JsonArray();
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
		      json.put("Police", policeCars);*/
		      
		      json.put("firstName", "John");
		      json.put("lastName", "Smith");
		      json.put("ID", "32A");
		      
		      JSONArray routeoptions = new JSONArray();
		      JSONObject route1 = new JSONObject();
		      route1.put("time-UTM", 1465832832);
		      route1.put("Location", "1010 Wedgewood Ave. Nashville, TN, 37203");
		      route1.put("time to", 20);
		      route1.put("distance to", 1.2);
		      route1.put("route description", "A lot of activity near the liquor store. Watch out during peak hours of 1-4 pm");
		      routeoptions.put(route1);
		      json.put("route options", routeoptions);
		      
		      JSONArray patrols = getPatrolArray();
		      json.put("Patrols", patrols);
		      
		      JSONArray oncall = new JSONArray();
		      JSONObject oncall1 = new JSONObject();
		      oncall1.put("time-UTM", 1465832810);
		      oncall1.put("Location", "Loc 1");
		      oncall1.put("Description", "Robbery");
		      oncall1.put("GPS lat", 36.144);
		      oncall1.put("GPS long", -86.796);
		      oncall1.put("Precinct", "South");
		      oncall.put(oncall1);
		      json.put("On Call Crimes", oncall);
		      
		      JSONArray historic = new JSONArray();
		      JSONObject historic1 = new JSONObject();
		      historic1.put("time-UTM", 1465832810);
		      historic1.put("Location", "Liquor store on 14th");
		      historic1.put("Description", "Burglary");
		      historic1.put("GPS lat", 36.1351);
		      historic1.put("GPS long", -86.796);
		      historic1.put("Precinct", "South");
		      historic.put(historic1);
		      json.put("Historic Crimes", historic);
		      
		      //------------------------------------------
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
	
	private JSONArray getPatrolArray() throws SQLException, JSONException, IOException{
		
		JSONArray ret = new JSONArray();
		
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = (Connection) DriverManager.getConnection(DATABASE_LOCATION);
		     System.out.println("GET PATROL ARRAY: Creating statement...");
		     stmt = c.createStatement();
		     String sql = "SELECT * FROM AVLData WHERE Datetime IN (SELECT MAX(Datetime) FROM AVLData GROUP BY CarID)";
		      ResultSet rs = stmt.executeQuery(sql);
		      
		      
		      while(rs.next()) {
		    	  JSONObject patrol = new JSONObject();
		    	  patrol.put("ID", rs.getString("CarID"));
			      patrol.put("Location", "Blair Ave.");
			      patrol.put("GPS lat", rs.getDouble("Lat"));
			      patrol.put("GPS long", rs.getDouble("Long"));
			      patrol.put("distance to", 1.2);
			      patrol.put("Precinct", "Midtown Hills");
			      ret.put(patrol);
		      }
		      
		      stmt.close();
			  c.close();
		      rs.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      return ret;
	}
	
	private JSONArray getCrimeArray() throws SQLException, JSONException, IOException{
		
		JSONArray ret = new JSONArray();
		
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = (Connection) DriverManager.getConnection(DATABASE_LOCATION);
		     System.out.println("GET PATROL ARRAY: Creating statement...");
		     stmt = c.createStatement();
		     String sql = "SELECT * FROM AVLData WHERE Datetime IN (SELECT MAX(Datetime) FROM AVLData GROUP BY CarID)";
		      ResultSet rs = stmt.executeQuery(sql);
		      
		      
		      while(rs.next()) {
		    	  JSONObject patrol = new JSONObject();
		    	  patrol.put("ID", rs.getString("CarID"));
			      patrol.put("Location", "Blair Ave.");
			      patrol.put("GPS lat", rs.getDouble("Lat"));
			      patrol.put("GPS long", rs.getDouble("Long"));
			      patrol.put("distance to", 1.2);
			      patrol.put("Precinct", "Midtown Hills");
			      ret.put(patrol);
		      }
		      
		      stmt.close();
			  c.close();
		      rs.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      return ret;
	}
	
	//Custom class to store crime data temporarily: avoid type-casting continuously

	

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
	
	public class CrimeDataHolder
	{
		double datetime;
		String location;
		String description;
		double lat_;
		double long_;
		String precinct;
	}
	
	public class PoliceDataHolder
	{
		double datetime;
		String carID;
		double lat_;
		double long_;
	}
	
	//Method to get current crimes on call
	protected ArrayList<CrimeDataHolder> getCurrentOnCallCrimes() throws SQLException{
		//create data holder
		ArrayList<CrimeDataHolder> crimesOnCall = new ArrayList<CrimeDataHolder>();		
		Connection c = null;
		Statement stmt = null;
		try{
			//fetch data from db
			Class.forName("org.sqlite.JDBC");
			c = (Connection) DriverManager.getConnection(DATABASE_LOCATION);
			System.out.println("GET PATROL ARRAY: Creating statement...");
			stmt = c.createStatement();
			String sql = "SELECT * FROM crime WHERE oncall = 1";
			ResultSet rs = stmt.executeQuery(sql);
			
			//iterate through results set and store in holder
			while(rs.next())
			{
				CrimeDataHolder crime = new CrimeDataHolder();
				crime.datetime = rs.getDouble("datetime");
				crime.location = rs.getString("location");
				crime.description = rs.getString("description");
				crime.lat_ = rs.getDouble("lat");
				crime.long_ = rs.getDouble("long");
				crime.precinct = rs.getString("precinct");
				
				crimesOnCall.add(crime);
			}
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		
		return crimesOnCall;
	}
	
	//Method to get current police placements
	protected ArrayList<PoliceDataHolder> getCurrentPolice() throws SQLException{
		//create data holder	
		ArrayList<PoliceDataHolder> policeCurr = new ArrayList<PoliceDataHolder>();
		Connection c = null;
		Statement stmt = null;
		try{
			//fetch data from db
			Class.forName("org.sqlite.JDBC");
			c = (Connection) DriverManager.getConnection(DATABASE_LOCATION);
			System.out.println("GET PATROL ARRAY: Creating statement...");
			stmt = c.createStatement();
			String sql = "SELECT * FROM AVLData WHERE Datetime IN (SELECT MAX(Datetime) FROM AVLData GROUP BY CarID)";
			ResultSet rs = stmt.executeQuery(sql);
			
			//iterate through results set and store in holder
			while(rs.next())
			{
				PoliceDataHolder policeTemp = new PoliceDataHolder();
				policeTemp.datetime = rs.getDouble("datetime");
				policeTemp.carID = rs.getString("carID");
				policeTemp.lat_ = rs.getDouble("lat");
				policeTemp.long_ = rs.getDouble("long");
				
				policeCurr.add(policeTemp);
			}
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		
		return policeCurr;
	}
	
	/*CODE FROM http://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude-what-am-i-doi
	 * 
	 * Calculate distance between two points in latitude and longitude taking
	 * into account height difference. If you are not interested in height
	 * difference pass 0.0. Uses Haversine method as its base.
	 * 
	 * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
	 * el2 End altitude in meters
	 * @returns Distance in Meters
	 */
	public static double distance(double lat1, double lat2, double lon1,
	        double lon2, double el1, double el2) {

	    final int R = 6371; // Radius of the earth

	    Double latDistance = Math.toRadians(lat2 - lat1);
	    Double lonDistance = Math.toRadians(lon2 - lon1);
	    Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
	            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
	            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
	    Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double distance = R * c * 1000; // convert to meters

	    double height = el1 - el2;

	    distance = Math.pow(distance, 2) + Math.pow(height, 2);

	    return Math.sqrt(distance);
	}
	
	public double[][] getDistanceMatrix(ArrayList<PoliceDataHolder> policeCurr,ArrayList<CrimeDataHolder> crimesCurr)
	{
		int numPolice = policeCurr.size();
		double[][] distanceMatrix = new double[numPolice][numPolice];
		double distTemp;
		int counterPolice = 0;
		int counterCrime = 0;
		for (PoliceDataHolder policeTemp : policeCurr)
		{
			for (CrimeDataHolder crimeTemp : crimesCurr)
			{
				distTemp = distance(policeTemp.lat_,crimeTemp.lat_,policeTemp.long_,crimeTemp.long_,0,0);
				distanceMatrix[counterPolice][counterCrime] = distTemp;
				counterCrime++;
			}
			counterPolice++;
		}
			
		return distanceMatrix;
		
	}
	
	protected int[] heuristicSolveTransport (double[][] dist)
	{
		double min;
		int minDistPolice = 0;
		int minDistCrime = 0;
		//for each police, mark the index of the target that it should go to 
		int targets[]  = new int[dist[0].length];
		//once we find a police for a crime, take that crime out of the rest of the problem
		Boolean crimeLeft[]  = new Boolean[dist[0].length];
		Boolean policeLeft[]  = new Boolean[dist[0].length];
		
		min = dist[0][0];
		//till crimes are left
		while(Arrays.asList(crimeLeft).contains(true))
		{
			//iterate through rows: police
		    for ( int i = 0; i < dist.length; i++ )
		    {
		    	//if the police is still available
		    	if (policeLeft[i]==true)
		    	{
					//iterate through columns: crimes
			    	for ( int j = 0; j < dist [ i ].length; j++ )
			    	{
			    		//if this crime still needs to be served
			    		if (crimeLeft[j]==true)
			    		{
				            if ( dist [ i ] [ j ] < min )
				            {
				            	//in case the total distance needs to be calculated
				            	min = dist [ i ] [ j ];
				            	minDistPolice = i;
				            	minDistCrime = j;
				            }				            
			    		} 		
			    	}
		    	}
		    }		    
		    policeLeft[minDistPolice] = false;
		    crimeLeft[minDistCrime] = false;
		    targets[minDistPolice] = minDistCrime;
		}
		return targets;
	}

	//method to solve Transportation Problem
	//get current Crimes on call, current police placements and return optimal police placements
	protected void getPoliceAssignments()
	{
		try
		{
			//fetch current police positions
			ArrayList<PoliceDataHolder> policeCurr = getCurrentPolice();
			//fetch crimes that need to be responded to
			ArrayList<CrimeDataHolder> crimesCurr = getCurrentOnCallCrimes();
			//calculate distances between the police points and crime points
			//we always treat that we have sufficient police to respond to crimes
			double[][] distMatrix = getDistanceMatrix(policeCurr,crimesCurr);
			//get assignments for each crime and police
			int[] targets = heuristicSolveTransport(distMatrix);
			
		}		
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
	}

	
}
