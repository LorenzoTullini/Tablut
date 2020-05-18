package minimax;

import client.TimeManager;
import model.Move;
import model.TableState;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

class SearchStatus {
    private double alpha, bestVal;
    private Move bestMove;
    private boolean stop;
    private final Set<Integer> alreadyVisitedStates;
    private final Semaphore barrier;
    private int done;

    private LinkedList<Move> allMoves;
    private LinkedList<Move> orderedMoves;
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
        orderedMoves = new LinkedList<>();
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
            alpha = Math.max(alpha, bestVal);
        }

        orderedMoves.add(move);

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

            orderedMoves.sort((m1, m2) -> Double.compare(m2.getCosto(), m1.getCosto()));
            allMoves = new LinkedList<>(orderedMoves.subList(0, orderedMoves.size() / 2));
        }
    }

    public void reset() {
        bestMove = null;
        alpha = Double.NEGATIVE_INFINITY;
        bestVal = Double.NEGATIVE_INFINITY;
        done = 0;
    }

    public void setInitialConditions(LinkedList<Move> moves, TableState state, TimeManager timeManager) {
        this.initialState = state;
        this.allMoves = moves;
        this.timeManager = timeManager;
        orderedMoves = new LinkedList<>();
    }

    public LinkedList<Move> getMoves() {
        return allMoves;
    }

    public TableState getInitialState() {
        return initialState;
    }

    public TimeManager getTimeManager() {
        return timeManager;
    }
}
