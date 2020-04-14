package minimax;

import model.Move;

import java.util.List;

public class MoveManager {
    private List<Move> movesToAssign;
    private int idx;
    private double bestScore;
    private Move bestMove;

    public MoveManager(List<Move> movesToAssign) {
        this.movesToAssign = movesToAssign;
        this.idx = 0;
    }

    synchronized Move getMove() {
        if (idx < movesToAssign.size()) {
            return movesToAssign.get(idx++);
        }

        return null;
    }

    synchronized public void updateMove(Move m) {
        if (m.getCosto() > bestScore) {
            bestScore = m.getCosto();
            bestMove = m;
        }
    }

    public Move getBestMove() {
        return bestMove;
    }
}
