package genetic;

import client.TimeManager;
import client.TimerThread;
import com.google.gson.Gson;
import minimax.Minimax;
import model.PlayerType;
import model.TableState;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class TestTraining {
    /////////////////////////////////////////
    //NON MODIFICARE
    private static int DIM_PESI = 10;
    /////////////////////////////////////////

    private static int NUM_INDIVIDUI = 10;
    private static int ELITISIMO = 4;
    private static int PROB_MUTAZIONE = 7;
    private static int NUM_PARTITE = 1; //Numero minimo di partite giocate da ogni individuo
    private static int NUM_GENERAZIONI = 5;
    private static int maxDepth = 3;
    private static int timeoutSec = 55;
    private static Random rndGen;

    public static void main(String[] args) {
        //genera la popolazione iniziale
        List<Individual> population = new ArrayList<>();

        double[][] weights = null;

        rndGen = new Random();
        rndGen.setSeed(System.currentTimeMillis());

        //1.1 Ottieni il vettore dei pesi da cui partire
        Path salvataggi = Path.of("./weights.json");
        if (Files.exists(salvataggi)) {
            //carica i dati dal file
            Gson gson = new Gson();
            try (FileReader reader = new FileReader(salvataggi.toFile())) {
                weights = gson.fromJson(reader, double[][].class);
            } catch (FileNotFoundException e) {
                System.err.println("Impossibile trovare il file di salvataggio");
                System.exit(-1);
            } catch (IOException e) {
                System.err.println("Errore durante la lettura del file di salvataggio");
                System.exit(-2);
            }
        } else {
            //è la prima generazione crea dati casuali
            weights = new double[NUM_INDIVIDUI][DIM_PESI];

            for (int i = 0; i < NUM_INDIVIDUI; i++) {
                for (int j = 0; j < DIM_PESI; j++) {
                    weights[i][j] = rndGen.nextDouble() * 10;

                }
            }
        }
        System.out.println("Voglio fare un gioco con te\n\n");

        System.out.println("---------------------------------------------------------------------\nGenerazione 0");
        //1.2 Genera la popolazione iniziale
        for (int i = 0; i < NUM_INDIVIDUI; i++) {
            population.add(new Individual(maxDepth, weights[i]));
        }

        //2.1 Fai tutte le sfide
        giocaPartite(population);

        //2.2 Metti in evidenza i migliori
        population.sort(Individual::compareTo);

        try (PrintWriter writer = new PrintWriter(salvataggi.toFile())) {
            Gson gson = new Gson();

            for (int i1 = 0; i1 < NUM_INDIVIDUI; i1++) {
                weights[i1] = population.get(i1).getWeigths();
            }

            writer.print(gson.toJson(weights));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int numGen = 0; numGen < NUM_GENERAZIONI; numGen++) {
            System.out.println("---------------------------------------------------------------------\nGENERAZIONE " + (numGen + 1));

            //3 Genera la nuova popolazione
            List<Individual> newPopulation = new ArrayList<>();

            //3.1 Seleziona gli individui da mantenere immutati
            for (int idx = 0; idx < ELITISIMO; idx++) {
                //tieni gli individui migliori
                newPopulation.add(population.get(idx));
            }

            int numIndividui = population.size();
            int i = 0;
            //3.2 Seleziona gli individui da ricombinare e ricombina i geni
            for (int idx = ELITISIMO; idx < NUM_INDIVIDUI + 6; idx += 2) {
                //Scegli il primo genitore

                while (rndGen.nextInt(100) > (60.0 / i + 1)) {
                    i = (i + 1) % numIndividui;
                }

                //Scegli il secondo genitore
                int j = i;
                while (rndGen.nextInt(100) > (60.0 / j + 1) || i == j) {
                    j = (j + 1) % numIndividui;
                }

                //Applica il crossover
                double[] wA = population.get(i).getWeigths();
                double[] wB = population.get(j).getWeigths();

                double[] newWA = new double[DIM_PESI];
                double[] newWB = new double[DIM_PESI];

                int crossover = rndGen.nextInt(DIM_PESI);
                for (int k = 0; k < DIM_PESI; k++) {
                    if (k < crossover) {
                        newWA[k] = wA[k];
                        newWB[k] = wB[k];
                    } else {
                        newWA[k] = wB[k];
                        newWB[k] = wA[k];
                    }
                }

                newPopulation.add(new Individual(maxDepth, wA));
                newPopulation.add(new Individual(maxDepth, wB));
                i++;

            }

            //3.2 Applica eventuali mutazioni
            int probMutazione = rndGen.nextInt(100);
            if (probMutazione < PROB_MUTAZIONE) {
                for (int idx = 0; idx < Math.ceil((NUM_INDIVIDUI * probMutazione) / 100.0); idx++) {
                    //scegli a caso un individuo da mutare
                    int individuoDaMutare = rndGen.nextInt(newPopulation.size());
                    int geneIdx = rndGen.nextInt(DIM_PESI);
                    double perc = (rndGen.nextInt(201) - 100) / 100.0;
                    newPopulation.get(individuoDaMutare).applyMutation(perc, geneIdx);
                }
            }


            //2.1 Fai tutte le sfide
            giocaPartite(newPopulation);


            //2.2 Metti in evidenza i migliori ed elimina i peggiori
            newPopulation.sort(Individual::compareTo);
            population = population.subList(0, NUM_INDIVIDUI);

            try (PrintWriter writer = new PrintWriter(salvataggi.toFile())) {
                Gson gson = new Gson();

                for (int i1 = 0; i1 < NUM_INDIVIDUI; i1++) {
                    weights[i1] = population.get(i1).getWeigths();
                }

                writer.print(gson.toJson(weights));
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("--> Dati salvati");

            //Stampa statistiche
            System.out.printf("--> Pedine mangiate  \tmed: %6.2f\tmax: %6.2f\tmin: %6.2f%n",
                    population.stream().map(ind -> (double) ind.getCapturedPawns()).reduce(0.0, Double::sum) / population.size(),
                    population.stream().map(ind -> (double) ind.getCapturedPawns()).max(Double::compare).orElse(-1.0),
                    population.stream().map(ind -> (double) ind.getCapturedPawns()).min(Double::compare).orElse(-1.0));
            System.out.printf("--> Pedine perse     \tmed: %6.2f\tmax: %6.2f\tmin: %6.2f%n",
                    population.stream().map(ind -> (double) ind.getLostPawns()).reduce(0.0, Double::sum) / population.size(),
                    population.stream().map(ind -> (double) ind.getLostPawns()).max(Double::compare).orElse(-1.0),
                    population.stream().map(ind -> (double) ind.getLostPawns()).min(Double::compare).orElse(-1.0));
            System.out.printf("--> Turni vittoria   \tmed: %6.2f\tmax: %6.2f\tmin: %6.2f%n",
                    population.stream().map(ind -> (double) ind.getTotalVictoriesTurnNumber()).reduce(0.0, Double::sum) / population.size(),
                    population.stream().map(ind -> (double) ind.getTotalVictoriesTurnNumber()).max(Double::compare).orElse(-1.0),
                    population.stream().map(ind -> (double) ind.getTotalVictoriesTurnNumber()).min(Double::compare).orElse(-1.0));

        }


    }

    private static void giocaPartite(@NotNull List<Individual> population) {
        System.out.println("--> Inizio partite");
        for (int i = 0; i < NUM_PARTITE * population.size(); i++) {
            System.out.println("--> --> Partita: " + (i + 1));
            int firstPlayer = i % population.size();
            int secondPlayer = 0;
            while ((secondPlayer = rndGen.nextInt(population.size())) == firstPlayer) ;

            Individual indA = population.get(firstPlayer);
            Individual indB = population.get(secondPlayer);

            gioca(indA, indB);
            gioca(indB, indA);
        }
    }

    private static void gioca(Individual fisrtInd, Individual secondInd) {
        fisrtInd.prepareForNewMatch(PlayerType.WHITE);
        secondInd.prepareForNewMatch(PlayerType.BLACK);
        Minimax whiteMinimax = fisrtInd.getPlayer();
        Minimax blackMinimax = secondInd.getPlayer();

        TableState s = new TableState();
        Set<Integer> schemi = new HashSet<>();
        TimeManager timeManager;
        int turn = 0;
        TimerThread tt;

        schemi.add(s.hashCode());
        while (!s.hasWhiteWon() && !s.hasBlackWon()) {
            //cominciano i bianchi
            timeManager = new TimeManager();
            tt = new TimerThread(timeManager, timeoutSec * 1000);
            tt.start();
            var whiteMove = whiteMinimax.alphabeta(s, timeManager, turn);
            tt.interrupt();
            if (whiteMove != null) {
                s = s.performMove(whiteMove);
            }
            if (s.hasWhiteWon()) {
                fisrtInd.addVictory();
                fisrtInd.addVictoryTurnNumber(turn);
                fisrtInd.addMatchPlayed();
                fisrtInd.addCapturedPawns(16 - s.getBlackPiecesCount());
                fisrtInd.addLostPawns(9 - s.getWhitePiecesCount());

                secondInd.addLoss();
                secondInd.addLossesTurnNumber(turn);
                secondInd.addMatchPlayed();
                secondInd.addCapturedPawns(9 - s.getWhitePiecesCount());
                secondInd.addLostPawns(16 - s.getBlackPiecesCount());
                break;
            } else if (s.hasBlackWon() || whiteMove == null) {
                secondInd.addVictory();
                secondInd.addVictoryTurnNumber(turn);
                secondInd.addMatchPlayed();
                secondInd.addCapturedPawns(16 - s.getBlackPiecesCount());
                secondInd.addLostPawns(9 - s.getWhitePiecesCount());

                fisrtInd.addLoss();
                fisrtInd.addLossesTurnNumber(turn);
                fisrtInd.addMatchPlayed();
                fisrtInd.addCapturedPawns(9 - s.getWhitePiecesCount());
                fisrtInd.addLostPawns(16 - s.getBlackPiecesCount());
                break;
            } else if (schemi.contains(s.hashCode())) {
                secondInd.addMatchPlayed();
                secondInd.addCapturedPawns(16 - s.getBlackPiecesCount());
                secondInd.addLostPawns(9 - s.getWhitePiecesCount());

                fisrtInd.addMatchPlayed();
                fisrtInd.addCapturedPawns(9 - s.getWhitePiecesCount());
                fisrtInd.addLostPawns(16 - s.getBlackPiecesCount());
                break;
            }
            schemi.add(s.hashCode());
            turn++;

            timeManager = new TimeManager();
            tt = new TimerThread(timeManager, timeoutSec * 1000);
            tt.start();
            var blackMove = blackMinimax.alphabeta(s, timeManager, turn);
            tt.interrupt();
            if (blackMove != null) {
                s = s.performMove(blackMove);
            }
            if (s.hasWhiteWon() || blackMove == null) {
                fisrtInd.addVictory();
                fisrtInd.addVictoryTurnNumber(turn);
                fisrtInd.addMatchPlayed();
                fisrtInd.addCapturedPawns(16 - s.getBlackPiecesCount());
                fisrtInd.addLostPawns(9 - s.getWhitePiecesCount());

                secondInd.addLoss();
                secondInd.addLossesTurnNumber(turn);
                secondInd.addMatchPlayed();
                secondInd.addCapturedPawns(9 - s.getWhitePiecesCount());
                secondInd.addLostPawns(16 - s.getBlackPiecesCount());
                break;
            } else if (s.hasBlackWon()) {
                secondInd.addVictory();
                secondInd.addVictoryTurnNumber(turn);
                secondInd.addMatchPlayed();
                secondInd.addCapturedPawns(16 - s.getBlackPiecesCount());
                secondInd.addLostPawns(9 - s.getWhitePiecesCount());

                fisrtInd.addLoss();
                fisrtInd.addLossesTurnNumber(turn);
                fisrtInd.addMatchPlayed();
                fisrtInd.addCapturedPawns(9 - s.getWhitePiecesCount());
                fisrtInd.addLostPawns(16 - s.getBlackPiecesCount());
                break;
            } else if (schemi.contains(s.hashCode())) {
                secondInd.addMatchPlayed();
                secondInd.addCapturedPawns(16 - s.getBlackPiecesCount());
                secondInd.addLostPawns(9 - s.getWhitePiecesCount());

                fisrtInd.addMatchPlayed();
                fisrtInd.addCapturedPawns(9 - s.getWhitePiecesCount());
                fisrtInd.addLostPawns(16 - s.getBlackPiecesCount());
                break;
            }
            schemi.add(s.hashCode());
            turn++;
        }
    }
}
