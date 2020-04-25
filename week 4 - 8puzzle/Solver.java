import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.Comparator;

public class Solver {
    private boolean canSolve = false;
    private Node endNode;

    private class Node {
        private final Board current;
        private final Node prev;
        private final int moves;
        private final int manhat;
        private final int priority;

        // current , prev , moves made, manhatten
        public Node(Board c, Node p, int m, int mh) {
            current = c;
            prev = p;
            moves = m;
            manhat = mh;
            priority = moves + manhat;
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException();

        Node original = new Node(initial, null, 0, initial.manhattan());
        Node twin = new Node(initial.twin(), null, 0, initial.twin().manhattan());
        Comparator<Node> sortByPriority = (o1, o2) -> (o1.priority
                - o2.priority); // create comparator

        // create minPQ
        MinPQ<Node> originalPQ = new MinPQ<>(sortByPriority);
        originalPQ.insert(original);

        MinPQ<Node> twinPQ = new MinPQ<>(sortByPriority);
        twinPQ.insert(twin);


        while (!originalPQ.min().current.isGoal() && !twinPQ.min().current.isGoal()) {
            // System.out.println("Main: ");
            addNeighbors(originalPQ);
            // System.out.println("Twin: ");
            addNeighbors(twinPQ);
        }

        if (originalPQ.min().current.isGoal()) {
            canSolve = true;
            endNode = originalPQ.min();
        }

    }

    private void addNeighbors(MinPQ<Node> pq) {
        Node currentNode = pq.delMin();
        Iterable<Board> check = currentNode.current.neighbors();

        if (currentNode.moves == 0) {
            for (Board nb : check) {
                pq.insert(new Node(nb, currentNode, currentNode.moves + 1,
                                   nb.manhattan()));
            }
        }
        else {
            for (Board nb : check) {
                if (!nb.equals(currentNode.prev.current)) {
                    pq.insert(new Node(nb, currentNode, currentNode.moves + 1,
                                       nb.manhattan()));
                }
            }
        }

    }


    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return canSolve;
    }

    // min number of moves to solve initial board
    public int moves() {
        if (endNode == null) return 0;
        return endNode.moves;
    }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {
        if (!isSolvable()) return null;
        Stack<Board> seq = new Stack<Board>();
        Node cur = endNode;
        for (int i = 0; i <= endNode.moves; i++) {
            seq.push(cur.current);
            cur = cur.prev;
        }
        return seq;

    }

    // test client (see below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

}
