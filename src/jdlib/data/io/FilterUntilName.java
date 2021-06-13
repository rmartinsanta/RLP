package jdlib.data.io;

import java.io.File;
import java.io.FilenameFilter;

//TODO: Maybe store the first accepted and filter the following...
public class FilterUntilName implements FilenameFilter {
	String name;
	boolean firstFound=false;
	
	public FilterUntilName(String name){
		if(name==null)
			throw new NullPointerException("The name for a filter can't be null.");
		if(name.isEmpty())
			firstFound=true;
		this.name = name;
	}
	
	@Override
	public boolean accept(File dir, String filename) {
		if(!firstFound)
			firstFound =filename.contains(name); 
		return firstFound;
	}
	
	@Override
	public String toString(){
		return "Filter until substring \"" + name + "\" is found.";
	}
}
