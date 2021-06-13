//package grafo.rlp.test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import grafo.rlp.data.RLPDataSetManager;
//import grafo.rlp.data.RLPInstance;
//
//class DataSetManager extends RLPDataSetManager {
//	private RLPDataSetManager data;
//
//	@BeforeAll
//	static void setUpBeforeClass() {
//	}
//
//	@AfterAll
//	static void tearDownAfterClass() {
//	}
//
//	@BeforeEach
//	void setUp() {
//
//		data = new RLPDataSetManager();
//	}
//
//	@AfterEach
//	void tearDown() {
//	}
//
//	@Test
//    void shouldFailWhenPathNotExists(){
//	    assertThrows(RuntimeException.class,() -> data.addDataSet("Invalid", "./fake_path/shouldfail"));
//
//    }
//
//	@Test
//	void canLoadSmallInstances() {
//		Iterable<RLPInstance> smallInstances = data.getDataSet(RLPDataSetManager.small_random);
//		int expected_instances = 280;
//		int read_instances=0;
//		for (RLPInstance instance : smallInstances) {
//			if(instance.validate()) //validate() prints to System.err inconsistencies
//				read_instances++;
//		}
//		assertEquals(expected_instances, read_instances);
//	}
//
//	@Test
//	void canLoadLargeInstances() {
//		Iterable<RLPInstance> largeInstances = data.getDataSet(RLPDataSetManager.large);
//		int expected_instances = 200;
//		int read_instances = 0;
//		for (RLPInstance instance : largeInstances) {
//			if(instance.validate()) //validate() prints to System.err inconsistencies
//				read_instances++;
//		}
//		assertEquals(expected_instances, read_instances);
//	}
//
//
//}
