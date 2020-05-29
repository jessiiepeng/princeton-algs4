import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;

public class BaseballElimination {

    private HashMap<String, Integer> teamNames;
    private int[] wins, remaining, loses;
    private int[][] against;
    private boolean[] isEliminated;
    private Bag<String>[] subsets;

    private int source, target;
    private final static double INFINITY = Double.POSITIVE_INFINITY;

    private final int nTeams, totalGames = 162;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In in = new In(filename);
        nTeams = in.readInt(); // read # of teams

        source = nTeams;
        target = nTeams + 1;

        teamNames = new HashMap<String, Integer>();
        wins = new int[nTeams];
        loses = new int[nTeams];
        remaining = new int[nTeams];
        against = new int[nTeams][nTeams];
        isEliminated = new boolean[nTeams];
        subsets = (Bag<String>[]) new Bag[nTeams];
        for (int v = 0; v < nTeams; v++) {
            subsets[v] = new Bag<String>();
        }

        for (int i = 0; i < nTeams; i++) {
            String name = in.readString();
            teamNames.put(name, i);
            wins[i] = in.readInt();
            loses[i] = in.readInt();
            remaining[i] = in.readInt();

            for (int j = 0; j < nTeams; j++) {
                against[i][j] = in.readInt();
            }
        }

        for (String team : teamNames.keySet()) {
            if (!checkTrivial(team)) { // not trivial elimination --> must do flowNetwork
                checkHard(team);
            }
        }
    }

    private void checkHard(String team) {
        FlowNetwork flowNetwork = new FlowNetwork(nTeams * nTeams + nTeams + 2);
        double sum = 0;
        int check = teamNames.get(team);
        int bottleNeck = wins[check] + remaining[check];

        for (int i = 0; i < nTeams - 1; i++) {
            for (int j = i + 1; j < nTeams; j++) {
                if (i != check && j != check) {
                    int game = nTeams + 2 + i + nTeams * j;
                    sum += against[i][j];
                    flowNetwork.addEdge(
                            new FlowEdge(source, game, against[i][j])); // source to game vertice
                    flowNetwork.addEdge(new FlowEdge(game, i, INFINITY)); // game vertice to team
                    flowNetwork.addEdge(new FlowEdge(game, j, INFINITY)); // game vertice to team
                }
            }
        }
        // connect team vertice to target
        for (int i = 0; i < nTeams; i++) {
            if (i != check) {
                if (bottleNeck - wins[i] < 0) flowNetwork.addEdge(new FlowEdge(i, target, 0));
                else flowNetwork.addEdge(new FlowEdge(i, target, bottleNeck - wins[i]));
            }
        }

        FordFulkerson ff = new FordFulkerson(flowNetwork, source, target);
        if (sum == ff.value()) {
            isEliminated[check] = false;
        }
        else {
            isEliminated[check] = true;
            for (String s : teamNames.keySet()) {
                if (!s.equals(team) && ff.inCut(teamNames.get(s))) {
                    subsets[check].add(s);
                }
            }
        }

    }

    private boolean checkTrivial(String team) {
        for (String check : teamNames.keySet()) {
            if (!team.equals(check) && wins[teamNames.get(team)] + remaining[teamNames.get(team)]
                    < wins[teamNames.get(check)]) {
                subsets[teamNames.get(team)].add(check);
                isEliminated[teamNames.get(team)] = true;
                return true;
            }
        }
        return false;
    }


    // number of teams
    public int numberOfTeams() {
        return nTeams;
    }

    // all teams
    public Iterable<String> teams() {
        return teamNames.keySet();
    }

    // number of wins for given team
    public int wins(String team) {
        if (!teamNames.containsKey(team)) throw new IllegalArgumentException();
        return wins[teamNames.get(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        if (!teamNames.containsKey(team)) throw new IllegalArgumentException();
        return loses[teamNames.get(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        if (!teamNames.containsKey(team)) throw new IllegalArgumentException();
        return remaining[teamNames.get(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        if (!teamNames.containsKey(team1) || !teamNames.containsKey(team2))
            throw new IllegalArgumentException();
        return against[teamNames.get(team1)][teamNames.get(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        if (!teamNames.containsKey(team)) throw new IllegalArgumentException();
        return isEliminated[teamNames.get(team)];
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (!teamNames.containsKey(team)) throw new IllegalArgumentException();
        if (isEliminated[teamNames.get(team)]) return subsets[teamNames.get(team)];
        return null;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }

        }
    }
}
