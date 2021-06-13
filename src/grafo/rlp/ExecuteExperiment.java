package grafo.rlp;

import grafo.rlp.algorithm.Improve.IteratedGreedy;
import grafo.rlp.algorithm.*;
import grafo.rlp.algorithm.constructive.*;
import grafo.rlp.data.*;
import grafo.rlp.interfaces.*;
import jdlib.tools.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class for executing experiments and reporting results.
 * <p>
 * Use {{@link #executeIG()}} to execute our iterated greedy approach
 * over a set of instances with a given parameters.
 */
public class ExecuteExperiment {


    public static void main(String[] args) throws Exception {
        // single execution and print to System.out

//	    executeConstructive();
//        executeGRASP();
//        executeIG();

        /*final experiment */
        experimentIG();
        //multipleIG_Experiments();
    }


    /**
     * Executes only the constructive algorithm and prints the results
     * in the standard output.
     */
    private static void executeConstructive() {
        Constructive<RLPInstance, RLPSolution> constructive;
        RLPDataSetManager dataSet = new RLPDataSetManager();

        //Configure execution:
        // - Seed for RNG
        long seed = 0;
        // - Data set
        String dataset_name = RLPDataSetManager.training;
        // - Algorithm parameters
        int multiStartIterations = 100;
        float alpha = Float.NaN;
        boolean shouldCleanConstruction = true;
        // - GRASP only
        boolean doLocalSearch = false;

        // Constructive methods: uncomment one
//        constructive = new ConstructiveRandom();
//        constructive = new ConstructiveGreedy1();
//        constructive = new ConstructiveC1(alpha);
        constructive = new ConstructiveC2(alpha);

        /* Algorithm */
        MultiStartConstructive algorithm = new MultiStartConstructive(constructive, multiStartIterations, shouldCleanConstruction, doLocalSearch);


        Iterable<RLPInstance> instances = dataSet.getDataSet(dataset_name);
        System.out.printf("Experiment: %s\n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
        System.out.printf("Algorithm: %s\n", algorithm);
        System.out.printf("Random seed: %s\n", seed);
        System.out.printf("Instances: %s\n", dataset_name);
        algorithm.printHeader();
        for (RLPInstance instance : instances) {
            RandomManager.setSeed(0);
            algorithm.execute(instance);
        }

    }

    private static void executeGRASP() {
        Constructive<RLPInstance, RLPSolution> constructive;
        RLPDataSetManager dataSet = new RLPDataSetManager();

        /* Seed for RNG */
        long seed = 0;
        /* Instances */
        String dataset_name = RLPDataSetManager.large;
        /* parameters */
        int multiStartIterations = 100;
        float alpha = Float.NaN;
        /* GRASP Experiment only */
        boolean shouldCleanConstruction = true; //Purge is harcoded to Purge1
        boolean doLocalSearch = true; //LocalSearch is harcoded to LS_AddOne_RemoveMany

        /* Constructive methods: uncomment one*/
//        constructive = new ConstructiveRandom();
//        constructive = new ConstructiveGreedy1();
//        constructive = new ConstructiveC1(alpha);
        constructive = new ConstructiveC2(alpha);

        /* Algorithm */
        MultiStartConstructive algorithm = new MultiStartConstructive(constructive, multiStartIterations, shouldCleanConstruction, doLocalSearch);


        Iterable<RLPInstance> instances = dataSet.getDataSet(dataset_name);
        System.out.printf("Experiment: %s\n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
        System.out.printf("Algorithm: %s\n", algorithm);
        System.out.printf("Random seed: %s\n", seed);
        System.out.printf("Instances: %s\n", dataset_name);
        algorithm.printHeader();
        for (RLPInstance instance : instances) {
            RandomManager.setSeed(0);
            algorithm.execute(instance);
        }

    }

    private static void executeIG() {
        Constructive<RLPInstance, RLPSolution> constructive;
        RLPDataSetManager dataSet = new RLPDataSetManager();
        /* Seed for RNG */
        long seed = 0;
        /* parameters */
        //  Best parameters in the preliminary experiments
        //  msIter=100, igIter=50, alpha=0.25f, ig_alpha=0.25f, ig_beta=0.10f, ig_delta = 1.0f
        int multiStartIterations = 100;
        int igIterations = 50; //10, 25, 50
        String dataset_name = RLPDataSetManager.mirrorTraining;
        float alpha = 0f; //{}
        float ig_alpha = 0f;
        float ig_beta = 0.50f; //{10%,20%,30%,40%,50%}
        float ig_delta = 1.0f; //When destroying X regenerators, those wont be used unless other delta*X are deployed

        //Algorithm instantiation
        Shake<RLPInstance, RLPSolution> ig = new IteratedGreedy(igIterations, ig_beta, new ReconstructiveC2(ig_alpha, ig_delta));
        MultiStartIteratedGreedy algorithm = new MultiStartIteratedGreedy(multiStartIterations, new ConstructiveC2(alpha), ig);

        //Execute experiment
        Experiment experiment = new Experiment(dataset_name, algorithm);
        experiment.setInitialSeed(seed).run();


    }

    private static void experimentIG() {
        Constructive<RLPInstance, RLPSolution> constructive;
        RLPDataSetManager dataSet = new RLPDataSetManager();
        /* Seed for RNG */
        long seed = 0;
        /* parameters */
        //  Best parameters in the preliminary experiments
        //  msIter=100, igIter=50, alpha=0.00f, ig_alpha=0.00f, ig_beta=0.10f, ig_delta = 1.0f
        int multiStartIterations = 100;
        int igIterations = 50; //10, 25, 50
        String dataset_name = RLPDataSetManager.very_large;
        float alpha = 0f; //{}
        float ig_alpha = 0f;
        float ig_beta = 0.50f; //{10%,20%,30%,40%,50%}
        float ig_delta = 1.0f; //When destroying X regenerators, those wont be used unless other delta*X are deployed

        //Algorithm instantiation
        Constructive<RLPInstance, RLPSolution> c1 = new ConstructiveC1(alpha);
        Constructive<RLPInstance, RLPSolution> c2 = new ConstructiveC2(alpha);
        RLPReconstructive rc1 = new ReconstructiveC1(ig_alpha, ig_delta);
        RLPReconstructive rc2 = new ReconstructiveC2(ig_alpha, ig_delta);

        //algorithm composition
        Constructive<RLPInstance, RLPSolution> initial_constructive = c2;
        RLPReconstructive reconstructive = rc2;
        Shake<RLPInstance, RLPSolution> ig = new IteratedGreedy(igIterations, ig_beta, reconstructive);
        MultiStartIteratedGreedy algorithm = new MultiStartIteratedGreedy(multiStartIterations, initial_constructive, ig);

        //Execute experiment
        Experiment experiment = new Experiment(dataset_name, algorithm);
        experiment.setInitialSeed(seed).run();


    }


    /**
     * This method can queue and execute multiple experiments sequentially.
     *
     * <p>This will generate a folder with the current date and time for all
     * executed experiments. A single summary file will be written with all
     * aggregated data, and for each experiment a result file will also be
     * written.
     *
     * @throws FileNotFoundException when the necessary folders do not exists
     */
    private static void multipleIG_Experiments() throws FileNotFoundException {
        /* Seed for RNG */
        long seed = 0;
        int repetitions = 1;

        /* parameters */
        //  Best parameters in the preliminary experiments
        //  msIter=100, igIter=50, alpha=0.25f, ig_alpha=0.25f, ig_beta=0.10f, ig_delta = 1.0f

        String dataset_name = RLPDataSetManager.robustness;
        float ig_delta = 1.0f; //When destroying X regenerators, those wont be used unless other delta*X are deployed
        int multiStartIterations = 100;
        float alpha_c[] = {0.00f};
        float alpha_r[] = {0.00f};
        int iterations[] = {50};
        float beta[] = {0.50f};

        /* Dejar por la noche, todas las combinaciones para iter=100*
        String dataset_name = RLPDataSetManager.large;
        float ig_delta = 1.0f; //When destroying X regenerators, those wont be used unless other delta*X are deployed
        int multiStartIterations = 100;
        float alphas[] = {0.00f, 0.25f, Float.NaN};
        int iterations[] = {10, 25, 50};
        float beta[] = {0.10f, 0.20f, 0.50f};
        /* */

        String[] many_datasets = {RLPDataSetManager.small_random, RLPDataSetManager.large, RLPDataSetManager.very_large};
        List<Experiment> myExperiments = new ArrayList<>();
        for(String ds: many_datasets)
        for (float alpha : alpha_c)
        for (float ig_alpha : alpha_r)
        for (float ig_beta : beta)
        for (int igIterations : iterations) {
            //Algorithm instantiation
            MultiStartIteratedGreedy algorithm = new MultiStartIteratedGreedy(
                    multiStartIterations,
                    new ConstructiveC2(alpha),
                    new IteratedGreedy(
                            igIterations,
                            ig_beta,
                            new ReconstructiveC2(ig_alpha, ig_delta)));

            myExperiments.add(new Experiment(ds, algorithm, repetitions));
        }


        File outut_directory = new File("./resources/experiments/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm.ss")));
        //noinspection ResultOfMethodCallIgnored
        outut_directory.mkdirs();


        PrintStream summary_output = new PrintStream(new File(outut_directory, "Summary.txt"));

        int count = 0;
        for (Experiment experiment : myExperiments) {
            PrintStream out = new PrintStream(new File(outut_directory, "Individual_" + experiment.toString() + ".txt"));
            experiment.setExternalOutput(out, summary_output);
            experiment.setInitialSeed(seed + count).run();
            out.flush();
            summary_output.flush();
            count++;
            out.close();
        }
        summary_output.close();
        System.out.println("Total experiments:" + count);
    }

}
