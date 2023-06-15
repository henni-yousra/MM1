package mm1withMeanBatchMethod;

public class StatCalculations {
	static int number_of_batches = 1000;
	
    
    public static double calculateConfidenceInterval(double meanOfMeans,double variance) {

        double confidenceLevel = 0.95; // Desired confidence level

        double standardError = Math.sqrt(variance / number_of_batches);
        double criticalValue = getCriticalValue(confidenceLevel, number_of_batches - 1);

        double marginOfError = criticalValue * standardError;
        double lowerBound = meanOfMeans- marginOfError;
        double upperBound = meanOfMeans + marginOfError;

        return marginOfError;
    	
    }
    
    public static double getCriticalValue(double confidenceLevel, int degreesOfFreedom) {
        // Using the t-distribution for the critical value
        // You can use a lookup table or a statistical library for more precise values
        // Here, we use a simplified formula for illustration purposes
        double z = 1.96; // For a 95% confidence level
        if (confidenceLevel > 0.95) {
            z = 2.576; // For a 99% confidence level
        }
        return z;
    }

    public static double calculateMean(double[] data) {
        double sum = 0;
        for (int j= 1; j< (number_of_batches-1) ;j++) {
            sum += data[j];
        }
        return sum / (number_of_batches-1);
    }

    public static double calculateVariance(double[] data, double mean) {
        double sumSquaredDiff = 0;
        for (int z= 1; z< (number_of_batches-1) ;z++) {
        	
            double diff = data[z] - mean;
            
            sumSquaredDiff += diff * diff;
        }
        return sumSquaredDiff / (number_of_batches-2);
    }
    
    public static void display_results(double[] batchMeans) {
        double meanOfMeans = calculateMean(batchMeans);
        double variance = calculateVariance(batchMeans, meanOfMeans);
        double marginOfError = calculateConfidenceInterval(meanOfMeans,variance);
        System.out.printf("%f\n", meanOfMeans);

        //System.out.println(marginOfError);
    
    }

}
