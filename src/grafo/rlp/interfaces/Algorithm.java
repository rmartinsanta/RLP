package grafo.rlp.interfaces;

import java.io.PrintStream;
import java.io.PrintWriter;

import grafo.rlp.data.RLPInstance;
import jdlib.data.interfaces.Instance;
import jdlib.data.interfaces.Solution;

public interface Algorithm<T extends Instance, S extends Solution<T>> {


    /**
     * This enumeration determines the type of a termination criteria.
     *
     * <ul>
     * <li><b>miliseconds</b>: Each instance will be processed until a time
     * limit (in miliseconds) is reached. It will use as many iterations as necessary.
     * <li><b>iterations</b>: Each instance will be processed a fixed number of times. It
     * will consume as much time as necessary.
     * <li><b>function:</b>: <i>not used yet</i>
     * </ul>
     */
    enum ExecutionType {
        miliseconds, iterations, function
    }

    /**
     * This enumerations determines how the results of the current execution
     * of the algorithm are presented:
     *
     * <ul>
     * <li><b>individual</b>: Reports the cost obtained and the time spend for each
     * iteration and for each instance.
     * <li><b>summary</b>(default): For each instance reports the best cost, the total
     * number of iterations and the total time.
     * <li><b>incremental</b>: For each instance reports the cost obtained and the
     * cumulative time spent (counting all previuos iterations for that instance) only
     * each time a better solution is found. Allways reports the last iteration
     * for each instance.
     * <ul>
     */
    enum ReportType {
        summary, individual, incremental
    }

    default void printHeader() {
        printHeader(System.out);
    }

    void printHeader(PrintStream out);

    S execute(RLPInstance instance);

    Algorithm setOutput(PrintWriter output);

    Algorithm setReport(ReportType reportType);

}
