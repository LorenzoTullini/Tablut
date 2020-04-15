package client;

import minimax.Minimax;
import model.Move;
import model.PlayerType;
import model.TableState;
import utils.Converter;
import utils.Network;
import utils.ServerMove;
import utils.ServerState;

import java.util.Arrays;
import java.util.Scanner;

public class WhiteClient {

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
        playerType = PlayerType.WHITE;
        ntw = new Network("localhost", 5800);

        //humanPlayer(playerType);
        aiPlayer(playerType);

    }

    //--------------------------------FUNZIONI DI TEST----------------------------------------
    private static void humanPlayer(PlayerType playerType){
        ntw.sendPlayerName("JavaBeneCosi");
        String stateJson;

        //Ricevo stato iniziale
        stateJson = ntw.getState();
        ServerState serverState =new ServerState(stateJson);
        TableState tableState = serverState.getTableState();
        System.out.println(tableState.toString());
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
            tableState = serverState.getTableState();
            System.out.println(tableState.toString());

            turn++;
        }
        ntw.distroyNetwork();
    }

    private static void aiPlayer(PlayerType playerType){
        ntw.sendPlayerName("JavaBeneCosi");
        String stateJson;

        //Ricevo stato iniziale
        stateJson = ntw.getState();
        ServerState serverState =new ServerState(stateJson);
        TableState tableState = serverState.getTableState();
        System.out.println("STATO INIZIALE: ");
        serverState.printStatus();
        int turn = 0;

        /*long start = System.currentTimeMillis();

        tt.start();
        Minimax minimax = new Minimax(playerType, 6, 2);
        Move bestMove = minimax.alphabeta(new TableState(), timeManager, 0);
        //Move bestMove = minimax.parallelAlphaBeta(new TableState(), timeManager, 0);
        tt.interrupt();

        long stop = System.currentTimeMillis();
        System.out.println("Ci ho messo: "+(stop - start));
        System.out.println("Ho trovato la mossa: " + bestMove.toString());*/

        Minimax minimax = new Minimax(playerType, 5);

        while(true) {
            System.out.println("Hashcode dello stato: " + Arrays.deepHashCode(tableState.getState()) + " turno: " + turn);

            //Controllo se lo stato ricevuto rappresenta una partita in corso
            if(serverState.haveIWin(playerType)){
                System.out.println("Ho vinto !!");
                break;
            }else if(serverState.haveILost(playerType)){
                System.out.println("Ho perso !!");
                break;
            }else if(serverState.isDraw()){
                System.out.println("Partita terminata in pareggio");
                break;
            }

            if(serverState.isMyTurn(playerType)){
                timeManager = new TimeManager();
                tt = new TimerThread(timeManager, 55*1000);

                tt.start();
                Move bestMove = minimax.alphabeta(tableState, timeManager, turn);
                tt.interrupt(); tt = null;

                ServerMove serverMove = Converter.covertMove(bestMove, playerType);
                System.out.println("Ho trovato la mossa (Server): " + serverMove.getFrom() + " " + serverMove.getTo());
                System.out.println("Ho trovato la mossa (My): " + bestMove.toString());

                ntw.sendMove(Converter.covertMove(bestMove, playerType));
            }else{
                System.out.println("Attendo la mossa dell'avversario:");
            }

            //Ricevo il nuovo stato
            stateJson = ntw.getState();
            serverState = new ServerState(stateJson);
            tableState = serverState.getTableState();
            System.out.println("NUOVO STATO: ");
            serverState.printStatus();

            turn+=1;
        }
        ntw.distroyNetwork();
    }
    //----------------------------------------------------------------------------------


}
