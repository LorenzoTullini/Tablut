package minimax;

import client.TimeManager;
import model.Move;
import model.TableState;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class SearchStatus {
    private double alpha, bestVal;
    private Move bestMove;
    private boolean stop;
    private final Set<Integer> alreadyVisitedStates;
    private Semaphore barrier;
    private int done;

    private TableState initialState;
    private TimeManager timeManager;
    private int turn;

    private List<Move> movesToCheck;

    public SearchStatus(Semaphore b) {
        alpha = Double.NEGATIVE_INFINITY;
        bestVal = Double.NEGATIVE_INFINITY;
        stop = false;
        alreadyVisitedStates = new HashSet<>();
        barrier = b;
        done = 0;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getBestVal() {
        return bestVal;
    }

    public synchronized double updateBestVal(double val, Move move) {
        if (val > bestVal) {
            bestVal = val;
            bestMove = move;
            bestMove.setCosto(bestVal);
            alpha = Math.max(alpha, bestVal);
        }

        return alpha;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public boolean isStop() {
        return stop;
    }

    public void updateVisitedStates(int hash) {
        alreadyVisitedStates.add(hash);
    }

    public Set<Integer> getAlreadyVisitedStates() {
        return alreadyVisitedStates;
    }

    public List<Move> getMovesToCheck() {
        return movesToCheck;
    }

    public void setMovesToCheck(List<Move> movesToCheck) {
        this.movesToCheck = movesToCheck;
    }

    public Move getBestMove() {
        return bestMove;
    }

    public synchronized void done() {
        done++;
        //System.out.println("Fatto!!");
        if (done == 4) {
            barrier.release();
        }
    }

    public void reset() {
        bestMove = null;
        alpha = Double.NEGATIVE_INFINITY;
        bestVal = Double.NEGATIVE_INFINITY;
        done = 0;
    }

    public void setInitialConditions(TableState initialState, TimeManager timeManager, int turn) {
        this.initialState = initialState;
        this.timeManager = timeManager;
        this.turn = turn;
    }

    public TableState getInitialState() {
        return initialState;
    }

    public TimeManager getTimeManager() {
        return timeManager;
    }

    public int getTurn() {
        return turn;
    }
}
