package grafo.rlp.algorithm.other;

import java.util.*;

import grafo.rlp.interfaces.RLPSolution;

/**
 * Algorithm to find the articulation points in
 * a feasible solution for the RLP.
 *
 * @param <S> a solution class for the RLP
 * @author juandavid.quintana
 * @version 1.0 (2019.11.26)
 */
public class RLPArticulationPoints<S extends RLPSolution> {

    /**
     * Returns the articulation points found in a feasible
     * solution for the RLP.
     *
     * <p>Precondition: There is only one connected component
     * of labelled nodes. (i.e. we have a feasible solution)</p>
     *
     * @param solution a feasible RLP solution
     * @return a set of labelled nodes (articulation points)
     */
    public Set<Integer> findArticulationPoints(S solution) {
        Set<Integer> points = new HashSet<>(solution.getObjectiveFunction());
        if (solution.getObjectiveFunction() < 2)
            return points; //No Articulation Points

        //initialize data structures
        boolean visited[] = new boolean[solution.getV()];
        int disc[] = new int[solution.getV()]; //discovery
        int low[] = new int[solution.getV()]; //lower node reachable
        int parent[] = new int[solution.getV()]; //parent in the implicit tree
        Arrays.fill(parent, -1);

        int start = searchArbitraryStartingPoint(solution);
        recursiveAux(start, solution, 0, visited, low, disc, parent, points);

        return points;
    }

    private int recursiveAux(int u, S solution, int iteration, boolean[] visit, int[] low, int[] disc, int[] parent, Set<Integer> setAP) {
        iteration++;
        visit[u] = true;
        disc[u] = iteration;
        low[u] = iteration;
        int children = 0;
        for (Integer v : solution.getEdges(u)) {
            if (!solution.isInSolution(v))
                continue; //ignore non-labeled
            if (!visit[v]) {
                parent[v] = u;
                iteration = recursiveAux(v, solution, iteration, visit, low, disc, parent, setAP);
                children++;
                if (low[v] >= disc[u] && parent[u] != -1)
                    setAP.add(u);
                //When going down the tree, we go down as much as we can...
                low[u] = Math.min(low[u], low[v]);
            } else if (parent[u] != v) {
                //When going up (through an edge not in the tree)
                //we only go up one edge
                low[u] = Math.min(low[u], disc[v]);
            }
        }
        if (parent[u] == -1 && children > 1)
            setAP.add(u);
        return iteration;

    }

    /**
     * Returns an arbitrary labelled node
     * @param solution RLP solution
     * @return the first labelled node found
     */
    private int searchArbitraryStartingPoint(S solution) {
        for (int i = 0; i < solution.getV(); i++) {
            if (solution.isInSolution(i)) {
                return i;
            }
        }
        return -1;
    }

}