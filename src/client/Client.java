package client;

import minimax.Minimax;
import minimax.SearchManager;
import model.Move;
import model.PlayerType;
import model.TableState;
import picocli.*;
import utils.Converter;
import utils.Network;
import utils.ServerMove;
import utils.ServerState;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Callable;


@CommandLine.Command(name = "Tablut", description = "Tablut player for AI challenge 2020")
public class Client implements Callable<Integer> {
    protected static TimeManager timeManager;
    protected static PlayerType playerType;
    protected static Network ntw;
    protected static TimerThread tt;

    //protected static double[] weights = {8.474103289835748,1.8295157913944782,1.291744934359984,5.920307944662863,2.788330155110984,3.378635735382176,4.368292230811901,1.8382531470540242,1.688222144073156,7.582662191261829};
    //protected static double[] weights = {8.021916181985366,1.0069419102736366,7.975680540770171,0.25510797785825434,5.093328241658882,4.729064241052356,4.171856197640489,2.041449275011095,8.590423109877092,1.6352226689530092};
    //protected static double[] weights = {4.70939710036113,8.731825529736884,7.257774900748454,7.221486991804536,4.6863836669851775,1.6587869381073184,5.070584122170242,3.0495995771240048,0.24045106209754752,8.70597677586224};
    //protected static double[] weights = {4.448338617446707,0.742233413226656,2.27705129510672,0.6185061255260171,6.719671599763579,4.896041045740383,6.835278141504425,0.45642664127451377,4.3449547688259695,3.9538584161218338};
    //protected static double[] weights = {4.33390619023233,5.298118218820553,8.58779261794399,2.9439039170482495,6.569481687056326,2.9158311744799525,7.580389872569201,8.175775867806239,8.630962464757433,4.245366951962528};
    //protected static double[] weights ={4.584377743927578,7.079483277270357,3.192965225478226,1.480581092631288,8.960026173394068,1.857562360010182,7.08287614389612,14.634985264738383,1.8401194488825734,4.030292506538813};
    //protected static double[] weights = {7.609734594562109,8.123747815463352,3.431904481529693,5.407894927330962,2.5234346798602947,1.9179947349067827,3.9697450276791937,7.975120185285592,3.2507008402266866,8.187762207546722};
    //protected static double[] weights = {5.414040062028535,0.06408644334610303,0.06859883511475595,8.102063972782604,1.7644592171299067,0.6864202948751519,6.680715182359252,1.9847345796833815,4.232931236749085,2.598971847125399};
    //protected static double[] weights = {9.445331073776009,8.670812658531949,8.12921127090503,1.8868417746983779,8.637273420997074,6.574100859946532,7.716411965121014,4.083149421565183,4.970152789660535,3.665129527693962};
    protected static double[] weights = {9.389776654558158,0.10574026884115462,2.0755798976385176,4.403229726133028,3.571539782776829,0.12944669674123999,7.044605336888342,0.5114350076553287,3.7401740227408142,3.352876193998066};
    //protected static double[] weights ={3.1301721620969025,9.16554180720929,3.9774668634507715,9.269123965711122,5.841269998856399,3.687072763859925,7.133994411268187,5.62716847375469,3.7401740227408142,3.352876193998066};
    @CommandLine.Option(names = {"-c", "--color"}, required = true, description = "player color")
    private String color = null;
    @CommandLine.Option(names = {"-t", "--timer"}, required = true, description = "move max time")
    protected static int timer = -1;
    @CommandLine.Option(names = {"-d", "--deapth"}, required = false, description = "max search deapth (default 5)")
    protected static int maxDepth = 5;
    protected static int whitePort = 5800;
    protected static int blackPort = 5801;
    private static int port = 0;
    @CommandLine.Option(names = {"-s", "--serverIp"}, required = true, description = "game server ip (default localhost)")
    protected static String serverAddress = "localhost";
    protected static String nome = "JavaBeneCosi";
    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "print this help")
    private boolean help = false;
    @CommandLine.Option(names = {"-v", "--verbose"}, required = false, description = "print more information about game")
    private static boolean verbose = false;

    public static void main(String... args) {
        int exitCode = new CommandLine(new Client()).execute(args);
        if (exitCode != 0) System.exit(exitCode);
        ntw = new Network(serverAddress, port);
        aiPlayer(playerType);
        System.exit(exitCode);
    }

    private static void printVerbose(String out){
        if(verbose) System.out.println(out);
    }

    @Override
    public Integer call() throws Exception {
        color = color.toLowerCase();
        if (color == null) return -1;
        if(timer == -1) return -1;
        if(color.equals("black")){
            playerType = PlayerType.BLACK;
            port = blackPort;
        }else if(color.equals("white")){
            playerType = PlayerType.WHITE;
            port = whitePort;
        }else{
            System.err.println("Player: "+color+" is not valid (black/white)");
            CommandLine.usage(this, System.err);
            return -1;
        }
        return 0;
    }

    protected static void aiPlayer(PlayerType playerType){
        ntw.sendPlayerName(nome);
        String stateJson;

        //Ricevo stato iniziale
        stateJson = ntw.getState();
        ServerState serverState =new ServerState(stateJson);
        TableState tableState = serverState.getTableState();
        System.out.println("STATO INIZIALE: ");
        serverState.printStatus();

        int turn = 0;
        SearchManager searchManager = new SearchManager(playerType, maxDepth, weights);

        while(true) {
            printVerbose("Hashcode dello stato: " + Arrays.deepHashCode(tableState.getState()));
            System.out.println("Turno: " + turn);
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
                tt = new TimerThread(timeManager, (timer-3)*1000);

                tt.start();
                Move bestMove = searchManager.search(tableState, timeManager, turn);
                tt.interrupt(); tt = null;

                ServerMove serverMove = Converter.covertMove(bestMove, playerType);
                System.out.println("Ho trovato la mossa (Server): " + serverMove.getFrom() + " " + serverMove.getTo());
                printVerbose("Ho trovato la mossa (My): " + bestMove.toString());

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
        searchManager.stop();
        ntw.distroyNetwork();
    }
    protected static void humanPlayer(PlayerType playerType){
        ntw.sendPlayerName(nome);
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

}
