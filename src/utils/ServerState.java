package utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.PlayerType;
import model.TableState;

public class ServerState {
    private String[][] serverStatus;
    private int[][] status;
    private String turn;


    public ServerState(String json){
        JsonObject jsObject = new JsonParser().parse(json).getAsJsonObject();
        turn = jsObject.get("turn").getAsString();
        status = Converter.convertServerState(json);
    }

    public int[][] getStatus() {
        return status;
    }

    //DEBUG
    public void printStatus() {
        for (int row = 0; row < status.length; row++) {
            for (int col = 0; col < status[row].length; col++) {
                System.out.printf("%4d", status[row][col]);
            }
            System.out.println();
        }
    }

    public boolean isMyTurn(PlayerType type) {
        if (type == PlayerType.BLACK && turn.equals("BLACK")) return true;
        if (type == PlayerType.WHITE && turn.equals("WHITE")) return true;
        return false;
    }

    public boolean haveIWin(PlayerType type){
        if (type == PlayerType.WHITE && turn.equals("WHITEWIN")) return true;
        if (type == PlayerType.BLACK && turn.equals("BLACKWIN")) return true;
        return false;
    }

    public boolean haveILost(PlayerType type){
        if (type == PlayerType.BLACK && turn.equals("WHITEWIN")) return true;
        if (type == PlayerType.WHITE && turn.equals("BLACKWIN")) return true;
        return false;
    }

    public TableState getTableState(){
        TableState tableState= new TableState(status, 0, 0);
        return tableState;
    }
}

