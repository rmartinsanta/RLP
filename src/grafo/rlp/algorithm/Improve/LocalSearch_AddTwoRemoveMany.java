package grafo.rlp.algorithm.Improve;


import grafo.rlp.data.Solution;
import grafo.rlp.interfaces.LocalSearch;
import grafo.rlp.interfaces.RLPSolution;

import java.util.Set;


/**
 * Local Search that improves the solution by trying to add two regenerators
 * to the solution and then taking away all that could be removed thanks to
 * it. O(V*R^4) = O(V^5)
 *
 * <p>Neighborhood: Add two regenerators and remove as many others as possible.</p>
 */
public class LocalSearch_AddTwoRemoveMany implements LocalSearch<RLPSolution> {


    @Override
    public void search(RLPSolution solution) { //O(V*R^4) = O(V^5)
        Solution solBackup = new Solution(solution);
        for (int i = 0; i < solution.getV(); i++) {
            for (int j = i + 1; j < solution.getV(); j++) {

                if (!solution.isInSolution(i) && !solution.isInSolution(j)) {
                    solution.putRegenerator(i);
                    solution.putRegenerator(j);
                    Purge1.purge(solution, Set.of(i, j));//we exclude i and j from the clean method
                    if (solution.getObjectiveFunction() < solBackup.getObjectiveFunction()) {
                        return; //improvement found
                    }
                    //restore backup
                    solution.copyOf(solBackup);
                }
            }
        }
    }

}
