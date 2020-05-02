import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.ArrayList;
import java.util.TreeSet;

public class PointSET {

    private TreeSet<Point2D> pointsTree;

    // construct an empty set of points
    public PointSET() {
        pointsTree = new TreeSet<Point2D>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return pointsTree.isEmpty();
    }

    // number of points in the set
    public int size() {
        return pointsTree.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        pointsTree.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        return pointsTree.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        pointsTree.forEach(Point2D::draw);
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();
        ArrayList<Point2D> insidePoints = new ArrayList<>();
        for (Point2D p : pointsTree) {
            if (rect.contains(p)) insidePoints.add(p);
        }
        return insidePoints;

    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (pointsTree == null) return null;
        double minVal = Double.POSITIVE_INFINITY;
        Point2D min = null;
        for (Point2D s : pointsTree) {
            if (Double.compare(p.distanceSquaredTo(s), minVal) < 0) {
                min = s;
                minVal = p.distanceSquaredTo(s);
            }

        }
        return min;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        // empty
        /*
        PointSET brute = new PointSET();
        if (brute.isEmpty()) System.out.println("empty");
        brute.insert(new Point2D(0.0, 0.5));
        brute.insert(new Point2D(0.3, 0.7));
        if (!brute.isEmpty()) System.out.println("not empty");
        System.out.println(brute.size());
        StdDraw.setPenRadius(0.1);
        brute.draw();

        if (brute.contains(new Point2D(0.0, 0.5))) System.out.println("contains");
        brute.insert(new Point2D(0.0, 0.4));
        StdDraw.clear();
        brute.draw();
        Point2D p = brute.nearest(new Point2D(0.0, 0.4));
        System.out.println(p.toString());


        for (Point2D s : brute.range(new RectHV(0.95, 0.95, 1.0, 1.0))) {
            System.out.println(s.toString());
        }

         */

    }
}
