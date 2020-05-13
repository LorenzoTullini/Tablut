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

public class WhiteClient extends Client {

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
