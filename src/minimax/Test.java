package minimax;

import client.TimeManager;
import client.TimerThread;
import model.PlayerType;
import model.TableState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Test {
    ////////////////////////////////////////////////////////////
    //parametri test
    static int NUMERO_PARTITE = 1;
    static int profonditaMax = 6;
    static int profonditaMin = 6;
    static int timeoutSec = 57;
    ////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////
    //risultati
    static int vittorieBianchi = 0;
    static int vittorieNeri = 0;
    static int timerScattatoBianchi = 0;
    static int timerScattatoNeri = 0;
    static List<Double> numTurni = new ArrayList<>();
    static List<Double> durataTurnoBianco = new ArrayList<>();
    static List<Double> durataTurnoNero = new ArrayList<>();
    ////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////
    //Costanti scacchiera
    public static final int W = 0;    // pedone bianco
    public static final int B = 1;    // pedone nero che lascia il suo campo
    public static final int BA = 2;   // pedone nero che è ancora nel campo nord / est
    public static final int BB = 3;   // pedone nero che è ancora nel campo sud / ovest
    public static final int E = 4;    // cella vuota
    public static final int K = 5;    // re
    private static SearchManager whitePlayer;
    private static SearchManager blackPlayer;

    ////////////////////////////////////////////////////////////
    public static void main(String[] args) {
        //Partite con la stessa profondità
        test1();

        //Scelta mossa
        //test2();

        //Partita singola a livelli diversi
//        test3();
    }

    public static void test1() {
        TimeManager timeManager = new TimeManager();
        long start, end;

        for (int profMax = profonditaMin; profMax <= profonditaMax; profMax++) {
            vittorieBianchi = 0;
            vittorieNeri = 0;
            numTurni = new ArrayList<>();
            durataTurnoBianco = new ArrayList<>();
            durataTurnoNero = new ArrayList<>();
            long inizioTest, fineTest;


            System.out.println("-------------------------------------------------");
            System.out.println("Profondità: " + profMax);
            inizioTest = System.currentTimeMillis();
            for (int i = 0; i < NUMERO_PARTITE; i++) {
                //System.out.print("PARTITA: " + (i + 1));
                System.out.print("#");
                TableState s = new TableState();
                Set<Integer> schemi = new HashSet<>();

                int turn = 0;
                double[] whiteWeight = {9.445331073776009, 8.670812658531949, 8.12921127090503, 1.8868417746983779, 8.637273420997074, 6.574100859946532, 7.716411965121014, 4.083149421565183, 4.970152789660535, 3.665129527693962};
                double[] blackWeight = {5.414040062028535, 0.06408644334610303, 0.06859883511475595, 8.102063972782604, 1.7644592171299067, 0.6864202948751519, 6.680715182359252, 1.9847345796833815, 4.232931236749085, 2.598971847125399};
                whitePlayer = new SearchManager(PlayerType.WHITE, profMax, whiteWeight);
                blackPlayer = new SearchManager(PlayerType.BLACK, profMax, blackWeight);
                TimerThread tt;

                schemi.add(s.hashCode());
                while (!s.hasWhiteWon() && !s.hasBlackWon()) {
                    //cominciano i bianchi
                    timeManager = new TimeManager();
                    tt = new TimerThread(timeManager, timeoutSec * 1000);
                    tt.start();
                    start = System.currentTimeMillis();
                    var whiteMove = whitePlayer.search(s, timeManager, turn);
                    //System.out.println(whiteMove);
                    end = System.currentTimeMillis();
                    tt.interrupt();
                    if (timeManager.isEnd()) {
                        timerScattatoBianchi++;
                    }
                    durataTurnoBianco.add((end - start) / 1000.0);
//                    System.out.println("[B | " + turn + "] " + whiteMove);
                    s = s.performMove(whiteMove);
                    if (s.hasWhiteWon()) {
                        vittorieBianchi++;
                        numTurni.add((double) turn);
                        //System.out.println("  --> Bianchi");
                        break;
                    } else if (s.hasBlackWon()) {
                        vittorieNeri++;
                        numTurni.add((double) turn);
                        //System.out.println("  --> Neri");
                        break;
                    } else if (schemi.contains(s.hashCode())) {
                        numTurni.add((double) turn);
                        //System.out.println("  --> Patta");
                        break;
                    }
                    schemi.add(s.hashCode());
                    turn++;

                    timeManager = new TimeManager();
                    tt = new TimerThread(timeManager, timeoutSec * 1000);
                    tt.start();
                    start = System.currentTimeMillis();
                    var blackMove = blackPlayer.search(s, timeManager, turn);
                    //System.out.println(blackMove);
                    end = System.currentTimeMillis();
                    tt.interrupt();
                    if (timeManager.isEnd()) {
                        timerScattatoNeri++;
                    }
                    durataTurnoNero.add((end - start) / 1000.0);
//                    System.out.println("[N | " + turn + "] " + blackMove);
                    s = s.performMove(blackMove);
                    if (s.hasWhiteWon()) {
                        vittorieBianchi++;
                        numTurni.add((double) turn);
                        //System.out.println("  --> Bianchi");
                        break;
                    } else if (s.hasBlackWon()) {
                        vittorieNeri++;
                        numTurni.add((double) turn);
                        //System.out.println("  --> Neri");
                        break;
                    } else if (schemi.contains(s.hashCode())) {
                        numTurni.add((double) turn);
                        //System.out.println("  --> Patta");
                        break;
                    }
                    schemi.add(s.hashCode());
                    turn++;
                }

                whitePlayer.stop();
                blackPlayer.stop();
            }
            fineTest = System.currentTimeMillis();
            for (int i = 0; i < NUMERO_PARTITE; i++) {
                System.out.print("\b");
            }
            System.out.printf("Durata Test:      \t%.2f s%n", (fineTest - inizioTest) / 1000.0);
            System.out.printf("Vittorie Bianche: \t%d (%.2f%%)%n", vittorieBianchi, (100.0 * vittorieBianchi) / NUMERO_PARTITE);
            System.out.printf("Vittorie Neri:    \t%d (%.2f%%)%n", vittorieNeri, (100.0 * vittorieNeri) / NUMERO_PARTITE);
            System.out.printf("Durata Turno Bianco  \tmed: %6.2f  |  max: %6.2f  |  min: %6.2f  |  timer: %3d%n",
                    durataTurnoBianco.stream().reduce(0.0, Double::sum) / (1.0 * durataTurnoBianco.size()),
                    durataTurnoBianco.stream().max(Double::compare).orElse(-1.0),
                    durataTurnoBianco.stream().min(Double::compare).orElse(-1.0),
                    timerScattatoBianchi);
            System.out.printf("Durata Turno Nero    \tmed: %6.2f  |  max: %6.2f  |  min: %6.2f  |  timer: %3d%n",
                    durataTurnoNero.stream().reduce(0.0, Double::sum) / (1.0 * durataTurnoBianco.size()),
                    durataTurnoNero.stream().max(Double::compare).orElse(-1.0),
                    durataTurnoNero.stream().min(Double::compare).orElse(-1.0),
                    timerScattatoNeri);
            System.out.printf("Numero turni         \tmed: %6.2f  |  max: %6.2f  |  min: %6.2f%n",
                    numTurni.stream().reduce(0.0, Double::sum) / (1.0 * numTurni.size()),
                    numTurni.stream().max(Double::compare).orElse(-1.0),
                    numTurni.stream().min(Double::compare).orElse(-1.0));
            System.out.println("-------------------------------------------------");
        }
    }

    public static void test2() {
//        int[][] table = new int[][]{
//                {E, E, E, E, E, E, E, E, E},
//                {E, E, E, E, E, E, E, E, E},
//                {E, E, E, E, B, E, B, E, E},
//                {E, E, B, E, K, B, E, E, E},
//                {E, E, E, E, E, E, E, E, E},
//                {E, E, E, B, E, E, E, E, E},
//                {E, B, W, E, W, B, E, E, E},
//                {E, E, E, E, E, E, E, E, E},
//                {E, E, E, E, E, E, E, E, E}
//        };
//
//        TableState state = new TableState(table);

//        Minimax player = new Minimax(PlayerType.BLACK, 5);
//        TimeManager timeManager = new TimeManager();
//        var tt = new TimerThread(timeManager, 10000 * 1000);
//        tt.start();
//        Move res = player.alphabeta(state, timeManager, 0);
//        tt.interrupt();
//        System.out.println(res);

//        Minimax player = new Minimax(PlayerType.BLACK, 4);
//        TimeManager timeManager = new TimeManager();
//        var tt = new TimerThread(timeManager, 10000 * 1000);
//        tt.start();
//        Move res = player.alphabeta(state, timeManager, 0);
//        tt.interrupt();
//        System.out.println(res);
//
//        TableState s = new TableState();
//        var moves = s.getAllMovesFor(PlayerType.WHITE);
//        System.out.println("");
    }

    private static void test3() {
//        TimeManager timeManager = new TimeManager();
//        long start, end;
//        TableState s = new TableState();
//        Set<Integer> schemi = new HashSet<>();
//
//        int turn = 0;
//        Minimax whiteMinimax = new Minimax(PlayerType.WHITE, 3);
//        Minimax blackMinimax = new Minimax(PlayerType.BLACK, 3);
//        TimerThread tt;
//
//        schemi.add(s.hashCode());
//        while (!s.hasWhiteWon() && !s.hasBlackWon()) {
//            //cominciano i bianchi
//            timeManager = new TimeManager();
//            tt = new TimerThread(timeManager, timeoutSec * 1000);
//            System.out.println("Mosse turno " + turn + " ---> " + s.getAllMovesFor(PlayerType.WHITE).size());
//            tt.start();
//            start = System.currentTimeMillis();
//            var whiteMove = whiteMinimax.alphabeta(s, timeManager, turn);
//            //System.out.println(whiteMove);
//            end = System.currentTimeMillis();
//            tt.interrupt();
//            if (timeManager.isEnd()) {
//                timerScattatoBianchi++;
//            }
//
//            System.out.println("[B | " + turn + "] " + whiteMove + "  -  (" + (end - start) / 1000.0 + ")");
//            s = s.performMove(whiteMove);
//            if (s.hasWhiteWon()) {
//                System.out.println("  --> Bianchi");
//                break;
//            } else if (s.hasBlackWon()) {
//                System.out.println("  --> Neri");
//                break;
//            } else if (schemi.contains(s.hashCode())) {
//                System.out.println("  --> Patta");
//                break;
//            }
//            schemi.add(s.hashCode());
//            turn++;
//
//            timeManager = new TimeManager();
//            tt = new TimerThread(timeManager, timeoutSec * 1000);
//            System.out.println("Mosse turno " + turn  +" ---> " + s.getAllMovesFor(PlayerType.BLACK).size());
//            tt.start();
//            start = System.currentTimeMillis();
//            var blackMove = blackMinimax.alphabeta(s, timeManager, turn);
//            //System.out.println(blackMove);
//            end = System.currentTimeMillis();
//            tt.interrupt();
//
//
//            System.out.println("[N | " + turn + "] " + blackMove + "  -  (" + (end - start) / 1000.0 + ")");
//            s = s.performMove(blackMove);
//            if (s.hasWhiteWon()) {
//                System.out.println("  --> Bianchi");
//                break;
//            } else if (s.hasBlackWon()) {
//                System.out.println("  --> Neri");
//                break;
//            } else if (schemi.contains(s.hashCode())) {
//                System.out.println("  --> Patta");
//                break;
//            }
//            schemi.add(s.hashCode());
//            turn++;
//        }
    }
}
