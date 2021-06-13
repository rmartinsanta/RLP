package grafo.rlp.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * This class can generate a random communication graph
 * for the RLP following the same method described in [6].
 * This graph is parameterized by the number of nodes <b>N</b>
 * and the percentage <b>P</b> non-directly connected nodes NDC.
 *
 * <p><i>An instance is generated according to two parameters,
 * the first one <b>N</b> controls the number of nodes
 * (problem size) and the other one <b>p</b> in [0,1] that
 * is the percentage of non-directly connected node pairs
 * (NDC).</i></p>
 *
 * <p><i>To ensure feasibility, we first construct an arbitrary
 * spanning tree over the <b>n</b> nodes. We then randomly
 * generate E edges and add them to the spanning tree
 * generated previously.
 * </i></p>
 *
 * <p>E_max = n*(n-1)/2</p>
 * <p>E = <b>floor</b>((1-p)*(E_max - (n-1)))</p>
 * <p>NDC = <b>ceil</b>(p*(E_max - (n-1))</p>

 *
 * <ul><li>[6] The regenerator Location Problem [Chen2010]</li></ul>
 *
 * @author juandavid
 * @version 1.0 (2020.02.10)
 */
@SuppressWarnings("WeakerAccess")
public class InstanceGenerator {


    /**
     * Randomly generates a new instance for the RLP following the same method
     * described in [6] (See class javadoc).
     * @param n number of vertex in the graph
     * @param p percentage of NDC
     * @return a random instance for the RLP
     */

    public static RLPInstance generateInstance(int n, float p){
        return generateInstance(n, p, new Random());
    }

    /**
     * Randomly generates a new instance for the RLP following the same method
     * described in [6] (See class javadoc).
     * @param n number of vertex in the graph
     * @param p percentage of NDC
     * @param seed seed used for the random number generator
     * @return a random instance for the RLP
     */

    public static RLPInstance generateInstance(int n, float p, long seed){
        return generateInstance(n, p, new Random(seed));
    }

    /**
     * Randomly generates a new instance for the RLP following the same method
     * described in [6] (See class javadoc).
     * @param n number of vertex in the graph
     * @param p percentage of NDC
     * @param rng Random number generator used to generate the graph
     * @return a random instance for the RLP
     */

    public static RLPInstance generateInstance(int n, float p, Random rng){
        RLPInstance instance = new RLPInstance(n);

        // ensure instance is feasible
        generateSpanningTree(instance, rng);
        assert(instance.getE() == n-1);

        //generate remaining edges randomly
        int remainingEdges = expectedRemainingEdges(n, p);
        generateRemainingEdges(instance, remainingEdges, rng);
        assert((n-1)+remainingEdges == instance.getE());
        return instance;
    }

    private static void generateRemainingEdges(RLPInstance instance, int edges, Random rng) {
        int misses = 0;
        while(edges>0){
            int v1 = rng.nextInt(instance.getV());
            int v2 = rng.nextInt(instance.getV());
            if(v1!=v2 && !instance.isEdge(v1, v2)){
                instance.addEdge(v1, v2);
                edges--;
            } else {
                misses++;
            }
        }
        System.out.printf(" Misses: %5.2fk", misses / 1000.0);
    }

    //TODO: Terminar....
    @SuppressWarnings("unused") @Deprecated
    private static void generateRemainingEdges_Roulette(RLPInstance instance, int edges, Random rng) {
        int[] weights = new int[instance.getV()];
        Arrays.fill(weights, instance.getV()-1);
        int total_weight = (instance.getV()*instance.getV()-1);

        for (int i = 0; i < edges; i++) {
            int v1 = roulette(weights, total_weight);
            int v2 = selectNextByIndex(instance, rng.nextInt(weights[v1]));
            assert(!instance.isEdge(v1,v2));
            instance.addEdge(v1,v2);
            total_weight -=2;
            weights[v1]-=1;
            weights[v2]-=1;
        }
    }

    @Deprecated
    private static int selectNextByIndex(RLPInstance instance, int nextInt) {
        throw new RuntimeException("Not yet implemente.");
    }

    @Deprecated
    private static int roulette(int[] weights, int total_weight) {
        throw new RuntimeException("Not yet implemente.");
    }

    private static int expectedRemainingEdges(int n, float p) {
        int all_possible = n*(n-1)/2;
        int spanningTreeEdges = (n-1);
        int ndc = expectedNDC(n, p);
        return all_possible - spanningTreeEdges - ndc;
    }

    /**
     * This method ensures the generated instances is feasible by generating
     * a spanning tree (n-1 edges) that connect all nodes.
     * @param instance instance where the spanning tree is generated
     * @param rng random number generator
     */
    private static void generateSpanningTree(RLPInstance instance, Random rng) {
        ArrayList<Integer> spanningTreeNodes = new ArrayList<>();
        ArrayList<Integer> isolatedNodes = new ArrayList<>();


        for (int i = 0; i < instance.getV(); i++) {
            isolatedNodes.add(i);
        }

        for (int i = 0; i < instance.getV(); i++) {
            int nextIndex = rng.nextInt(isolatedNodes.size());
            int nextNode = popEfficiently(isolatedNodes, nextIndex);

            //connect the selected node to any node in the spanning tree
            if(!spanningTreeNodes.isEmpty()){
                int treeNode = spanningTreeNodes.get(rng.nextInt(spanningTreeNodes.size()));
                instance.addEdge(nextNode, treeNode);
            }
            spanningTreeNodes.add(nextNode);
        }
    }

    /**
     * Efficient get-remove an element (given its index) from an ArrayList.
     *
     * <p>This method assumes it's not important to maintain the order in the list.
     * It swaps the desired element with the last element and then efficiently
     * removes it from the last position.</p>
     * @param list non-empty list
     * @param popIndex index of the element to be removed
     * @return removed element
     */
    private static int popEfficiently(ArrayList<Integer> list, int popIndex) {
        int lastIndex = list.size()-1;
        if(lastIndex != popIndex){
            //swap elements at popIndex and lastElement
            int popElement = list.set(popIndex, list.get(lastIndex));
            list.set(lastIndex, popElement);
        }
        //efficiently remove popElement from last position
        return list.remove(lastIndex);
    }

    @SuppressWarnings("unused")
    public static int expectedEdges(int n, float p){
        //E = floor((1-p)*(E_max - (n-1)) +(n-1)
        int max_edges = n*(n-1)/2; //edges in complete graph
        int min_edges = (n-1); //edges in a feasible instance
        return (int) Math.floor((1-p)*(max_edges-min_edges) + (n-1));
    }

    public static int expectedNDC(int n, float p){
        //NDC = ceil(p*()E_max - (n-1)
        int max_edges = n*(n-1)/2; //edges in complete graph
        int min_edges = (n-1); //edges in a feasible instance
        return (int) Math.ceil(p*(max_edges-min_edges));
    }




}
