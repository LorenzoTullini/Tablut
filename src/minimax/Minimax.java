package minimax;

import model.Move;
import model.PlayerType;
import model.TableState;

public class Minimax {
    public Move minimax(TableState initialState, Object gestore, PlayerType playerType, int turn) {
        return callMinimax(initialState, gestore, playerType, turn, 1);
    }

    public TableState getNewTableState() {
        return null;
    }

    private Move callMinimax(TableState initialState, Object gestore, PlayerType playerType, int turn, int level) {
        return null;
    }
}
