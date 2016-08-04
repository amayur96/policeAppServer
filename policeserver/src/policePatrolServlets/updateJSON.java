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
	//private static final String DATABASE_LOCATION = "jdbc:sqlite:/Users/ayanmukhopadhyay/Documents/workspace/policeAppServer/policeAppServer/Databases/test.db";
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
		// TODO: We'll need multiple tables for the different categories in the
		// JSON file.
		// check if tables are created.
		if (!checkTableExists()) {
			createTable();
		}
		System.out.println("init worked");
	}

	/**
	 * @throws IOException
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	protected void sendJSON(HttpServletResponse response, String precinct)
			throws SQLException, JSONException, IOException {

		Connection c = null;
		Statement stmt = null;
		try {
			System.out.println("SEND JSON: Creating statement...");
			// STEP 5: Extract data from result set

			JSONObject json = new JSONObject();

			json.put("firstName", "John");
			json.put("lastName", "Smith");
			json.put("ID", "32A");

			JSONArray routeoptions = new JSONArray();
			JSONObject route1 = new JSONObject();
			route1.put("datetime", 1465832832);
			route1.put("Location", "1010 Wedgewood Ave. Nashville, TN, 37203");
			route1.put("time to", 20);
			route1.put("distance to", 1.2);
			route1.put(
					"route description",
					"A lot of activity near the liquor store. Watch out during peak hours of 1-4 pm");
			routeoptions.put(route1);
			json.put("route options", routeoptions);

			JSONArray patrols = getPatrolArray(precinct);
			json.put("Patrols", patrols);

			JSONArray oncall = getOnCallCrimeArray(precinct);
			json.put("On Call Crimes", oncall);

			JSONArray historic = getHistoricCrimeArray(precinct);
			json.put("Historic Crimes", historic);
			// ------------------------------------------
			response.setContentType("application/json");
			response.getWriter().write(json.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private JSONArray getPatrolArray(String precinct) throws SQLException,
			JSONException, IOException {

		JSONArray ret = new JSONArray();

		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = (Connection) DriverManager.getConnection(DATABASE_LOCATION);
			System.out.println("GET PATROL ARRAY: Creating statement...");
			stmt = c.createStatement();
			String sql;
			if (precinct == null) {
				sql = "SELECT * FROM police WHERE Datetime IN (SELECT MAX(Datetime) FROM police GROUP BY CarID) GROUP BY CarID";
				System.out.println("precinct was null");
			} else {
				sql = "SELECT * FROM police WHERE PRECINCT = '"
						+ precinct
						+ "' AND Datetime IN (SELECT MAX(Datetime) FROM police GROUP BY CarID) GROUP BY CarID";
			}

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				JSONObject patrol = new JSONObject();
				patrol.put("ID", rs.getString("CarID"));
				patrol.put("Location", "Blair Ave.");
				patrol.put("GPS lat", rs.getDouble("Lat"));
				patrol.put("GPS long", rs.getDouble("Long"));
				patrol.put("Precinct", rs.getString("PRECINCT"));
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

	private JSONArray getOnCallCrimeArray(String precinct) throws SQLException,
			JSONException, IOException {

		JSONArray ret = new JSONArray();

		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = (Connection) DriverManager.getConnection(DATABASE_LOCATION);
			System.out.println("GET ONCALLCRIME ARRAY: Creating statement...");
			stmt = c.createStatement();
			String currentTime = "201608311000";
			String sql;

			if (precinct == null) {
				sql = "SELECT * FROM crime WHERE datetime >= " + currentTime
						+ " AND oncall = 1";
				System.out.println("precinct was null");
			} else {
				sql = "SELECT * FROM crime WHERE datetime >= " + currentTime
						+ " AND oncall = 1" + " AND PRECINCT = '" + precinct
						+ "'";
			}

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				JSONObject oncall = new JSONObject();
				oncall.put("datetime", rs.getString("Datetime"));
				oncall.put("Location", rs.getString("location"));
				oncall.put("Description", rs.getString("description"));
				oncall.put("GPS lat", rs.getDouble("lat"));
				oncall.put("GPS long", rs.getDouble("long"));
				oncall.put("Precinct", rs.getString("precinct"));
				ret.put(oncall);
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

	// Custom class to store crime data temporarily: avoid type-casting
	// continuously

	private JSONArray getHistoricCrimeArray(String precinct)
			throws SQLException, JSONException, IOException {

		JSONArray ret = new JSONArray();

		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = (Connection) DriverManager.getConnection(DATABASE_LOCATION);
			System.out.println("GET HISTORICCRIME ARRAY: Creating statement...");
			stmt = c.createStatement();
			String currentTime = "201608311000";
			String sql;

			if (precinct == null) {
				sql = "SELECT * FROM crime WHERE datetime >= " + currentTime
						+ " AND oncall = 0";
				System.out.println("precinct was null");
			} else {
				sql = "SELECT * FROM crime WHERE datetime >= " + currentTime
						+ " AND oncall = 0" + " AND PRECINCT = '" + precinct
						+ "'";
			}
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				JSONObject historic = new JSONObject();
				historic.put("datetime", rs.getString("Datetime"));
				historic.put("Location", rs.getString("location"));
				historic.put("Description", rs.getString("description"));
				historic.put("GPS lat", rs.getDouble("lat"));
				historic.put("GPS long", rs.getDouble("long"));
				historic.put("Precinct", rs.getString("precinct"));
				ret.put(historic);
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

	protected void insertJSONData(HttpServletRequest request) throws Exception {
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
				// edit
				String date = obj.getString("Datetime");
				String id = obj.getString("ID");
				double lat = obj.getDouble("latitude");
				double lng = obj.getDouble("longitude");
				String precinct = obj.getString("precinct");
				sql = "INSERT INTO `police`(Datetime,CarID,Lat,Long, PRECINCT) VALUES ('"
						+ date
						+ "','"
						+ id
						+ "','"
						+ lat
						+ "','"
						+ lng
						+ "','"
						+ precinct + "')";
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

	public String getPrecinct(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String paramName = "precinct";
		String paramValue = request.getParameter(paramName);

		if (paramValue == null) {
			System.out.println("No precinct value found");
		} else {
			System.out.println("Precinct is: " + paramValue);
		}
		return paramValue;
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		try {
			String precinct = getPrecinct(request, response);
			sendJSON(response, precinct);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// doPost(request, response);
		try {
			insertJSONData(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void createDB() {
		Connection c = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = (Connection) DriverManager.getConnection(DATABASE_LOCATION);
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
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
			ResultSet res = md.getTables(null, null, "police", null);
			if (res.next()) {
				System.out.println("CHECK TABLE EXISTS: Table already exists");
				System.out.println("   " + res.getString("TABLE_CAT") + ", "
						+ res.getString("TABLE_SCHEM") + ", "
						+ res.getString("TABLE_NAME") + ", "
						+ res.getString("TABLE_TYPE") + ", "
						+ res.getString("REMARKS"));
				c.close();
				return true;
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("CHECK TABLE EXISTS: Table does not exist");
		return false;
	}

	public static void createTable() {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = (Connection) DriverManager.getConnection(DATABASE_LOCATION);
			System.out.println("CREATE TABLE: Opened database successfully");
			String sql = "CREATE TABLE police " + "(Datetime TEXT NOT NULL, "
					+ " CarID TEXT NOT NULL, " + " Lat REAL, " + " Long REAL, "
					+ " PRECINCT TEXT NOT NULL)";

			stmt = (Statement) c.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("CREATE TABLE: Table created successfully");
	}

	public class CrimeDataHolder {
		double datetime;
		String location;
		String description;
		double lat_;
		double long_;
		String precinct;
	}

	public class PoliceDataHolder {
		double datetime;
		String carID;
		double lat_;
		double long_;
	}

	// Method to get current crimes on call
	protected ArrayList<CrimeDataHolder> getCurrentOnCallCrimes()
			throws SQLException {
		// create data holder
		ArrayList<CrimeDataHolder> crimesOnCall = new ArrayList<CrimeDataHolder>();
		Connection c = null;
		Statement stmt = null;
		try {
			// fetch data from db
			Class.forName("org.sqlite.JDBC");
			c = (Connection) DriverManager.getConnection(DATABASE_LOCATION);
			System.out.println("GET PATROL ARRAY: Creating statement...");
			stmt = c.createStatement();
			String sql = "SELECT * FROM crime WHERE oncall = 1";
			ResultSet rs = stmt.executeQuery(sql);

			// iterate through results set and store in holder
			while (rs.next()) {
				CrimeDataHolder crime = new CrimeDataHolder();
				crime.datetime = rs.getDouble("datetime");
				crime.location = rs.getString("location");
				crime.description = rs.getString("description");
				crime.lat_ = rs.getDouble("lat");
				crime.long_ = rs.getDouble("long");
				crime.precinct = rs.getString("precinct");

				crimesOnCall.add(crime);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return crimesOnCall;
	}

	// Method to get current police placements
	protected ArrayList<PoliceDataHolder> getCurrentPolice()
			throws SQLException {
		// create data holder
		ArrayList<PoliceDataHolder> policeCurr = new ArrayList<PoliceDataHolder>();
		Connection c = null;
		Statement stmt = null;
		try {
			// fetch data from db
			Class.forName("org.sqlite.JDBC");
			c = (Connection) DriverManager.getConnection(DATABASE_LOCATION);
			System.out.println("GET PATROL ARRAY: Creating statement...");
			stmt = c.createStatement();
			String sql = "SELECT * FROM AVLData WHERE Datetime IN (SELECT MAX(Datetime) FROM AVLData GROUP BY CarID)";
			ResultSet rs = stmt.executeQuery(sql);

			// iterate through results set and store in holder
			while (rs.next()) {
				PoliceDataHolder policeTemp = new PoliceDataHolder();
				policeTemp.datetime = rs.getDouble("datetime");
				policeTemp.carID = rs.getString("carID");
				policeTemp.lat_ = rs.getDouble("lat");
				policeTemp.long_ = rs.getDouble("long");

				policeCurr.add(policeTemp);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return policeCurr;
	}

	/*
	 * CODE FROM
	 * http://stackoverflow.com/questions/3694380/calculating-distance-
	 * between-two-points-using-latitude-longitude-what-am-i-doi
	 * 
	 * Calculate distance between two points in latitude and longitude taking
	 * into account height difference. If you are not interested in height
	 * difference pass 0.0. Uses Haversine method as its base.
	 * 
	 * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
	 * el2 End altitude in meters
	 * 
	 * @returns Distance in Meters
	 */
	public static double distance(double lat1, double lat2, double lon1,
			double lon2, double el1, double el2) {

		final int R = 6371; // Radius of the earth

		Double latDistance = Math.toRadians(lat2 - lat1);
		Double lonDistance = Math.toRadians(lon2 - lon1);
		Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2)
				* Math.sin(lonDistance / 2);
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c * 1000; // convert to meters

		double height = el1 - el2;

		distance = Math.pow(distance, 2) + Math.pow(height, 2);

		return Math.sqrt(distance);
	}

	public double[][] getDistanceMatrix(ArrayList<PoliceDataHolder> policeCurr,
			ArrayList<CrimeDataHolder> crimesCurr) {
		int numPolice = policeCurr.size();
		int numCrimes = crimesCurr.size();
		double[][] distanceMatrix = new double[numPolice][numCrimes];
		double distTemp;
		int counterPolice = 0;
		int counterCrime = 0;
		for (PoliceDataHolder policeTemp : policeCurr) {
			for (CrimeDataHolder crimeTemp : crimesCurr) {
				distTemp = distance(policeTemp.lat_, crimeTemp.lat_,
						policeTemp.long_, crimeTemp.long_, 0, 0);
				distanceMatrix[counterPolice][counterCrime] = distTemp;
				counterCrime++;
			}
			counterPolice++;
		}

		return distanceMatrix;

	}

	protected int[] heuristicSolveTransport(double[][] dist) {
		double min;
		int minDistPolice = 0;
		int minDistCrime = 0;
		// for each police, mark the index of the target that it should go to
		int targets[] = new int[dist[0].length];
		// once we find a police for a crime, take that crime out of the rest of
		// the problem
		Boolean crimeLeft[] = new Boolean[dist[0].length];
		Boolean policeLeft[] = new Boolean[dist[0].length];

		min = dist[0][0];
		// till crimes are left
		while (Arrays.asList(crimeLeft).contains(true)) {
			// iterate through rows: police
			for (int i = 0; i < dist.length; i++) {
				// if the police is still available
				if (policeLeft[i] == true) {
					// iterate through columns: crimes
					for (int j = 0; j < dist[i].length; j++) {
						// if this crime still needs to be served
						if (crimeLeft[j] == true) {
							if (dist[i][j] < min) {
								// in case the total distance needs to be
								// calculated
								min = dist[i][j];
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

	// method to solve Transportation Problem
	// get current Crimes on call, current police placements and return optimal
	// police placements
	protected HashMap<String,double[]> getPoliceAssignments() {
		int[] targets = new int[0];
		HashMap<String,double[]> assignments = new HashMap<String,double[]>();
		try {
			// fetch current police positions
			ArrayList<PoliceDataHolder> policeCurr = getCurrentPolice();
			// fetch crimes that need to be responded to
			ArrayList<CrimeDataHolder> crimesCurr = getCurrentOnCallCrimes();
			// calculate distances between the police points and crime points
			// we always treat that we have sufficient police to respond to
			// crimes
			double[][] distMatrix = getDistanceMatrix(policeCurr, crimesCurr);
			// get assignments for each crime and police
			targets = heuristicSolveTransport(distMatrix);		

			//define new hash map: key(carID),value(lat long)
			
			double[] tempLatLong;
			for (int counter = 0;counter<policeCurr.size();counter++)
			{
				tempLatLong = new double[] {crimesCurr.get(targets[counter]).lat_,crimesCurr.get(targets[counter]).long_};
				assignments.put(policeCurr.get(counter).carID, tempLatLong);
				
			}
			
			
		}
		 catch (SQLException e) {
				e.printStackTrace();
			}
		return assignments;
	}

}
