package minimax;

import client.TimeManager;
import model.Move;
import model.PlayerType;
import model.TableState;

import java.util.ArrayList;
import java.util.List;


public class Minimax {
    private static int maxDepth = 3;
    private static IHeuristic[] whiteEheuristic = null;
    private static IHeuristic[] blackEheuristic = null;
    //L'avversario gioca sempre come min
    private PlayerType myColour, opponentColour;
    private IHeuristic[] myHeuristic, opponentHeuristic;

    public Minimax(PlayerType myColour) {
        if (whiteEheuristic == null || blackEheuristic == null) {
            //se non sono già stati inizializzati
            whiteEheuristic = new IHeuristic[3];
            blackEheuristic = new IHeuristic[3];
        }

        this.myColour = myColour;
        this.opponentColour = (myColour == PlayerType.WHITE) ? PlayerType.BLACK : PlayerType.WHITE;

        this.myHeuristic = new IHeuristic[3];
        this.opponentHeuristic = new IHeuristic[3];
        this.myHeuristic = (myColour == PlayerType.WHITE) ? whiteEheuristic : blackEheuristic;
        this.opponentHeuristic = (myColour == PlayerType.BLACK) ? whiteEheuristic : blackEheuristic;
    }

    public Move minimax(TableState initialState, TimeManager gestore, int turn) {
        return performMinimax(initialState, gestore, true, turn, 0);
    }

    private Move performMinimax(TableState state, TimeManager gestore, boolean isMaxTurn, int turn, int currentDepth) {
        var allPossibleMoves = state.getAllMovesFor((isMaxTurn) ? myColour : opponentColour);
        List<Move> results = new ArrayList<>();

        //determina quale euristica usare
        int heuristicIndex;
        if (turn < 40) {
            heuristicIndex = 0;
        } else if (turn < 70) {
            heuristicIndex = 1;
        } else {
            heuristicIndex = 2;
        }

        //metto prima l'if così non viene valutato una singola volta
        if (currentDepth < maxDepth && !gestore.isEnd()) {
            for (Move m : allPossibleMoves) {
                var newState = state.performMove(m);

                //procedi con l'esplorazione
                Move res = performMinimax(newState, gestore, !isMaxTurn, turn + 1, currentDepth + 1);
                if (res != null) {
                    results.add(res);
                }
            }
        } else {
            for (Move m : allPossibleMoves) {
                var newState = state.performMove(m);

                //termina l'esplorazione, valuta tutti i figli e ritorna la mosaa che produce il risultato migliore
                m.setCosto(isMaxTurn ? myHeuristic[heuristicIndex].evaluate(newState) : opponentHeuristic[heuristicIndex].evaluate(newState));
                results.add(m);
            }
        }

        Move bestMove = null;
        double bestCost;

        if (isMaxTurn) {
            bestCost = Double.NEGATIVE_INFINITY;
            //rstituisci la mossa migliore
            for (Move m : results) {
                if (m.getCosto() > bestCost) {
                    bestMove = m;
                    bestCost = m.getCosto();
                }
            }
        } else {
            bestCost = Double.POSITIVE_INFINITY;
            //rstituisci la mossa migliore
            for (Move m : results) {
                if (m.getCosto() < bestCost) {
                    bestMove = m;
                    bestCost = m.getCosto();
                }
            }
        }

        return bestMove;
    }

}
