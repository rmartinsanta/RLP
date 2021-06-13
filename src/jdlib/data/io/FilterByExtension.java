package jdlib.data.io;

import java.io.File;
import java.io.FilenameFilter;

public class FilterByExtension implements FilenameFilter {
	private String extension;
	public FilterByExtension(String ext){
		if(ext==null)
			throw new IllegalArgumentException("The extension cannot be null,");
		ext=ext.trim().toLowerCase();
		if(!ext.isEmpty() && ext.indexOf('.')!=0)
			ext = '.'+ext;
		extension=ext;
	}

	@Override
	public boolean accept(File dir, String name) {
		if(!extension.isEmpty())
			return name.toLowerCase().endsWith(extension);
		return true; //no extension => accepts all.
	}
	
	@Override
	public String toString(){
		return "Filter by extension < "+extension+" >";
	}
	
}