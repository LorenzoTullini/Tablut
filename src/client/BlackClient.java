package client;

import minimax.Minimax;
import model.Coord;
import model.Move;
import model.PlayerType;
import model.TableState;
import utils.*;

import java.util.Random;
import java.util.Scanner;

public class BlackClient {

    private static TimeManager timeManager;
    private static PlayerType playerType;
    private static Network ntw;
    private static TimerThread tt;

    /*
    TODO Lista parametri:
       - Nome giocatore
       - Player Type
       - IP server
       - Porta server
    */


    public static void main(String argv[]) {
        timeManager = new TimeManager();
        tt = new TimerThread(timeManager, 60*1000);

        playerType = PlayerType.BLACK;
        ntw = new Network("localhost", 5801);

        //humanPlayer(PlayerType.WHITE);
        aiPlayer(playerType);

    }

    //--------------------------------FUNZIONI DI TEST----------------------------------------
    private static void humanPlayer(PlayerType playerType){
        ntw.sendPlayerName("JavaBeneCosi");
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

    private static void aiPlayer(PlayerType playerType){
        ntw.sendPlayerName("JavaBeneCosi");
        String stateJson;

        //Ricevo stato iniziale
        stateJson = ntw.getState();
        ServerState serverState =new ServerState(stateJson);
        serverState.printStatus();
        TableState tableState = serverState.getTableState();
        int turn = 0;

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
                tt.start();
                //Qui va minmax
                tt.interrupt();
                //ntw.sendMove(Converter.covertMove(m, playerType));
            }else{
                System.out.println("Attendo la mossa dell'avversario:");
            }

            //Ricevo il nuovo stato
            stateJson = ntw.getState();
            serverState = new ServerState(stateJson);
            serverState.printStatus();
            tableState = serverState.getTableState();

            turn++;
        }
        ntw.distroyNetwork();
    }
    //----------------------------------------------------------------------------------


}
