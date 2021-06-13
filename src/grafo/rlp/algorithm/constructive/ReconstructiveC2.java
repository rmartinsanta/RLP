package grafo.rlp.algorithm.constructive;

import grafo.rlp.data.Candidate;
import grafo.rlp.data.RLPInstance;
import grafo.rlp.data.Solution;
import grafo.rlp.interfaces.Constructive;
import grafo.rlp.interfaces.RLPReconstructive;
import grafo.rlp.interfaces.RLPSolution;
import jdlib.tools.RandomManager;

import java.util.*;

/**
 * Algoritmo Greedy. (Note: This class was 'forked' from ConstructiveC2 to allow the
 * reconstruction of partial solutions with excluded regenerators)
 *
 * <ul>
 * <li>Initial node: Selected randomly.</li>
 * <li>Candidates: Non-labeled nodes adjacent to any labeled node </li>
 * <li>Score: Non-labeled adjacents</li>
 * <li>Selection: at random from the restricted candidate liste</li>
 * </ul>
 *
 * @author juandavid
 */
public class ReconstructiveC2 implements Constructive<RLPInstance, RLPSolution>, RLPReconstructive {
    /* Algorithm parameters */
    private final float parameter_alpha; //may be NaN or [0.0f, 1.0f]
    private final float deterioration_factor;

    /* algorithm data */
    private Random random;
    private float alpha; //actual alpha used [0.0f, 1.0f]
    private Candidate[] candidateByIndex;
    private ArrayList<Candidate> candidateList;
    private boolean[] connected; //connected vertex (either a regenerator or adjacent to one)
    int connectedNodes;
    private Solution solution;

    public ReconstructiveC2(float alpha, float deterioration_factor) {
        this.deterioration_factor = deterioration_factor;
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
        this.connectedNodes = 0;

        //Random initialization:
        //we put a single node in the candidate list
        int initialNode = random.nextInt(instance.getV());
        candidateList.add(candidateByIndex[initialNode]);

        while (!isFeasible(solution)) {
            int nextNode = selectRandomCandidateRCL();
            solution.putRegenerator(nextNode);
            updateCandidateList(nextNode, candidateByIndex, candidateList, new HashSet<>());
        }
//        System.out.println("Candidate List (solucion): " + candidateList.toString());
        return solution;
    }

    @Override
    public RLPSolution reconstructSolution(RLPInstance instance, List<Integer> selected, Set<Integer> excludedCandidates) {
        random = RandomManager.getRandom();
        //Use a random alpha each time when the parameter is NaN
        this.alpha = Float.isNaN(parameter_alpha) ? random.nextFloat() : parameter_alpha;

        //initialize data structures
        solution = new Solution(instance);
        connected = new boolean[solution.getV()];
        candidateByIndex = initializeCandidatesScore(solution);
        candidateList = new ArrayList<>(solution.getV());

        reconstructPartialSolution(selected, excludedCandidates);

        //Reconstruction first part (ignore excluded regenerators)
        int newAdded = 0;
        int addedLimit = (int) Math.ceil(excludedCandidates.size()*deterioration_factor);
        while (!isFeasible(solution) && !candidateList.isEmpty() && newAdded < addedLimit) {
            int nextNode = selectRandomCandidateRCL();
//            System.out.println("  Remaining candidates: " + candidateList);
//            System.out.println("  Seleccionado: " + nextNode);
//            if(candidateByIndex[nextNode].score==0)
//                System.out.println("  Seleccionado: " + nextNode + ", Score: " + candidateByIndex[nextNode].score );
            solution.putRegenerator(nextNode);
            newAdded++;
            updateCandidateList(nextNode, candidateByIndex, candidateList, excludedCandidates);
        }

        //Reconstruction second part: included all candidates
        if(!isFeasible(solution))
            candidateList = reconstructCandidateList(new HashSet<>());
        while (!isFeasible(solution)) {
            int nextNode = selectRandomCandidateRCL();
//            System.out.println("  Remaining candidates: " + candidateList);
//            System.out.println("  Seleccionado: " + nextNode);
            solution.putRegenerator(nextNode);
            updateCandidateList(nextNode, candidateByIndex, candidateList, Collections.EMPTY_SET);
        }

//        System.out.println("Reconstruida : " + solution.getRegeneratorAsList());
        return solution;
    }

