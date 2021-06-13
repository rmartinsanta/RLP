package grafo.rlp.data;


import jdlib.data.AbstractDataSetManager;
import jdlib.data.interfaces.InstanceReader;


/**
 * This class offers easy access to all the test instances. Given the data
 * set key (and optionally a FilenameFilter) this class returns an iterable of
 * instances.
 *
 * @author juandavid.quintana@urjc.es
 * @version 2.1.0
 * @date 2019.06.03
 */
public class RLPDataSetManager extends AbstractDataSetManager<RLPInstance> {

    //DataSet names
    public final static String small_random = "small_random";
    public final static String large = "large";
    public final static String training = "training";
    //DataSet paths to instances
    private final static String path_small_random = "./resources/rlp-instances/random";
    private final static String path_large = "./resources/rlp-instances/large";
    private final static String path_training = "./resources/rlp-instances/training";

    //experimental datasets //TODO:
    private final static String path_mirror_large = "./resources/rlp-instances/LargeMirror";
    public final static String mirror_large = "large_mirror";

    private final static String path_very_large = "./resources/rlp-instances/VeryLarge";
    public final static String very_large = "very_large";

    //extra dataset for comparison
    public final static String robustness = "robustness";
    private final static String path_robustness = "./resources/rlp-instances/robustness";

    //data set for training
    public final static String mirrorTraining = "trainingMirror";
    private final static String path_mirror_training = "./resources/rlp-instances/trainingMirror";

    public RLPDataSetManager(){
        this.loadDefaultDataSets();
    }


    protected InstanceReader<RLPInstance> getInstanceReader(){
        return new RLPInstanceReader();
    }

    private void loadDefaultDataSets(){
        this.addDataSet(small_random, path_small_random);
        this.addDataSet(large, path_large);
        this.addDataSet(training, path_training);
        //TODO: experimental datasets
        this.addDataSet(mirror_large, path_mirror_large);
        this.addDataSet(very_large, path_very_large);
        //TODO: extra datasets
        this.addDataSet(robustness, path_robustness);
        this.addDataSet(mirrorTraining, path_mirror_training);
    }


}




