package jdlib.data.io;

import java.io.File;
import java.io.FilenameFilter;

public class FilterBySubstring implements FilenameFilter {
	String substring;
	
	public FilterBySubstring(String name){
		if(name==null)
			throw new NullPointerException("The substring for a filter can't be null.");
		this.substring = name.toLowerCase();
	}
	
	@Override
	public boolean accept(File dir, String filename) {
		return substring.isEmpty() || filename.toLowerCase().contains(substring);
	}
	
	@Override
	public String toString(){
		return "FilterBySubstring<"+ substring +">";
	}
}