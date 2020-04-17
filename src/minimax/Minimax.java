package minimax;

import client.TimeManager;
import model.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

//import org.jetbrains.annotations.NotNull;


public class Minimax {
    private static double[] defaultWeights = {1, 1, 1, 1.5, 1, 2, 1.5, 2.5, 8, 1};

    private final int maxDepth;
    private double[] weights;

    //L'avversario gioca sempre come min
    private final PlayerType myColour, opponentColour;
    private final IHeuristic myHeuristic, opponentHeuristic;
    private final Set<Integer> alreadyVisitedStates;

    private Random rndGen;

    public Minimax(PlayerType myColour, int maxDepth) {
        this(myColour, maxDepth, defaultWeights);
    }


    public Minimax(PlayerType myColour, int maxDepth, double[] weights) {
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

        this.alreadyVisitedStates = new HashSet<Integer>();
    }


    public Move alphabeta(@NotNull TableState initialState, TimeManager timeManager, int turn) {
        // Il primo livello va separato dagli altri perchè deve ritornare una mossa e non un valore
        Move res = null;
        double bestCost = Double.NEGATIVE_INFINITY;
        double val;
        double alpha = Double.NEGATIVE_INFINITY, beta = Double.POSITIVE_INFINITY;

        for (Move m : initialState.getAllMovesFor(myColour)) {
            //parte con il turno di min perché questo qua è già il turno di max
            TableState newState = initialState.performMove(m);
            val = performAlphabeta(newState, timeManager, false, turn + 1, 1, alpha, beta);

            if (val > bestCost) {
                res = m;
                bestCost = val;
            }

            alpha = Math.max(bestCost, alpha);
        }
        if (res != null) {
            //Se siamo riusciti a fare una mossa
            res.setCosto(bestCost);
        }

        return res;
    }

    private double performAlphabeta(@NotNull TableState state, TimeManager timeManager, boolean isMaxTurn, int turn,
                                    int currentDepth, double alpha, double beta) {
        if (alreadyVisitedStates.contains(state.hashCode())) {
            //se mlo stato è già stato visto la partita è patta
            return 0;
        }

        List<Move> allPossibleMoves = state.getAllMovesFor((isMaxTurn) ? myColour : opponentColour);

        if (currentDepth == maxDepth || timeManager.isEnd() || allPossibleMoves.isEmpty() || state.hasBlackWon() || state.hasWhiteWon()) {
            //valuta il nodo corrente
            return isMaxTurn ?
                    myHeuristic.evaluate(state, currentDepth) :
                    -opponentHeuristic.evaluate(state, currentDepth);
        }

        double bestCost;
        TableState newState = null;
        if (isMaxTurn) {
            bestCost = Double.NEGATIVE_INFINITY;

            for (Move m : allPossibleMoves) {
                newState = state.performMove(m);

                bestCost = Math.max(bestCost, performAlphabeta(newState, timeManager, false, turn + 1, currentDepth + 1, alpha, beta));

                alpha = Math.max(alpha, bestCost);
                if (alpha >= beta) {
                    break;
                }
            }
        } else {
            bestCost = Double.POSITIVE_INFINITY;

            for (Move m : allPossibleMoves) {
                newState = state.performMove(m);
                var val = performAlphabeta(newState, timeManager, true, turn + 1, currentDepth + 1, alpha, beta);
                bestCost = Math.min(bestCost, val);

                beta = Math.min(beta, bestCost);
                if (alpha >= beta) {
                    break;
                }
            }
        }

        return bestCost;
    }

    public double[] getWeights() {
        return weights;
    }
}
