package grafo.rlp.algorithm.Improve;

import grafo.rlp.algorithm.other.RLPArticulationPoints;
import grafo.rlp.interfaces.RLPSolution;
import jdlib.tools.RandomManager;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * <p>This is improvement method detects unnecessary labelled
 * nodes (regenerators) and removes them from the solution.
 * </p>
 *
 * <p>A regenerator <i>R</i> can be removed when:
 * <ol>
 * <li>R it's not an articulation point</li>
 * <li>R it's not the only regenerator that connects any of
 * its adjacent non-regenerator nodes</li>
 * </ol>
 * </p>
 *
 * <p><b>Note:</b> The solution obtained is dependent on the removal order.
 * The removal order is randomized.</p>
 *
 * @author juandavid
 * @version 2.0 (2019.11.26)
 */

public class Purge1 {

    public static void purge(RLPSolution solution) {
        purge(solution, Collections.EMPTY_SET);
    }

    /**
     * Removes unnecessary regenerators from an RLP solution while mantaining a feasible
     * solution. O(V*R^2) = O(V^3)
     *
     * <p>A regenerator <i>R</i> can be removed when:
     * <ol>
     * <li>R it's not an articulation point</li>
     * <li>R it's not the only regenerator that connects any of
     * its adjacent non-regenerator nodes</li>
     * </p>
     *
     * @param solution an RLP feasible solution
     * @param excluded excluded nodes from the removal procedure
     */
    public static void purge(RLPSolution solution, Set<Integer> excluded) { //O(v^3)
        int[] nonLabeledConnections = edgesToNonLabelledNodes(solution); //O(R*V) = O(V^2)
        RLPArticulationPoints<RLPSolution> algorithmAP = new RLPArticulationPoints<>();

        //Articulation points cant be removed
        Set<Integer> articulationPoints = algorithmAP.findArticulationPoints(solution);//O(R*V) = O(V^2)

        List<Integer> regenerators = solution.getRegeneratorAsList();
        Collections.shuffle(regenerators, RandomManager.getRandom());

        for (Integer r : regenerators) { //O(V*R^2) = O(V^3)

            if (excluded.contains(r) || articulationPoints.contains(r) || hasDependentNode(solution, r, nonLabeledConnections))
                continue;
            boolean isInternal = solution.getEdges(r).size() - nonLabeledConnections[r] > 1;
            solution.removeRegenerator(r);

            //update connections
            for (Integer adj : solution.getEdges(r)) {
                nonLabeledConnections[adj]++;
            }

            //We only recalculate the articulation points
            //when removing internal nodes
            if (isInternal)
                articulationPoints = algorithmAP.findArticulationPoints(solution);

        }
    }


    /**
     * Returns true if there is any unlabeled node whose only labelled adjacent is r itself.
     *
     * @param solution              an RLP feasible solution
     * @param r                     the labelled node who may have a dependent children
     * @param nonLabeledConnections an array with the number of connections to unlabelled nodes for each node
     * @return true if r has any dependent node
     */
    private static boolean hasDependentNode(RLPSolution solution, Integer r, int[] nonLabeledConnections) { //O(V)
        //Returns true if there is a node whose only adjacent regenerator is r
        for (Integer adj : solution.getEdges(r)) {
            if (!solution.isInSolution(adj) && solution.getEdges(adj).size() - nonLabeledConnections[adj] == 1)
                return true;
        }
        return false;
    }

    /**
     * Returns an array with the number of connections
     * to unlabelled nodes for each node. Complexity: O(V^2)
     *
     * @param solution an RLP feasible solution
     * @return array with the number of connections to unlabelled nodes
     */
    private static int[] edgesToNonLabelledNodes(RLPSolution solution) {
        int[] nonLabeledConnections = new int[solution.getV()];

        //connection = degree
        for (int node = 0; node < solution.getV(); node++) {
            nonLabeledConnections[node] = solution.getEdges(node).size();
        }

        //connections = degree - labelled
        for (int r = 0; r < solution.getV(); r++) {
            if (solution.isInSolution(r)) {
                for (Integer adj : solution.getEdges(r)) {
                    nonLabeledConnections[adj]--;
                }
            }
        }

        return nonLabeledConnections;
    }

    @Override
    public String toString() {
        return "P1";
    }
}
