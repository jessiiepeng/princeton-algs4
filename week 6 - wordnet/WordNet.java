import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.KosarajuSharirSCC;

import java.util.HashMap;

public class WordNet {

    private Digraph digraph;
    private HashMap<String, Bag<Integer>> synsetMap;
    private HashMap<Integer, String> idMap;
    private SAP sap;
    private KosarajuSharirSCC checkDAG;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) throw new IllegalArgumentException();
        // check if input isn't rooted DAG --> throw argument

        synsetMap = new HashMap<>();
        idMap = new HashMap<>();

        In inSynsets = new In(synsets);
        In inHypernyms = new In(hypernyms);

        // use hashmaps to record synsets with String: Bag<Integer> (Word: List of Ids)
        while (inSynsets.hasNextLine()) {
            String[] splitLine = inSynsets.readLine().split(",");
            int id = Integer.parseInt(splitLine[0]);
            for (String word : splitLine[1].split(" ")) {
                if (!synsetMap.containsKey(word)) {
                    synsetMap.put(word, new Bag<>());
                }
                synsetMap.get(word).add(id);
                idMap.put(id, splitLine[1]);
            }

        }
        inSynsets.close();

        // create Digraph using In
        digraph = new Digraph(synsetMap.size());
        while (inHypernyms.hasNextLine()) {
            String[] splitLine = inHypernyms.readLine().split(",");
            int origin = Integer.parseInt(splitLine[0]);
            for (int i = 1; i < splitLine.length; i++) {
                digraph.addEdge(origin, Integer.parseInt(splitLine[i]));
            }
        }
        inHypernyms.close();

        sap = new SAP(digraph);
        checkDAG = new KosarajuSharirSCC(digraph);
        if (checkDAG.count() != digraph.V()) throw new IllegalArgumentException();
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return synsetMap.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException();
        return synsetMap.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new IllegalArgumentException();
        if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException();
        return sap.length(synsetMap.get(nounA), synsetMap.get(nounB));

    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new IllegalArgumentException();
        if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException();
        return idMap.get(sap.ancestor(synsetMap.get(nounA), synsetMap.get(nounB)));
    }

    // do unit testing of this class
    public static void main(String[] args) {
        // not testing yet

    }

}
