package jdlib.data.interfaces;

public interface InstanceWriter<E extends Instance> {

    /**
     * Writes an Instance to a text file.
     * @param instance instance to be written
     * @param filePath path to the output file
     */
    void writeInstance(E instance, String filePath);
}
