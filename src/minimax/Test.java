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
    static int NUMERO_PARTITE = 10;
    static int profonditaMax = 5;
    static int profonditaMin = 3;
    static int timeoutSec = 20;
    ////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////
    //risultati
    static int vittorieBianchi = 0;
    static int vittorieNeri = 0;
    static List<Double> numTurni = new ArrayList<>();
    static List<Double> durataTurnoBianco = new ArrayList<>();
    static List<Double> durataTurnoNero = new ArrayList<>();
    ////////////////////////////////////////////////////////////

    public static void main(String[] args) {
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
                Minimax whiteMinimax = new Minimax(PlayerType.WHITE, profMax);
                Minimax blackMinimax = new Minimax(PlayerType.BLACK, profMax);
                TimerThread tt;

                schemi.add(s.hashCode());
                while (!s.hasWhiteWon() && !s.hasBlackWon()) {
                    //cominciano i bianchi
                    timeManager = new TimeManager();
                    tt = new TimerThread(timeManager, timeoutSec * 1000);
                    tt.start();
                    start = System.currentTimeMillis();
                    var whiteMove = whiteMinimax.alphabeta(s, timeManager, turn);
                    end = System.currentTimeMillis();
                    tt.interrupt();
                    durataTurnoBianco.add((end - start) / 1000.0);
                    //System.out.println("[B | " + turn + "] " + whiteMove);
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
                    var blackMove = blackMinimax.alphabeta(s, timeManager, turn);
                    end = System.currentTimeMillis();
                    tt.interrupt();
                    durataTurnoNero.add((end - start) / 1000.0);
                    //System.out.println("[N | " + turn + "] " + blackMove);
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
            }
            fineTest = System.currentTimeMillis();
            for (int i = 0; i < NUMERO_PARTITE; i++) {
                System.out.printf("\b");
            }
            System.out.printf("Durata Test:      \t%.2f s%n", (fineTest - inizioTest) / 1000.0);
            System.out.printf("Vittorie Bianche: \t%d (%.2f%%)%n", vittorieBianchi, (100.0 * vittorieBianchi) / NUMERO_PARTITE);
            System.out.printf("Vittorie Neri:    \t%d (%.2f%%)%n", vittorieNeri, (100.0 * vittorieNeri) / NUMERO_PARTITE);
            System.out.printf("Durata Turno Bianco  \tmed: %6.2f\tmax: %6.2f\tmin: %6.2f%n",
                    durataTurnoBianco.stream().reduce(0.0, Double::sum) / (1.0 * durataTurnoBianco.size()),
                    durataTurnoBianco.stream().max(Double::compare).orElse(-1.0),
                    durataTurnoBianco.stream().min(Double::compare).orElse(-1.0));
            System.out.printf("Durata Turno Nero    \tmed: %6.2f\tmax: %6.2f\tmin: %6.2f%n",
                    durataTurnoNero.stream().reduce(0.0, Double::sum) / (1.0 * durataTurnoBianco.size()),
                    durataTurnoNero.stream().max(Double::compare).orElse(-1.0),
                    durataTurnoNero.stream().min(Double::compare).orElse(-1.0));
            System.out.printf("Numero turni         \tmed: %6.2f\tmax: %6.2f\tmin: %6.2f%n",
                    numTurni.stream().reduce(0.0, Double::sum) / (1.0 * numTurni.size()),
                    numTurni.stream().max(Double::compare).orElse(-1.0),
                    numTurni.stream().min(Double::compare).orElse(-1.0));
            System.out.println("-------------------------------------------------");
        }
    }
}
