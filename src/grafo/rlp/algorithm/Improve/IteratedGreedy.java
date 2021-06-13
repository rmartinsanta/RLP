package grafo.rlp.algorithm.Improve;

import grafo.rlp.algorithm.other.RLPArticulationPoints;
import grafo.rlp.data.*;
import grafo.rlp.interfaces.RLPReconstructive;
import grafo.rlp.interfaces.RLPSolution;
import grafo.rlp.interfaces.Shake;
import jdlib.tools.RandomManager;

import java.util.*;

public class IteratedGreedy implements Shake<RLPInstance, RLPSolution> {
    private final RLPReconstructive reconstructive;
    Random rng;
    /* algorithm parameters */
    private final float parameter_beta;

    private long iterationLimit;

    public IteratedGreedy(long iterations, float beta, RLPReconstructive reconstructive) {
        this.iterationLimit = iterations;
        this.parameter_beta = checkBeta(beta);
        this.reconstructive = reconstructive;
    }

    private float checkBeta(float beta) {
        boolean valid = beta >= 0.0f && beta <= 1.0f;
        if (!valid)
            throw new RuntimeException(String.format("The alpha parameter is not valid. (beta=%.2f)", beta));
        return beta;
    }

    @Override
    public RLPSolution pertubate(RLPSolution solution) {
        this.rng = RandomManager.getRandom();
        RLPSolution bestSolution = solution;
//        System.out.println("\tIterated Greedy, start=" + solution.getObjectiveFunction());
        for (int i = 1; i <= iterationLimit; i++) {
            /* perturbate */
            solution = new Solution(bestSolution);
            Set<Integer> removed = destroySolution(solution, parameter_beta);
            List<Integer> selected = solution.getRegeneratorAsList();
            solution = reconstructive.reconstructSolution(solution.getInstance(), selected, removed);

            /* post process solution: remove unnecesary regenerators */
            int start = solution.getObjectiveFunction();
            Purge1.purge(solution);
            int end = solution.getObjectiveFunction();

            if (solution.getObjectiveFunction() < bestSolution.getObjectiveFunction()) {
//                System.out.printf("\t\tSub-iteration (%d): Reconstruida: %d, Mejorada: %d (Eliminados: %d)\n", i, start, end, (start - end));
                bestSolution = solution;
                i = 0; //it will be incremented to 1 in the next iteration
            }
        }


        return bestSolution;
    }


    private Set<Integer> destroySolution(RLPSolution solution, float beta) {
        RLPArticulationPoints<RLPSolution> searchAP = new RLPArticulationPoints<>();
        Set<Integer> removed = new HashSet<>();

        //number of regenerators to remove
        int nToRemove = (int) Math.ceil(solution.getObjectiveFunction() * beta);
        assert (solution.getObjectiveFunction() > 1);
        assert (solution.getObjectiveFunction() > nToRemove);
        if (nToRemove >= solution.getObjectiveFunction())
            return removed;

        //remove n regenerators radomly (except for articulations points)
        for (int i = 0; i < nToRemove; i++) {
            Set<Integer> articulationPoints = searchAP.findArticulationPoints(solution);
            List<Integer> candidateList = solution.getRegeneratorAsList();
            if (candidateList.size() - articulationPoints.size() <= 0)
                break; //imposible to remove anymore regenerators
            int selectedCandidate = selectCandidateForRemoval(candidateList, articulationPoints);
            solution.removeRegenerator(selectedCandidate);
            removed.add(selectedCandidate);
        }

        return removed;
    }

    private int selectCandidateForRemoval(List<Integer> candidateList, Set<Integer> articulationPoints) {
        int randomlySelected = rng.nextInt(candidateList.size() - articulationPoints.size());

        int selected = -1;
        int i = 0;
        for (Integer candidate : candidateList) {
            if (articulationPoints.contains(candidate))
                continue; //ignore AP (do not increase counter)
            if (i == randomlySelected) {
                selected = candidate;
                break;
            }
            i++;

        }
        return selected;
    }

    @Override
    public String toString() {
        return String.format("IG(%dit, RandomDestructive(beta=%.2f), %s)", iterationLimit, parameter_beta, reconstructive.toString());
    }
}
