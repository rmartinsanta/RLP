package grafo.rlp.data;

public class Candidate implements Comparable<Candidate> {
    public int node;
    public int score;

    public Candidate(int node, int score) {
        this.node = node;
        this.score = score;
    }

    public Candidate(Candidate candidate) {
        this.node = candidate.node;
        this.score = candidate.score;
    }

    @Override
    public int compareTo(Candidate o) {
        assert (o != null);
        int compare = Integer.compare(this.score, o.score);
        if (compare != 0)
            return compare;
        //Absolute order required by BinarySearch
        return Integer.compare(this.node, o.node);
    }

    @Override
    public String toString() {
        return "C" + node + "=" + score;
    }

}