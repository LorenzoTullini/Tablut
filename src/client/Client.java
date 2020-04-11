package client;

import model.Coord;
import model.Move;
import model.PlayerType;
import utils.Converter;
import utils.Network;
import utils.ServerMove;
import utils.ServerPlayerType;

import java.util.Random;
import java.util.Scanner;

public class Client {
    private static TimeManager timeManager;
    private static PlayerType playerType;
    private static Network ntw;
    /*
    TODO Lista parametri:
       - Nome giocatore
       - Player Type
       - IP server
       - Porta server
    */

    public static void main(String argv[]) {
        timeManager = new TimeManager();
        TimerThread tt = new TimerThread(timeManager, 60*1000);
        playerType = PlayerType.WHITE;
        ntw = new Network("localhost", 5800);

        humanPlayer(ServerPlayerType.WHITE);

    }

    //--------------------------------FUNZIONI DI TEST----------------------------------------
    private static void humanPlayer(ServerPlayerType type){
        ntw.sendPlayerName("Chesani");
        String state;

        while(true) {
            System.out.println("Aspetto l'avversario ...");
            state = ntw.getState();

            System.out.print("Scegli pedina: ");
            Scanner scanner = new Scanner(System.in);
            String from = scanner.nextLine();
            System.out.print("Fai la tua mossa: ");
            String to = scanner.nextLine();

            ServerMove serverMove = new ServerMove(from, to, type);
            ntw.sendMove(serverMove);
        }


    }

    private static void protocolTester(){
        //--------INIZIO PROTOCOLLO SERVER--------
        ntw.sendPlayerName("Chesani");
        String state = ntw.getState();
        System.out.println(state);
        Move randomMove = randomMoveGenerator();
        ntw.sendMove(randomMove, playerType);
        //--------FINE PROTOCOLLO SERVER--------
        ntw.distroyNetwork();
    }

    private static Move randomMoveGenerator(){
        Random random = new Random();
        Coord from = new Coord(random.nextInt(8), random.nextInt(8));
        Coord to = new Coord(random.nextInt(8), random.nextInt(8));
        return new Move(from,to);
    }
    //----------------------------------------------------------------------------------
}
