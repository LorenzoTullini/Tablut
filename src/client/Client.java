package client;

import model.Coord;
import model.Move;
import model.PlayerType;
import utils.*;

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

        humanPlayer(PlayerType.WHITE);

    }

    //--------------------------------FUNZIONI DI TEST----------------------------------------
    private static void humanPlayer(PlayerType playerType){
        ntw.sendPlayerName("Chesani");
        String stateJson;

        //Ricevo stato iniziale
        stateJson = ntw.getState();
        ServerState serverState =new ServerState(stateJson);
        serverState.printStatus();

        while(true) {
            //Controllo se lo stato ricevuto rappresenta una partita in corso
            if(serverState.haveIWin(playerType)){
                System.out.println("Ho vinto !!");
                break;
            }else if(serverState.haveILost(playerType)){
                System.out.println("Ho perso !!");
                break;
            }

            if(serverState.isMyTurn(playerType)){
                System.out.print("Scegli pedina: ");
                Scanner scanner = new Scanner(System.in);
                String from = scanner.nextLine();
                System.out.print("Fai la tua mossa: ");
                String to = scanner.nextLine();

                ServerMove serverMove = new ServerMove(from, to, Converter.typeConverter(playerType));
                ntw.sendMove(serverMove);
            }else{
                System.out.println("Attendo la mossa dell'avversario:");
            }

            //Ricevo il nuovo stato
            stateJson = ntw.getState();
            serverState = new ServerState(stateJson);
            serverState.printStatus();
        }
        ntw.distroyNetwork();
    }

    //----------------------------------------------------------------------------------


}
