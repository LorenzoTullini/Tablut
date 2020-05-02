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

    /*DEBUG
    public void printStatus() {
        for (int row = 0; row < status.length; row++) {
            for (int col = 0; col < status[row].length; col++) {
                System.out.printf("%4d", status[row][col]);
            }
            System.out.println();
        }
    }*/

    public void printStatus() {
        int i, j, cell;
        char c = 'a';
        System.out.printf("\n\n");
        /*for (i = 0; i < 9; i++) {
            System.out.printf("%c    ", c);
            c++;
        }
        System.out.printf("\n      ");
        for (i = 0; i < 9; i++) {
            System.out.printf("----");
        }
        System.out.printf("\n");*/

        for (i = 0; i < 9; i++) {
            for (j = 0; j < 9; j++) {
                if (j == 0)
                    System.out.printf("%d  |  ", i+1);

                switch(status[i][j]) {
                    case 4: System.out.printf("▹\u2001\u2001"); break;
                    case 1: System.out.printf("○\u2001\u2001"); break;
                    case 2: System.out.printf("○\u2001\u2001"); break;
                    case 3: System.out.printf("○\u2001\u2001"); break;
                    case 5: System.out.printf("◉\u2001\u2001"); break;
                    case 0: System.out.printf("●\u2001\u2001"); break;
                    default: System.out.printf("▹\u2001\u2001"); break;
                }

                if (j == 8)
                    System.out.printf("|  %d", i+1);
            }
            System.out.printf("\n");
            if (i < 8)
                System.out.printf("\n");
        }

        /*System.out.printf("      ");
        for (i = 0; i < 9; i++) {
            System.out.printf("----");
        }
        System.out.printf("\n      ");
        c = 'a';
        for (i = 0; i < 9; i++) {
            System.out.printf(" %c  ", c);
            c++;
        }*/
        System.out.printf("\n");
        System.out.printf("\n\n");
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

    public boolean isDraw() {
        if(turn.equals("DRAW")) return true;
        return false;
    }

    public TableState getTableState(){
        TableState tableState= new TableState(status);
        return tableState;
    }
}

