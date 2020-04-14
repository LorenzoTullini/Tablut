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
    static int NUMERO_PARTITE = 30;
    static int profonditaMax = 5;
    static int vittorieBianchi = 0;
    static int vittorieNeri = 0;
    static List<Integer> numTurni = new ArrayList<>();
    static List<Double> durataTurnoBianco = new ArrayList<>();
    static List<Double> durataTurnoNero = new ArrayList<>();


    public static void main(String[] args) {
        TimeManager timeManager = new TimeManager();
        long start, end;

        for (int profMax = 3; profMax <= profonditaMax; profMax++) {
            vittorieBianchi = 0;
            vittorieNeri = 0;
            numTurni = new ArrayList<>();
            durataTurnoBianco = new ArrayList<>();
            durataTurnoNero = new ArrayList<>();

            System.out.println("-------------------------------------------------");
            System.out.println("Profondità: " + profMax);

            for (int i = 0; i < NUMERO_PARTITE; i++) {
                //System.out.print("PARTITA: " + (i + 1));
                System.out.print("#");
                TableState s = new TableState();
                Set<Integer> schemi = new HashSet<>();

                int turn = 0;
                Minimax whiteMinimax = new Minimax(PlayerType.WHITE, profMax, 2);
                Minimax blackMinimax = new Minimax(PlayerType.BLACK, profMax, 2);
                TimerThread tt;

                schemi.add(s.hashCode());
                while (!s.hasWhiteWon() && !s.hasBlackWon()) {
                    //cominciano i bianchi
                    tt = new TimerThread(timeManager, 55 * 1000);
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
                        numTurni.add(turn);
                        //System.out.println("  --> Bianchi");
                        break;
                    } else if (s.hasBlackWon()) {
                        vittorieNeri++;
                        numTurni.add(turn);
                        //System.out.println("  --> Neri");
                        break;
                    } else if (schemi.contains(s.hashCode())) {
                        numTurni.add(turn);
                        //System.out.println("  --> Patta");
                        break;
                    }
                    schemi.add(s.hashCode());
                    turn++;

                    tt = new TimerThread(timeManager, 55 * 1000);
                    tt.start();
                    start = System.currentTimeMillis();
                    var blackMove = whiteMinimax.alphabeta(s, timeManager, turn);
                    end = System.currentTimeMillis();
                    tt.interrupt();
                    durataTurnoNero.add((end - start) / 1000.0);
                    //System.out.println("[N | " + turn + "] " + blackMove);
                    s = s.performMove(blackMove);
                    if (s.hasWhiteWon()) {
                        vittorieBianchi++;
                        numTurni.add(turn);
                        //System.out.println("  --> Bianchi");
                        break;
                    } else if (s.hasBlackWon()) {
                        vittorieNeri++;
                        numTurni.add(turn);
                        //System.out.println("  --> Neri");
                        break;
                    } else if (schemi.contains(s.hashCode())) {
                        numTurni.add(turn);
                        //System.out.println("  --> Patta");
                        break;
                    }
                    schemi.add(s.hashCode());
                    turn++;
                }
            }
            for (int i = 0; i < NUMERO_PARTITE; i++) {
                System.out.printf("\b");
            }
            System.out.printf("Vittorie Bianche: %s (%.2f%%)%n", vittorieBianchi, (100.0 * vittorieBianchi) / NUMERO_PARTITE);
            System.out.printf("Vittorie Neri: %s (%.2f%%)%n", vittorieNeri, (100.0 * vittorieNeri) / NUMERO_PARTITE);
            System.out.printf("Durata Turno Bianco  med: %.2f  max: %.2f min: %.2f%n",
                    durataTurnoBianco.stream().reduce(0.0, (a, b) -> a + b) / (1.0 * durataTurnoBianco.size()),
                    durataTurnoBianco.stream().max(Double::compare).orElse(-1.0),
                    durataTurnoBianco.stream().min(Double::compare).orElse(-1.0));
            System.out.printf("Durata Turno Nero    med: %.2f  max: %.2f min: %.2f%n",
                    durataTurnoNero.stream().reduce(0.0, (a, b) -> a + b) / (1.0 * durataTurnoBianco.size()),
                    durataTurnoNero.stream().max(Double::compare).orElse(-1.0),
                    durataTurnoNero.stream().min(Double::compare).orElse(-1.0));
            System.out.println("-------------------------------------------------");
        }
    }
}
