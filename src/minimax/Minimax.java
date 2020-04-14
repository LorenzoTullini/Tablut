package minimax;

import client.TimeManager;
import model.Move;
import model.PlayerType;
import model.TableState;
import org.jetbrains.annotations.NotNull;

import java.util.*;

//import org.jetbrains.annotations.NotNull;


public class Minimax {
    private final int maxDepth;
    //L'avversario gioca sempre come min
    private final PlayerType myColour, opponentColour;
    private final IHeuristic[] myHeuristic, opponentHeuristic;
    private final Set<Integer> alreadyVisitedStates;
    private final int threadNumber;
    private Random rndGen;

    public Minimax(PlayerType myColour, int maxDepth, int threadNumber) {
        IHeuristic[] whiteEheuristic = new IHeuristic[3];
        IHeuristic[] blackEheuristic = new IHeuristic[3];

        //inizializzazione euristiche
        whiteEheuristic[0] = (TableState s, int depth) ->
                s.getBlackPiecesCount() - s.getBlackPiecesCount()
                        + 5 - s.getKingDistance()
                        + ((s.hasWhiteWon()) ? 100 + maxDepth - depth : 0)
                        + ((s.hasBlackWon()) ? -(100 + maxDepth - depth) : 0);
        whiteEheuristic[1] = (TableState s, int depth) ->
                s.getBlackPiecesCount() - s.getBlackPiecesCount()
                        + 2 * (5 - s.getKingDistance())
                        + ((s.hasWhiteWon()) ? 100 + maxDepth - depth : 0)
                        + ((s.hasBlackWon()) ? -(100 + maxDepth - depth) : 0);
        whiteEheuristic[2] = (TableState s, int depth) ->
                s.getBlackPiecesCount() - s.getBlackPiecesCount()
                        + 3 * (5 - s.getKingDistance())
                        + ((s.hasWhiteWon()) ? 100 + maxDepth - depth : 0)
                        + ((s.hasBlackWon()) ? -(100 + maxDepth - depth) : 0);

        blackEheuristic[0] = (TableState s, int depth) ->
                s.getBlackPiecesCount() - s.getWhitePiecesCount()
                        - (5 - s.getKingDistance())
                        + getSafeZoneProtection(s)
                        + ((s.hasBlackWon()) ? 100 + maxDepth - depth : 0)
                        + ((s.hasWhiteWon()) ? -(100 + maxDepth - depth) : 0);
        blackEheuristic[1] = (TableState s, int depth) ->
                2 * (s.getBlackPiecesCount() - s.getWhitePiecesCount())
                        - 1.5 * (5 - s.getKingDistance())
                        + getSafeZoneProtection(s)
                        + ((s.hasBlackWon()) ? 100 + maxDepth - depth : 0)
                        + ((s.hasWhiteWon()) ? -(100 + maxDepth - depth) : 0);
        blackEheuristic[2] = (TableState s, int depth) ->
                3 * (s.getBlackPiecesCount() - s.getWhitePiecesCount())
                        - 1.5 * (5 - s.getKingDistance())
                        + getSafeZoneProtection(s)
                        + ((s.hasBlackWon()) ? 100 + maxDepth - depth : 0)
                        + ((s.hasWhiteWon()) ? -(100 + maxDepth - depth) : 0);


        this.maxDepth = maxDepth;

        this.myColour = myColour;
        this.opponentColour = (myColour == PlayerType.WHITE) ? PlayerType.BLACK : PlayerType.WHITE;

        this.myHeuristic = (myColour == PlayerType.WHITE) ? whiteEheuristic : blackEheuristic;
        this.opponentHeuristic = (myColour == PlayerType.WHITE) ? blackEheuristic : whiteEheuristic;

        this.alreadyVisitedStates = Collections.synchronizedSet(new HashSet<Integer>());

        this.threadNumber = threadNumber;

        this.rndGen = new Random();
    }


    public Move minimax(@NotNull TableState initialState, TimeManager timeManager, int turn) {
        //aggiungi lo stato prodotto dall'avversario
        alreadyVisitedStates.add(initialState.hashCode());
        Move res = performMinimax(initialState, timeManager, true, turn, 0, null);
        //aggiungi lo stato appena prodotto
        alreadyVisitedStates.add(initialState.performMove(res).hashCode());
        return res;
    }

    public Move alphabeta(@NotNull TableState initialState, TimeManager timeManager, int turn) {
        //aggiungi lo stato prodotto dall'avversario
        alreadyVisitedStates.add(initialState.hashCode());
        Move res = performAlphabeta(initialState, timeManager, true, turn, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, null);
        //aggiungi lo stato appena prodotto
        alreadyVisitedStates.add(initialState.performMove(res).hashCode());
        return res;
    }

