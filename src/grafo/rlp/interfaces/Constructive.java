package grafo.rlp.interfaces;

import jdlib.data.interfaces.Instance;
import jdlib.data.interfaces.Solution;

/**
 * Represents a constructive method for a given problem.
 * @author jesussanchezoro
 *
 * @param <T> the type of the instance
 * @param <S> the type of the solution
 */
public interface Constructive<T extends Instance, S extends Solution<T>> {

	/**
	 * Constructs a new solution using the given instance
	 * @param instance the instance to be used in the construction
	 * @return the constructed solution
	 */
	S constructSolution(T instance);

}
