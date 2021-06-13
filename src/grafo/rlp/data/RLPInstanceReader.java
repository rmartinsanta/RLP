package grafo.rlp.data;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jdlib.data.interfaces.InstanceReader;

/**
 * This class can read an instance for the RLP from a text file.
 *
 * <p><b>Note</b> In the adjacency matrix a <b>0</b> means there <b>is</b> an edge.
 *
 * @author juandavid.quintana@urjc.es
 * @version 1.0.1 (19.10.2018)
 */
public class RLPInstanceReader implements InstanceReader<RLPInstance> {
    private String name = "";
    private int nodes;
    private int ndc;
    private int expected_edges;
    //Parameters declared in the filename (only for error checking∫)
    private int filename_n = -1;
    private float filename_p = Float.NaN;
    //token constants
    private final int CONNECTED = 0; // in the adjacency matrix a 0 means there is an edge

    public RLPInstance readInstance(String instanceFilePath) {
        try {
            File f = new File(instanceFilePath);
            name = f.getName();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));

            //parse file data
            parseParametersInFilename(name);
            parseHeader(br);
            int adjMatrix[][] = parseMatrix(br);
            List<Edge> listPairs = parseNDC(br);
            assert (br.readLine() == null); //EoF (End of File)

            checkDataConsistency(adjMatrix, listPairs);

            //generate instance
            RLPInstance instance = generateInstance(adjMatrix);
            instance.setName(name);

            //lightweight validation
            if (expected_edges != instance.getE())
                throw new RuntimeException(String.format("Mismatch between expected edges (%d) and actual edges (%d) in the instance.", expected_edges, instance.getE()));
            //heavier validation
            checkNDC(instance, listPairs);

            return instance;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void parseHeader(BufferedReader br) throws IOException {
        //read first line
        String declaredNodesLine[] = br.readLine().replace(';', ' ').trim().split("=");
        if (declaredNodesLine.length == 2 && !declaredNodesLine[0].equalsIgnoreCase("nNodes"))
            throw new RuntimeException("Unexpected file format. Expected \"nNodes=<Number>\" but string \"" + declaredNodesLine[0] + "\" was found.");
        nodes = Integer.parseInt(declaredNodesLine[1]);

        //read second line
        String declaredNDCLine[] = br.readLine().replace(';', ' ').trim().split("=");
        if (declaredNDCLine.length == 2 && !declaredNDCLine[0].equalsIgnoreCase("nPairs"))
            throw new RuntimeException("Unexpected file format. Expected \"nPairs=<Number>\" but string \"" + declaredNDCLine[0] + "\" was found.");
        ndc = Integer.parseInt(declaredNDCLine[1]);

        expected_edges = (nodes * (nodes - 1) / 2) - ndc;
    }


    /**
     * Parse the filename of the instance and saves the
     * parameters found in the filename.
     *
     * @param filename of the instance
     */
    private void parseParametersInFilename(String filename) {
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(filename);
        List<Integer> parameters = new ArrayList<>();
        while (matcher.find()) {
            parameters.add(Integer.parseInt(matcher.group()));
        }

        if (parameters.size() != 3)
            System.err.println("Warning: Unexpected format in file name: \"" + filename + "\" (Expected 3 numbers in filename but " + parameters.size() + " found.)");
        else {
            filename_n = parameters.get(0);
            filename_p = 0.1f * parameters.get(1);
        }
    }


    private int[][] parseMatrix(BufferedReader br) throws IOException {
        String costLine = br.readLine().trim();
        if (!costLine.equalsIgnoreCase("Cost="))
            throw new RuntimeException("Unexpected error parsing file. Expected token: \"Cost=\" but \"" + costLine + "\" was found.");

        String skipEmptyLine = br.readLine().trim();
        assert (skipEmptyLine.trim().isEmpty());
        int m[][] = new int[nodes][nodes];
        for (int i = 0; i < nodes; i++) {
            String rowLine = br.readLine();
            String[] data = rowLine.split("[ ]+");
            if (data.length != nodes) {
                throw new RuntimeException("Unexpected number of columns found while parsing matrix data. Found:" + data.length + " expected:" + nodes);
            }
            for (int j = 0; j < nodes; j++) {
                int cell = Integer.parseInt(data[j]);
                m[i][j] = cell;
            }
            skipEmptyLine = br.readLine();
            assert (skipEmptyLine.trim().isEmpty());

        }
        return m;
    }


