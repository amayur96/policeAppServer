package policePatrolServlets;

// TODO: add conditions for police coefficient to be positive.
// TODO: figure out how to read data into the data structures
// TODO: write calculate alpha and calculate log L methods.

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ClusterAlgorithm {
	public ArrayList<Cluster> clusters;
	public ArrayList<Double> iterLikelihood;

	public static void main(String[] args) {
		
		String csvFile = "/Users/genexli/Documents/CrimeDataAnalysis/survAnalysisRows1mileNoCensoringHierarchical_robbery_1_2_4_1.csv";
        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
        	
        	String[] header;
        	
        	if((line = br.readLine()) != null) {
        		header = line.split(cvsSplitBy);
        		for(int i = 0; i < header.length; i++) {
            		System.out.println(header[i] + " at position " + i);
            	}
        	}
        	
        	Grid[] grids = new Grid[900];
        	
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] crimeDataPoint = line.split(cvsSplitBy);
                // get the grid number                
                String gridNumberString = crimeDataPoint[0].substring(1, crimeDataPoint[0].length()-1);
                int gridNumber = (int) Double.parseDouble(gridNumberString);
                
                // if null, instantiate a new grid.
                if(grids[gridNumber] == null) grids[gridNumber] = new Grid();
                // put data in grid.
                grids[gridNumber].addData(crimeDataPoint);
            }
            
            int NUM_GRIDS = 0;
            
            for(int i = 0; i < grids.length; i++) {
            	if(grids[i] != null) NUM_GRIDS++;
            }
            System.out.println("number of distinct grids is: " + NUM_GRIDS);
            
        }catch (IOException e) {
            e.printStackTrace();
        }
        
        /*

		// initalize the cluster list.
		clusters = new ArrayList<Cluster>();
		// initialize each grid with grid number, training set matrix, test set matrix.
		for(int i = 0; i < NUM_GRIDS; i++) {
			// read in information from CSV file. 

			Grid g = new Grid();
			// put in grid number
			g.gridNumber = ;
			// put in training set matrix
			double[][] training = new double[][];
			g.trainingSet = training;
			// put in test set matrix.
			double[][] test = new double[][];
			g.testSet =  test;
			// put in static feature set
			double[] staticF = new double[];
			g.staticFeatures = staticF;

			Cluster c = new Cluster();
			// put g into c.
			c.put(g);
			// c.calculate() alpha vector
			c.calculateAlpha();
			// c.calculate likelihood.
			c.calculatelogL(); 
			// put c into arraylist of clusters.
			clusters.add(c);
		}

		// initialize the likelihood list.
		iterLikelihood = new ArrayList<Double>();

		double logL = calculateLikelihood();
		int lnew = logL;
		int lold = Double.NEGATIVE_INFINITY;
		int iter = 1;

		while (lnew - lold >= EPSILON/iter) {

			// to cluster:
			// for each cluster we calculate the distance from all other clusters. 
			// find the min distance cluster. using the mean of the static features. 
			int minDistance = Integer.MAX;
			int[] pair = new int[2]; 
			for (i = 0; i < clusters.length; i++) {
				for(j = i+1; j < clusters.length; j++) {
					Cluster a = clusters.get(i);
					Cluster b = clusters.get(j);
					double[] aFeatures = a.getAverageStaticX();
					double[] bFeatures = b.getAverageStaticX();

					// calculate RMS distance.
					int dist = ; //
					if(dist < minDistance) {
						minDistance = dist;
						pair[0] = i;
						pair[1] = j;
					}
				}
			}
			// now that the two clusters with the smallest distance have been found, we merge them. 
			// Merge these two clusters:
			// delete them from arraylist and put in a new cluster with both grids.
			Cluster merged = merge(clusters.get(pair[0]), clusters.get(pair[1]));
			clusters.remove(pair[0]);
			clusters.remove(pair[1]);
			clusters.add(merged);

			logL = calculateLikelihood();

			lold = lnew;
			lnew = logL // new calculated likelihood of the present iteration.
			iter++;

			// TODO: add conditions of making sure that the police coefficient is positive.
			// calculate boolean of coefficient: true if correct, false if wrong.
			boolean valid = true;
			for(Cluster cluster : clusters) {
				if(!cluster.isAlphavalid()) valid = false;
			}

			if(clusters.size() <= 10 && !valid)
				break;
		}
		*/
	}

	public double calculateLikelihood() {
		double sum = 0;
		for (Cluster cluster : clusters) {
				// sum up log likelihoods,
				sum += cluster.getlogL();
		}

		// adds the likelihood to the arraylist.
		iterLikelihood.add(sum);
		return sum;
	}


	public Cluster merge(Cluster a, Cluster b) {
		Cluster ret = new Cluster();

		for (Grid g : a.grids) {
			// add them to the merged cluster's grids.
			ret.put(g);
		}

		for (Grid g : b.grids) {
			// add them to the merged cluster's grids.
			ret.put(g);
		}
		ret.calculateAlpha();
		ret.calculatelogL();
		return ret;
	}
}