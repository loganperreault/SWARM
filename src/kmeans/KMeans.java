package kmeans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import Distance.Distance;
import Distance.Euclidean;
import driver.DataPoint;

public class KMeans {
	
	// the number of clusters, or means
	private int k = 0;
	private int numFeatures = 0;
	// the amount of change allowable during an update of the algorithm for completion
	private double threshold = 0.01;
	// set the min and max values for the random center initialization
	private double initCenterMin = -0.3;
	private double initCenterMax =  0.3;
	// the actual centers found by the algorithm
	private List<List<Double>> centers;
	// the map that keeps track of which center datapoints are assigned to
	private Map<DataPoint, Integer> clusters = new HashMap<DataPoint, Integer>();
	
	Random random = new Random(11235);
	Distance dist = new Euclidean();
	
	public KMeans() {
		
	}
	
	/**
	 * @param data	The training data that will be used to identify clusters
	 */
	public void train(List<DataPoint> data) {
		// set k to the number of potential classes
		k = data.get(0).getOutputs().size();
		// get the size of the input space
		numFeatures = data.get(0).getFeatures().size();
		
		// set up the new list of centers
		initializeCenters();
		
		double change;
		do {
			change = trainIteration(data);
		} while (change > threshold);
	}
	
	public int test(DataPoint datapoint) {
		double minDistance = Double.MAX_VALUE;
		int closestCenter = 0;
		for (int center = 0; center < k; center++) {
			double distance = dist.distance(datapoint.getFeatures(), centers.get(center));
			if (distance < minDistance) {
				minDistance = distance;
				closestCenter = center;
			}
		}
		return closestCenter;
	}
	
	public double trainIteration(List<DataPoint> data) {
		double change = 0.0;
		for (DataPoint datapoint : data) {
			int closestCenter = test(datapoint);
			clusters.put(datapoint, closestCenter);
		}
		change = calculateCenters(data);
		return change;
	}
	
	private double calculateCenters(List<DataPoint> data) {
		double change = 0.0;
		List<List<List<Double>>> points = new ArrayList<>(k);
		for (int cluster = 0; cluster < k; cluster++) {
			List<List<Double>> clust = new ArrayList<>();
			points.add(clust);
		}
		for (DataPoint datapoint : data) {
			int cluster = clusters.get(datapoint);
	    	points.get(cluster).add(datapoint.getFeatures());
	    }
		
	    // update centers
	    //System.out.println(points.get(0).size() + " vs "+points.get(1).size());
	    for (int center = 0; center < k; center++) {
			for (int feature = 0; feature < numFeatures; feature++) {
				double average = 0.0;
				for (List<Double> point : points.get(center)) {
					average += point.get(feature);
				}
				if (points.get(center).size() != 0)	
					average /= points.get(center).size();
				//System.out.println(Math.abs(average - centers.get(center).get(feature)));
				change = Math.max(change, Math.abs(average - centers.get(center).get(feature)));
				centers.get(center).set(feature, average);
			}
		}
	    //System.out.println("CHANGE: "+change+"\n");
	    return change;
	}
	
	/**
	 * Initializes the centers to random variables within a specified range.
	 */
	private void initializeCenters() {
		centers = new ArrayList<>(k);
		for (int center = 0; center < k; center++) {
			ArrayList<Double> newCenter = new ArrayList<Double>(numFeatures);
			for (int feature = 0; feature < numFeatures; feature++) {
				newCenter.add(initCenterMin + (initCenterMax - initCenterMin) * random.nextDouble());
			}
			centers.add(newCenter);
		}
	}
	
	/**
	 * Retrieves the centers that were calculated by the algorithm.
	 * 
	 * @return	The centers as calculated by the algorithm.
	 */
	public List<List<Double>> getCenters() {
		return centers;
	}
	
	/**
	 * Describes the algorithm in the form "K-Means Clustering",
	 * where "K" is the number of clusters, assuming the algorithm
	 * has been trained.
	 */
	public String toString() {
		String clusters = "K";
		if (k > 0)
			clusters = Integer.toString(k);
		return clusters+"-Means Clustering";
	}

}
