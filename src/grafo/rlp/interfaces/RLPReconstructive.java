package grafo.rlp.interfaces;

import grafo.rlp.data.RLPInstance;

import java.util.List;
import java.util.Set;

/**
 * Represents a reconstructive method for this problem
 *
 */
public interface RLPReconstructive extends Constructive<RLPInstance, RLPSolution> {


    /**
     * Reconstructs a partial solution for the RLP into a feasible one.
     * @param instance instance for the RLP
     * @param selected candidates part of the partial solution
     * @param excludedCandidates candidates excluded in the first stage of the reconstructive phase.
     * @return
     */
	RLPSolution reconstructSolution(RLPInstance instance, List<Integer> selected, Set<Integer> excludedCandidates);
}
