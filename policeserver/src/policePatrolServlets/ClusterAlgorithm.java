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
	public Grid[] grids;
	public int NUM_GRIDS;
	
	public ClusterAlgorithm() {
		clusters = new ArrayList<Cluster>();
		iterLikelihood = new ArrayList<Double>();
		grids = new Grid[900];
		
	}
	public void readData() {
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
        	
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] crimeDataPoint = line.split(cvsSplitBy);
                // get the grid number
                
                for(int i = 0; i < crimeDataPoint.length; i++) {
        			System.out.print(crimeDataPoint[i] + " ");
        		}
                System.out.println();
                
                int gridNumber = Integer.parseInt(crimeDataPoint[0]);
                // if null, instantiate a new grid.
                if(grids[gridNumber] == null) grids[gridNumber] = new Grid(gridNumber);
                // put data in grid.
                grids[gridNumber].addData(crimeDataPoint);
            }
            
            NUM_GRIDS = 0;
            
            for(int i = 0; i < grids.length; i++) {
            	if(grids[i] != null) {
            		NUM_GRIDS++;
            		
            		Cluster c = new Cluster();
                	c.put(grids[i]);
                	// c.calculate() alpha vector
        			c.calculateAlpha();
        			// c.calculate likelihood.
        			c.calculatelogL(); 
                	
            	}
            }
            System.out.println("number of distinct grids is: " + NUM_GRIDS);
            
        }catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public static void main(String[] args) {
		ClusterAlgorithm ca = new ClusterAlgorithm();
		
		ca.readData();
		
		/*

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