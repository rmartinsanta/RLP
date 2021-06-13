package grafo.rlp.interfaces;

import jdlib.data.interfaces.Instance;
import jdlib.data.interfaces.Solution;

/**
 * Represents a perturbation method that modifies a solution.
 * Depending on the implementation the returned solution may
 * not be a complete solution nor a feasible one.
 * @param <S> the type of the Solution
 */
public interface Shake<T extends Instance,S extends Solution<T>> {

    /**
     * Modifies the given solution applying a perturbation. The returned
     * solution may be unfeasible or incomplete depending on the implementation.
     * @param solution
     * @return
     */
    public S pertubate(S solution);
}

