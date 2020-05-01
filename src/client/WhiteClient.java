package client;

import model.PlayerType;
import utils.Network;

public class WhiteClient extends Client{

    public static void main(String argv[]) {
        playerType = PlayerType.WHITE;
        ntw = new Network(serverAddress, whitePort);
        aiPlayer(playerType);
    }

    public static void runAsWhite() {
        playerType = PlayerType.WHITE;
        ntw = new Network(serverAddress, whitePort);
        aiPlayer(playerType);
    }
}
