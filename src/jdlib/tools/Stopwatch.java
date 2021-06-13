package jdlib.tools;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is intended to be used as a static (global) Stopwatch class used to measure the running time of
 * an algorithm over a single execution. Each lap (start-stop pair) can be used to measure each stage of the algorithm.
 * 
 * <p>The method hasTimeLimitExceded can be used to check if a time limit was exceded and hence stop the algorithm.
 * The total_nanoseconds consumed time is measured as the sum of previous laps and the on going interval (if any).
 * 
 * <p><b>Note</b>: this class internally uses nanoseconds presition, however return values are in miliseconds.
 * @date 2017.01.12
 * @author Juan David Quintana
 * @version next
 * Modified interface
 * @version 1.0.1
 * Internally uses nanoseconds presition. All returned values are returned as miliseconds.
 * 
 * @version 1.0.0
 * FIXME: In some cases miliseconds precision is not when using a time limit. The algoritm may be
 * fast enough to tale less than one miliseconds resulting in 0ms which doesn't add any time.
 * <pre>
 * 		long ms = 0;
		while(ms<1){ //May never be achieved unless the OS do a context change.
			long start = System.currentTimeMillis();
			long spent=System.currentTimeMillis()-start;
			ms+=spent;//elapsed time will ussually be 0
		}
 * </pre>
 */
public class Stopwatch {
	/** Time limit in Nanoseconds */
	private static long timelimit_nanoseconds;
	private static long total_nanoseconds;
	private static List<Long> laps_miliseconds = new ArrayList<Long>(10);
	private static long start;
	private static boolean started = false;
	private static boolean hasTimeLimit=false;
	private static final long long_1e6 = 1000000l; //convert from nanoseconds to miliseconds
	
	public static void clear(){
		total_nanoseconds = 0;
		laps_miliseconds.clear();
		started = false;
		//hasTimeLimit=false;//leave previous flag
		timelimit_nanoseconds = -1;
	}
	

	public static void enableTimeLimit(long miliseg){
		hasTimeLimit=true;
		timelimit_nanoseconds = miliseg*long_1e6; //internally nanoseconds
	}
	
	public static void disableTimeLimit(){
		hasTimeLimit=false;
	}
	
	public static void start(){
		start = System.nanoTime();
		started=true;
	}
	
	public static void stop(){
		if(!started)
			throw new RuntimeException("Stopwatch not started.");
		long lap = System.nanoTime()-start;
		laps_miliseconds.add(lap/long_1e6);
		total_nanoseconds += lap;
		started = false;
	}
	
	public static long getCurrentLap(){
		long current = 0;
		if(started) 
			current = System.nanoTime() - start;
		return current/long_1e6;
	}
	
	
	public static boolean hasTimelimitExceeded(){
		if(!hasTimeLimit)
			return false;
		long current = 0;
		if(started) //If the stopwatch is running... 
			current = System.nanoTime() - start; 
		return total_nanoseconds + current > timelimit_nanoseconds;
	}
	
	public static long getLap(int n){
		if(n>0 && n<=laps_miliseconds.size()){
			return laps_miliseconds.get(n-1);
		}
		if(n==0){
			throw new RuntimeException(String.format("No such lap exists: %d. The laps start from 1 and go to N (number of laps).",n));
		}
		throw new RuntimeException(String.format("No such lap exists: %d (Index: %d / size: %d).",n, n-1, laps_miliseconds.size()));
	}
	
	
	public static long getTotalNanoTime(){
		long current = 0;
		if(started) //If the stopwatch is running... 
			current = System.nanoTime() - start;
		return (total_nanoseconds+current);
	}
	
	public static long getTotalMiliTime(){
		long current = 0;
		if(started) //If the stopwatch is running... 
			current = System.nanoTime() - start;
		return (total_nanoseconds+current)/long_1e6;
	}
	
	/** Returns the number of laps, in other word each lapse of time computed by each pair start-stop*/
	public static int size(){
		return laps_miliseconds.size();
	}
	
	/** Clear the stored times for laps, but not the total_nanoseconds time, nor anything else.*/
	public static void clearLaps(){
		laps_miliseconds.clear();
	}
	

}
