package minimax;

import model.TableState;

public interface IHeuristic {
    double evaluate(TableState s);
}
