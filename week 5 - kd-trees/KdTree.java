import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;

public class KdTree {
    private Node root;
    private int size;
    private ArrayList<Point2D> range;
    private double minValue;
    private Point2D minPoint;
    private Node prevRoot;


    private class Node {
        public Point2D point;
        public boolean vertical;
        public boolean isLeft;
        public Node parent;
        public Node left;
        public Node right;
        public RectHV rect;

        // point, is it vertical, previous node, is it left or right node, rectangle it is contained in
        public Node(Point2D po, boolean v, Node p, boolean il, RectHV r) {
            point = po;
            vertical = v;
            parent = p;
            left = null;
            right = null;
            isLeft = il;
            rect = r;
        }
    }

    // construct an empty set of points
    public KdTree() {
        size = 0;
        root = null;
        prevRoot = new Node(null, false, null, false, null);
    }

    // is the set empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // number of points in the set
    public int size() {
        return size;
    }

    // recursively checking to add node into right position
    private void addRecursive(Node current, Node prev, Point2D p, boolean left) {

        if (current == null) {
            if (prev.vertical) {
                if (left) {
                    prev.left = new Node(p, false, prev, true,
                                         new RectHV(prev.rect.xmin(), prev.rect.ymin(),
                                                    prev.point.x(),
                                                    prev.rect.ymax()));
                }
                else prev.right = new Node(p, false, prev, false,
                                           new RectHV(prev.point.x(), prev.rect.ymin(),
                                                      prev.rect.xmax(),
                                                      prev.rect.ymax()));
            }
            else { // horizontal
                if (left) {
                    prev.left = new Node(p, true, prev, true,
                                         new RectHV(prev.rect.xmin(), prev.rect.ymin(),
                                                    prev.rect.xmax(),
                                                    prev.point.y()));
                }
                else prev.right = new Node(p, true, prev, false,
                                           new RectHV(prev.rect.xmin(), prev.point.y(),
                                                      prev.rect.xmax(),
                                                      prev.rect.ymax()));
            }
        }
        else {
            if (current.vertical) {
                if (Double.compare(p.x(), current.point.x()) < 0)
                    addRecursive(current.left, current, p, true);
                else addRecursive(current.right, current, p, false);
            }
            else {
                if (Double.compare(p.y(), current.point.y()) < 0)
                    addRecursive(current.left, current, p, true);
                else addRecursive(current.right, current, p, false);
            }
        }
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (root == null) {
            root = new Node(p, true, prevRoot, true,
                            new RectHV(0.0, 0.0, 1.0, 1.0));
            size++;
        }
        else if (!contains(p)) {
            addRecursive(root, prevRoot, p, true);
            size++;
        }
    }

    // recursive check to see if it's in tree
    private boolean recursiveCheck(Node current, Point2D p) {
        if (current == null) return false;
        if (current.point.equals(p)) return true;

        if (current.vertical) {
            if (Double.compare(p.x(), current.point.x()) < 0)
                return recursiveCheck(current.left, p);
            else return recursiveCheck(current.right, p);
        }
        else {
            if (Double.compare(p.y(), current.point.y()) < 0)
                return recursiveCheck(current.left, p);
            else return recursiveCheck(current.right, p);
        }
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        return recursiveCheck(root, p);
    }

    // recursively draw points starting from root, keeping track of dimension of box left to draw
    private void recursiveDraw(Node node, RectHV rect) {
        if (node != null) {

            if (node.vertical) {
                StdDraw.setPenColor(StdDraw.RED);
                StdDraw.line(node.point.x(), rect.ymin(), node.point.x(), rect.ymax());
                if (node.left != null) recursiveDraw(node.left, node.left.rect);
                if (node.right != null) recursiveDraw(node.right, node.right.rect);

            }
            else {
                StdDraw.setPenColor(StdDraw.BLUE);
                StdDraw.line(rect.xmin(), node.point.y(), rect.xmax(), node.point.y());
                if (node.left != null) recursiveDraw(node.left, node.left.rect);
                if (node.right != null) recursiveDraw(node.right, node.right.rect);
            }

            StdDraw.setPenColor(StdDraw.BLACK);
            node.point.draw();

        }
    }


    // draw all points to standard draw
    public void draw() {
        if (isEmpty()) throw new IllegalArgumentException();
        recursiveDraw(root, root.rect);
    }

    // recursively check nodes to see if they lie in rectangle
    private void recursiveRange(Node node, RectHV rect) {
        if (node != null) {
            if (rect.contains(node.point)) range.add(node.point);

            if (node.right != null && node.right.rect.intersects(rect))
                recursiveRange(node.right, rect);
            if (node.left != null && node.left.rect.intersects(rect))
                recursiveRange(node.left, rect);

        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();
        range = new ArrayList<>();
        recursiveRange(root, rect);
        return range;
    }

    private void recursiveNearest(Node node, Point2D p) {
        if (node != null && node.point.distanceSquaredTo(p) < minValue) {
            minValue = node.point.distanceSquaredTo(p);
            minPoint = node.point;
        }

        if (node != null && node.vertical) {
            // right side of splitting line
            if (p.x() >= node.point.x()) {
                // check right side first
                if (node.right != null && node.right.rect.distanceSquaredTo(p) <= minValue)
                    recursiveNearest(node.right, p);
                // then check left side
                if (node.left != null && node.left.rect.distanceSquaredTo(p) <= minValue)
                    recursiveNearest(node.left, p);
            }
            // left side of splitting line
            else {
                if (node.left != null && node.left.rect.distanceSquaredTo(p) <= minValue)
                    recursiveNearest(node.left, p);
                if (node.right != null && node.right.rect.distanceSquaredTo(p) <= minValue)
                    recursiveNearest(node.right, p);
            }
        }
        else if (node != null) { // horizontal
            if (p.y() >= node.point.y()) {
                if (node.right != null && node.right.rect.distanceSquaredTo(p) <= minValue)
                    recursiveNearest(node.right, p);
                if (node.left != null && node.left.rect.distanceSquaredTo(p) <= minValue)
                    recursiveNearest(node.left, p);
            }
            else {
                if (node.left != null && node.left.rect.distanceSquaredTo(p) <= minValue)
                    recursiveNearest(node.left, p);
                if (node.right != null && node.right.rect.distanceSquaredTo(p) <= minValue)
                    recursiveNearest(node.right, p);
            }
        }

    }


    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        minValue = Double.POSITIVE_INFINITY;
        minPoint = null;
        recursiveNearest(root, p);
        return minPoint;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        // empty
        /*
        KdTree kd = new KdTree();
        kd.insert(new Point2D(0.785, 0.725));
        kd.insert(new Point2D(0.32, 0.708));
        StdDraw.setPenRadius(0.1);
        kd.draw();
        System.out.println(kd.nearest(new Point2D(0.435, 0.997)).toString());

         */


    }
}
