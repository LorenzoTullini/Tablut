package minimax;

import client.TimeManager;
import model.Move;
import model.PlayerType;
import model.TableState;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.Semaphore;


public class Minimax extends Thread {
    private int maxDepth;
    private final double[] weights;

    //L'avversario gioca sempre come min
    private final PlayerType myColour, opponentColour;
    private final IHeuristic myHeuristic, opponentHeuristic;
    private Set<Integer> alreadyVisitedStates;
    private final Semaphore sem;
    private final SearchStatus searchStatus;
    private final int idx;

    public Minimax(PlayerType myColour, int maxDepth, double[] weights, Semaphore sem, int idx, SearchStatus status) {
        this.sem = sem;
        this.idx = idx;
        this.searchStatus = status;
        this.weights = weights;

        //inizializzazione euristiche
        IHeuristic whiteEheuristic = (TableState s, int depth) ->
                weights[0] * (16 - s.getBlackPiecesCount()) //attacco
                        + weights[1] * (s.getWhitePiecesCount()) //difesa
                        + weights[2] * (6 - s.getKingDistance()) //distanza del re dalla vittoria
                        + weights[3] * ((s.hasWhiteWon()) ? 100 + maxDepth - depth : 0) //vittoria
                        + weights[4] * ((s.hasBlackWon()) ? -(100 + maxDepth - depth) : 0); //sconfitta


        IHeuristic blackEheuristic = (TableState s, int depth) ->
                weights[5] * (9 - s.getWhitePiecesCount()) //attacco
                        + weights[6] * (s.getBlackPiecesCount()) //difesa
                        + weights[7] * s.getKingDistance() //distanza del re dalla vittoria
                        + weights[8] * ((s.hasBlackWon()) ? 100 + maxDepth - depth : 0) //vittoria
                        + weights[9] * ((s.hasWhiteWon()) ? -(100 + maxDepth - depth) : 0); //sconfitta

        this.maxDepth = maxDepth;

        this.myColour = myColour;
        this.opponentColour = (myColour == PlayerType.WHITE) ? PlayerType.BLACK : PlayerType.WHITE;

        this.myHeuristic = (myColour == PlayerType.WHITE) ? whiteEheuristic : blackEheuristic;
        this.opponentHeuristic = (myColour == PlayerType.WHITE) ? blackEheuristic : whiteEheuristic;

        this.alreadyVisitedStates = new HashSet<>();
    }


    public void setMaxDepth(int newMaxDepth) {
        maxDepth = newMaxDepth;
    }

    @Override
    public void run() {
        TableState initialState = null;
        TimeManager timeManager = null;
        int turn = 0;
        while (true) {
            try {
                sem.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (searchStatus.isStop()) {
                return;
            }

            //Ottieni le condizioni iniziali
            initialState = searchStatus.getInitialState();
            timeManager = searchStatus.getTimeManager();
            turn = searchStatus.getTurn();
            alreadyVisitedStates = new HashSet<>(searchStatus.getAlreadyVisitedStates());

            // Il primo livello va separato dagli altri perchè deve restituire una mossa e non un valore
            Move res = null;
            double val;
            double alpha = Double.NEGATIVE_INFINITY, beta = Double.POSITIVE_INFINITY;

            List<Move> allMoves = (LinkedList<Move>) initialState.getAllMovesFor(myColour);
            for (int i = idx; i < allMoves.size(); i += 4) {
                //parte con il turno di min perché questo qua è già il turno di max
                Move m = allMoves.get(i);
                TableState newState = initialState.performMove(m);
                val = performAlphabeta(newState, timeManager, false, turn + 1, 1, alpha, beta);

                alpha = searchStatus.updateBestVal(val, m);
            }

            searchStatus.done();
        }
    }

    private double performAlphabeta(@NotNull TableState state, TimeManager timeManager, boolean isMaxTurn, int turn,
                                    int currentDepth, double alpha, double beta) {
        double bestCost;
        TableState newState;
        int currentHash = state.hashCode();
        if (alreadyVisitedStates.contains(currentHash)) {
            //se lo stato è già stato visto la partita è patta
            bestCost = 0;
        } else {
            alreadyVisitedStates.add(currentHash);

            Deque<Move> allPossibleMoves = state.getAllMovesFor((isMaxTurn) ? myColour : opponentColour);

            /*
             * Condizioni di valutazione:
             *   - raggiunta profondità massima
             *   - tempo scaduto
             *   - nessuna mossa disponiile
             *   - vittoria di uno dei due giocatori
             */
            if (currentDepth == maxDepth || timeManager.isEnd() || allPossibleMoves.isEmpty() || state.hasBlackWon() ||
                    state.hasWhiteWon()) {
                //Raggiunto un nodo foglia

                bestCost = isMaxTurn ?
                        myHeuristic.evaluate(state, currentDepth) :
                        -opponentHeuristic.evaluate(state, currentDepth);
            } else {
                //Raggiunto un nodo intermedio
                if (isMaxTurn) {
                    bestCost = Double.NEGATIVE_INFINITY;

                    for (Move m : allPossibleMoves) {
                        newState = state.performMove(m);

                        bestCost = Math.max(bestCost,
                                performAlphabeta(newState, timeManager, false, turn + 1,
                                        currentDepth + 1, alpha, beta));

                        alpha = Math.max(alpha, bestCost);
                        if (alpha >= beta) {
                            break;
                        }
                    }
                } else {
                    bestCost = Double.POSITIVE_INFINITY;

                    for (Move m : allPossibleMoves) {
                        newState = state.performMove(m);

                        bestCost = Math.min(bestCost,
                                performAlphabeta(newState, timeManager, true, turn + 1,
                                        currentDepth + 1, alpha, beta));

                        beta = Math.min(beta, bestCost);
                        if (alpha >= beta) {
                            break;
                        }
                    }
                }
            }

            alreadyVisitedStates.remove(currentHash);
        }

        return bestCost;
    }

    public double[] getWeights() {
        return weights;
    }
}
