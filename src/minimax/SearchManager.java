package minimax;

import client.TimeManager;
import model.Move;
import model.PlayerType;
import model.TableState;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Semaphore;

public class SearchManager {
    private static final double[] defaultWeights = {1, 1, 1, 1.5, 1, 2, 3, 2.5, 5, 1};

    private Minimax[] workers;
    private Semaphore sem, barrier;

    private SearchStatus status;

    private boolean depthChanged = false;
    private int threadNumber;

    public SearchManager(PlayerType myColour, int maxDepth) {
        this(myColour, maxDepth, defaultWeights);
    }

    public SearchManager(PlayerType myColour, int maxDepth, double[] weights) {
        this(myColour, maxDepth, weights, Runtime.getRuntime().availableProcessors());
    }


    public SearchManager(PlayerType myColour, int maxDepth, double[] weights, int threadNumber) {
        this.threadNumber = threadNumber;

        barrier = new Semaphore(0);
        sem = new Semaphore(0);

        status = new SearchStatus(barrier);
        workers = new Minimax[threadNumber];

        for (int i = 0; i < threadNumber; i++) {
            workers[i] = new Minimax(myColour, maxDepth, weights, sem, i, status);
            workers[i].start();
        }

        status.reset();
    }

    public Move search(@NotNull TableState initialState, TimeManager timeManager, int turn) {
        //System.out.println("--> Turno: " + turn);
        status.updateVisitedStates(initialState.hashCode());
        status.setInitialConditions(initialState, timeManager, turn);
        sem.release(threadNumber);
        //System.out.println("--> Ricerca iniziata");

        try {
            barrier.acquire();
            //System.out.println("--> Dati acquisiti");
        } catch (InterruptedException e) {
            System.err.println("Il manager è stato interrotto durante l'attesa del completamento della ricerca");
        }

        Move bestMove = status.getBestMove();
        if (bestMove != null) {
            TableState newState = initialState.performMove(bestMove);
            status.updateVisitedStates(newState.hashCode());
            status.reset();

            if (!depthChanged && newState.getBlackPiecesCount() < 4 && newState.getWhitePiecesCount() < 4) {
                for (int i = 0; i < threadNumber; i++) {
                    workers[i].setMaxDepth(7);
                }
                depthChanged = true;
                //System.out.println("--> Profondità cambiata");
            }
        }
        return bestMove;
    }

    public void stop() {
        status.setStop(true);
        sem.release(threadNumber);

        for (int i = 0; i < threadNumber; i++) {
            try {
                workers[i].join();
            } catch (InterruptedException e) {
                System.err.println("Il manager è stato interrotto durante l'attesa degli altri thread");
            }

        }
    }
}
