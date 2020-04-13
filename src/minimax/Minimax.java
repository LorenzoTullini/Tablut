package minimax;

import client.TimeManager;
import model.Move;
import model.PlayerType;
import model.TableState;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;


public class Minimax {
    private final int maxDepth;
    //L'avversario gioca sempre come min
    private final PlayerType myColour, opponentColour;
    private final IHeuristic[] myHeuristic, opponentHeuristic;
    private final Set<Integer> alreadyVisitedStates;
    private final ForkJoinPool fjPool;

    public Minimax(PlayerType myColour, int maxDepth, int threadNumber) {
        IHeuristic[] whiteEheuristic = new IHeuristic[3];
        IHeuristic[] blackEheuristic = new IHeuristic[3];

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


        this.maxDepth = maxDepth;

        this.myColour = myColour;
        this.opponentColour = (myColour == PlayerType.WHITE) ? PlayerType.BLACK : PlayerType.WHITE;

        this.myHeuristic = (myColour == PlayerType.WHITE) ? whiteEheuristic : blackEheuristic;
        this.opponentHeuristic = (myColour == PlayerType.WHITE) ? blackEheuristic : whiteEheuristic;

        this.alreadyVisitedStates = new HashSet<>();

        this.fjPool = new ForkJoinPool(threadNumber);
    }


    public Move minimax(@NotNull TableState initialState, TimeManager gestore, int turn) {
        //aggiungi lo stato prodotto dall'avversario
        alreadyVisitedStates.add(initialState.hashCode());
        Move res = performMinimax(initialState, gestore, true, turn, 0, null);
        //aggiungi lo stato appena prodotto
        alreadyVisitedStates.add(initialState.performMove(res).hashCode());
        return res;
    }

    public Move alphabeta(@NotNull TableState initialState, TimeManager gestore, int turn) {
        //aggiungi lo stato prodotto dall'avversario
        alreadyVisitedStates.add(initialState.hashCode());
        Move res = performAlphabeta(initialState, gestore, true, turn, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, null);
        //aggiungi lo stato appena prodotto
        alreadyVisitedStates.add(initialState.performMove(res).hashCode());
        return res;
    }

    public Move parallelAlphaBeta(@NotNull TableState initialState, TimeManager gestore, int turn) {
        //Lancia in parallelo threadNumber thread paralleli che effettuano il calcolo su threadNumber sottoalberi distinti in parallelo.
        //Da un singolo problema di ricerca ne abbiamo ora n distinti che però possono essere eseguiti in parallelo

        List<Move> allPossibleMoves = initialState.getAllMovesFor(myColour);
        Move result = null;

        alreadyVisitedStates.add(initialState.hashCode());
        ForkJoinTask<Move> task = fjPool.submit(() ->
                allPossibleMoves.parallelStream().map((Move m) ->
                        performAlphabeta(initialState.performMove(m), gestore, false, turn + 1, 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, m)
                ).max((Move m1, Move m2) -> {
                    int res = 0;

                    if (m1.getCosto() < m2.getCosto()) {
                        res = -1;
                    } else if (m1.getCosto() > m2.getCosto()) {
                        res = 1;
                    }

                    return res;
                }).orElse(null));

        try {
            result = task.get();
            alreadyVisitedStates.add(initialState.performMove(result).hashCode());
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }

        return result;
    }

    private Move performMinimax(@NotNull TableState state, TimeManager gestore, boolean isMaxTurn, int turn, int currentDepth, Move performedMove) {
        if (performedMove != null && alreadyVisitedStates.contains(state.hashCode())) {
            performedMove.setCosto(0);
            return performedMove;
        }

        List<Move> allPossibleMoves = state.getAllMovesFor((isMaxTurn) ? myColour : opponentColour);

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

    private Move performAlphabeta(@NotNull TableState state, TimeManager gestore, boolean isMaxTurn, int turn, int currentDepth, double alpha, double beta, Move performedMove) {
        if (performedMove != null && alreadyVisitedStates.contains(state.hashCode())) {
            performedMove.setCosto(0);
            return performedMove;
        }

        List<Move> allPossibleMoves = state.getAllMovesFor((isMaxTurn) ? myColour : opponentColour);

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

    //Controlla in maniera più accurata la presenza di stati già visitati
    private Move performAlphabetaWithHistoryCheck(@NotNull TableState state, TimeManager gestore, boolean isMaxTurn, int turn, int currentDepth, double alpha, double beta, Move performedMove, @NotNull Set<Integer> previousHistory) {
        //se questo stato è già stato raggiunto
        if (performedMove != null && alreadyVisitedStates.contains(state.hashCode())) {
            performedMove.setCosto(0);
            return performedMove;
        }

        List<Move> allPossibleMoves = state.getAllMovesFor((isMaxTurn) ? myColour : opponentColour);

        //se è ora di terminare l'esplorazione
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

        //esplora il sottoalbero
        Move bestMove = null;
        double bestCost;
        //Ad ogni nodo si crea un universo alternativo che non deve andare ad avere effetti sugli altri universi
        Set<Integer> myHistory = new HashSet<>(previousHistory);
        myHistory.add(state.hashCode());
        if (isMaxTurn) {
            bestCost = Double.NEGATIVE_INFINITY;
            TableState newState;
            for (Move m : allPossibleMoves) {
                newState = state.performMove(m);
                performAlphabetaWithHistoryCheck(newState, gestore, false, turn + 1, currentDepth + 1, alpha, beta, m, myHistory);

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
                performAlphabetaWithHistoryCheck(newState, gestore, true, turn + 1, currentDepth + 1, alpha, beta, m, myHistory);

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
