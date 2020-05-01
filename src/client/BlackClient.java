package client;

import model.PlayerType;
import utils.Network;


public class BlackClient extends Client {

    public static void main(String argv[]) {
        playerType = PlayerType.BLACK;
        ntw = new Network(serverAddress, blackPort);
        aiPlayer(playerType);
    }

    public static void runAsBlack() {
        playerType = PlayerType.BLACK;
        ntw = new Network(serverAddress, blackPort);
        aiPlayer(playerType);
    }

}
