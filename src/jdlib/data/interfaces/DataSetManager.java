package jdlib.data.interfaces;

public interface DataSetManager<E extends Instance> {

	/**
	 * Add all instances in the given path as a new data
	 * set with the specified key.
	 * @param dataKey - data set identifier
	 * @param path - folder path were the instances are stored
	 */
	public void addDataSet(String dataKey, String path);

	/**
	 * Returns all instances in the specified data set.
	 * @param dataKey - data set identifier
	 * @return an iterable of all instances
	 */
	public Iterable<E> getDataSet(String dataKey);


}
