package grafo.rlp.algorithm.constructive;

import java.util.*;

import grafo.rlp.data.RLPInstance;
import grafo.rlp.data.Solution;
import grafo.rlp.interfaces.Constructive;
import grafo.rlp.interfaces.RLPSolution;
import jdlib.tools.RandomManager;
/**
 * Algoritmo Greedy.
 * 
 * <ul>
 * <li>Initial node: Selected <b></b>randomly</b>.</li>
 * <li>Candidates: Non-labeled nodes adjacent to any labeled node</li>
 * <li>Score: Non-labeled adjacents</li>
 * </ul>
 * @author juandavid
 *
 */
public class ConstructiveGreedy1 implements Constructive<RLPInstance, RLPSolution> {
	Random random;
	private Candidate[] directAccessCandidates;
	private ArrayList<Candidate> candidateList;
	private boolean[] visited;
	private Solution solution;

	
	@Override
	public RLPSolution constructSolution(RLPInstance instance) {
		random = RandomManager.getRandom();
		solution = new Solution(instance);
		visited = new boolean[solution.getV()];
		directAccessCandidates = new Candidate[solution.getV()];
		candidateList = new ArrayList<>(solution.getV());
		initializeCandidatesScore(directAccessCandidates);
		//Random initialization
		int initialNode = random.nextInt(instance.getV());
		candidateList.add(directAccessCandidates[initialNode]);
		while(!isFeasible(solution)) {
			int nextNode = selectBestCandidate(candidateList);
			visited[nextNode] = true;
			solution.putRegenerator(nextNode);
			updateCandidateList(nextNode, directAccessCandidates, candidateList);
		}
		return solution;
	}

	private void initializeCandidatesScore(Candidate[] arrayCandidates) {
		for(int i=0; i<arrayCandidates.length; i++) {
			Candidate c = new Candidate(i, solution.getEdges(i).size());
			arrayCandidates[i] = c;
		}
	}

	private void updateCandidateList(int nextNode, Candidate[] arrayCandidates, ArrayList<Candidate> listCandidates) {
		Iterable<Integer> adjacencyList = solution.getEdges(nextNode);
		for (Integer adjacent : adjacencyList) {
			if(!visited[adjacent]) {
				listCandidates.add(arrayCandidates[adjacent]);
				visited[adjacent]=true;
			}
			arrayCandidates[adjacent].score--;
		}
		listCandidates.sort(null);//Sort by natural order (score)
	}

	private int selectBestCandidate(List<Candidate> candidateList) {
		//best candidate is in last position
		int lastIndex = candidateList.size()-1;
		int bestIndex = candidateList.get(lastIndex).node;
		//overwrite the selected candidate with the last
		//candidateList.set(bestIndex, candidateList.get(candidateList.size()-1));
		//efficiently remove last position
		candidateList.remove(candidateList.size()-1);
		return bestIndex;
	}

	private boolean isFeasible(RLPSolution solution) {
		assert(solution.getObjectiveFunction() + candidateList.size() <= solution.getV());
		return solution.getObjectiveFunction() + candidateList.size() == solution.getV();
	}



	private static class Candidate implements Comparable<Candidate> {
		int node;
		int score;
		
		public Candidate(int node, int score) {
			this.node = node;
			this.score = score;
		}

		@Override
		public int compareTo(Candidate o) {
			assert(o != null);
			return Integer.compare(this.score, o.score);
		}
	}

    @Override
    public String toString() {
        return "G1";
    }



}