    private void reconstructPartialSolution(List<Integer> selected, Set<Integer> excludedCandidates) {
        //First we label all selected as already connected.
        //This prevent them to be included in the candidate list
        //since when we reconstruct we dont take/remove selected
        //from the candidate list
        connectedNodes = 0;
        Set<Integer> excluded = new HashSet<>(excludedCandidates);
        excluded.addAll(selected);

        //Included selected in the partial solution and
        //update candidate list and scores
        for (Integer s : selected) {
            solution.putRegenerator(s);
            updateCandidateList(s, candidateByIndex, candidateList, excluded);
        }
    }

    private ArrayList<Candidate> reconstructCandidateList(Set<Integer> excluded) {
        ArrayList<Candidate> newCandidateList = new ArrayList<>();
        for (int i = 0; i < solution.getV(); i++) {
            if (connected[i] && !solution.isInSolution(i) && !excluded.contains(i))
                newCandidateList.add(candidateByIndex[i]);
        }

        return newCandidateList;
    }


    private void updateCandidateList(int nextNode, Candidate[] arrayCandidates, ArrayList<Candidate> listCandidates, Set<Integer> excluded) {
        boolean unlabeledRegenerator = !connected[nextNode];
        //Initializes the score for a regenerator not marked previously as connected
        //This happens for the first regenerator when generating a solution from scratch
        //or when reconstructing a partial solution
        if(unlabeledRegenerator){
            connected[nextNode]=true;
            connectedNodes++;
            for(Integer adj: solution.getEdges(nextNode)){
                arrayCandidates[adj].score--;
            }
        }

        for (Integer covered :  solution.getEdges(nextNode)) {
            if (!connected[covered]) {
                connected[covered] = true;
                connectedNodes++;
                if(!excluded.contains(covered))
                    listCandidates.add(arrayCandidates[covered]);
                //Decrease scores for adjacents to this candidate
                for(Integer adj: solution.getEdges(covered)){
                    arrayCandidates[adj].score--;
                }
            }

        }
    }


    private Candidate[] initializeCandidatesScore(Solution solution) {
        Candidate arrayCandidates[] = new Candidate[solution.getV()];
        for (int i = 0; i < arrayCandidates.length; i++) {
            Candidate c = new Candidate(i, solution.getEdges(i).size());
            arrayCandidates[i] = c;
        }
        return arrayCandidates;
    }



    private int selectRandomCandidateRCL() {
        //precondition: candidateList is sorted (min score first)
        candidateList.sort(null);
        //calculate threshold (alpha = 0 -> greedy; alpha = 1 -> random)
        int min = Math.max(candidateList.get(0).score, 0);
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
        candidateList.remove(candidateList.size() - 1);

        return nextNode;
    }

    private boolean isFeasible(RLPSolution solution) {
        assert(connectedNodes > 0 && connectedNodes <= solution.getV());
        return connectedNodes == solution.getV();
    }

    @Override
    public String toString() {
        String param = Float.isNaN(parameter_alpha) ? "RND" : String.format("%.2f", parameter_alpha);
        return String.format("RC2(alpha=%s, delta=%.2f)", param, deterioration_factor);
    }


    public Candidate[] getCandidatesScore(){
        Candidate[] copy = new Candidate[candidateByIndex.length];
        Arrays.stream(candidateByIndex).forEach(oldCandidate ->copy[oldCandidate.node] = new Candidate(oldCandidate));
        return copy;
    }




}
