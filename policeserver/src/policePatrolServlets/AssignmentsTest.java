package policePatrolServlets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import policePatrolServlets.updateJSON.CrimeDataHolder;
import policePatrolServlets.updateJSON.PoliceDataHolder;

public class AssignmentsTest {
	
	private static final String DATABASE_LOCATION = "jdbc:sqlite:/Users/genexli/Documents/policeAppServer/Databases/test.db";
	
	
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
			String sql = "SELECT * FROM crimeAssignmentTest WHERE oncall = 1";
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
			String sql = "SELECT * FROM police WHERE Datetime IN (SELECT MAX(Datetime) FROM police GROUP BY CarID) GROUP BY CarID";
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
		System.out.println("number of police " + numPolice);
		System.out.println("number of crimes " + numCrimes);
		double[][] distanceMatrix = new double[numPolice][numCrimes];
		double distTemp;
		int counterPolice = 0;
		int counterCrime = 0;
		for (PoliceDataHolder policeTemp : policeCurr) {
			counterCrime = 0;
			for (CrimeDataHolder crimeTemp : crimesCurr) {
				System.out.println("added distance to array index " + counterPolice + " " + counterCrime);
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
		int targets[] = new int[dist.length];
		// once we find a police for a crime, take that crime out of the rest of
		// the problem
		Boolean crimeLeft[] = new Boolean[dist[0].length];
		Boolean policeLeft[] = new Boolean[dist.length];

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
	
	public static void main(String[] args) {
		AssignmentsTest test = new AssignmentsTest();
		
		HashMap<String, double[]> assignments = test.getPoliceAssignments();
		for(Map.Entry<String, double[]> entry : assignments.entrySet()) {
			String key = entry.getKey();
			double lat = entry.getValue()[0];
			double longg = entry.getValue()[1];
			
			System.out.println("Police car ID: " + key + " goes to lat " + lat + " long " + longg);
		}
	}

}
