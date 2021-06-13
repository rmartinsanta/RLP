package grafo.rlp;

import grafo.rlp.algorithm.Improve.IteratedGreedy;
import grafo.rlp.algorithm.MultiStartIteratedGreedy;
import grafo.rlp.algorithm.constructive.ConstructiveC1;
import grafo.rlp.algorithm.constructive.ReconstructiveC2;
import grafo.rlp.data.RLPDataSetManager;
import grafo.rlp.data.RLPInstance;
import grafo.rlp.interfaces.Constructive;
import grafo.rlp.interfaces.RLPSolution;
import grafo.rlp.interfaces.Shake;
import jdlib.tools.RandomManager;

/**
 * Simple methods to test the algorithms in a given instance
 */
public class Testing {


    public static void main(String[] args) {
        RandomManager.setSeed(0);
//        testConstructive();
//        testMultiStartIteratedGreedy();
    }



    private static void testConstructive() {
        RandomManager.setSeed(0);

        RLPDataSetManager dataSet = new RLPDataSetManager();
        RLPInstance instance = dataSet.getInstanceFromDataSet(RLPDataSetManager.small_random, 40);

        Constructive<RLPInstance, RLPSolution> constructive;
//		constructive = new ConstructiveRandom();
//		constructive = new ConstructiveGreedy1();
        constructive = new ConstructiveC1(0.25f);
        RLPSolution bestSolution = null;

        for (int i = 0; i < 100; i++) {
            RLPSolution solution = constructive.constructSolution(instance);
            if (bestSolution == null || solution.getObjectiveFunction() < bestSolution.getObjectiveFunction()) {
                bestSolution = solution;
            }
        }
        System.out.printf("Best solution: %s\n", bestSolution);

    }

    private static void testMultiStartIteratedGreedy() {
        RandomManager.setSeed(0);

        RLPDataSetManager dataSet = new  RLPDataSetManager();
        RLPInstance instance = dataSet.getInstanceFromDataSet(RLPDataSetManager.small_random, 1);

        /* Seed for RNG */
        long seed = 0;
        /* parameters */
        //Best parameters in the preliminary experiments
        int multiStartIterations = 100;
        int igIterations = 50; //10, 25, 50
        float alpha = 0.25f; //{}
        float ig_alpha = 0.25f;
        float ig_beta = 0.10f; //{10%,20%,30%,40%,50%}
        float ig_delta = 1f; //When destroying X regenerators, those wont be used unless other delta*X are deployed


        Constructive<RLPInstance, RLPSolution> c1 = new ConstructiveC1(alpha);
        Constructive<RLPInstance, RLPSolution> c2 = new ConstructiveC1(alpha);
        Shake<RLPInstance, RLPSolution> ig = new IteratedGreedy(igIterations, ig_beta, new ReconstructiveC2(ig_alpha, ig_delta));
        MultiStartIteratedGreedy algorithm = new MultiStartIteratedGreedy(multiStartIterations, c1, ig);

        algorithm.execute(instance);
    }
}


