package jdlib.tools;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;



/**
 * The class RandomManager follows a Singleton pattern to manage a single instance of Random.
 * @date 2015.12.04
 * @author Juan David Quintana
 * @version 1.1.0
 */

public class RandomManager {

	private static Random rnd;
	
	/** Sets the seed for the global instance of Random*/
	public static void setSeed(long seed){
		if(rnd==null)
			rnd = new Random(seed);
		else
			rnd.setSeed(seed);
	}
	
	/** Returns the global instance of Radom*/
	public static Random getRandom(){
		if(rnd==null){
			throw new RuntimeException("RandomManager's seed not initialized.");
		}
		return rnd;
	}
	/* Note: To recover the current seed at a given executionTime, just recover AtomicLong value
	 * from the de debugger (to replicate some result) and then:
	 * long currentSeed = AtomicLongValue ^ 0x5DEECE66DL;
	 */
	/** Returns the seed for the current state in the random numbers generator (to reproduce test from a certain point)*/
	public static long getSeed(){
		if(rnd == null)
			throw new RuntimeException("RandomManager's seed not initialized.");
		try {
			Field field = Random.class.getDeclaredField("seed");
			field.setAccessible(true);
		    AtomicLong scrambledSeed = (AtomicLong) field.get(rnd);   //this needs to be XOR'd with 0x5DEECE66DL
		    field.setAccessible(false);
		    long theSeed = scrambledSeed.get() ^ 0x5DEECE66DL;
		    return theSeed;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException("Access error to the seed: "+e.getMessage());
		}
	}
	
}
