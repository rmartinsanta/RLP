package grafo.rlp.data;

import jdlib.data.interfaces.InstanceWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * <p>This writer can be used to save an instance of the RLP to
 * a text file. When written to a file, <b>nodes are numbered
 * from 1 to N</b>. In the adjacency matrix a 0 implies a connection
 * and (N+1) implies a non-directly connected pair (NDC).</p>
 *
 * <p>The text format used is exactly the same used by [6] for
 * compatibility reasons.</p>
 *
 * <p>Example: Notice that before nPairs there is an empty space,
 * numbers from the adjacency matrix all have two spaces to its
 * right, that after each row of said matrix there is an empty
 * line, and finally in the UnPairedArc lines, all numbers have
 * a single space to its right. These details are not critical,
 * however are kept for consistency between all data sets.</p>
 * <pre>
 * nNodes=4;\n
 * ·nPairs=2;\n
 * Cost=\n
 * \n
 * 0··5··0··0··\n
 * \n
 * 0··0··5··0··\n
 * \n
 * 0··5··0··0··\n
 * \n
 * 0··0··5··0··\n
 * \n
 * UnPairedArc=
 * 1·2·\n
 * 2·3·\n
 *
 * </pre>
 *
 * @version 0.0.1 (2020.01.08)
 */
public class RLPInstanceWriter implements InstanceWriter<RLPInstance> {


    @Override
    public void writeInstance(RLPInstance instance, String filePath) {
        int nodes = instance.getV();
        int ndc = (nodes * (nodes - 1) / 2) - instance.getE();

        boolean matrix[][] = generateMatrix(instance);

        File file = new File(filePath);
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.printf("nNodes=%d;\n nPairs=%d;\n", instance.getV(), ndc);
            printMatrix(writer, matrix);
            printNDC(writer, matrix, ndc);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    private void printMatrix(PrintWriter writer, boolean[][] matrix) {
        //Convention:
        //0 ----> Connected
        //N*1 --> Non-connected
        final int EDGE = 0;
        final int NDC = matrix.length + 1;

        writer.printf("Cost=\n\n");
        for (int i = 0; i < matrix.length; i++) {
            for (int j=0; j < matrix.length; j++) {
                int value = matrix[i][j] || i==j ? EDGE : NDC;
                writer.printf("%d  ", value);
            }
            writer.printf("\n\n");
        }
    }


    private void printNDC(PrintWriter writer, boolean[][] matrix, int ndc) {
        //Nodes are numbered from [1, N] in the file
        final int NODE_OFFSET = 1;

        writer.printf("UnPairedArc=\n");
        int actual_ndc = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = i + 1; j < matrix.length; j++) {
                if (!matrix[i][j]) {
                    writer.printf("%d %d \n", i + NODE_OFFSET, j + NODE_OFFSET);
                    actual_ndc++;
                }
            }
        }
        assert (ndc == actual_ndc);
    }


    private boolean[][] generateMatrix(RLPInstance instance) {
        int n = instance.getV();
        int edges = 0;
        boolean[][] matrix = new boolean[n][n];
        for (int v1 = 0; v1 < n; v1++) {
            for (Integer v2 : instance.getEdges(v1)) {
                matrix[v1][v2] = true;
                edges++;
                assert (v2 < n);
                assert (v1 != v2);
                assert (v1 < v2 || matrix[v2][v1]); //assert symmetric matrix
            }
        }
        assert (edges / 2 == instance.getE());
        return matrix;
    }
}
