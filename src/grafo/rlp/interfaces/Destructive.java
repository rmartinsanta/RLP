package grafo.rlp.interfaces;
import jdlib.data.interfaces.Solution;

/**
 * Represents a destructive method for a given problem.
 * @param <S> the type of the solution
 */
public interface Destructive<S extends Solution> {

    /**
     * Partially destroys the given solution. The returned
     * solution may need to be repaired.
     * @param s solution to be partially destroyed.
     * @return the same solution but partially destroyed.
     */
    public S destructSolution(Solution s);
}
