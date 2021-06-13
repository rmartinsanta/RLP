package grafo.rlp.data;

import java.util.*;

import jdlib.data.interfaces.Instance;

/**
 * Represents an instance for the Regenerator Location Problem.
 *
 * <p><b>Note</b>: Nodes are numbered from 0 to (N-1).
 *
 * @author juandavid.quintana@urjc.es
 * @version 1.0.1 (2018.10.19)
 */
public class RLPInstance implements Instance {
    // Instance file header data
    private final int VERTICES;
    private String name;
    //private int expectedEdges;
    private List<Integer>[] adjList; // Adjacency list
    private boolean[][] adjMatrix; //adjacency matrix

    //invalid edges are ignored but accounted for error checking
    private int edges; //Actual number of edges read from the file
    private int repeatedEdges;//number of duplicated edges in the instance
    private int loopEdges; // number of loops in the instance

    @SuppressWarnings("unchecked")
    public RLPInstance(int vertices) {
        this.name = "";
        this.VERTICES = vertices;
        this.edges = 0;
        this.loopEdges = 0;
        this.repeatedEdges = 0;
        this.adjList = new List[VERTICES];
        this.adjMatrix = new boolean[VERTICES][VERTICES];
        for (int i = 0; i < VERTICES; i++) {
            adjList[i] = new ArrayList<>((int) (VERTICES * 1.4f));
        }
    }

    public RLPInstance(int vertices, boolean[][] adjacency_mat) {
        this(vertices);
        //load matrix
        for (int v1 = 0; v1 < vertices; v1++) {
            for (int v2 = v1 + 1; v2 < vertices; v2++) {
                if (adjacency_mat[v1][v2])
                    this.addEdge(v1, v2);
            }
        }

    }

    /**
     * Adds an undirected edge to the instance graph.
     *
     * @param start start vertex
     * @param end   end vertex
     */
    public void addEdge(int start, int end) {
        if (start != end) {
            this.edges++;
            boolean repeated = !addDirectedEdge(start, end) || !addDirectedEdge(end, start);
            if (repeated)
                this.repeatedEdges++;
        } else {
            this.loopEdges++;
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean addDirectedEdge(int start, int end) {
        boolean added = false;
        if (!this.isEdge(start,end)) {
            adjList[start].add(end);
            adjMatrix[start][end] = true;
            added = true;
        }
        return added;
    }

    /**
     * Removes an undirected edge from the instance graph.
     * Since the graph is undirected removeEdge(v1,v2) is
     * equivalent to removeEdge(v2,v2).
     *
     * @param start start vertex
     * @param end   end vertex
     */
    @SuppressWarnings("unused")
    public void removeEdge(int start, int end) {
        if (start != end && this.isEdge(start, end)) {
            this.edges--;
            removeDirectedEdge(start, end);
            removeDirectedEdge(end, start);
        }
    }

    private void removeDirectedEdge(int start, int end) {
        adjList[start].remove((Integer) end);
        adjMatrix[start][end] = false;

    }

    /**
     * Returns the total number of vertices in the graph for this instance.
     *
     * @return |V| Total number of vertices (the order)
     */
    public int getV() {
        return this.VERTICES;
    }

    /**
     * Returns the total number of edges |E| in the graph for this instance.
     *
     * @return |E| Total number of edges (the size)
     */
    public int getE() {
        return this.edges;
    }

    /**
     * Returns the edge list for a given vertex v.
     *
     * @param v the vertex
     * @return the edge list of v
     */
    public List<Integer> getEdges(int v) {
        return this.adjList[v];
    }

    /**
     * Returns a reference to the adjacency matrix of this instance. The
     * matrix contains true at if there is an edge between v1 and v2.
     * @return adjacency matrix.
     */
    public boolean[][] getAdjacencyMatrix(){
        return this.adjMatrix;
    }

    /**
     * Returns {@code true} if the edge between the specified vertex exists.
     *
     * @param v1 start point
     * @param v2 end point
     * @return boolean - true if the edge exists
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isEdge(int v1, int v2) {
        return adjMatrix[v1][v2];
//        return adjList[v1].contains(v2);
    }

    public boolean validate() {
        boolean isValid;
        isValid = checkActualEdges();
        if (this.repeatedEdges > 0)
            System.err.println(String.format("RLPInstance.Warning: Instance <%s> has <%d> repeated edges. These are ignored.", this.name, this.repeatedEdges));
        if (this.loopEdges > 0)
            System.err.println(String.format("RLPInstance.Warning: Instance <%s> has <%d> loop edges. These are ignored.", this.name, this.loopEdges));
        return isValid;
    }

    private boolean checkActualEdges() {
        boolean areEqual, isEven;
        int actualDirectedEdges = Arrays.stream(adjList).mapToInt(List::size).sum();
        isEven = actualDirectedEdges % 2 == 0;
        if (!isEven)
            System.err.println(String.format("RLPInstance.Warning: the number of (directed) edges is not even for instance <%s>. (edges: %d, read: %d)", name, actualDirectedEdges, this.edges * 2));

        areEqual = (actualDirectedEdges / 2) == this.edges;
        if (!areEqual)
            System.err.println(String.format("RLPInstance.Warning: Unexpected number of actual edges for instance <%s>. (actual: %d, expected: %d)", name, actualDirectedEdges / 2, this.edges));

        return areEqual && isEven;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        int n = getV();
        int e_max = n * (n - 1) / 2;
        int e_min = (n - 1);
        int ndc = e_max - edges;
        double p = 100.0 * ndc / (e_max - e_min);
        return String.format("%-25s %3d Vertex %8d Edges %7d NDC (%2.2f%%)\n", name, VERTICES, edges, ndc, p);
    }

}
