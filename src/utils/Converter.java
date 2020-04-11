package utils;

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

    //TODO: TableState converter

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
}
