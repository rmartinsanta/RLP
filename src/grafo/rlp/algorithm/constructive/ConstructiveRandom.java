package grafo.rlp.algorithm.constructive;

import java.util.*;

import grafo.rlp.data.RLPInstance;
import grafo.rlp.data.Solution;
import grafo.rlp.interfaces.Constructive;
import grafo.rlp.interfaces.RLPSolution;
import jdlib.tools.RandomManager;

/**
 * Random constructive.
 * <ul>
 * <li>Initial node: selected randomly</li>
 * <li>Candidate list: Non-labeled nodes adjacent to any labeled node (regenerator)</li>
 * <li>Next Candidate: selected randomly from candidate list</li>
 * </ul>
 * 
 * @author juandavid
 *
 */
public class ConstructiveRandom implements Constructive<RLPInstance, RLPSolution> {
	Random random;
	private ArrayList<Integer> candidateList;
	private boolean[] visited;
	private Solution solution;

	@Override
	public RLPSolution constructSolution(RLPInstance instance) {
		random = RandomManager.getRandom();
		solution = new Solution(instance);
		visited = new boolean[solution.getV()];
		candidateList = new ArrayList<>(solution.getV());
		//Random initialization
		int initialNode = random.nextInt(instance.getV());
		candidateList.add(initialNode);
		while(!isFeasible(solution)) {
			int nextNode = selectRandomCandidate(candidateList);
			visited[nextNode] = true;
			solution.putRegenerator(nextNode);
			updateCandidateList(nextNode);
		}
		return solution;
	}

	private void updateCandidateList(int nextNode) {
		Iterable<Integer> adjacencyList = solution.getEdges(nextNode);
		for (Integer adjacent : adjacencyList) {
			if(!visited[adjacent]) {
				visited[adjacent]=true;
				candidateList.add(adjacent);
			}
		}
	}

	private int selectRandomCandidate(List<Integer> candidateList) {
		int nextIndex = random.nextInt(candidateList.size());
		int nextNode = candidateList.get(nextIndex);
		int lastIndex = candidateList.size()-1;
		//overwrite the selected candidate with the last one
		candidateList.set(nextIndex, candidateList.get(lastIndex));
		//efficiently remove last position
		candidateList.remove(lastIndex);
		return nextNode;
	}

	private boolean isFeasible(RLPSolution solution) {
		assert(solution.getObjectiveFunction() + candidateList.size() <= solution.getV());
		return solution.getObjectiveFunction() + candidateList.size() == solution.getV();
	}

	@Override
	public String toString() {
	    return "CR";
    }




}
