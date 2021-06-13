package jdlib.tools;

import java.io.*;
import java.util.*;

import jdlib.data.interfaces.Instance;
import jdlib.data.interfaces.InstanceReader;
import jdlib.data.io.StringNaturalOrderComparator;



/**
 * This iterator returns all instances in a given folder. It requires
 * an InstanceReader object that can read such files.
 * @date 2018.10.15
 * @author juandavid.quintana@urjc.es
 * @version 0.0.1
 */
public class InstanceReaderIterator<E extends Instance> implements Iterator<E>{
	int n=0;
	File[] files;
	InstanceReader<E> instanceReader;
	
	public InstanceReaderIterator(String dir, InstanceReader<E> instanceReader ){
		this(dir, instanceReader, null); //pass a null filter
	}
	
	public InstanceReaderIterator(String dir, InstanceReader<E> instanceReader, FilenameFilter filter){
		File folder = new File(dir);
		this.instanceReader = instanceReader;
		this.files = folder.listFiles();
		Arrays.sort(this.files, new StringNaturalOrderComparator());
		if(filter!=null)
			this.files = filterFiles(this.files,filter);
	}

	@Override
	public boolean hasNext() {
		return n<files.length;
	}

	@Override
	public E next() {
		E instance = instanceReader.readInstance(this.files[n].getAbsolutePath());
		this.n++;
		return instance;
	}
	
	private File[] filterFiles(File[] files, FilenameFilter filter) {
		ArrayList<File> filtered = new ArrayList<File>(20);
		for(File file: files){
			if(filter==null || filter.accept(file.getParentFile(), file.getName())){
				filtered.add(file);
			}
		}
		//"new File[0]" -> used to determine the output type at runtime
		return filtered.toArray(new File[0]);
	}
	
}