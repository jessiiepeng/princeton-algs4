/* *****************************************************************************
 *  Name: Jessie Peng
 *  Date: 4/19/2020
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;

public class FastCollinearPoints {
    private final ArrayList<LineSegment> linesFound;

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        if (points == null) throw new IllegalArgumentException();
        Point[] copyPoints = Arrays.copyOf(points, points.length);
        Arrays.sort(copyPoints);
        valid(copyPoints);
        linesFound = new ArrayList<LineSegment>();

        for (int i = 0; i < copyPoints.length; i++) {
            Point current = copyPoints[i];
            // System.out.println("Current: " + current.toString());
            Point[] sortedPoints = Arrays.copyOf(copyPoints, copyPoints.length);
            Arrays.sort(sortedPoints, current.slopeOrder());

            int j = 1;
            while (j < sortedPoints.length - 2) {
                // System.out.println("Index " + j);
                Point min = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
                Point max = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);

                double curSlope = current.slopeTo(sortedPoints[j]);

                int check = 2;
                if (current.slopeTo(sortedPoints[j]) == current.slopeTo(sortedPoints[j + check])) {
                    // check if any of the first 3 points + current point are minimums
                    if (current.compareTo(min) < 0) min = current;
                    if (current.compareTo(max) > 0) max = current;
                    for (int m = 0; m < 3; m++) {
                        if (sortedPoints[j + m].compareTo(min) < 0) min = sortedPoints[j + m];
                        if (sortedPoints[j + m].compareTo(max) > 0) max = sortedPoints[j + m];
                    }
                    check++;
                    while (j + check < sortedPoints.length) {
                        if (current.slopeTo(sortedPoints[j + check]) != curSlope) break;
                        if (sortedPoints[j + check].compareTo(min) < 0)
                            min = sortedPoints[j + check];
                        if (sortedPoints[j + check].compareTo(max) > 0)
                            max = sortedPoints[j + check];
                        check++;
                    }
                    // if current is min, can add line segment to remove duplicates
                    if (min.equals(current)) {
                        linesFound.add(new LineSegment(min, max));
                    }
                    j = j + check;
                }
                else {
                    j++;
                }

            }

        }

    }

    private void valid(Point[] points) {
        if (points == null) throw new IllegalArgumentException();
        for (int i = 0; i < points.length - 2; i++) {
            if (points[i] == null || points[i].compareTo(points[i + 1]) == 0)
                throw new IllegalArgumentException();
        }
    }

    // the number of line segments
    public int numberOfSegments() {
        return linesFound.size();
    }

    public LineSegment[] segments() {
        LineSegment[] linesArr = new LineSegment[linesFound.size()];
        linesFound.toArray(linesArr);
        return Arrays.copyOf(linesArr, linesArr.length);

    }

    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
