/* *****************************************************************************
 *  Name: Jessie Peng
 *  Date: 3/28/2020
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

    private static final double CON = 1.96;
    private final int trialNum;
    private double[] fractions;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException();
        }

        trialNum = trials;
        fractions = new double[trials];
        Percolation pr;

        for (int t = 0; t < trials; t++) {
            pr = new Percolation(n);

            while (!pr.percolates()) {
                int row = StdRandom.uniform(1, n + 1);
                int col = StdRandom.uniform(1, n + 1);
                pr.open(row, col);
            }
            fractions[t] = pr.numberOfOpenSites() / ((double) (n * n));
        }

    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(fractions);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(fractions);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return StdStats.mean(fractions) - ((CON * StdStats.stddev(fractions)) / Math
                .sqrt(trialNum));
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return StdStats.mean(fractions) + ((CON * StdStats.stddev(fractions)) / Math
                .sqrt(trialNum));
    }

    // test client (see below)
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int t = Integer.parseInt(args[1]);

        PercolationStats ps = new PercolationStats(n, t);
        System.out.println("mean                    = " + ps.mean());
        System.out.println("stddev                  = " + ps.stddev());
        System.out.println(
                "95% confidence interval = [" + ps.confidenceLo() + ", " + ps.confidenceHi() + "]");

    }
}
