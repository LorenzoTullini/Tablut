package minimax;

import client.TimeManager;
import model.Move;
import model.PlayerType;
import model.TableState;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Collectors;


public class Minimax {
    private final int maxDepth;
    //TODO Le euristiche devono tenere conto anche della possibilità che una mossa possa condurre alla vittoria
    private static IHeuristic[] whiteEheuristic = null;
    private static IHeuristic[] blackEheuristic = null;
    //L'avversario gioca sempre come min
    private final PlayerType myColour, opponentColour;
    private final IHeuristic[] myHeuristic, opponentHeuristic;

    public Minimax(PlayerType myColour, int maxDepth) {
        if (whiteEheuristic == null || blackEheuristic == null) {
            //se non sono già stati inizializzati
            whiteEheuristic = new IHeuristic[3];
            blackEheuristic = new IHeuristic[3];

            //inizializzazione euristiche
            whiteEheuristic[0] = (TableState s, int depth) ->
                    s.getBlackPiecesCount() - s.getBlackPiecesCount() + s.getKingDistance() +
                            ((s.hasWhiteWon()) ? 100 + maxDepth - depth : 0) +
                            ((s.hasBlackWon()) ? -(100 + maxDepth - depth) : 0);
            whiteEheuristic[1] = (TableState s, int depth) ->
                    s.getBlackPiecesCount() - s.getBlackPiecesCount() + 2 * s.getKingDistance() +
                            ((s.hasWhiteWon()) ? 100 + maxDepth - depth : 0) +
                            ((s.hasBlackWon()) ? -(100 + maxDepth - depth) : 0);
            whiteEheuristic[2] = (TableState s, int depth) ->
                    s.getBlackPiecesCount() - s.getBlackPiecesCount() + 3 * s.getKingDistance() +
                            ((s.hasWhiteWon()) ? 100 + maxDepth - depth : 0) +
                            ((s.hasBlackWon()) ? -(100 + maxDepth - depth) : 0);

            //TODO aggiugnere conteggio dei pezzi in ognuno dei quattro angoli
            blackEheuristic[0] = (TableState s, int depth) ->
                    s.getBlackPiecesCount() - s.getWhitePiecesCount() - s.getKingDistance() +
                            ((s.hasBlackWon()) ? 100 + maxDepth - depth : 0) +
                            ((s.hasWhiteWon()) ? -(100 + maxDepth - depth) : 0);
            blackEheuristic[1] = (TableState s, int depth) ->
                    2 * (s.getBlackPiecesCount() - s.getWhitePiecesCount()) - 1.5 * s.getKingDistance() +
                            ((s.hasBlackWon()) ? 100 + maxDepth - depth : 0) +
                            ((s.hasWhiteWon()) ? -(100 + maxDepth - depth) : 0);
            blackEheuristic[2] = (TableState s, int depth) ->
                    3 * (s.getBlackPiecesCount() - s.getWhitePiecesCount()) - 1.5 * s.getKingDistance() +
                            ((s.hasBlackWon()) ? 100 + maxDepth - depth : 0) +
                            ((s.hasWhiteWon()) ? -(100 + maxDepth - depth) : 0);
        }

        this.maxDepth = maxDepth;

        this.myColour = myColour;
        this.opponentColour = (myColour == PlayerType.WHITE) ? PlayerType.BLACK : PlayerType.WHITE;

        this.myHeuristic = (myColour == PlayerType.WHITE) ? whiteEheuristic : blackEheuristic;
        this.opponentHeuristic = (myColour == PlayerType.BLACK) ? whiteEheuristic : blackEheuristic;
    }


    public Move minimax(TableState initialState, TimeManager gestore, int turn) {
        return performMinimax(initialState, gestore, true, turn, 0, null);
    }

    public Move alphabeta(TableState initialState, TimeManager gestore, int turn) {
        return performAlphabeta(initialState, gestore, true, turn, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, null);
    }

    public Move parallelAlphaBeta(TableState initialState, TimeManager gestore, int turn, int threadNumber) {
        //Lancia in parallelo threadNumber thread paralleli che effettuano il calcolo su threadNumber sottoalberi distinti in parallelo.
        //Da un singolo problema di ricerca ne abbiamo ora n distinti che però possono essere eseguiti in parallelo
        ForkJoinPool fjPool = new ForkJoinPool(threadNumber);
        List<Move> allPossibleMoves = initialState.getAllMovesFor(myColour);
        Optional<Move> result;

        ForkJoinTask<List<Move>> task = fjPool.submit(() ->
                allPossibleMoves.parallelStream().map((Move m) ->
                        performAlphabeta(initialState.performMove(m), gestore, false, turn + 1, 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, m)
                ).collect(Collectors.toList()));

        try {
            result = task.get().stream().max((Move m1, Move m2) -> {
                int res = 0;

                if (m1.getCosto() < m2.getCosto()) {
                    res = -1;
                } else if (m1.getCosto() > m2.getCosto()) {
                    res = 1;
                }

                return res;
            });
        } catch (Exception e) {
            result = Optional.empty();
        }

        return result.orElse(null);
    }

