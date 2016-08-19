package policePatrolServlets;

import java.util.ArrayList;

public class Grid {
	public int gridNumber;
	public ArrayList<Double[]> trainingSet;
	public ArrayList<Double[]> testSet;
	public double[] staticFeatures;
	public static final int STATIC_FEATURES_LENGTH = 8; // what is the length of this?
	
	public void addData(String[] crimeDataPoint) {
		
		String gridNumberString = crimeDataPoint[0].substring(1, crimeDataPoint[0].length()-1);
		this.gridNumber = (int) Double.parseDouble(gridNumberString);
		
		// get static features
		if(staticFeatures == null) {
			staticFeatures = new double[STATIC_FEATURES_LENGTH];
			String liquorCounter = crimeDataPoint[3].substring(1, crimeDataPoint[3].length()-1);
			String liquorCounterRetail = crimeDataPoint[4].substring(1, crimeDataPoint[4].length()-1);
			String pastCrimeGrid30 = crimeDataPoint[12].substring(1, crimeDataPoint[12].length()-1);
			String pawnCounter = crimeDataPoint[24].substring(1, crimeDataPoint[24].length()-1);
			String homelessCounter = crimeDataPoint[25].substring(1, crimeDataPoint[25].length()-1);
			String popDens = crimeDataPoint[26].substring(1, crimeDataPoint[26].length()-1);
			String houseDens = crimeDataPoint[27].substring(1, crimeDataPoint[27].length()-1);
			String meanIncomeScaled = crimeDataPoint[28].substring(1, crimeDataPoint[28].length()-1);
			
			staticFeatures[0] = Double.parseDouble(liquorCounter);
			staticFeatures[1] = Double.parseDouble(liquorCounterRetail);
			staticFeatures[2] = Double.parseDouble(pastCrimeGrid30);
			staticFeatures[3] = Double.parseDouble(pawnCounter);
			staticFeatures[4] = Double.parseDouble(homelessCounter);
			staticFeatures[5] = Double.parseDouble(popDens);
			staticFeatures[6] = Double.parseDouble(houseDens);
			staticFeatures[7] = Double.parseDouble(meanIncomeScaled);
			
		}
		
		// read in all the features, for now into training set. 
		
		
		
	}
}