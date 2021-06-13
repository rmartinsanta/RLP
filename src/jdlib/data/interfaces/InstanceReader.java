package jdlib.data.interfaces;

public interface InstanceReader<E extends Instance> {
	
	/**
	 * Returns an instance of type E
	 * @param filePath
	 * @return
	 */
	E readInstance(String filePath);
}
