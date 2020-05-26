import edu.princeton.cs.algs4.Picture;

import java.util.ArrayList;

public class SeamCarver {
    private int width, height, tWidth, tHeight;
    private Picture copyPicture;
    private int[][] colors;
    private boolean calledFromHorizontal;


    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException();
        copyPicture = new Picture(picture);
        height = copyPicture.height();
        width = copyPicture.width();
        tHeight = width;
        tWidth = height;

        calledFromHorizontal = false;
        colors = new int[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                colors[row][col] = copyPicture.getRGB(col, row);
            }
        }
    }


    // current picture
    public Picture picture() {
        return new Picture(copyPicture);
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    private double xEnergy(int col, int row) {
        int rgb1 = colors[row][col - 1];
        int r1 = (rgb1 >> 16) & 0xFF;
        int g1 = (rgb1 >> 8) & 0xFF;
        int b1 = (rgb1) & 0xFF;

        int rgb2 = colors[row][col + 1];
        int r2 = (rgb2 >> 16) & 0xFF;
        int g2 = (rgb2 >> 8) & 0xFF;
        int b2 = (rgb2) & 0xFF;

        return Math.pow(r2 - r1, 2) + Math.pow(g2 - g1, 2) + Math.pow(b2 - b1, 2);
    }

    private double yEnergy(int col, int row) {

        int rgb1 = colors[row - 1][col];
        int r1 = (rgb1 >> 16) & 0xFF;
        int g1 = (rgb1 >> 8) & 0xFF;
        int b1 = (rgb1) & 0xFF;

        int rgb2 = colors[row + 1][col];
        int r2 = (rgb2 >> 16) & 0xFF;
        int g2 = (rgb2 >> 8) & 0xFF;
        int b2 = (rgb2) & 0xFF;

        return Math.pow(r2 - r1, 2) + Math.pow(g2 - g1, 2) + Math.pow(b2 - b1, 2);
    }

    // energy of pixel at column x and row y <-- always going to check vertical
    public double energy(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) throw new IllegalArgumentException();
        if (x == 0 || x == width - 1 || y == 0 || y == height - 1) return 1000;
        return Math.sqrt(xEnergy(x, y) + yEnergy(x, y));
    }

    // transpose energy function
    private double[][] transpose(double[][] energyArray) {
        tHeight = width;
        tWidth = height;

        // always need to rotate horizontal
        // need to rotate 90 clockwise --> rotate energy array
        double[][] copy = new double[tHeight][tWidth];
        for (int row = 0; row < height; row++) {
            double[] rowTemp
                    = energyArray[row]; // access each row by row then add then to appropraite place
            int newCol = height - 1 - row;
            for (int newRow = 0; newRow < width; newRow++) {
                copy[newRow][newCol] = rowTemp[newRow];
            }
        }
        return copy;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        // first transpose then run find vertical seam
        calledFromHorizontal = true;
        int[] seam = findVerticalSeam();
        calledFromHorizontal = false;

        for (int i = 0; i < seam.length; i++) {
            seam[i] = tWidth - 1 - seam[i];
        }
        return seam;
    }

    private double[][] getEnergyArray(int height, int width) {
        double[][] energyArray = new double[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                energyArray[row][col] = energy(col, row);
            }
        }
        return energyArray;
    }


    // sequence of indices for vertical seam // -1 be left, 0 be up, 1 be right
    public int[] findVerticalSeam() {
        int curHeight, curWidth;
        double[][] energyArray = getEnergyArray(height, width);
        double[][] curEnergyArray;

        if (calledFromHorizontal) { // must be horizontal so transpose energy
            curHeight = tHeight;
            curWidth = tWidth;
            curEnergyArray = transpose(energyArray);
        }
        else { // must be vertical so keep energy the same
            curHeight = height;
            curWidth = width;
            curEnergyArray = energyArray;
        }

        double[][] distTo = new double[curHeight][curWidth];
        int[][] cameFrom = new int[curHeight][curWidth];

        // make mindistance originally big + create energyArray
        for (int row = 0; row < curHeight; row++) {
            for (int col = 0; col < curWidth; col++) {
                distTo[row][col] = Double.POSITIVE_INFINITY;
            }
        }

        // make first row come from -2 + have dist 1000 (edge)
        for (int i = 0; i < curWidth; i++) {
            cameFrom[0][i] = -2;
            distTo[0][i] = 1000;
        }

        for (int row = 0; row < curHeight - 1; row++) {
            for (int col = 0; col < curWidth; col++) {
                // check bottom 3
                if (col - 1 >= 0) {
                    if (distTo[row + 1][col - 1]
                            > curEnergyArray[row + 1][col - 1] + distTo[row][col]) {
                        distTo[row + 1][col - 1] = curEnergyArray[row + 1][col - 1]
                                + distTo[row][col];
                        cameFrom[row + 1][col - 1] = 1;
                    }
                }
                if (col + 1 < curWidth) {
                    if (distTo[row + 1][col + 1]
                            > curEnergyArray[row + 1][col + 1] + distTo[row][col]) {
                        distTo[row + 1][col + 1] = curEnergyArray[row + 1][col + 1]
                                + distTo[row][col];
                        cameFrom[row + 1][col + 1] = -1;
                    }
                }
                if (distTo[row + 1][col] > curEnergyArray[row + 1][col] + distTo[row][col]) {
                    distTo[row + 1][col] = curEnergyArray[row + 1][col] + distTo[row][col];
                    cameFrom[row + 1][col] = 0;
                }
            }
        }


        int minCol = 0;
        double minValue = Double.POSITIVE_INFINITY;
        for (int col = 0; col < curWidth; col++) {
            if (distTo[curHeight - 1][col] < minValue) {
                minCol = col;
                minValue = distTo[curHeight - 1][col];
            }
        }

        ArrayList<Integer> minPath = new ArrayList<>();

        int row = curHeight - 1, col = minCol;
        while (true) {
            minPath.add(col);
            if (minPath.size() == curHeight) break;
            if (cameFrom[row][col] == -1) col--;
            else if (cameFrom[row][col] == 1) col++;
            row--;
        }


        int[] seam = new int[curHeight];
        for (int i = 0; i < curHeight; i++) {
            seam[i] = minPath.get(curHeight - 1 - i);
        }

        return seam;
    }

    private void setNewPicture() {
        Picture newPicture = new Picture(width, height);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                newPicture.setRGB(col, row, colors[row][col]);
            }
        }
        copyPicture = newPicture;


    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException();
        if (height <= 1 || seam.length != width) throw new IllegalArgumentException();

        height--;
        tWidth--;

        int[][] copyColors = new int[height][width];


        int index = 0;
        // remove by checking each column
        for (int col = 0; col < width; col++) {
            // check validity of seam
            if (seam[col] < 0 || seam[col] >= height + 1) throw new IllegalArgumentException();
            if (col != width - 1) {
                if (Math.abs(seam[col] - seam[col + 1]) > 1)
                    throw new IllegalArgumentException();
            }
            for (int row = 0; row <= height; row++) {
                if (row != seam[col]) {
                    copyColors[index][col] = colors[row][col];
                    index++;
                }
            }
            index = 0;
        }

        colors = copyColors;
        setNewPicture();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException();
        if (width <= 1 || seam.length != height) throw new IllegalArgumentException();

        width--;
        tHeight--;

        int[][] copyColors = new int[height][width];


        // remove by checking each row
        for (int i = 0; i < seam.length; i++) {
            // check validity
            if (seam[i] < 0 || seam[i] >= width + 1) throw new IllegalArgumentException();
            if (i != seam.length - 1) {
                if (Math.abs(seam[i] - seam[i + 1]) > 1) throw new IllegalArgumentException();
            }
            // remove by using Systems array
            System.arraycopy(colors[i], 0, copyColors[i], 0, seam[i]);
            System.arraycopy(colors[i], seam[i] + 1, copyColors[i], seam[i],
                             width - seam[i]);
        }

        colors = copyColors;
        setNewPicture();
    }

    //  unit testing (optional)
    public static void main(String[] args) {
        // empty
    }

}
