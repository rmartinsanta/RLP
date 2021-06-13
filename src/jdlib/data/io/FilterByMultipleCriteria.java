package jdlib.data.io;

import java.io.File;
import java.io.FilenameFilter;



public class FilterByMultipleCriteria implements FilenameFilter {
	Iterable<FilenameFilter> filters;
	public FilterByMultipleCriteria(Iterable<FilenameFilter> filters){
		this.filters = filters;
	}
	

	@Override
	public boolean accept(File dir, String name) {
		if(this.filters!=null){
			for (FilenameFilter f : filters) {
				if(f != null && !f.accept(dir, name) )
					return false;
			}
		}
		return true;
	}
	
}