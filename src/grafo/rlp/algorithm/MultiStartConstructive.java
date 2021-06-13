package grafo.rlp.algorithm;

import grafo.rlp.algorithm.Improve.LocalSearch_AddOneRemoveMany;
import grafo.rlp.algorithm.Improve.Purge1;

import grafo.rlp.data.RLPInstance;
import grafo.rlp.data.Solution;
import grafo.rlp.interfaces.Algorithm;
import grafo.rlp.interfaces.Constructive;
import grafo.rlp.interfaces.LocalSearch;
import grafo.rlp.interfaces.RLPSolution;
import jdlib.tools.RandomManager;
import jdlib.tools.Stopwatch;

import java.io.PrintStream;
import java.io.PrintWriter;

public class MultiStartConstructive implements Algorithm<RLPInstance, RLPSolution> {

    /* parameters */
    private long iterationLimit;
    private Constructive<RLPInstance, RLPSolution> constructive;
    private LocalSearch<RLPSolution> ls;
    private boolean shouldCleanConstruction;
    private boolean doLocalSearch;

    /* other data*/
    private PrintWriter consoleOutput;
    private PrintWriter output;
    private String header = "# Instance;OF;iterations; time(ms);solution";
    private String outputFormat = "%s;%d;%d;%d;%s";

    public MultiStartConstructive(Constructive<RLPInstance, RLPSolution> constructive, long multiStartIterations, boolean shouldCleanConstruction, boolean doLocalSearch) {
        this.constructive = constructive;
        this.iterationLimit = multiStartIterations;
        this.shouldCleanConstruction = shouldCleanConstruction;
        this.doLocalSearch = doLocalSearch;
        this.ls = new LocalSearch_AddOneRemoveMany();

        this.consoleOutput = new PrintWriter(System.out);
        this.output = consoleOutput; //by default its the same
    }

    public MultiStartConstructive(Constructive<RLPInstance, RLPSolution> constructive, long multiStartIterations) {
        this(constructive, multiStartIterations, false, false);
    }


    @Override
    public void printHeader(PrintStream out) {
        out.println(header);
    }

    @Override
    public RLPSolution execute(RLPInstance instance) {
        Stopwatch.clear(); //reset time
        RandomManager.setSeed(0); //default seed=0

//        System.out.println("Instancia: "+ instance.getName());
        int iteration = 0;
        RLPSolution bestSolution = null;
        do {
            iteration++;

            //Construct initial solution for this iteration
            Stopwatch.start();
            RLPSolution solution = constructive.constructSolution(instance);
            Stopwatch.stop();

            /* post-optimization stage */
            if (shouldCleanConstruction) {
                Stopwatch.start();
                Purge1.purge(solution);
                Stopwatch.stop();
            } /* */

            /* Local Search */
            if (doLocalSearch){
                Stopwatch.start();
                int oldCost;
                do {
                    oldCost = solution.getObjectiveFunction();
                    ls.search(solution);
                } while (solution.getObjectiveFunction() < oldCost);
                Stopwatch.stop();
            } /* */
//            if(!solution.validate())
//                System.err.printf("Solucion invalida (%d): %s\n", iteration, solution);

            if (bestSolution == null || solution.getObjectiveFunction() < bestSolution.getObjectiveFunction()) {
                bestSolution = new Solution(solution);
//                System.out.printf("Best Improved: cost=%d (%s)\n", solution.getObjectiveFunction(), solution);
//                if(!bestSolution.validate())
//                    System.err.printf("Solucion invalida (%d): %s\n", iteration, bestSolution);
            }


        } while (iteration < iterationLimit
                && !Stopwatch.hasTimelimitExceeded());
        //report best solution
        output.println(String.format(outputFormat, instance.getName(), bestSolution.getObjectiveFunction(), iteration, Stopwatch.getTotalMiliTime(), bestSolution.getRegeneratorAsList().toString()));
        output.flush();
        return bestSolution;
    }

    @Override
    public Algorithm setOutput(PrintWriter output) {
        this.output = output;
        return this;
    }

    @Override
    public Algorithm setReport(ReportType reportType) {
        throw new UnsupportedOperationException("Not yet implemented. The report by default is \"summary\"");
    }

    @Override
    public String toString() {
        String postProcess = shouldCleanConstruction ? "+Purge" : "";
        String localSearch = doLocalSearch? ls.toString() : "";
        return iterationLimit + "i_x_" + constructive + postProcess + localSearch;
    }

}
