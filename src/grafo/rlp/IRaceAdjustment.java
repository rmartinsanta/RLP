package grafo.rlp;

import grafo.rlp.algorithm.Improve.IteratedGreedy;
import grafo.rlp.algorithm.MultiStartIteratedGreedy;
import grafo.rlp.algorithm.constructive.*;
import grafo.rlp.data.RLPInstance;
import grafo.rlp.data.RLPInstanceReader;
import grafo.rlp.interfaces.Constructive;
import grafo.rlp.interfaces.RLPReconstructive;
import grafo.rlp.interfaces.RLPSolution;
import grafo.rlp.interfaces.Shake;
import jdlib.tools.RandomManager;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Main class used for parameters tuning in IRace.
 *
 * <p>All parameters, including instance and seed, are received as parameters and
 * only the objective function is reported as a number in the last line of the
 * standard output
 * <p>
 * command line example:
 * --msiter 100 --alphac 0.00 --alphar 0.25 --beta 0.5 --deterioration 1 --igiter 50 --instance "./resources/rlp-instances/large/dataN200P7Inst10.txt"
 * optional commands: (default values)
 * --seed 0
 */
public class IRaceAdjustment {

    /* parameters */
    private static long seed = 0;
    private static long multistart_iterations;
    private static float alpha_constructive;
    private static int ig_iterations;
    private static float alpha_reconstructive;
    private static float beta;
    private static float delta;
    /* instance */
    private static String fInstance;
    /* default output */
    final private static PrintStream defaultOutput = System.out;


    public static void main(String[] args) {
        // parse parameters
        parseParameters(args);
        // environment
        RandomManager.setSeed(seed);
        RLPInstanceReader reader = new RLPInstanceReader();
        RLPInstance instance = reader.readInstance(fInstance);

        //Algorithm init
        Constructive<RLPInstance, RLPSolution> constructive = new ConstructiveC2(alpha_constructive);
        RLPReconstructive reconstructive = new ReconstructiveC2(alpha_reconstructive, delta);
        Shake<RLPInstance, RLPSolution> ig = new IteratedGreedy(ig_iterations, beta, reconstructive);
        MultiStartIteratedGreedy algorithm = new MultiStartIteratedGreedy(multistart_iterations, constructive, ig);

        //Execute
        System.setOut(System.err);
        RLPSolution solution = algorithm.execute(instance);
        System.setOut(defaultOutput);

        //report output
        //System.out.printf("%d %.2f",solution.getObjectiveFunction(), Stopwatch.getTotalMiliTime()/1000.0);
        System.out.printf("%d", solution.getObjectiveFunction());

    }

    private static void parseParameters(String[] args) {
        System.err.println("Parameters array: " + Arrays.toString(args));
        Map<String, String> params = new HashMap<>();
        for (int i = 0; i < args.length; i = 2 + i) {
            String name = args[i].trim().toLowerCase().replaceAll("-", "");
            String value = args[i + 1].trim();
            params.put(name, value);
        }

        //algorithm = params.get("algorithm"); //unused
        seed = Integer.parseInt(params.getOrDefault("seed", "0"));
        multistart_iterations = Integer.parseInt(params.get("msiter"));
        alpha_constructive = Float.parseFloat(params.get("alphac"));
        if (alpha_constructive == -1.0)
            alpha_constructive = Float.NaN;
        alpha_reconstructive = Float.parseFloat(params.get("alphar"));
        if (alpha_reconstructive == -1.0)
            alpha_reconstructive = Float.NaN;
        beta = Float.parseFloat(params.get("beta"));
        delta = Float.parseFloat(params.get("deterioration"));
        ig_iterations = Integer.parseInt(params.get("igiter"));
        fInstance = params.get("instance");
    }
}
