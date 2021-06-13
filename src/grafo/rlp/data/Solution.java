package grafo.rlp.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import grafo.rlp.algorithm.other.RLPConnectedComponents;
import grafo.rlp.interfaces.RLPSolution;

public class Solution implements RLPSolution {

    final RLPInstance instance;
    final int n;
    boolean labeled[];
    int cost;



    /**
     * New instance constructor.
     * @param instance for this solution
     */
    public Solution(RLPInstance instance) {
        this.instance = instance;
        this.n = instance.getV();
        this.labeled = new boolean[n];
    }

    /**
     * Copy constructor
     * @param solution orignal solution to copy
     */
    public Solution(RLPSolution solution) {
        this(solution.getInstance());
        for (Integer regenerator : solution.getRegeneratorAsList()) {
            this.putRegenerator(regenerator);
        }
    }

    @Override
    public String getName() {
        return instance.getName();
    }


    @Override
    public RLPInstance getInstance() {
        return instance;
    }

    @Override
    public int getV() {
        return instance.getV();
    }

    @Override
    public List<Integer> getEdges(int node) {
        return instance.getEdges(node);
    }

    @Override
    public void putRegenerator(int node) {
        if(!this.labeled[node]) {
            this.labeled[node] = true;
            this.cost++;
        } else
            throw new RuntimeException("This node already holds a regenerator");
    }

    @Override
    public void removeRegenerator(int node) {
        if(this.labeled[node]) {
            this.labeled[node] = false;
            this.cost--;
        } else {
            throw new RuntimeException("This node is not a regenerator");
        }
    }

    @Override
    public int getObjectiveFunction() {
        return cost;
    }

    @Override
    public void copyOf(RLPSolution otherSolution) {
        assert(this.getName().equals(otherSolution.getName()));
        assert(this.getV()==otherSolution.getV());
        boolean[] labels;
        if(otherSolution instanceof Solution){
            labels = ((Solution) otherSolution).labeled;
        } else {
            labels = otherSolution.getRegeneratorsAsArray();
        }
        this.cost = otherSolution.getObjectiveFunction();
        for(int i=0; i<this.getV(); i++){
            this.labeled[i] = labels[i];
        }

    }

    @Override
    public boolean isInSolution(int node) {
        return this.labeled[node];
    }


    public boolean validate() {
        int actualRegenerators = 0;
        for (int i = 0; i < this.labeled.length; i++) {
            if (this.labeled[i])
                actualRegenerators++;
        }
        //Check the OF actual value
        boolean hasCoherentObjectiveFunction = this.cost == actualRegenerators;

        //Check if the regenerators constitute a single connected component
        RLPConnectedComponents<Solution> algorithmCC = new RLPConnectedComponents<>();
        List<Set<Integer>> connectedComponents = algorithmCC.findConnectedComponents(this);
        boolean isSingleComponent = connectedComponents.size() == 1;

        //Check if all nodes are connected to a regenerator
        List<Integer> regenerators = this.getRegeneratorAsList();
        boolean connected[] = new boolean[this.getV()];
        for (Integer r : regenerators) {
            connected[r]=true;
            for (Integer adj : this.getEdges(r)) {
                connected[adj]=true;
            }
        }
        int nConnected = 0;
        for (boolean b : connected) {
            if (b) {
                nConnected++;
            }
        }
        boolean allConnected = nConnected == this.getV();

        //returns true for feasible and valid solution
        return hasCoherentObjectiveFunction && allConnected && isSingleComponent;
    }


    @Override
    public List<Integer> getRegeneratorAsList() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < labeled.length; i++) {
            if (labeled[i])
                list.add(i);
        }
        return list;
    }

    @Override
    public boolean[] getRegeneratorsAsArray() {
        return this.labeled.clone();
    }


    @Override @Deprecated
    public boolean hasEdge(int node1, int node2) {
        //Complexity O(n), avoid its usage
        return this.instance.isEdge(node1, node2);
    }

    @Override
    public String toString() {
        return "RLPSolution [n=" + n + ", cost=" + cost + ", labeled="
                + this.getRegeneratorAsList() + ", instance=" + instance.getName() + "]";
    }



}
