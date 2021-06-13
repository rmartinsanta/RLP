package grafo.rlp.interfaces;

import grafo.rlp.data.RLPInstance;
import jdlib.data.interfaces.Solution;

import java.util.List;

/**
 * <p>This is the interface of solutions for an instance in the RLP problem.
 *
 * <p>Declares all the necesary methods for a solution implementation to allow
 * the construction of a solution and it's manipulation/update.
 *
 * */
public interface RLPSolution extends Solution<RLPInstance> {

	/** Returns the name of this instance */
	String getName();
	/** The instance for this solution*/
	RLPInstance getInstance();
	/** Return the total number of nodes of the instance */
	int getV();
    /** This returns the edges/connections from this node to all other nodes. */
    List<Integer> getEdges(int node);
    /** True if an edge from node1 to node2 exist.*/
    boolean hasEdge(int node1, int node2);

	/** This method puts a regenerator in the specified node. */
	void putRegenerator(int regenerator);
	/** This method removes the specified regenerator from a candidate node. */
	void removeRegenerator(int regenerator);
	/**  Returns a list of the regenerators used in the solution.*/
	List<Integer> getRegeneratorAsList();
	/** Returns a boolean array with regenerators labeled as true */
	boolean[] getRegeneratorsAsArray();
	/** True if the node is part of the current solution */
	boolean isInSolution(int node);
	/** The number of regenerators used in the current solution */
	int getObjectiveFunction();

	/** Make this solution a copy of another one */
	void copyOf(RLPSolution otherSolution);
	/** The regenerators used in this solution as a string */
	String toString();

	/** Brute force check if the current solution is in a valid state. */
	boolean validate();

}
