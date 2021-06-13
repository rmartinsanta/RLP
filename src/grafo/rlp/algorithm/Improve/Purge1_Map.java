package grafo.rlp.algorithm.Improve;

import grafo.rlp.algorithm.other.RLPArticulationPoints;
import grafo.rlp.interfaces.RLPSolution;
import jdlib.tools.RandomManager;

import java.util.*;

/**
 * <p>This is improvement method detects unnecessary regenerators and eliminates them
 * from the solution.
 * </p>
 *
 * <p>A regenerator can be removed while mantaining a feasible solution if (1) it's not
 * an articulation point and (2) it's not the only regenerator of any of its adjacent
 * non-regenerator.
 * </p>
 *
 * <p>This class is a map-based implementation of the algorithm used in Purge1. It's
 * deprecated since there is no apparent improvement in computing time and this
 * implementation is more complex and less maintainable.
 * </p>
 * @author juandavid
 */

@Deprecated
public class Purge1_Map {

    //We use two maps to keep in check the coverage of each regenerator
    //parentsMap: node -> set with the regenerators that cover it
    //dependentChild: regenerator -> set with the nodes only this one regenerator covers
    //There is no significant decrease in computing time


    public static void purge(RLPSolution solution) { //O(v^3)
        RLPArticulationPoints<RLPSolution> algorithmAP = new RLPArticulationPoints<>();

        //All Articulations points are required
        Set<Integer> articulationPoints = algorithmAP.findArticulationPoints(solution);

        //Parents for each node (regenerators included)
        //Map::Node -> Its adjacent regenerators
        Map<Integer, Set<Integer>> parents = getParentsMap(solution);

        //Dependent childs...
        //Map::Node -> adjacent whose only regenerator is the current node
        Map<Integer, Set<Integer>> dependentChild = getDependentChildrenMap(solution, parents);

        //Regenerators
        List<Integer> regenerators = solution.getRegeneratorAsList();
        Collections.shuffle(regenerators, RandomManager.getRandom());

        for (Integer r : regenerators) {
            if (articulationPoints.contains(r)) {
                continue;
            }

            if (dependentChild.get(r).isEmpty()) {
                boolean isInternal = parents.get(r).size() > 1;
                solution.removeRegenerator(r);

                //update maps
                updateRelations(solution, r, parents, dependentChild);
                //We only recalculate the articulation points
                //when the removed regenerator is an internal node
                //This happens when its connected to more than one regenerator
                if (isInternal)
                    articulationPoints = algorithmAP.findArticulationPoints(solution);
            }
        }
    }

    private static Map<Integer, Set<Integer>> getDependentChildrenMap(RLPSolution solution, Map<Integer, Set<Integer>> parents) {
        Map<Integer, Set<Integer>> dependentChild = new HashMap<>();
        for (int i = 0; i < solution.getV(); i++) {
            dependentChild.put(i, new HashSet<>());
        }
        for (int i = 0; i < solution.getV(); i++) {
            if (parents.get(i).size() == 1 && !solution.isInSolution(i)) { //&& !solution.isInSolution(i)
                final int child = i;
                //actually just one, but we cant pop a single element from a set.
                parents.get(i).forEach(r -> dependentChild.get(r).add(child));
//                for (Integer p : parents.get(i)) {
//                    dependentChild.get(p).add(i);
//                }
            }
        }
        return dependentChild;
    }

    private static Map<Integer, Set<Integer>> getParentsMap(RLPSolution solution) {
        Map<Integer, Set<Integer>> parents = new HashMap<>();
        for (int i = 0; i < solution.getV(); i++) {
            parents.put(i, new HashSet<>());
        }
        for (int parent = 0; parent < solution.getV(); parent++) {
            if (solution.isInSolution(parent))
                for (Integer adj : solution.getEdges(parent)) {
                    parents.get(adj).add(parent);
                }
        }
        return parents;
    }

    private static void updateRelations(RLPSolution solution, Integer removedRegenerator, Map<Integer, Set<Integer>> parents, Map<Integer, Set<Integer>> dependentChild) {
        //The removed regenerator may turn into a dependent children itself
        if(parents.get(removedRegenerator).size() == 1)
            parents.get(removedRegenerator).forEach(r -> dependentChild.get(r).add(removedRegenerator));

        for (Integer adj : solution.getEdges(removedRegenerator)) {
            parents.get(adj).remove(removedRegenerator);
            if (parents.get(adj).size() == 1 && !solution.isInSolution(adj)) {//&& !solution.isInSolution(adj)
                final int child = adj;
                //actually we only have one r, but we cant pop an element from a set.
                parents.get(adj).forEach(r -> dependentChild.get(r).add(child));
//                for (Integer r : parents.get(adj)) {
//                    dependentChild.get(r).add(adj);
//
//                }
            }
        }
    }

    @Override
    public String toString(){
        return "P1MAP";
    }
}
