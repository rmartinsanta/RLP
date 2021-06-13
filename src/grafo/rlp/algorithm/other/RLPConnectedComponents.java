package grafo.rlp.algorithm.other;

import grafo.rlp.interfaces.RLPSolution;

import java.util.*;

/**
 * Algorithm to find connected components of labelled nodes (also known
 * as regenerators) in an solution instance for the RLP.
 *
 * @param <S> A solution class for the RLP
 * @author juandavid.quintana
 * @version 1.0 2019.11.26
 */
public class RLPConnectedComponents<S extends RLPSolution> {
    private int nVisited; //only used as sanity check when assertions are enabled

    /**
     * Find all connected components of labelled nodes (or installed regenerators).
     *
     * @param solution a solution for the RLP
     * @return a list with all connected components of labelled nodes
     */
    public List<Set<Integer>> findConnectedComponents(S solution) {
        nVisited = 0;
        List<Set<Integer>> components = new ArrayList<>();
        boolean visited[] = new boolean[solution.getV()];
        for (int i = 0; i < solution.getV(); i++) {
            if (!visited[i] && solution.isInSolution(i)) { //skip visited and unlabelled
                Set<Integer> bfs = recursiveDFS(i, visited, solution);
                components.add(bfs);
            }

        }
        assert (nVisited == solution.getObjectiveFunction());
        return components;
    }

    /**
     * Returns true if a RLPSolution has more than one connected component
     * of labelled nodes (or installed regenerators).
     *
     * @param solution an solution for the RLP
     * @return true if the instance has many components
     */
    public boolean hasManyComponents(S solution) {
        int nComponents = 0;
        boolean visited[] = new boolean[solution.getV()];
        for (int i = 0; i < solution.getV() && nComponents < 2; i++)
            if (!visited[i] && solution.isInSolution(i)) {
                iterativeDFS(i, visited, solution);
                nComponents++;
            }
        return nComponents>1;
    }

    /**
     * Returns a set with all the labeled nodes that can be reached from the
     * starting point. The graph traversal can only visit labelled nodes and
     * uses an depth first search implementation.
     *
     * @param start starting node
     * @param visited nodes that have been visited
     * @param solution an RLP solution
     * @return a set with all labelled nodes visited
     */
    private Set<Integer> recursiveDFS(int start, boolean[] visited, RLPSolution solution) {
        Set<Integer> dfs = new HashSet<>();
        auxDFS(start, visited, solution, dfs);
        return dfs;
    }

    /**
     * Auxiliar method for the recursiveDFS traversal of the graph and its labelled nodes.
     * @param node node we are currently visiting
     * @param visited nodes than have been visited
     * @param solution an RLP solution
     * @param dfs nodes visited in this recursiveDFS traversal of the solution
     */
    private void auxDFS(int node, boolean[] visited, RLPSolution solution, Set<Integer> dfs) {
        nVisited++;
        visited[node] = true;
        dfs.add(node);
        for (Integer adj : solution.getEdges(node))
            if (!visited[adj] && solution.isInSolution(adj)) {
                auxDFS(adj, visited, solution, dfs);
            }
    }

    /**
     * Iterative Depth-First Search Implementation that marks as visited all
     * labelled nodes reachable from the starting node.
     * @param start start node for the dfs traversal
     * @param visited nodes than have been visited
     * @param solution an RLP solution
     */
    private void iterativeDFS(int start, boolean[] visited, RLPSolution solution) {
        ArrayList<Integer> stack = new ArrayList<>();
        stack.add(start); //push
        while(!stack.isEmpty()){
            int node = stack.get(stack.size()-1); //pop
            visited[start]=true;
            for (Integer adjacentNode : solution.getEdges(node)) {
                if(!visited[adjacentNode] && solution.isInSolution(adjacentNode))
                    stack.add(adjacentNode); //push
            }
        }


    }

}