package client;

public class Client {
    private static TimeManager timeManager;

    /*
    TODO Lista parametri:
       - Nome giocatore
       - Player Type
       - IP server
       - Porta server
    */

  /*  public static void main(String argv[]) {
        timeManager = new TimeManager();
        TimerThread tt = new TimerThread(timeManager, 20*1000);
        Network ntw = new Network("localhost", 9999);

        /*tt.start();
        try {
            pseudoMiniMax();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

      /*  System.out.println("[Main] Invio mossa al meraviglioso server di Chesani");
        ntw.sendPlayerName("Chesani è bellissimo");
        ntw.distroyNetwork();

    }

    private static void pseudoMiniMax() throws InterruptedException {
        while (!timeManager.isEnd()){
            System.out.println("[MiniMax] Faccio conti difficilissimi !!!");
            Thread.sleep(1000);
        }
    }*/
}