    public Move parallelAlphaBeta(@NotNull TableState initialState, TimeManager timeManager, int turn) {
        //Lancia in parallelo threadNumber thread paralleli che effettuano il calcolo su threadNumber sottoalberi distinti in parallelo.
        //Da un singolo problema di ricerca ne abbiamo ora n distinti che però possono essere eseguiti in parallelo

        List<Move> allPossibleMoves = initialState.getAllMovesFor(myColour);
        MoveManager moveManager = new MoveManager(allPossibleMoves);
        Move result = null;
        Thread[] th = new Thread[threadNumber];

        for (int idx = 0; idx < threadNumber; idx++) {
            th[idx] = new Thread(() -> {
                Move m;
                while ((m = moveManager.getMove()) != null) {
                    TableState newState = initialState.performMove(m);
                    Move res = performAlphabeta(newState, timeManager, true, turn, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, null);
                    moveManager.updateMove(res);
                }
            });
            th[idx].start();
        }
        for (int idx = 0; idx < threadNumber; idx++) {
            try {
                th[idx].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return moveManager.getBestMove();
    }

    private Move performMinimax(@NotNull TableState state, TimeManager timeManager, boolean isMaxTurn, int turn, int currentDepth, Move performedMove) {
        if (performedMove != null && alreadyVisitedStates.contains(state.hashCode())) {
            performedMove.setCosto(0);
            return performedMove;
        }

        List<Move> allPossibleMoves = state.getAllMovesFor((isMaxTurn) ? myColour : opponentColour);

        if (currentDepth == maxDepth || timeManager.isEnd() || allPossibleMoves.isEmpty() || state.hasBlackWon() || state.hasWhiteWon()) {
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
                performMinimax(newState, timeManager, false, turn + 1, currentDepth + 1, m);

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
                performMinimax(newState, timeManager, true, turn + 1, currentDepth + 1, m);

                if (m.getCosto() < bestCost) {
                    bestCost = m.getCosto();
                    bestMove = m;
                }
            }
        }

        return bestMove;
    }

    private Move performAlphabeta(@NotNull TableState state, TimeManager timeManager, boolean isMaxTurn, int turn, int currentDepth, double alpha, double beta, Move performedMove) {
        if (performedMove != null && alreadyVisitedStates.contains(state.hashCode())) {
            performedMove.setCosto(0);
            return performedMove;
        }

        List<Move> allPossibleMoves = state.getAllMovesFor((isMaxTurn) ? myColour : opponentColour);

        if (currentDepth == maxDepth || timeManager.isEnd() || allPossibleMoves.isEmpty() || state.hasBlackWon() || state.hasWhiteWon()) {
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
                var val = rndGen.nextInt(100);

                performedMove.setCosto(isMaxTurn ?
                        myHeuristic[heuristicIndex].evaluate(state, currentDepth) + (val < 4 ? 1 : 0) :
                        -opponentHeuristic[heuristicIndex].evaluate(state, currentDepth) - (val < 4 ? 1 : 0));
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
                performAlphabeta(newState, timeManager, false, turn + 1, currentDepth + 1, alpha, beta, m);

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
                performAlphabeta(newState, timeManager, true, turn + 1, currentDepth + 1, alpha, beta, m);

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
    private Move performAlphabetaWithHistoryCheck(@NotNull TableState state, TimeManager timeManager, boolean isMaxTurn, int turn, int currentDepth, double alpha, double beta, Move performedMove, @NotNull Set<Integer> previousHistory) {
        //se questo stato è già stato raggiunto
        if (performedMove != null && alreadyVisitedStates.contains(state.hashCode())) {
            performedMove.setCosto(0);
            return performedMove;
        }

        List<Move> allPossibleMoves = state.getAllMovesFor((isMaxTurn) ? myColour : opponentColour);

        //se è ora di terminare l'esplorazione
        if (currentDepth == maxDepth || timeManager.isEnd() || allPossibleMoves.isEmpty() || state.hasBlackWon() || state.hasWhiteWon()) {
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
                performAlphabetaWithHistoryCheck(newState, timeManager, false, turn + 1, currentDepth + 1, alpha, beta, m, myHistory);

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
                performAlphabetaWithHistoryCheck(newState, timeManager, true, turn + 1, currentDepth + 1, alpha, beta, m, myHistory);

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

    static double getSafeZoneProtection(@NotNull TableState s) {
        double res = 0.0;
        //angolo alto sinistra
        if (s.getState()[0][0] == 1) {
            res++;
        }
        if (s.getState()[2][1] == 1) {
            res++;
        }
        if (s.getState()[3][1] == 1) {
            res++;
        }
        if (s.getState()[1][2] == 1) {
            res++;
        }
        if (s.getState()[1][3] == 1) {
            res++;
        }
        //angolo alto destra
        if (s.getState()[0][8] == 1) {
            res++;
        }
        if (s.getState()[1][6] == 1) {
            res++;
        }
        if (s.getState()[1][5] == 1) {
            res++;
        }
        if (s.getState()[2][7] == 1) {
            res++;
        }
        if (s.getState()[3][7] == 1) {
            res++;
        }
        //angolo basso destra
        if (s.getState()[8][8] == 1) {
            res++;
        }
        if (s.getState()[6][7] == 1) {
            res++;
        }
        if (s.getState()[5][7] == 1) {
            res++;
        }
        if (s.getState()[7][6] == 1) {
            res++;
        }
        if (s.getState()[7][5] == 1) {
            res++;
        }
        //angolo basso sinistra
        if (s.getState()[8][0] == 1) {
            res++;
        }
        if (s.getState()[6][1] == 1) {
            res++;
        }
        if (s.getState()[5][1] == 1) {
            res++;
        }
        if (s.getState()[7][2] == 1) {
            res++;
        }
        if (s.getState()[7][3] == 1) {
            res++;
        }

        return res / 20;
    }
}
