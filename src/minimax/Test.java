package minimax;

import client.TimeManager;
import client.TimerThread;
import model.PlayerType;
import model.TableState;

public class Test {
    static int NUMERO_PARTITE = 1;
    static int vittorieBianchi = 0;
    static int vittorieNeri = 0;

    public static void main(String[] args) {
        TimeManager timeManager = new TimeManager();


        for (int i = 0; i < NUMERO_PARTITE; i++) {
            TableState s = new TableState();
            int turn = 0;
            Minimax whiteMinimax = new Minimax(PlayerType.WHITE, 5, 2);
            Minimax blackMinimax = new Minimax(PlayerType.WHITE, 5, 2);
            TimerThread tt;
            while (!s.hasWhiteWon() && !s.hasBlackWon()) {
                //cominciano i bianchi
                tt = new TimerThread(timeManager, 55 * 1000);
                tt.start();
                var whiteMove = whiteMinimax.alphabeta(s, timeManager, turn);
                tt.interrupt();
                System.out.println("[B | " + turn + "]" + whiteMove);
                s = s.performMove(whiteMove);
                turn++;
                if (s.hasWhiteWon()) {
                    vittorieBianchi++;
                    break;
                }
                tt = new TimerThread(timeManager, 55 * 1000);
                tt.start();
                var blackMove = whiteMinimax.alphabeta(s, timeManager, turn);
                tt.interrupt();
                System.out.println("[N | " + turn + "]" + blackMove);
                s = s.performMove(whiteMove);
                turn++;
                if (s.hasWhiteWon()) {
                    vittorieBianchi++;
                    break;
                }
            }

            System.out.println("-------------------------------------------------");
            System.out.println("Vittorie Bianche: " + vittorieBianchi);
            System.out.println("Vittorie Neri: " + vittorieNeri);
            System.out.println("-------------------------------------------------");
        }
    }
}
