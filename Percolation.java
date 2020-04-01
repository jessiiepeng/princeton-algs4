/* *****************************************************************************
 *  Name: Jessie Peng
 *  Date: 3/28/2020
 *  Description: program to imitate Monte Carlo to check if percolation is
 *               true
 **************************************************************************** */

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private final int len;
    private final WeightedQuickUnionUF uf;
    private boolean[][] grid;
    private final int top = 0, bottom;
    private int counter = 0;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) throw new IllegalArgumentException();

        len = n;
        grid = new boolean[len + 1][len + 1];

        for (int i = 1; i <= len; i++) {
            for (int j = 1; j <= len; j++) {
                grid[i][j] = false;
            }
        }

        uf = new WeightedQuickUnionUF(len * len + 2);
        bottom = len * len + 1;

    }

    private boolean checkBounds(int row, int col) {
        if (row < 1 || row > len || col < 1 || col > len) {
            return false;
        }
        return true;
    }

    private int getIndex(int row, int col) {
        return (row - 1) * len + col;
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (!checkBounds(row, col)) {
            throw new IllegalArgumentException();
        }

        if (!isOpen(row, col)) {
            grid[row][col] = true;
            counter++;

            int num = getIndex(row, col);

            if (row == 1) { // union top
                uf.union(num, top);
            }
            if (row == len) { // union bottom
                uf.union(num, bottom);
            }

            // union possible neighbours
            if (checkBounds(row - 1, col) && isOpen(row - 1, col)) {
                uf.union(num, getIndex(row - 1, col)); // up
            }
            if (checkBounds(row + 1, col) && isOpen(row + 1, col)) {
                uf.union(num, getIndex(row + 1, col)); // down
            }
            if (checkBounds(row, col - 1) && isOpen(row, col - 1)) {
                uf.union(num, getIndex(row, col - 1)); // left
            }
            if (checkBounds(row, col + 1) && isOpen(row, col + 1)) {
                uf.union(num, getIndex(row, col + 1)); // right
            }
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        if (!checkBounds(row, col)) {
            throw new IllegalArgumentException();
        }
        return grid[row][col];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if (!checkBounds(row, col)) {
            throw new IllegalArgumentException();
        }
        return uf.connected(top, getIndex(row, col));
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return counter;
    }

    // does the system percolate?
    public boolean percolates() {
        if (uf.connected(top, bottom)) {
            return true;
        }
        return false;
    }

    // test client (optional)
    public static void main(String[] args) {
        // not needed
    }

}
