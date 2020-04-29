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

    private static int maxNumIndividui = 20;
    private static int elitismo = 4;
    private static int probMutazione = 5;
    private static int numPartite = 4; //Numero minimo di partite giocate da ogni individuo
    private static int numGenerazioni = 10;
    private static int maxDepth = 5;
    private static int timeoutSec = 55;
    private static int limiteTurni = 500;
    private static int limiteTurniSenzaPedineMangiate = 50;
    private static final int upperLimitWeight = 10;
    private static final int surplusIndividuals = 6;
    private static final int randomSurplusIndividuals = 3;
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
                            maxNumIndividui = Integer.parseInt(args[i]);
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
                            elitismo = Integer.parseInt(args[i]);
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
                            probMutazione = Integer.parseInt(args[i]);
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
                            numPartite = Integer.parseInt(args[i]);
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
                            numGenerazioni = Integer.parseInt(args[i]);
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
                    System.err.println("Opzione non riconosciuta");
                    System.exit(-1);
                }
            }
        }

        if (maxNumIndividui <= elitismo) {
            System.err.println("La cardinalità della popolazione deve essere maggiore del valore di elitismo");
            System.exit(-1);
        }

        if (maxNumIndividui <= 0) {
            System.err.println("Il numero di individui deve essere un intero positivo");
            System.exit(-1);
        }

        if (elitismo <= 0) {
            System.err.println("Il valore di elitismo deve essere un intero positivo");
            System.exit(-1);
        }

        if (probMutazione < 0 || probMutazione > 100) {
            System.err.println("La probabilità di mutazione deve essere compresa tra 0 e 100");
            System.exit(-1);
        }

        if (numPartite <= 0) {
            System.err.println("Il numero di partite deve essere un intero positivo");
            System.exit(-1);
        }

        if (numGenerazioni <= 0) {
            System.err.println("Il numero di generazioni deve essere un intero positivo");
            System.exit(-1);
        }

        if (maxDepth <= 0) {
            System.err.println("La profondità massima deve essere un intero positivo");
            System.exit(-1);
        }

        if (timeoutSec <= 0) {
            System.err.println("Il timeout deve essere un intero positivo");
            System.exit(-1);
        }

        if (limiteTurni <= 0) {
            System.err.println("Il limite di turni deve essere un intero positivo");
            System.exit(-1);
        }

        if (limiteTurniSenzaPedineMangiate <= 0 || limiteTurniSenzaPedineMangiate > limiteTurni) {
            System.err.println("Il limite di turni senza pedine mangiate deve essere un intero positivo minore di limiteTurni");
            System.exit(-1);
        }

        System.out.println("#####################################");
        System.out.println("Popolazione: " + maxNumIndividui);
        System.out.println("Elitismo: " + elitismo);
        System.out.println("Probabilità mutazioni: " + probMutazione);
        System.out.println("Numero partite: " + numPartite);
        System.out.println("Numero generazioni: " + numGenerazioni);
        System.out.println("Profondità massima: " + maxDepth);
        System.out.println("Limite turni: " + limiteTurni);
        System.out.println("Limite turni senza mangiare pedine: " + limiteTurniSenzaPedineMangiate);
        System.out.println("#####################################");

        //genera la popolazione iniziale
        List<Individual> population = new ArrayList<>();

        rndGen = new Random();
        rndGen.setSeed(System.currentTimeMillis());

        //1.1 Ottieni il vettore dei pesi da cui partire
        Path salvataggi = Path.of("./weights.json");
        if (Files.exists(salvataggi)) {
            double[][] weights = null;
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

            for (int i = 0; i < maxNumIndividui; i++) {
                population.add(new Individual(maxDepth, weights[i]));
            }

            System.out.println("Caricato salvataggio precedente");
        } else {
            //è la prima generazione, crea dati casuali
            for (int i = 0; i < maxNumIndividui; i++) {
                population.add(generaIndivisuoCasuale());
            }
            System.out.println("Genrati pesi casuali");
        }


        //Comincia a giocare
        for (int numGen = 0; numGen < numGenerazioni; numGen++) {
            System.out.println("---------------------------------------------------------------------\nGENERAZIONE " + (numGen + 1));
            //2.1 Fai tutte le sfide
            giocaPartite(population);

            //2.2 Metti in evidenza i migliori
            population.sort(Individual::compareTo);
            population = population.subList(0, maxNumIndividui);

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
            for (int idx = 0; idx < elitismo; idx++) {
                //tieni gli individui migliori
                newPopulation.add(new Individual(maxDepth, population.get(idx).getWeigths()));
            }

            int i = 0;
            //3.2 Seleziona gli individui da ricombinare e ricombina i geni
            for (int idx = elitismo; idx < maxNumIndividui + surplusIndividuals - randomSurplusIndividuals; idx += 2) {
                //Scegli il primo genitore
                while (rndGen.nextInt(100) > (60.0 / i + 1)) {
                    i = (i + 1) % maxNumIndividui;
                }

                //Scegli il secondo genitore
                int j = i;
                while (rndGen.nextInt(100) > (60.0 / j + 1) || i == j) {
                    j = (j + 1) % maxNumIndividui;
                }

                //Applica il crossover
                double[] wA = population.get(i).getWeigths();
                double[] wB = population.get(j).getWeigths();

                double[] newWA = new double[DIM_PESI];
                double[] newWB = new double[DIM_PESI];

                int crossover = rndGen.nextInt(DIM_PESI - 2) + 1;
                for (int k = 0; k < DIM_PESI; k++) {
                    if (k < crossover) {
                        newWA[k] = wA[k];
                        newWB[k] = wB[k];
                    } else {
                        newWA[k] = wB[k];
                        newWB[k] = wA[k];
                    }
                }

                newPopulation.add(new Individual(maxDepth, newWA));
                newPopulation.add(new Individual(maxDepth, newWB));

                i = (i + 1) % maxNumIndividui;
            }

            for (int idx = 0; idx < randomSurplusIndividuals; idx++) {
                newPopulation.add(generaIndivisuoCasuale());
            }


            //3.2 Applica eventuali mutazioni
            int probMutazione = rndGen.nextInt(100);
            if (probMutazione < TestTraining.probMutazione) {
                for (int idx = 0; idx < Math.ceil((maxNumIndividui * probMutazione) / 100.0); idx++) {
                    //scegli a caso un individuo da mutare
                    int individuoDaMutare = rndGen.nextInt(newPopulation.size());
                    int geneIdx = rndGen.nextInt(DIM_PESI);
                    double perc = (rndGen.nextInt(201) - 100) / 100.0;
                    newPopulation.get(individuoDaMutare).applyMutation(perc, geneIdx);
                }
            }
            //aggiorna il numero massimo di turni per fornire condizioni più stringenti
            if (numGen % 20 == 0) {
                double turniMedio = population.stream().map(Individual::getMeanVictoriesTurnNumber).reduce(0.0, Double::sum) / population.size();
                double turniMax = population.stream().map(Individual::getMeanVictoriesTurnNumber).max(Double::compare).orElse(-1.0);

                if (turniMax > 0 && turniMedio > 0 && limiteTurni > 150) {
                    var appo = (int) (turniMedio + 2 * ((turniMax - turniMedio) / 3));

                    limiteTurni = Math.max(appo, 150);
                    limiteTurniSenzaPedineMangiate = Math.max(30, Math.min(500, (int) (limiteTurni * 0.9)));
                }

                System.out.println("--> Aggiornato limite turni [" + limiteTurni + "]");
            }


            population = newPopulation;
        }
    }

    private static void saveSate(List<Individual> population, Path salvataggi) {
        double[][] weights = new double[maxNumIndividui][DIM_PESI];
        try (PrintWriter writer = new PrintWriter(salvataggi.toFile())) {
            Gson gson = new Gson();

            for (int i1 = 0; i1 < maxNumIndividui; i1++) {
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
        int[][] accoppiamenti = new int[numPartite * giocatori][2];

        for (int i = 0; i < numPartite * giocatori; i++) {
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

        for (int i = 0; i < numPartite * giocatori; i++) {
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

    private static Individual generaIndivisuoCasuale() {
        double[] w = new double[DIM_PESI];

        for (int i = 0; i < DIM_PESI; i++) {
            w[i] = rndGen.nextDouble() * upperLimitWeight;
        }

        return new Individual(maxDepth, w);
    }
}
