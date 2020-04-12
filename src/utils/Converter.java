package utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.Board;
import model.Coord;
import model.Move;
import model.PlayerType;

public class Converter {
    private static String convertCoord(Coord c){
        char x = c.getX()<0 || c.getX()>8 ? '?' : (char)('a' + c.getX());
        int y = c.getY();
        String res = ""+x+y;
        return res;
    }

    public static ServerMove covertMove(Move m, PlayerType playerType){
        String from = convertCoord(m.getFrom());
        String to = convertCoord(m.getTo());
        ServerPlayerType serverPlayerType = null;
        switch (playerType){
            case WHITE: serverPlayerType = ServerPlayerType.WHITE;
            case BLACK: serverPlayerType = ServerPlayerType.BLACK;
        }
        return new ServerMove(from,to,serverPlayerType);
    }

    public static int[][] convertServerState(String jsonServerBoard){
        int row = 0;
        int col = 0;


        int E = 0;
        int B = 1; // pedone nero che lascia il suo campo
        int BA = 2; //pedone nero che è ancora nel campo nord / est
        int BB = 3; //pedone nero che è ancora nel campo sud / ovest
        int W = 4;
        int K = 5;

        int CA = -2; // campi neri parte nord / est
        int CB = -3; // campi neri parte sud / ovest

        JsonObject jsObject = new JsonParser().parse(jsonServerBoard).getAsJsonObject();
        JsonArray serverBoard = jsObject.getAsJsonArray("board");
        int[][] emptyBoard = new Board().getBoard();
        int[][] status = new int[emptyBoard.length][emptyBoard.length];

        for(JsonElement r : serverBoard){
            for(JsonElement c: r.getAsJsonArray()){
                String element = c.getAsString();
                switch (element){
                    case "EMPTY":
                        status[row][col] = E;
                        break;
                    case "BLACK":
                        if(emptyBoard[row][col] == CA){
                            status[row][col] = BA;
                        }else if(emptyBoard[row][col] == CB){
                            status[row][col] = BB;
                        }else status[row][col] = B;
                        break;
                    case "WHITE":
                        status[row][col] = W;
                        break;
                    case "KING":
                        status[row][col] = K;
                        break;
                }
                col ++;
            }
            col = 0;
            row ++;
        }
        return status;
    }

    public static PlayerType typeConverter(ServerPlayerType serverPlayerType){
        if (serverPlayerType.equalsTurn("W")) return PlayerType.WHITE;
        if (serverPlayerType.equalsTurn("B")) return PlayerType.BLACK;
        return null;
    }

    public static ServerPlayerType typeConverter(PlayerType playerType){
        if (playerType == PlayerType.WHITE) return ServerPlayerType.WHITE;
        if (playerType == PlayerType.BLACK) return ServerPlayerType.BLACK;
        return null;
    }
}
