package grafo.rlp.algorithm.constructive;

import java.util.*;

import grafo.rlp.data.Candidate;
import grafo.rlp.data.RLPInstance;
import grafo.rlp.data.Solution;
import grafo.rlp.interfaces.Constructive;
import grafo.rlp.interfaces.RLPSolution;
import jdlib.tools.RandomManager;

/**
 * Greedy algorithm.
 *
 * <ul>
 * <li>Initial node: Selected randomly.</li>
 * <li>Candidates: Non-labeled nodes adjacent to any labeled node </li>
 * <li>Score: Non-labeled adjacents</li>
 * <li>Selection: at random from the restricted candidate list</li>
 * </ul>
 *
 * @author juandavid
 */
public class ConstructiveC1 implements Constructive<RLPInstance, RLPSolution> {

    /* Algorithm parameters */
    private final float parameter_alpha; //may be NaN or [0.0f, 1.0f]

    /* algorithm data */
    private Random random;
    private float alpha; //actual alpha used [0.0f, 1.0f]
    private Candidate[] candidateByIndex;
    private ArrayList<Candidate> candidateList;
    private boolean[] connected; //connected vertex (either a regenerator or adjacent to one)
    private Solution solution;

    public ConstructiveC1(float alpha) {
        if (Float.isNaN(alpha) || (alpha >= 0.0f && alpha <= 1.0f)) {
            this.parameter_alpha = alpha;
        } else {
            throw new RuntimeException(String.format("Invalid alpha for ContructiveGRASP (alpha=%.2f).", alpha));
        }
    }

    @Override
    public RLPSolution constructSolution(RLPInstance instance) {
        random = RandomManager.getRandom();
        //Use a random alpha each time when the parameter is NaN
        this.alpha = Float.isNaN(parameter_alpha) ? random.nextFloat() : parameter_alpha;

        //initialize data structures
        solution = new Solution(instance);
        connected = new boolean[solution.getV()];
        candidateByIndex = initializeCandidatesScore(solution);
        candidateList = new ArrayList<>(solution.getV());

        //Random initialization:
        //we put a single node in the candidate list
        int initialNode = random.nextInt(instance.getV());
        candidateList.add(candidateByIndex[initialNode]);
        connected[initialNode] = true;

        while (!isFeasible(solution)) {
            int nextNode = selectRandomCandidateRCL();
            solution.putRegenerator(nextNode);
            updateCandidateList(nextNode, candidateByIndex, candidateList);
        }
//        System.out.println("Candidate List (solucion): " + candidateList.toString());
        return solution;
    }


    private Candidate[] initializeCandidatesScore(Solution solution) {
        Candidate arrayCandidates[] = new Candidate[solution.getV()];
        for (int i = 0; i < arrayCandidates.length; i++) {
            Candidate c = new Candidate(i, solution.getEdges(i).size());
            arrayCandidates[i] = c;
        }
        return arrayCandidates;
    }

    private void updateCandidateList(int nextNode, Candidate[] arrayCandidates, ArrayList<Candidate> listCandidates) {
        for (Integer adjacent :  solution.getEdges(nextNode)) {
            if (!connected[adjacent]) {
                listCandidates.add(arrayCandidates[adjacent]);
                connected[adjacent] = true;
            }
            arrayCandidates[adjacent].score--;
        }
    }

    private int selectRandomCandidateRCL() {
        //precondition: candidateList is sorted (min score first)
        candidateList.sort(null);
        //calculate threshold (alpha = 0 -> greedy; alpha = 1 -> random)
        int min = Math.max(candidateList.get(0).score, 1);
        int max = candidateList.get(candidateList.size() - 1).score;
        int threshold = max - (int) (alpha * (max - min));
        //restricted candidates interval [startIndex, size()-1]
        // binarySearch returns the position where it should go if not found
        int startIndex = Collections.binarySearch(candidateList, new Candidate(-1, threshold));
        startIndex = -(startIndex + 1);

        //select random candidates
        int rnd = random.nextInt(candidateList.size() - startIndex);
        int nextIndex = startIndex + rnd;
        int nextNode = candidateList.get(nextIndex).node;
        //List<Candidate> test = candidateList.subList(startIndex, candidateList.size() - 1);

        //efficiently remove the selected candidate:
        //overwrite the connected candidate with the last
        candidateList.set(nextIndex, candidateList.get(candidateList.size() - 1));
        //efficiently remove last position
        candidateList.remove(candidateList.size()-1);

        return nextNode;
    }

    private boolean isFeasible(RLPSolution solution) {
        assert (solution.getObjectiveFunction() + candidateList.size() <= solution.getV());
        return solution.getObjectiveFunction() + candidateList.size() == solution.getV();
    }

    @Override
    public String toString(){
        String param = Float.isNaN(parameter_alpha)? "RND": String.format("%.2f", parameter_alpha);
        return String.format("C1(alpha=%s)", param);
    }

    public Candidate[] getCandidatesScore(){
        Candidate[] copy = new Candidate[candidateByIndex.length];
        Arrays.stream(candidateByIndex).forEach(oldCandidate ->copy[oldCandidate.node] = new Candidate(oldCandidate));
        return copy;
    }


}
