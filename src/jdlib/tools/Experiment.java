package jdlib.tools;

import grafo.rlp.data.RLPDataSetManager;
import grafo.rlp.data.RLPInstance;
import grafo.rlp.interfaces.Algorithm;
import grafo.rlp.interfaces.RLPSolution;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Experiment {


    //public enum Report {all, individual, summary}


    private final Iterable<RLPInstance> instances;
    private final Algorithm<RLPInstance, RLPSolution> algorithm;
    private String dataSetName = "";
    private long seed = 0;
    private int repetitions = 1;

    private PrintStream out = System.out;
    private PrintStream summary_out = System.out;


    public Experiment(Iterable<RLPInstance> instances, Algorithm<RLPInstance, RLPSolution> algorithm) {
        this(instances, algorithm, 1);
    }

    public Experiment(Iterable<RLPInstance> instances, Algorithm<RLPInstance, RLPSolution> algorithm, int repetitions) {
        this.instances = instances;
        this.algorithm = algorithm;
        this.repetitions = repetitions;
    }

    public Experiment(String dataSetName, Algorithm<RLPInstance, RLPSolution> algorithm) {
        this(dataSetName,algorithm, 1);

    }

    public Experiment(String dataSetName, Algorithm<RLPInstance, RLPSolution> algorithm, int repetitions) {
        RLPDataSetManager dataSetManager = new RLPDataSetManager();
        this.dataSetName = dataSetName;
        this.instances = dataSetManager.getDataSet(this.dataSetName);
        this.algorithm = algorithm;
        this.repetitions = repetitions;

    }


    public Experiment setInitialSeed(long seed) {
        this.seed = seed;
        return this;
    }

    public Experiment setExternalOutput(PrintStream stdout) {
        this.out = stdout;
        //this.summary_out = stdout;
        return this;
    }


    public Experiment setExternalOutput(PrintStream stdout, PrintStream summaryOut) {
        this.out = stdout;
        this.summary_out = summaryOut;
        return this;
    }


    public void run() {

        out.printf("# Experiment: %s\n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        out.printf("# Algorithm: %s\n", algorithm);
        out.printf("# Random seed: %s\n", seed);
        out.printf("# Instances: %s\n", dataSetName);
        if(repetitions > 1)
            out.printf("# Executions per instance: %d\n", repetitions);

        //Algorithm header
        PrintStream backupOutput = redirectOutput(out); //forces everything to be printed through "out"
        algorithm.printHeader();
        long instances_count = 0;
        long total_time = 0L;
        long total_regenerators = 0L;
        for (RLPInstance instance : instances) {
            for (int i = 0; i < repetitions; i++) {
                instances_count++;
                RandomManager.setSeed(seed+i);
                RLPSolution bestSolution = algorithm.execute(instance);
                total_time += Stopwatch.getTotalMiliTime();
                total_regenerators += bestSolution.getObjectiveFunction();
            }
        }
        redirectOutput(backupOutput);

        double nr = total_regenerators / (double) instances_count;
        double avg = total_time / (double) instances_count;

        summary_out.printf("%s; %.4f; %.3f\n", algorithm.toString(), nr, avg);
    }

    /**
     * Redirect all calls to System.out default standard output to a given
     * output PrintStream.
     *
     * @param redirection PrintStream that will be used to print everything sent to System.out
     * @return The previous PrintStream used by System.out (This may be used to back up the system
     * default standard output)
     */
    private PrintStream redirectOutput(PrintStream redirection) {
        PrintStream old = System.out;
        System.setOut(redirection);
        return old;

    }


    public String toString() {
        return dataSetName + "_" + algorithm.toString();
    }
}
