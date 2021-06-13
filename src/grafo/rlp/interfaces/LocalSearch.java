package grafo.rlp.interfaces;


import jdlib.data.interfaces.Solution;

public interface LocalSearch<S extends Solution> {

    void search(S solution);

}
