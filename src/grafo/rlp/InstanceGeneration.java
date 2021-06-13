package grafo.rlp;

import grafo.rlp.data.InstanceGenerator;
import grafo.rlp.data.RLPInstance;
import grafo.rlp.data.RLPInstanceWriter;

import java.io.File;
import java.util.Random;

/**
 * This class can batch generate and saves new instances for
 * the RLP.
 *
 * @version 0.0.1 (2020.01.08)
 */
public class InstanceGeneration {


    private final static String base_path = "resources";
    private final static String workingDirName = "generatedInstances";

    /**
     * This method generates different group of instances for the RLP.
     *
     * <ul>
     * <il>Large: Instances similar to those in [6]</il>
     * <il>VeryLarge: Instances in the state of art</il>
     * </ul>
     *
     * <p>The instances are saved in "./base_path/workingDirName"
     * within a folder with its data set name. The folder defined
     * in {@code base_path} must exists. Any other necessary folder
     * is automatically created.
     * </p>
     *
     * @param args
     */
    public static void main(String[] args) {
        File path = generateWorkingPath();

//        generateLargeInstances(path);
//        generateVeryLargeInstances(path);

        generateTrainingMirrorDataSet(path);

    }

    private static void generateTrainingMirrorDataSet(File path) {
        String dataSetName = "trainingMirror";
        int m = 4; //instances per parameter group
        int[] ns = {200, 300, 400, 500, 600, 800, 1000}; //number of vertices
        float[] ps = {0.50f, 0.70f, 0.90f}; //percentage of NDC
        generateInstances(path, dataSetName, m, ns, ps);
        System.out.println("Generated: "+ m*ns.length*ps.length + " instances.");
    }

    private static void generateLargeInstances(File path) {
        String dataSetName = "LargeMirror";
        int m = 10; //instances per parameter group
        int[] ns = {200, 300, 400, 500}; //number of vertices
        float[] ps = {0.10f, 0.30f, 0.50f, 0.70f, 0.90f}; //percentage of NDC
        generateInstances(path, dataSetName, m, ns, ps);

    }

    private static void generateVeryLargeInstances(File basePath) {
        String dataSetName = "VeryLarge";
        int m = 10; //instances per parameter group
        int[] ns = {600, 800, 1000}; //number of vertices
        float[] ps = {0.10f, 0.30f, 0.50f, 0.70f, 0.90f}; //percentage of NDC
        generateInstances(basePath, dataSetName, m, ns, ps);
    }

    /**
     * Generates a batch of instances for the RLP.
     *
     * <p>This method generates nInstances per each combination in [ns x ps].
     * Each instance is saved to a file within a folder named as the data set
     * name.</p>
     *
     * @param basePath    path where the new folder for the instances is saved/created
     * @param dataSetName name of the data set
     * @param nInstances  number of instances per group
     * @param ns          array with all values for parameter n
     * @param ps          array with all values for parameter p
     */
    public static void generateInstances(File basePath, String dataSetName, int nInstances, int[] ns, float[] ps) {

        File outputPath = new File(basePath, dataSetName);
        if (!outputPath.exists())
            outputPath.mkdir();

        System.out.println("Generating \"" + dataSetName + "\"");
        System.out.println("Path: \"" + outputPath + "\"\n");

        RLPInstanceWriter writer = new RLPInstanceWriter();
        String namePattern = "dataN%dP%dInst%d.txt";

        Random rng = new Random(0);
        long nstime = -System.currentTimeMillis();
        for (int n : ns) {
            System.out.println("Generating N=" + n);
            for (float p : ps) {
                for (int i = 0; i < nInstances; i++) {
                    String name = String.format(namePattern, n, (int) (p * 10), i + 1);
                    System.out.printf("\tInstance: \"%s\"", name);
                    long time1 = -System.currentTimeMillis();
                    RLPInstance instance = InstanceGenerator.generateInstance(n, p, rng);
                    time1 += System.currentTimeMillis();
                    long time2 = -System.currentTimeMillis();
                    writer.writeInstance(instance, outputPath + "/" + name);
                    time2 += System.currentTimeMillis();
                    System.out.printf(" (Generate: %.3fs; Write: %.3fs)\n", time1 / 1000.0, time2 / 1000.0);
                }
            }
        }
        nstime += System.currentTimeMillis();
        System.out.printf("Elapsed time: %5.3f\n\n", nstime / 1000.0);

    }

    /**
     * This method checks if the base_path exist in the current
     * working path and then creates a new working folder in it.
     *
     * @return A file with the working directory for this class main method
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static File generateWorkingPath() {
        //check base path exists
        File f = new File("./" + base_path);
        if (!f.exists() || !f.isDirectory()) {
            f = new File(".");
            String message = String.format("The required directory \"%s\" doesn't exists in the current working path: \"%s\"", base_path, f.getAbsolutePath());
            throw new RuntimeException(message);
        }

        //create a directory for the generated instances
        File outputDirectory = new File(f, workingDirName);
        if (!outputDirectory.exists()) {
            outputDirectory.mkdir();
        }
        return outputDirectory;
    }
}
