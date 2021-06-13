package grafo.rlp.algorithm;

import java.io.PrintStream;
import java.io.PrintWriter;

import grafo.rlp.algorithm.Improve.LocalSearch_AddOneRemoveMany;
import grafo.rlp.data.RLPInstance;
import grafo.rlp.data.Solution;
import grafo.rlp.interfaces.*;
import jdlib.tools.Stopwatch;

public class MultiStartIteratedGreedy implements Algorithm<RLPInstance, RLPSolution> {

    // Parameters
    private long iterationLimit; //maximum number of iterations
    Constructive<RLPInstance, RLPSolution> constructive;
    Shake<RLPInstance, RLPSolution> iteratedGreedy;

    private String header = "# Instance;OF;iterations; time(ms);solution;found(ms)";
    private String outputFormat = "%s;%d;%d;%d;%s;%d";

    public MultiStartIteratedGreedy(long multiStartIterations, Constructive<RLPInstance, RLPSolution> constructive, Shake<RLPInstance, RLPSolution> iteratedGreedy) {
        this.iterationLimit = multiStartIterations;
        this.constructive = constructive;
        this.iteratedGreedy = iteratedGreedy;
    }


    @Override
    public void printHeader(PrintStream out) {
        out.println(header);

    }

    @Override
    public RLPSolution execute(RLPInstance instance) {
        LocalSearch<RLPSolution> ls = null;
        ls = new LocalSearch_AddOneRemoveMany();
//        ls = new LocalSearch_AddTwoRemoveMany();


        Stopwatch.clear(); //reset time
        long time_to_best = -1;
        int iteration = 0;
        RLPSolution bestSolution = null;
        do {
            iteration++;

            /* Construct initial solution for this iteration */
            Stopwatch.start();
            RLPSolution solution = constructive.constructSolution(instance);
            Stopwatch.stop();
//            System.out.printf("Iteration (%d): start=%d, (%s)\n",iteration, solution.getObjectiveFunction(), solution);


            /* Iterated Greedy*/
            Stopwatch.start();
            solution = iteratedGreedy.pertubate(solution);
            Stopwatch.stop();

            /* Loocal Search *
            int initial = solution.getObjectiveFunction();
            Stopwatch.start();
            int oldCost;
            do {
                oldCost = solution.getObjectiveFunction();
                ls.search(solution);
//                if(solution.getObjectiveFunction() < oldCost)
//                    System.out.printf("Improvement found: %d -> %d\n", oldCost, solution.getObjectiveFunction());
            } while (solution.getObjectiveFunction() < oldCost);
            Stopwatch.stop();
            int last = solution.getObjectiveFunction();
//            System.out.printf("Local Search (iteration=%3d): %d -> $d\n",iteration, initial, last);
            /* */

            if (bestSolution == null || solution.getObjectiveFunction() < bestSolution.getObjectiveFunction()) {
                bestSolution = new Solution(solution);
                time_to_best = Stopwatch.getTotalMiliTime();
//                System.out.printf("Best Improved: cost=%d (%s)\n", solution.getObjectiveFunction(), solution);
//                if(!bestSolution.validate())
//                    System.err.printf("Solucion invalida (%d): %s\n", iteration, bestSolution);
            }

        } while (iteration < iterationLimit
                && !Stopwatch.hasTimelimitExceeded());
        //report best solution
        /* Local Search *
        RLPSolution solution = bestSolution;
        int initial = solution.getObjectiveFunction();
        Stopwatch.start();
        int oldCost;
//            if(ls!=null)
        do {
            oldCost = solution.getObjectiveFunction();
            ls.search(solution);
//                if(solution.getObjectiveFunction() < oldCost)
//                    System.out.printf("Improvement found: %d -> %d\n", oldCost, solution.getObjectiveFunction());
        } while (solution.getObjectiveFunction() < oldCost);
        Stopwatch.stop();
        int last = solution.getObjectiveFunction();
//            System.out.printf("Local Search (iteration=%3d): %d -> $d\n",iteration, initial, last);
        /* */


        System.out.println(String.format(outputFormat, instance.getName(), bestSolution.getObjectiveFunction(), iteration, Stopwatch.getTotalMiliTime(), bestSolution.getRegeneratorAsList().toString(), time_to_best));
        return bestSolution;
    }

    @Override
    public Algorithm setOutput(PrintWriter output) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Algorithm setReport(ReportType reportType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        return String.format("MSIG( %dit, %s, %s )", iterationLimit, constructive.toString(), iteratedGreedy.toString());
    }

}
