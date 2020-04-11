package minimax;

import client.TimeManager;
import model.Move;
import model.PlayerType;
import model.TableState;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class Minimax {
    private int maxDepth;
    //TODO Le euristiche devono tenere conto anche della possibilità che una mossa possa condurre alla vittoria
    private static IHeuristic[] whiteEheuristic = null;
    private static IHeuristic[] blackEheuristic = null;
    //L'avversario gioca sempre come min
    private PlayerType myColour, opponentColour;
    private IHeuristic[] myHeuristic, opponentHeuristic;

    public Minimax(PlayerType myColour, int maxDepth) {
        if (whiteEheuristic == null || blackEheuristic == null) {
            //se non sono già stati inizializzati
            whiteEheuristic = new IHeuristic[3];
            blackEheuristic = new IHeuristic[3];

            //inizializzazione euristiche
        }

        this.maxDepth = maxDepth;

        this.myColour = myColour;
        this.opponentColour = (myColour == PlayerType.WHITE) ? PlayerType.BLACK : PlayerType.WHITE;

        this.myHeuristic = new IHeuristic[3];
        this.opponentHeuristic = new IHeuristic[3];
        this.myHeuristic = (myColour == PlayerType.WHITE) ? whiteEheuristic : blackEheuristic;
        this.opponentHeuristic = (myColour == PlayerType.BLACK) ? whiteEheuristic : blackEheuristic;
    }


    public Move minimax(TableState initialState, TimeManager gestore, int turn) {
        return performMinimax(initialState, gestore, true, turn, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    private Move performMinimax(TableState state, TimeManager gestore, boolean isMaxTurn, int turn, int currentDepth, double alfa, double beta) {
        var allPossibleMoves = state.getAllMovesFor((isMaxTurn) ? myColour : opponentColour);
        Move bestMove = null;
        double bestCost;

        //determina quale euristica usare
        int heuristicIndex;
        if (turn < 40) {
            heuristicIndex = 0;
        } else if (turn < 70) {
            heuristicIndex = 1;
        } else {
            heuristicIndex = 2;
        }

        if (isMaxTurn) {
            bestCost = Double.NEGATIVE_INFINITY;
        } else {
            bestCost = Double.POSITIVE_INFINITY;
        }

        //metto prima l'if così non viene valutato una singola volta
        if (currentDepth < maxDepth && !gestore.isEnd()) {
            Move res;
            for (Move m : allPossibleMoves) {
                var newState = state.performMove(m);

                //procedi con l'esplorazione
                res = performMinimax(newState, gestore, !isMaxTurn, turn + 1, currentDepth + 1, alfa, beta);
                if (res != null) {
                    if (isMaxTurn) {
                        if (res.getCosto() > bestCost) {
                            bestCost = res.getCosto();
                            bestMove = res;
                        }

                        alfa = Math.max(alfa, bestCost);

                        if (alfa >= beta) {
                            break;
                        }
                    } else {
                        if (res.getCosto() < bestCost) {
                            bestCost = res.getCosto();
                            bestMove = res;
                        }

                        beta = Math.min(beta, bestCost);
                        if (alfa >= beta) {
                            break;
                        }
                    }
                }
            }
        } else {
            System.out.println("Sto valutando le foglie al livello: " + currentDepth);
            Instant start = Instant.now();
            for (Move m : allPossibleMoves) {
                var newState = state.performMove(m);

                //termina l'esplorazione, valuta tutti i figli e ritorna la mosaa che produce il risultato migliore
                m.setCosto(isMaxTurn ? myHeuristic[heuristicIndex].evaluate(newState, currentDepth) : opponentHeuristic[heuristicIndex].evaluate(newState, currentDepth));
                if (isMaxTurn) {
                    if (m.getCosto() > bestCost) {
                        bestCost = m.getCosto();
                        bestMove = m;
                    }

                    alfa = Math.max(alfa, bestCost);

                } else {
                    if (m.getCosto() < bestCost) {
                        bestCost = m.getCosto();
                        bestMove = m;
                    }

                    beta = Math.min(beta, bestCost);
                }
                if (alfa >= beta) {
                    break;
                }
            }
            Instant stop = Instant.now();
            System.out.println("Ho finito di valutare le foglie al livello: " + currentDepth + "in: " + Duration.between(start, stop) + " ms");
        }
        //TODO Bisogna trovare un modo per dire che se una mossa porta direttamente alla vittoria quella mossa è direttamente da scegliere. Però min cercherà sempre di evitarla...


        return bestMove;
    }
}
