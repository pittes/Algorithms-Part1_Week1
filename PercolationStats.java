/* *****************************************************************************
 *  Name:              Alan Turing
 *  Coursera User ID:  123456
 *  Last modified:     1/1/2019
 **************************************************************************** */

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

    private static final double CONFIDENCE_95 = 1.96;

    private final int gridDimension;
    private final int testCount;
    private double[] threshold;

    // Perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials)
    {
        if (n <= 0 || trials <= 0)
            throw new IllegalArgumentException("Grid dimension and trials both must be greater than zero (0).");
        gridDimension = n;
        testCount = trials;
        threshold = new double[trials];
    }

    // Sample mean of percolation threshold
    public double mean()
    {
        return StdStats.mean(threshold);
    }

    // Sample standard deviation of percolation threshold
    public double stddev()
    {
        return StdStats.stddev(threshold);
    }

    // Low endpoint of 95% confidence interval
    public double confidenceLo()
    {
        return mean() - (CONFIDENCE_95 * stddev()) / Math.sqrt(testCount);
    }

    // High endpoint of 95% confidence interval
    public double confidenceHi()
    {
        return mean() + (CONFIDENCE_95 * stddev()) / Math.sqrt(testCount);
    }

    private int getRandomRow()
    {
        return StdRandom.uniform(gridDimension) + 1;
    }

    private int getRandomCol()
    {
        return StdRandom.uniform(gridDimension) + 1;
    }

    private double runTest(int n)
    {
        Percolation perc = new Percolation(n);
        while (!perc.percolates()) {
            perc.open(getRandomRow(), getRandomCol());
        }
        double percolationThreshold = perc.numberOfOpenSites() / (double) (gridDimension * gridDimension);
        return percolationThreshold;
    }

    // Test client
    public static void main(String[] args) {
        PercolationStats stats = new PercolationStats(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        // Stopwatch timer = new Stopwatch();
        for (int trial = 0; trial < stats.testCount; trial++)
        {
            stats.threshold[trial] = stats.runTest(stats.gridDimension);
        }
        // System.out.println("Stopwatch: " + timer.elapsedTime());
        System.out.println("mean\t\t\t\t\t= " + stats.mean());
        System.out.println("stddev\t\t\t\t\t= " + stats.stddev());
        double[] confidence = { stats.confidenceLo(), stats.confidenceHi() };
        System.out.println("95% confidence interval\t= [" + confidence[0] + ", " + confidence[1] + "]");
    }
}
