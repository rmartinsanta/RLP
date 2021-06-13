package grafo.rlp.algorithm.Improve;

import grafo.rlp.data.Solution;
import grafo.rlp.interfaces.LocalSearch;
import grafo.rlp.interfaces.RLPSolution;

import java.util.Set;

/**
 * Local Search that improves the solution by trying to add one regenerator
 * to the solution and then taking away all that could be removed thanks to
 * it. O(V*R^2) = O(V^4)
 *
 * <p>Neighborhood: Add one Regenerator and remove as many others as possible.</p>
 */
public class LocalSearch_AddOneRemoveMany implements LocalSearch<RLPSolution> {

    @Override
    public void search(RLPSolution solution) { //O(V*R^3) = O(V^4)
        Solution solBackup = new Solution(solution);
        for (int i = 0; i < solution.getV(); i++) {
            if (!solution.isInSolution(i)) {
                solution.putRegenerator(i);
                Purge1.purge(solution, Set.of(i));//we exclude i from the clean method
                if (solution.getObjectiveFunction() < solBackup.getObjectiveFunction())
                    return; //improvement found

                //restore solution when no improvement is found
                solution.copyOf(solBackup);
            }
        }
    }

    @Override
    public String toString() {
        return "LS_1xRM";
    }

}
