package minimax;

import model.TableState;

public interface IHeuristic {
    //Se la mossa conduce alla vittoria il risultato sarà +infty - maxDepth + depth
    //Così vengono privilegiate le strategie che conducono più velocemente alla vittoria
    double evaluate(TableState s, int depth);
}
