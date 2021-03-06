package minimax;

import client.TimeManager;
import model.Move;
import model.PlayerType;
import model.TableState;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Semaphore;

public class SearchManager {
    private static final double[] defaultWeights = {1, 1, 1, 1.5, 1, 2, 3, 2.5, 5, 1};

    private double[] weights;

    private final Minimax[] workers;
    private final Semaphore sem;
    private final Semaphore barrier;

    private SearchStatus status;

    private final int threadNumber;
    private final int maxDepth;
    private final PlayerType myColour;

    public SearchManager(PlayerType myColour, int maxDepth) {
        this(myColour, maxDepth, defaultWeights);
    }

    public SearchManager(PlayerType myColour, int maxDepth, double[] weights) {
        this(myColour, maxDepth, weights, Runtime.getRuntime().availableProcessors());
    }


    public SearchManager(@NotNull PlayerType myColour, int maxDepth, double[] weights, int threadNumber) {
        if (threadNumber <= 0) {
            throw new IllegalArgumentException("Numero di thread non valido");
        }

        this.threadNumber = threadNumber;
        this.myColour = myColour;
        this.maxDepth = maxDepth;
        this.weights = weights;

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

    public Move search(@NotNull TableState initialState, @NotNull TimeManager timeManager) throws InterruptedException {
        //System.out.println("--> Turno: " + turn);
        status.updateVisitedStates(initialState.hashCode());
        status.setInitialConditions(initialState.getAllMovesFor(myColour), initialState, timeManager);

        Move bestMove = null;
        int currentDepth = maxDepth;

        do {
            var deadThreads = status.getDeadThreads();

            for (int d : deadThreads) {
                try {
                    workers[d].join();
                } catch (InterruptedException e) {
                    System.err.println("--> Il manager è stato interrotto durante l'attesa della terminazione di un thread. Continuo");
                }
                workers[d] = new Minimax(myColour, maxDepth, weights, sem, d, status);
            }

            System.out.println("--> Inizio ricerca a profondità " + currentDepth);
            status.reset();
            sem.release(threadNumber);
            System.out.println("--> --> Ricerca iniziata");

            try {
                barrier.acquire();
                System.out.println("--> --> Dati acquisiti");
            } catch (InterruptedException e) {
                System.err.println("--> --> Il manager è stato interrotto durante l'attesa del completamento della ricerca");
                throw new InterruptedException("Search Manager interrotto");
            }

            if (bestMove == null || !timeManager.isEnd()) {
                bestMove = status.getBestMove();
                System.out.println("--> Trovata nuova mossa: " + bestMove);

                for (int i = 0; i < threadNumber; i++) {
                    workers[i].setMaxDepth(currentDepth + 2);
                }
                currentDepth += 2;
            } else {
                System.out.println("--> Tempo scaduto. Uso la mossa trovata precedentemente");
            }
        } while (!timeManager.isEnd() && bestMove != null);

        for (int i = 0; i < threadNumber; i++) {
            workers[i].setMaxDepth(maxDepth);
        }

        if (bestMove != null) {
            TableState newState = initialState.performMove(bestMove);
            status.updateVisitedStates(newState.hashCode());
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
                System.err.println("--> Il manager è stato interrotto durante l'attesa degli altri thread");
            }
        }
    }

    public SearchStatus getStatus() {
        return status;
    }

    public void setStatus(SearchStatus status) {
        this.status = status;
    }
}
