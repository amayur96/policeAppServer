package policePatrolServlets;

import java.util.ArrayList;


public class Cluster {
	public ArrayList<Grid> grids;
	public double[] alpha;
	public double logL;

	public void put(Grid g) {
		grids.add(g);
	}

	public void calculateAlpha() {
		// TODO: calculate alpha
		// call an r script with the data.
	}

	public void calculatelogL() {
		// TODO: calculate logL;
	}

	public boolean isAlphaValid() {
		return true;
	}

	public double getlogL() {
		return logL;
	}

	public double[] getAverageStaticX() {
		double[] averageX = new double[Grid.STATIC_FEATURES_LENGTH];
		int numGrids = grids.size();

		for(int i = 0; i < averageX.length; i++) {
			int sumi = 0;
			for(Grid g : grids) {
				sumi += g.staticFeatures[i];
			}

			averageX[i] = sumi/numGrids;
		}
		
		return averageX;
	}
}