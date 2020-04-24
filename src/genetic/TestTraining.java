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

    private static int NUM_INDIVIDUI = 20;
    private static int ELITISIMO = 4;
    private static int PROB_MUTAZIONE = 5;
    private static int NUM_PARTITE = 4; //Numero minimo di partite giocate da ogni individuo
    private static int NUM_GENERAZIONI = 10;
    private static int maxDepth = 5;
    private static int timeoutSec = 55;
    private static int limiteTurni = 500;
    private static int limiteTurniSenzaPedineMangiate = 50;
    private static Random rndGen;

    public static void main(String[] args) {
        //controllo argomenti

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--help":
                case "-h": {
                    System.out.println("Benvenuto a Narnia, amico");
                    System.out.println("tablut <options>");
                    System.out.println("");
                    System.out.println("Opzioni:");
                    System.out.println("\t-h, --help:");
                    System.out.println("\t\tVisualizza questa guida");
                    System.out.println("\t--num-individui:");
                    System.out.println("\t\tImposta il numero di individui che compongono la popolazione");
                    System.out.println("\t\tDi default sono impostati 20 individui");
                    System.out.println("\t--elitismo:");
                    System.out.println("\t\tImposta il numero di individui matenuti tra una generazione e l'altra");
                    System.out.println("\t\tDi default il valore è 4");
                    System.out.println("\t--prob-mutazioni:");
                    System.out.println("\t\tImposta la probabilità complessiva di mutazione di un individuo");
                    System.out.println("\t\tDi default il valore è 5 (5%)");
                    System.out.println("\t--num-partite:");
                    System.out.println("\t\tImposta il numero di partite che ogni individuo deve giocare.");
                    System.out.println("\t\tOgni partita è composta da un andata e da un ritorno.");
                    System.out.println("\t\tDi default il valore è 4");
                    System.out.println("\t--num-generazioni:");
                    System.out.println("\t\tImposta il numero di generazioni");
                    System.out.println("\t\tDi default il valore è 10");
                    System.out.println("\t--max-depth:");
                    System.out.println("\t\tImposta la profondità massima di esplorazione dell'albero");
                    System.out.println("\t\tDi default il valore è 5");
                    System.out.println("\t--timeout:");
                    System.out.println("\t\tImposta il timeout del server");
                    System.out.println("\t\tDi default il valore è 55 secondi");
                    System.out.println("\t--limite-turni:");
                    System.out.println("\t\tImposta la durata massima di una partita");
                    System.out.println("\t\tDi default il valore è 500");
                    System.out.println("\t--turni-senza-mangiare:");
                    System.out.println("\t\tImposta il numero massimo di turni consecutivi per cui nessuna");
                    System.out.println("\t\tdelle due parti può non mangiare");
                    System.out.println("\t\tDi default il valore è 50");

                    System.exit(0);
                    break;
                }
                case "--num-individui": {
                    i++;
                    if (i < args.length) {
                        try {
                            NUM_INDIVIDUI = Integer.parseInt(args[i]);
                        } catch (NumberFormatException e) {
                            System.err.println("Formato numerico errato");
                            System.exit(-1);
                        }
                    } else {
                        System.err.println("Numero di argomenti errato");
                        System.exit(-1);
                    }
                    break;
                }
                case "--elitismo": {
                    i++;
                    if (i < args.length) {
                        try {
                            ELITISIMO = Integer.parseInt(args[i]);
                        } catch (NumberFormatException e) {
                            System.err.println("Formato numerico errato");
                            System.exit(-1);
                        }
                    } else {
                        System.err.println("Numero di argomenti errato");
                        System.exit(-1);
                    }
                    break;
                }
                case "--prob-mutazioni": {
                    i++;
                    if (i < args.length) {
                        try {
                            PROB_MUTAZIONE = Integer.parseInt(args[i]);
                        } catch (NumberFormatException e) {
                            System.err.println("Formato numerico errato");
                            System.exit(-1);
                        }
                    } else {
                        System.err.println("Numero di argomenti errato");
                        System.exit(-1);
                    }
                    break;
                }
                case "--num-partite": {
                    i++;
                    if (i < args.length) {
                        try {
                            NUM_PARTITE = Integer.parseInt(args[i]);
                        } catch (NumberFormatException e) {
                            System.err.println("Formato numerico errato");
                            System.exit(-1);
                        }
                    } else {
                        System.err.println("Numero di argomenti errato");
                        System.exit(-1);
                    }
                    break;
                }
                case "--num-generazioni": {
                    i++;
                    if (i < args.length) {
                        try {
                            NUM_GENERAZIONI = Integer.parseInt(args[i]);
                        } catch (NumberFormatException e) {
                            System.err.println("Formato numerico errato");
                            System.exit(-1);
                        }
                    } else {
                        System.err.println("Numero di argomenti errato");
                        System.exit(-1);
                    }
                    break;
                }
                case "--max-depth": {
                    i++;
                    if (i < args.length) {
                        try {
                            maxDepth = Integer.parseInt(args[i]);
                        } catch (NumberFormatException e) {
                            System.err.println("Formato numerico errato");
                            System.exit(-1);
                        }
                    } else {
                        System.err.println("Numero di argomenti errato");
                        System.exit(-1);
                    }
                    break;
                }
                case "--timeout": {
                    i++;
                    if (i < args.length) {
                        try {
                            timeoutSec = Integer.parseInt(args[i]);
                        } catch (NumberFormatException e) {
                            System.err.println("Formato numerico errato");
                            System.exit(-1);
                        }
                    } else {
                        System.err.println("Numero di argomenti errato");
                        System.exit(-1);
                    }
                    break;
                }
                case "--limite-turni": {
                    i++;
                    if (i < args.length) {
                        try {
                            limiteTurni = Integer.parseInt(args[i]);
                        } catch (NumberFormatException e) {
                            System.err.println("Formato numerico errato");
                            System.exit(-1);
                        }
                    } else {
                        System.err.println("Numero di argomenti errato");
                        System.exit(-1);
                    }
                    break;
                }
                case "--turni-senza-mangiare": {
                    i++;
                    if (i < args.length) {
                        try {
                            limiteTurniSenzaPedineMangiate = Integer.parseInt(args[i]);
                        } catch (NumberFormatException e) {
                            System.err.println("Formato numerico errato");
                            System.exit(-1);
                        }
                    } else {
                        System.err.println("Numero di argomenti errato");
                        System.exit(-1);
                    }
                    break;
                }
                default: {
                    System.err.println("Opzione non riconosciuta. Scrivi bene pataca");
                    System.exit(-1);
                }

            }
        }

        System.out.println("#####################################");
        System.out.println("Popolazione: " + NUM_INDIVIDUI);
        System.out.println("Elitismo: " + ELITISIMO);
        System.out.println("Probabilità mutazioni: " + PROB_MUTAZIONE);
        System.out.println("Numero partite: " + NUM_PARTITE);
        System.out.println("Numero generazioni: " + NUM_GENERAZIONI);
        System.out.println("Profondità massima: " + maxDepth);
        System.out.println("Limite turni: " + limiteTurni);
        System.out.println("Limite turni senza mangiare pedine: " + limiteTurniSenzaPedineMangiate);
        System.out.println("#####################################");

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


        //1.2 Genera la popolazione iniziale
        for (int i = 0; i < NUM_INDIVIDUI; i++) {
            population.add(new Individual(maxDepth, weights[i]));
        }


        for (int numGen = 0; numGen < NUM_GENERAZIONI; numGen++) {
            System.out.println("---------------------------------------------------------------------\nGENERAZIONE " + (numGen + 1));
            //2.1 Fai tutte le sfide
            giocaPartite(population);

            //2.2 Metti in evidenza i migliori
            population.sort(Individual::compareTo);
            population = population.subList(0, NUM_INDIVIDUI);

            saveSate(population, salvataggi);
            System.out.println("--> Dati salvati");

            //Stampa statistiche
            System.out.printf("--> Pedine mangiate per partita  \tmed: %6.2f\tmax: %6.2f\tmin: %6.2f%n",
                    population.stream().map(Individual::getMeanCapturedPawns).reduce(0.0, Double::sum) / population.size(),
                    population.stream().map(Individual::getMeanCapturedPawns).max(Double::compare).orElse(-1.0),
                    population.stream().map(Individual::getMeanCapturedPawns).min(Double::compare).orElse(-1.0));
            System.out.printf("--> Pedine perse per partita     \tmed: %6.2f\tmax: %6.2f\tmin: %6.2f%n",
                    population.stream().map(Individual::getMeanLostPawns).reduce(0.0, Double::sum) / population.size(),
                    population.stream().map(Individual::getMeanLostPawns).max(Double::compare).orElse(-1.0),
                    population.stream().map(Individual::getMeanLostPawns).min(Double::compare).orElse(-1.0));
            System.out.printf("--> Turni vittoria               \tmed: %6.2f\tmax: %6.2f\tmin: %6.2f%n",
                    population.stream().map(Individual::getMeanVictoriesTurnNumber).reduce(0.0, Double::sum) / population.size(),
                    population.stream().map(Individual::getMeanVictoriesTurnNumber).max(Double::compare).orElse(-1.0),
                    population.stream().map(Individual::getMeanVictoriesTurnNumber).min(Double::compare).orElse(-1.0));

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
            for (int idx = ELITISIMO; idx < numIndividui + 6; idx += 2) {
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

                i = (i + 1) % numIndividui;
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

            population = newPopulation;
        }
    }

    private static void saveSate(List<Individual> population, Path salvataggi) {
        double[][] weights = new double[NUM_INDIVIDUI][DIM_PESI];
        try (PrintWriter writer = new PrintWriter(salvataggi.toFile())) {
            Gson gson = new Gson();

            for (int i1 = 0; i1 < NUM_INDIVIDUI; i1++) {
                weights[i1] = population.get(i1).getWeigths();
            }

            writer.print(gson.toJson(weights));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void giocaPartite(@NotNull List<Individual> population) {
        System.out.println("--> Generazione accoppiamenti");
        int giocatori = population.size();

        //Genera accoppiamenti partita
        int[][] accoppiamenti = new int[NUM_PARTITE * giocatori][2];

        for (int i = 0; i < NUM_PARTITE * giocatori; i++) {
            accoppiamenti[i][0] = i % giocatori;
            accoppiamenti[i][1] = i % giocatori;
        }


        rndGen.setSeed(System.currentTimeMillis());
        int a, b, temp;
        for (int i = 0; i < giocatori * 5; i++) {
            a = rndGen.nextInt(giocatori);
            b = rndGen.nextInt(giocatori);

            temp = accoppiamenti[a][0];
            accoppiamenti[a][0] = accoppiamenti[b][0];
            accoppiamenti[b][0] = temp;

            a = rndGen.nextInt(giocatori);
            b = rndGen.nextInt(giocatori);

            temp = accoppiamenti[a][1];
            accoppiamenti[a][1] = accoppiamenti[b][1];
            accoppiamenti[b][1] = temp;
        }

        System.out.println("--> Inizio partite");

        for (int i = 0; i < NUM_PARTITE * giocatori; i++) {
            int firstPlayer = accoppiamenti[i][0];
            int secondPlayer = accoppiamenti[i][1];

            System.out.println("--> --> Partita: " + (i + 1));
            Individual indA = population.get(firstPlayer);
            Individual indB = population.get(secondPlayer);

            System.out.println("--> --> --> Andata");
            gioca(indA, indB);
            System.out.println("--> --> --> Ritorno");
            gioca(indB, indA);
        }
    }

    private static void gioca(Individual whiteInd, Individual blackInd) {
        whiteInd.prepareForNewMatch(PlayerType.WHITE);
        blackInd.prepareForNewMatch(PlayerType.BLACK);
        Minimax whiteMinimax = whiteInd.getPlayer();
        Minimax blackMinimax = blackInd.getPlayer();

        int lastEatTurn = 0;
        int lastWhiteCount = 9;
        int lastBlackCount = 16;

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
//            var whiteMove = whiteMinimax.alphabetaTest(s, timeManager, turn);
            tt.interrupt();
            if (whiteMove != null) {
                s = s.performMove(whiteMove);
                if (lastBlackCount != s.getBlackPiecesCount() || lastWhiteCount != s.getWhitePiecesCount()) {
                    lastBlackCount = s.getBlackPiecesCount();
                    lastWhiteCount = s.getWhitePiecesCount();
                    lastEatTurn = turn;
                }
            }
            if (s.hasWhiteWon()) {
                vittoriaBianco(whiteInd, blackInd, s, turn);
                break;
            } else if (s.hasBlackWon() || whiteMove == null) {
                vittoriaNero(whiteInd, blackInd, s, turn);
                break;
            } else if (schemi.contains(s.hashCode())) {
                risoluzionePatta(whiteInd, blackInd, s);
                break;
            }
            schemi.add(s.hashCode());
            turn++;

            timeManager = new TimeManager();
            tt = new TimerThread(timeManager, timeoutSec * 1000);
            tt.start();
            var blackMove = blackMinimax.alphabeta(s, timeManager, turn);
//            var blackMove = blackMinimax.alphabetaTest(s, timeManager, turn);
            tt.interrupt();
            if (blackMove != null) {
                s = s.performMove(blackMove);
                if (lastBlackCount != s.getBlackPiecesCount() || lastWhiteCount != s.getWhitePiecesCount()) {
                    lastBlackCount = s.getBlackPiecesCount();
                    lastWhiteCount = s.getWhitePiecesCount();
                    lastEatTurn = turn;
                }
            }
            if (s.hasWhiteWon() || blackMove == null) {
                vittoriaBianco(whiteInd, blackInd, s, turn);
                break;
            } else if (s.hasBlackWon()) {
                vittoriaNero(whiteInd, blackInd, s, turn);
                break;
            } else if (schemi.contains(s.hashCode())) {
                risoluzionePatta(whiteInd, blackInd, s);
                break;
            }
            schemi.add(s.hashCode());
            turn++;

            if (turn >= limiteTurni) {
                risoluzionePatta(whiteInd, blackInd, s);
                System.out.println("--> --> --> --> Superato limite turni [" + limiteTurni + "]");
                break;
            }

            if (turn - lastEatTurn > limiteTurniSenzaPedineMangiate) {
                risoluzionePatta(whiteInd, blackInd, s);
                System.out.println("--> --> --> --> Nessuna pedina mangiata per troppi turni [" + limiteTurniSenzaPedineMangiate + "]");
                break;
            }
        }
    }

    private static void vittoriaBianco(Individual whiteInd, Individual blackInd, TableState s, int turn) {
        whiteInd.addVictory();
        whiteInd.addVictoryTurnNumber(turn);
        blackInd.addLoss();
        blackInd.addLossesTurnNumber(turn);

        risoluzionePartita(whiteInd, blackInd, s);
    }

    private static void vittoriaNero(Individual whiteInd, Individual blackInd, TableState s, int turn) {
        whiteInd.addLoss();
        whiteInd.addLossesTurnNumber(turn);
        blackInd.addVictory();
        blackInd.addVictoryTurnNumber(turn);

        risoluzionePartita(whiteInd, blackInd, s);
    }

    private static void risoluzionePatta(Individual fisrtInd, Individual secondInd, TableState s) {
        risoluzionePartita(secondInd, fisrtInd, s);
    }

    private static void risoluzionePartita(Individual whiteInd, Individual blackInd, TableState s) {
        whiteInd.addMatchPlayed();
        whiteInd.addCapturedPawns(16 - s.getBlackPiecesCount());
        whiteInd.addLostPawns(9 - s.getWhitePiecesCount());
        blackInd.addMatchPlayed();
        blackInd.addCapturedPawns(9 - s.getWhitePiecesCount());
        blackInd.addLostPawns(16 - s.getBlackPiecesCount());
    }
}
