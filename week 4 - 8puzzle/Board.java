import java.util.ArrayList;

public class Board {

    private final int[][] board;
    private final int N;
    private final int hamming;
    private final int manhatten;
    private int x0;
    private int y0;
    private boolean goal;
    private ArrayList<Board> neighbor;


    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        N = tiles.length;
        board = new int[N][N];

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                board[i][j] = tiles[i][j];
                if (board[i][j] == 0) {
                    x0 = i;
                    y0 = j;
                }
            }
        }
        int h = 0;
        int m = 0;
        goal = true;
        int check = 1;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (board[i][j] != check && check != N * N) {
                    h++;
                    goal = false;
                }
                if (board[i][j] != 0) {
                    // System.out.println(board[i][j]);

                    int row = board[i][j] / N;
                    if (board[i][j] % N == 0) row--;
                    int col = board[i][j] - 1 - row * N;

                    // System.out.println("Row " + row + " Col " + col);
                    // System.out.println("i " + i + " j " + j);
                    m = m + Math.abs(row - i) + Math.abs(col - j);
                    // System.out.println("Man " + m);
                }
                check++;
            }
        }
        hamming = h;
        manhatten = m;
    }

    // string representation of this board
    public String toString() {
        StringBuilder rep = new StringBuilder();
        rep.append(N);
        for (int i = 0; i < N; i++) {
            rep.append('\n');
            for (int j = 0; j < N; j++) {
                rep.append(board[i][j] + " ");
            }
        }
        return rep.toString();
    }

    // board dimension n
    public int dimension() {
        return N;
    }

    // number of tiles out of place
    public int hamming() {
        return hamming;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        return manhatten;

    }

    // is this board the goal board?
    public boolean isGoal() {
        return goal;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;

        Board that = (Board) y;

        if (that.dimension() != N) return false;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (this.board[i][j] != that.board[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    private int[][] copyArray(int[][] arr) {
        int[][] copy = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                copy[i][j] = arr[i][j];
            }
        }
        return copy;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        neighbor = new ArrayList<Board>();
        findNeighbor(x0, y0 - 1);
        findNeighbor(x0, y0 + 1);
        findNeighbor(x0 - 1, y0);
        findNeighbor(x0 + 1, y0);
        return neighbor;
    }

    // find neighboring board if possible
    private void findNeighbor(int newX, int newY) {
        if (newX >= 0 && newX < N && newY >= 0 && newY < N) {
            int[][] copy = copyArray(board);
            int num = copy[newX][newY];
            copy[x0][y0] = num;
            copy[newX][newY] = 0;
            Board bCopy = new Board(copy);
            neighbor.add(bCopy);
        }

    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int[][] twin = copyArray(board);
        // any arbitrary swapping so swap first 2, except if 0,0 is 0 swap next 2
        if (twin[0][0] != 0 && twin[1][0] != 0) {
            int num = twin[0][0];
            int num2 = twin[1][0];
            twin[0][0] = num2;
            twin[1][0] = num;
        }
        else {
            int num = twin[0][1];
            int num2 = twin[1][1];
            twin[0][1] = num2;
            twin[1][1] = num;
        }
        return new Board(twin);
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        // empty
        /*
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);
        System.out.println(initial.manhattan());

         */

    }
}
