import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;

public class BruteCollinearPoints {
    private final ArrayList<LineSegment> linesFound;

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        if (points == null) throw new IllegalArgumentException();
        Point[] copyPoints = Arrays.copyOf(points, points.length);
        Arrays.sort(copyPoints);
        valid(copyPoints);

        linesFound = new ArrayList<LineSegment>();

        // p is original point / pqrs order
        for (int p = 0; p < copyPoints.length - 3; p++) {
            for (int q = p + 1; q < copyPoints.length - 2; q++) {
                for (int r = q + 1; r < copyPoints.length - 1; r++) {
                    if (copyPoints[p].slopeTo(copyPoints[q]) == copyPoints[p]
                            .slopeTo(copyPoints[r])) {
                        for (int s = r + 1; s < copyPoints.length; s++) {
                            if (copyPoints[p].slopeTo(copyPoints[q]) == copyPoints[p]
                                    .slopeTo(copyPoints[s])) {
                                linesFound.add(new LineSegment(copyPoints[p], copyPoints[s]));
                            }
                        }
                    }
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

    // the line segments
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
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }

}

