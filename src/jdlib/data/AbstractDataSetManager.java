package jdlib.data;


import java.io.*;
import java.util.*;

import jdlib.data.interfaces.Instance;
import jdlib.data.interfaces.InstanceReader;
import jdlib.data.io.FilterBySubstring;
import jdlib.data.io.StringNaturalOrderComparator;
import jdlib.tools.InstanceReaderIterator;


/**
 * This class offers easy access to all the test instances. Given the data
 * set key (and optionally a FilenameFilter) this class returns an iterable of
 * instances.
 *
 *
 *
 * @author juandavid.quintana@urjc.es
 * @version 2.1.0 (2019.06.03)
 */
public abstract class AbstractDataSetManager<E extends Instance> {
    private Map<String, String> dataSets = new HashMap<>(100); //Map<idDataSet, dataSetFolder>
    private InstanceReader<E> instanceReader;

    protected AbstractDataSetManager(){
        instanceReader = getInstanceReader();
    }

    /**
     * This abstract method determines the reader class used to readd instances from files.
     * */
    protected abstract InstanceReader<E> getInstanceReader();


    /**
     * Returns an iterable of instances of the selected group.
     *
     * @param dataKey data set alias
     * @return iterable of all instances for the given group.
     */
    public Iterable<E> getDataSet(String dataKey) {

        if (!dataSets.containsKey(dataKey))
            throw new RuntimeException("Data set <" + dataKey + "> is not in the library.");
        //System.out.println(dataSets.get(dataKey));
        return () -> new InstanceReaderIterator<>(dataSets.get(dataKey), instanceReader);
    }

    public E getInstanceFromDataSet(String dataKey, int n) {
        if (!dataSets.containsKey(dataKey))
            throw new RuntimeException("Data set <" + dataKey + "> is not in the library.");
        File folder = new File(dataSets.get(dataKey));
        File[] files = folder.listFiles();
        Arrays.sort(files, new StringNaturalOrderComparator());
        if (n < 0 || n > files.length)
            throw new RuntimeException(String.format("Invalid instance number: %d (max: %d", n, files.length));
        return instanceReader.readInstance(files[n].getAbsolutePath());

    }

    /**
     * Returns the instances from a data set that contains a given name in its file name.
     *
     * @param dataKey data set alias
     * @param substring to be search in the instance name
     * @return iterable of all instances cointaining the given name in its file name
     */
    public Iterable<E> getDataSet(String dataKey, String substring) {
        if (!dataSets.containsKey(dataKey))
            throw new RuntimeException("Data set <" + dataKey + "> is not in the library.");
        FilenameFilter filterByName = new FilterBySubstring(substring);
        return () -> new InstanceReaderIterator<>(dataSets.get(dataKey), instanceReader, filterByName);
    }

    /**
     * Returns the instances from a data set that contains a given name in its file name.
     *
     * @param dataKey data set alias
     * @param filter to accept or reject files
     * @return iterable of all instances containing the given name in its file name
     */
    public Iterable<E> getDataSet(String dataKey, FilenameFilter filter) {
        if (!dataSets.containsKey(dataKey))
            throw new RuntimeException("Data set <" + dataKey + "> is not in the library.");
        return () -> new InstanceReaderIterator<>(dataSets.get(dataKey), instanceReader, filter);
    }

    public void addDataSet(String dataKey, String path) {
        File p = new File(path);
        if(!p.exists()){
            throw new RuntimeException(String.format("Instances path doesn't exists. (%s)", p.getAbsolutePath()));
        } else if(!p.isDirectory()){
            throw new RuntimeException(String.format("Instances path must be a directory. (%s)", p.getAbsolutePath()));
        }

        dataSets.put(dataKey, path);
    }

}



