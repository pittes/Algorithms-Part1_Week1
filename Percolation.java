/* *****************************************************************************
 *  Name:              Alan Turing
 *  Coursera User ID:  123456
 *  Last modified:     1/1/2019
 **************************************************************************** */

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private static final boolean CLOSED = false;
    private static final boolean OPEN = true;
    private static final int VIRTUAL_SOURCE = 0;
    private static final int NEIGHBOR_COUNT = 4;

    private final int dimension;
    private boolean[][] grid;
    private final WeightedQuickUnionUF uf;
    private int[][] openSites;
    private int openSiteCount;
    private boolean isPercolating;

    // Creates n-by-n grid, with all sites initially blocked
    public Percolation(int n)
    {
        if (n <= 0)
            throw new IllegalArgumentException("Grid dimension must be greater than zero (0).");
        dimension = n;
        int gridSize = n * n;
        uf = new WeightedQuickUnionUF(gridSize + 1); // extra 1 for virtual root site
        isPercolating = false;

        grid = new boolean[dimension][dimension];
        for (int i = 0; i < dimension; i++)
        {
            for (int j = 0; j < dimension; j++)
            {
                grid[i][j] = CLOSED; // initialize closed (false)
            }
        }
        openSites = new int[dimension][];
    }

    /**
     * Opens the site (row, col) if it is not open already
     * 1. Validate row, col values
     * 2. Mark position as open
     * 3. Perform WeightedQuickUnionUF functions to link site with open neighbors
     */
    public void open(int row, int col)
    {
        validateSite(row, col);
        int idx = xyTo1D(row, col);
        if (grid[row - 1][col - 1] == CLOSED) {
            grid[row - 1][col - 1] = OPEN;
            openSiteCount++;
            // if from first row, connect to virtual source site
            if (row == 1)
                uf.union(VIRTUAL_SOURCE, xyTo1D(row, col));
            // instead of using ArrayList, work with arrays
            int rowsOpenSiteCount = openSites[row - 1] == null ? 0 : openSites[row - 1].length;
            int[] existingOpenSitesInRow = openSites[row - 1] == null ? new int[1] : openSites[row - 1];
            int[] updatedOpenSitesInRow = new int[rowsOpenSiteCount + 1];
            System.arraycopy(existingOpenSitesInRow, 0, updatedOpenSitesInRow, 0, rowsOpenSiteCount);
            openSites[row - 1] = updatedOpenSitesInRow;
            openSites[row - 1][rowsOpenSiteCount] = xyTo1D(row, col);

            int[][] neighbors = getNeighborSites(row, col);
            for (int[] nbr : neighbors)
            {
                if (nbr != null)
                {
                    int neighborRow = nbr[0];
                    int neighborCol = nbr[1];
                    if (isOpen(neighborRow, neighborCol))
                        uf.union(idx, xyTo1D(neighborRow, neighborCol));
                }
            }
            // Once percolation begins, no further need to evaluate
            if (!isPercolating)
                checkPercolationStatus(row, col);
        }
    }

    // Is the site (row, col) open?
    public boolean isOpen(int row, int col)
    {
        validateSite(row, col);
        // grid uses 0-index row, col values
        return grid[row - 1][col - 1] == OPEN;
    }

    // Is the site (row, col) full? 'Full' means a site has open path to top row
    public boolean isFull(int row, int col)
    {
        this.validateSite(row, col);
        int ufIdx = xyTo1D(row, col);
        int rootOfCurrentSite = uf.find(ufIdx);
        int sourceRoot = uf.find(VIRTUAL_SOURCE);
        return rootOfCurrentSite == sourceRoot;
    }

    // Returns the number of open sites
    public int numberOfOpenSites()
    {
        return openSiteCount;
    }

    // Does the system percolate?
    public boolean percolates()
    {
        return isPercolating;
    }

    private void checkPercolationStatus(int row, int col)
    {
        if (openSiteCount >= dimension &&
            isFull(row, col) &&
            openSites[dimension - 1] != null)
        {
            int idxOfLastSiteOpened = xyTo1D(row, col);
            for (int siteIdx : openSites[dimension - 1])
            {
                // Checking if UF array-index [0..n^2 - 1] of bottom row open sites
                // is connected to most recently opened site
                if (uf.find(idxOfLastSiteOpened) == uf.find(siteIdx))
                    isPercolating = true;
            }
        }
    }

    /**
     * Returns index of 1-dimensional union-find array
     * @param row index using [1..n] values
     * @param col index using [1..n] values
     * @return Ex. Assume dimension, n = 5 (producing array length 25 + 1 (for virtual source @ index 0)).
     * Then, (3, 5) (i.e., row 3, col 5) => index 15
     */
    private int xyTo1D(int row, int col)
    {
        return ((row - 1) * dimension) + col;
    }

    private int[][] getNeighborSites(int row, int col)
    {
        int[][] neighbors = new int[NEIGHBOR_COUNT][]; // clockwise: above, right, below, left
        if (row > 1 && row <= dimension)
            neighbors[0] = new int[] {row - 1, col}; // ABOVE
        if (col >= 1 && col < dimension)
            neighbors[1] = new int[] {row, col + 1}; // RIGHT
        if (row >= 1 && row < dimension)
            neighbors[2] = new int[] {row + 1, col}; // BELOW
        if (col > 1 && col <= dimension)
            neighbors[3] = new int[] {row, col - 1}; // LEFT
        return neighbors;
    }

    private void validateSite(int row, int col)
    {
        if (row < 1 || row > dimension)
            throw new IllegalArgumentException(String.format("ROW index %d out of range!", row));
        if (col < 1 || col > dimension)
            throw new IllegalArgumentException(String.format("COLUMN index %d out of range!", col));
    }

    // Test client (optional)
    public static void main(String[] args) {
        Percolation perc = new Percolation(4);
        perc.open(2, 2);
        perc.open(3, 2);
        boolean hasSameParent = perc.uf.find(perc.xyTo1D(2, 2)) == perc.uf.find(perc.xyTo1D(3, 2));
        System.out.println("Has same parent? " + hasSameParent);
        // for (int[] row : perc.grid)
        // {
        //     System.out.println(Arrays.toString(row));
        // }
    }
}