    private Move performMinimax(TableState state, TimeManager gestore, boolean isMaxTurn, int turn, int currentDepth, Move performedMove) {
        List<Move> allPossibleMoves = state.getAllMovesFor((isMaxTurn) ? myColour : opponentColour);
        //TODO deve entare in questo if anche se si tratta di uno stato di vittoria
        if (currentDepth == maxDepth || gestore.isEnd() || allPossibleMoves.isEmpty() || state.hasBlackWon() || state.hasWhiteWon()) {
            //valuta il nodo corrente
            int heuristicIndex;
            if (turn < 40) {
                heuristicIndex = 0;
            } else if (turn < 70) {
                heuristicIndex = 1;
            } else {
                heuristicIndex = 2;
            }
            //Per scrupolo, probabilmente si può togliere
            if (performedMove != null) {
                performedMove.setCosto(isMaxTurn ?
                        myHeuristic[heuristicIndex].evaluate(state, currentDepth) :
                        -opponentHeuristic[heuristicIndex].evaluate(state, currentDepth));
            }

            return performedMove;
        }


        Move bestMove = null;
        double bestCost;

        if (isMaxTurn) {
            bestCost = Double.NEGATIVE_INFINITY;
            TableState newState;
            for (Move m : allPossibleMoves) {
                newState = state.performMove(m);
                performMinimax(newState, gestore, false, turn + 1, currentDepth + 1, m);

                if (m.getCosto() > bestCost) {
                    bestCost = m.getCosto();
                    bestMove = m;
                }
            }
        } else {
            bestCost = Double.POSITIVE_INFINITY;
            TableState newState;
            for (Move m : allPossibleMoves) {
                newState = state.performMove(m);
                performMinimax(newState, gestore, true, turn + 1, currentDepth + 1, m);

                if (m.getCosto() < bestCost) {
                    bestCost = m.getCosto();
                    bestMove = m;
                }
            }
        }

        return bestMove;
    }

    private Move performAlphabeta(TableState state, TimeManager gestore, boolean isMaxTurn, int turn, int currentDepth, double alpha, double beta, Move performedMove) {
        List<Move> allPossibleMoves = state.getAllMovesFor((isMaxTurn) ? myColour : opponentColour);
        //TODO deve entare in questo if anche se si tratta di uno stato di vittoria
        if (currentDepth == maxDepth || gestore.isEnd() || allPossibleMoves.isEmpty() || state.hasBlackWon() || state.hasWhiteWon()) {
            //valuta il nodo corrente
            int heuristicIndex;
            if (turn < 40) {
                heuristicIndex = 0;
            } else if (turn < 70) {
                heuristicIndex = 1;
            } else {
                heuristicIndex = 2;
            }
            //Per scrupolo, probabilmente si può togliere
            if (performedMove != null) {
                performedMove.setCosto(isMaxTurn ?
                        myHeuristic[heuristicIndex].evaluate(state, currentDepth) :
                        -opponentHeuristic[heuristicIndex].evaluate(state, currentDepth));
            }

            return performedMove;
        }


        Move bestMove = null;
        double bestCost;

        if (isMaxTurn) {
            bestCost = Double.NEGATIVE_INFINITY;
            TableState newState;
            for (Move m : allPossibleMoves) {
                newState = state.performMove(m);
                performAlphabeta(newState, gestore, false, turn + 1, currentDepth + 1, alpha, beta, m);

                if (m.getCosto() > bestCost) {
                    bestCost = m.getCosto();
                    bestMove = m;
                }

                alpha = Math.max(alpha, m.getCosto());
                if (alpha >= beta) {
                    return bestMove;
                }
            }
        } else {
            bestCost = Double.POSITIVE_INFINITY;
            TableState newState;
            for (Move m : allPossibleMoves) {
                newState = state.performMove(m);
                performAlphabeta(newState, gestore, true, turn + 1, currentDepth + 1, alpha, beta, m);

                if (m.getCosto() < bestCost) {
                    bestCost = m.getCosto();
                    bestMove = m;
                }

                beta = Math.min(beta, m.getCosto());
                if (alpha >= beta) {
                    return bestMove;
                }
            }
        }

        return bestMove;
    }
}
