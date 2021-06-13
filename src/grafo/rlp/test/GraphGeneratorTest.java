//package grafo.rlp.test;
//
//import grafo.rlp.data.InstanceGenerator;
//import grafo.rlp.data.RLPInstance;
//import grafo.rlp.data.RLPInstanceReader;
//import grafo.rlp.data.RLPInstanceWriter;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.Collections;
//import java.util.Random;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class GraphGeneratorTest {
//
//    RLPInstanceWriter writer;
//    RLPInstanceReader reader;
//
//    @BeforeEach
//    void setUp() {
//        writer = new RLPInstanceWriter();
//        reader = new RLPInstanceReader();
//    }
//
//    @AfterEach
//    void tearDown() {
//    }
//
//    @Test
//    void canGenerateInstance_withExpectedEdges() {
//        int[] n = {10, 40, 100, 500};
//        float[] p = {0.9f, 0.1f, 0.4f, 0.7f};
//        int[] ndc = {33, 75, 1941, 86976};
//        int[] edges = {12, 705, 3009, 37774};
//
//        for (int i = 0; i < n.length; i++) {
//            RLPInstance instance = InstanceGenerator.generateInstance(n[i], p[i]);
//            int actual_edges = instance.getE();
//            assertEquals(edges[i], actual_edges);
//            assertTrue(instance.validate());
//        }
//    }
//
//    @Test
//    void canWriteAndRecoverInstance() {
//        String save_path = "./resources/generatedInstances/" + "aux.txt";
//        //Generates a random instance
//        RLPInstance instance = InstanceGenerator.generateInstance(10, 0.1f, 0);
//        //Write and read that instance
//        writer.writeInstance(instance, save_path);
//        RLPInstance readInstance = reader.readInstance(save_path);
//        assertEqualInstances(instance, readInstance);
//    }
//
//    @Test
//    void canGenerateRandomInstances_withoutExceptions() {
//        int[] ns = {10,20,30,50, 100, 500};
//        float[] ps = {0f, 0.1f, 0.3f, 0.7f, 1.0f};
//        String save_path = "./resources/generatedInstances/" + "aux.txt";
//
//        Random rng = new Random(0);
//
//        for (int n : ns) {
//            for (float p : ps) {
//                RLPInstance instance = InstanceGenerator.generateInstance(n,p, rng);
//                int expected = expectedEdges(n,p);
//                assertEquals(expected, instance.getE());
//                //Write and read that instance
//                writer.writeInstance(instance, save_path);
//                RLPInstance readInstance = reader.readInstance(save_path);
//                assertEqualInstances(instance, readInstance);
//            }
//        }
//    }
//
//
//
//
//
//
//    private int expectedEdges(int n, float p){
//        //E = floor((1-p)*(E_max - (n-1)) +(n-1)
//        int max_edges = n*(n-1)/2; //edges in complete graph
//        int min_edges = (n-1); //edges in a feasible instance
//        return (int) Math.floor((1-p)*(max_edges-min_edges) + (n-1));
//    }
//
//    private int expectedNDC(int n, float p){
//        //NDC = ceil(p*()E_max - (n-1)
//        int max_edges = n*(n-1)/2; //edges in complete graph
//        int min_edges = (n-1); //edges in a feasible instance
//        return (int) Math.ceil(p*(max_edges-min_edges));
//    }
//
//
//    private void assertEqualInstances(RLPInstance expectedInstance, RLPInstance instance) {
//        //both instances should be exactly the same
//        assertEquals(expectedInstance.getV(), instance.getV());
//        assertEquals(expectedInstance.getE(), instance.getE());
//        for (int i = 0; i < expectedInstance.getV(); i++) {
//            Collections.sort(expectedInstance.getEdges(i));
//            Collections.sort(instance.getEdges(i));
//            String list1 = "v" + i + ": " + expectedInstance.getEdges(i).toString();
//            String list2 = "v" + i + ": " + instance.getEdges(i).toString();
//            assertEquals(list1, list2);
//        }
//
//    }
//
//}