//package grafo.rlp.test;
//
//import grafo.rlp.algorithm.other.RLPArticulationPoints;
//import grafo.rlp.data.RLPInstance;
//import grafo.rlp.data.Solution;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class RLPArticulationPointsTest {
//
//
//
//
//
//    @BeforeEach
//    void setUp() {
//    }
//
//    @AfterEach
//    void tearDown() {
//    }
//
//    @Test
//    void findArticulationPoints() {
//        Solution solution = generateSolution1();
//        RLPArticulationPoints<Solution> algorithm = new RLPArticulationPoints<>();
//
//        Set<Integer> ap = algorithm.findArticulationPoints(solution);
//
//        List<Integer> expected = List.of(4, 5, 8);
//
//        assertEquals(expected.toString(), new TreeSet<>(ap).toString());
//        assertTrue(ap.containsAll(expected));
//        assertEquals(3, ap.size());
//
//        solution.removeRegenerator(9);
//        ap = algorithm.findArticulationPoints(solution);
//        expected = List.of(3, 4, 5, 8);
//        assertEquals(expected.toString(), new TreeSet<>(ap).toString());
//        assertTrue(ap.containsAll(expected));
//        assertEquals(4, ap.size());
//
//
//    }
//
//    public static Solution generateSolution1(){
//        String draw =
//                "   0     1   \n" +
//                "   |     |      \n" +
//                "2--3--4--5--6   \n" +
//                "|  |  |         \n" +
//                "7--8--9-10     \n" +
//                "   |            \n" +
//                "   11"          ;
//        String labeled =
//                "         1   \n" +
//                "         |      \n" +
//                "   3--4--5--6   \n" +
//                "   |  |         \n" +
//                "   8--9        \n" +
//                "   |            \n" +
//                "   11"          ;
//        String input =
//                "12 12 8\n" +
//                "1 3 4 5 6 8 9 11\n" +
//                "0 3\n" +
//                "1 5\n" +
//                "2 3\n" +
//                "2 7\n" +
//                "3 4\n" +
//                "3 8\n" +
//                "4 5\n" +
//                "4 9\n" +
//                "5 6\n" +
//                "7 8\n" +
//                "8 9\n" +
//                "8 11\n" +
//                "9 10";
//
//        Solution s = parseSolution(new Scanner(input));
//        return s;
//    }
//
//
//    /**
//     * Parses a RLPSolution given the following fotmat:
//     * <pre>
//     * vertex edges labeled
//     * r1 r2 r3 ... r4
//     * v1 u1
//     * v1 u2
//     * ...
//     * vE uE
//     * </pre>
//     *
//     * <p>Where r_i are labeled nodes, and each pair (u,v) denotes an edge.</p>
//     * @param sc
//     * @return
//     */
//    private static Solution parseSolution(Scanner sc) {
//        int v = sc.nextInt();
//        int e = sc.nextInt();
//        int r = sc.nextInt();
//        List<Integer> labeled = new ArrayList<>();
//        for(int i=0; i<r; i++)
//            labeled.add(sc.nextInt());
//
//        RLPInstance instance = new RLPInstance(v);
//        for(int i=0; i<e; i++){
//            int u = sc.nextInt();
//            int w = sc.nextInt();
//            instance.addEdge(u,w);
//        }
//
//        Solution solution = new Solution(instance);
//        labeled.forEach(integer -> solution.putRegenerator(integer));
//        return solution;
//    }
//}
//
//
//
//
