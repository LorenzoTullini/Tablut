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
        //double[] weights = {8.474103289835748,1.8295157913944782,1.291744934359984,5.920307944662863,2.788330155110984,3.378635735382176,4.368292230811901,1.8382531470540242,1.688222144073156,7.582662191261829};
        //double[] weights = {8.021916181985366,1.0069419102736366,7.975680540770171,0.25510797785825434,5.093328241658882,4.729064241052356,4.171856197640489,2.041449275011095,8.590423109877092,1.6352226689530092};
        //double[] weights = {4.70939710036113,8.731825529736884,7.257774900748454,7.221486991804536,4.6863836669851775,1.6587869381073184,5.070584122170242,3.0495995771240048,0.24045106209754752,8.70597677586224};
        //double[] weights = {4.448338617446707,0.742233413226656,2.27705129510672,0.6185061255260171,6.719671599763579,4.896041045740383,6.835278141504425,0.45642664127451377,4.3449547688259695,3.9538584161218338};
        //double[] weights = {4.33390619023233,5.298118218820553,8.58779261794399,2.9439039170482495,6.569481687056326,2.9158311744799525,7.580389872569201,8.175775867806239,8.630962464757433,4.245366951962528};
        //double[] weights ={4.584377743927578,7.079483277270357,3.192965225478226,1.480581092631288,8.960026173394068,1.857562360010182,7.08287614389612,14.634985264738383,1.8401194488825734,4.030292506538813};
        //double[] weights = {7.609734594562109,8.123747815463352,3.431904481529693,5.407894927330962,2.5234346798602947,1.9179947349067827,3.9697450276791937,7.975120185285592,3.2507008402266866,8.187762207546722};
        double[] weights = {5.414040062028535,0.06408644334610303,0.06859883511475595,8.102063972782604,1.7644592171299067,0.6864202948751519,6.680715182359252,1.9847345796833815,4.232931236749085,2.598971847125399};
        Minimax minimax = new Minimax(playerType, 4, weights);

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
                tt = new TimerThread(timeManager, 57*1000);

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