    /**
     * This method parses the declaration block for the NDC pairs. This
     * data is read as formatted in the file. (Nodes are numbered from 1 to N)
     *
     * @param br reader
     * @return List with all the NDC pairs.
     * @throws IOException when unexpected IO errors
     */
    private List<Edge> parseNDC(BufferedReader br) throws IOException {
        String unPairedArcLine = br.readLine().trim();
        if (!unPairedArcLine.equalsIgnoreCase("UnPairedArc="))
            throw new RuntimeException("Unexpected error parsing file. Expected token \"UnPairedArc=\" but \"" + unPairedArcLine + "\" was found.");
        List<Edge> listNDC = new ArrayList<>(ndc + 1);
        for (int i = 0; i < ndc; i++) {
            String[] data = br.readLine().trim().split("[ ]+");
            int v_start = Integer.parseInt(data[0]);
            int v_end = Integer.parseInt(data[1]);
            listNDC.add(new Edge(v_start, v_end));
        }
        return listNDC;
    }


    private RLPInstance generateInstance(int[][] m) {
        RLPInstance instance = new RLPInstance(nodes);
        //Add edges...
        for (int i = 0; i < nodes; i++) {
            for (int j = i + 1; j < nodes; j++) {
                if (m[i][j] == CONNECTED) {
                    instance.addEdge(i, j);
                }
            }
        }
        return instance;
    }


    /**
     * Check that all read data is consistent.
     *
     * @param m         adjacency matrix read from file
     * @param listPairs non directly connected list of pairs read from file
     */
    private void checkDataConsistency(int[][] m, List<Edge> listPairs) {
        int complete_graph_edges = (nodes * (nodes - 1) / 2);

        //Optional: Check if the declared number of nodes in the filename and in the file data is the same.
        if (filename_n != -1 && filename_n != nodes) {
            String INCONSISTENT_NODE_DECLARATION = "Instance: <%s> the number of nodes declared in the filename and the ones declared inside the file doesn't match. (filename: %d, data: %d)\n";
            System.err.printf(INCONSISTENT_NODE_DECLARATION, name, nodes, filename_n);
        }

        //Optional: Check p-value declared in both filename and file data
        float actual_p = ndc / (float) (complete_graph_edges - (nodes - 1));
        if (!Float.isFinite(filename_p) && actual_p >= (filename_p - 0.05f) && actual_p <= filename_p + 0.05f) {
            String warningMessage = "Instance: <%s> the declared and actual <p> doesn't match. (filename: %.2f, actual: %.2f)";
            System.err.println(String.format(warningMessage, name, nodes, filename_n));
        }

        for (Edge ndc : listPairs) {
            if (m[ndc.start - 1][ndc.end - 1] == CONNECTED) {
                String exceptionMessage = "Unexpected error parsing file declared ndc pairs"
                        + " and the matrix doesn't match: There's and edge between nodes (%d-%d).";
                throw new RuntimeException(String.format(exceptionMessage, ndc.start, ndc.end));
            }
        }
    }


    private void checkNDC(RLPInstance instance, List<Edge> listPairs) {
        Set<Edge> setNDC = new HashSet<>();
        for (int i = 0; i < nodes; i++)
            for (int j = i + 1; j < nodes; j++)
                if (!instance.isEdge(i, j))
                    setNDC.add(new Edge(i + 1, j + 1));

        for (Edge ndc : listPairs)
            if (!setNDC.remove(ndc))
                throw new IllegalStateException("Failed NDC nodes validation: a declared NDC is missing from the generated instance.");

        if (!setNDC.isEmpty())
            throw new IllegalStateException("Failed NDC validation: the instance has more NDC than declared.");
    }

    static class Edge {
        int start, end;

        Edge(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + end;
            result = prime * result + start;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Edge other = (Edge) obj;
            if (end != other.end)
                return false;
            return start == other.start;
        }

    }


}
