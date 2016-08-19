package policePatrolServlets;

import java.util.ArrayList;

public class Grid {
	public int gridNumber;
	public ArrayList<double[]> trainingSet;
	public ArrayList<double[]> testSet;
	public double[] staticFeatures;
	public static final int STATIC_FEATURES_LENGTH = 8; // what is the length of this?
	
	public Grid(int num) {
		gridNumber = num;
		trainingSet = new ArrayList<double[]>();
		testSet = new ArrayList<double[]>();
	}
	public void addData(String[] crimeDataPoint) {
		
		// get static features
		if(staticFeatures == null) {
			staticFeatures = new double[STATIC_FEATURES_LENGTH];
			
			int[] staticColumns = {3, 4, 12, 24, 25, 26, 27, 28};
			
			for(int i = 0; i < staticColumns.length; i++) {
				int col = staticColumns[i];
				String val = crimeDataPoint[col];
				staticFeatures[i] = Double.parseDouble(val);
			}
		}
		
		// read in all the features, for now into training set. 
		int[] features = {1, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,
				26, 27, 28};
		double[] featureVector = new double[features.length];
		
		for(int i = 0; i < featureVector.length; i++) {
			int col = features[i];
			String val = crimeDataPoint[col];
			featureVector[i] = Double.parseDouble(val);
		}
		
		trainingSet.add(featureVector);
	}
	
	public int getTrainingSetExamples() {
		return trainingSet.size();
	}
}