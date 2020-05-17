import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Collections;

public class SAP {

    private final Digraph digraph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException();
        digraph = G;
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (v < 0 || w < 0 || v >= digraph.V() || w >= digraph.V())
            throw new IllegalArgumentException();
        if (v == w) return 0;
        return sap("Distance", new ArrayList<Integer>(Collections.singletonList(v)),
                   new ArrayList<Integer>(Collections.singletonList(w)));
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (v < 0 || w < 0 || v >= digraph.V() || w >= digraph.V())
            throw new IllegalArgumentException();
        if (v == w) return v;
        return sap("Ancestor", new ArrayList<Integer>(Collections.singletonList(v)),
                   new ArrayList<Integer>(Collections.singletonList(w)));
    }

    private boolean checkIter(Iterable<Integer> it) {
        for (Integer i : it) {
            if (i == null || i < 0 || i >= digraph.V()) return false;
        }
        return true;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException();
        if (!checkIter(v) || !checkIter(w)) throw new IllegalArgumentException();
        return sap("Distance", v, w);
    }


    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException();
        if (!checkIter(v) || !checkIter(w)) throw new IllegalArgumentException();
        return sap("Ancestor", v, w);
    }

    private int sap(String type, Iterable<Integer> v, Iterable<Integer> w) {
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(digraph, v);

        int minDist = Integer.MAX_VALUE;
        int minAnc = -1;
        Queue<Integer> q = new Queue<Integer>();
        boolean[] marked = new boolean[digraph.V()];
        int[] distTo = new int[digraph.V()];


        for (int j : w) {
            q.enqueue(j);
            marked[j] = true;
            distTo[j] = 0;
        }

        while (!q.isEmpty()) {
            int cur = q.dequeue();
            if (bfsV.hasPathTo(cur)) {
                if (bfsV.distTo(cur) + distTo[cur] < minDist) {
                    minDist = bfsV.distTo(cur) + distTo[cur];
                    minAnc = cur;
                }
            }
            for (int i : digraph.adj(cur)) {
                if (!marked[i] || distTo[i] > distTo[cur] + 1) {
                    q.enqueue(i);
                    marked[i] = true;
                    distTo[i] = distTo[cur] + 1;
                }

            }
        }
        if (minAnc == -1) return -1;
        if (type.equals("Distance")) return minDist;
        return minAnc;

    }

    private int sap2(String type, Iterable<Integer> v, Iterable<Integer> w) {
        // bfs
        boolean[] marked = new boolean[digraph.V()];
        int[] distTo = new int[digraph.V()];
        boolean[] cameFromV = new boolean[digraph.V()];
        int minDist = Integer.MAX_VALUE;
        int idMin = -1;

        Queue<Integer> q = new Queue<Integer>();
        for (int i : v) {
            q.enqueue(i);
            marked[i] = true;
            distTo[i] = 0;
            cameFromV[i] = true;
        }
        for (int j : w) {
            q.enqueue(j);
            if (marked[j]) {
                if (type.equals("Distance")) return 0;
                return j;
            }
            marked[j] = true;
            distTo[j] = 0;
        }

        while (!q.isEmpty()) {
            int cur = q.dequeue();
            // System.out.println("Current = " + cur);
            // if (cameFromV[cur]) System.out.println("V Path");
            // System.out.println("Distance: " + distTo[cur]);
            for (int next : digraph.adj(cur)) {
                if (marked[next] && cameFromV[next] != cameFromV[cur]) {
                    if (distTo[next] + distTo[cur] + 1 < minDist) {
                        minDist = distTo[next] + distTo[cur] + 1;
                        idMin = next;
                    }

                }
                if (!marked[next]) {
                    q.enqueue(next);
                    marked[next] = true;
                    distTo[next] = distTo[cur] + 1;
                    cameFromV[next] = cameFromV[cur];
                }
            }

        }
        if (idMin == -1) return -1;
        if (type.equals("Distance")) return minDist;
        return idMin;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
