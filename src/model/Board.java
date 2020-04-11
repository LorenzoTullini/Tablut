package model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private final int L = 1; // Liberation = escapes dei Cianchi
    private final int F = 2; // Fortress = castello
    private final int E = 0; // Empty = celle vuote
    private final int CW = 3; // campi bianchi
    private final int CA = -2; // campi neri parte nord / est
    private final int CB =-3; // campi neri parte sud / ovest
    private int board[][];

    public Board() {
        this.board = new int[][]{
                {E, L, L, CA, CA, CA, L, L, E},
                {L, E, E, E, CA, E, E, E, L},
                {L, E, E, E, CW, E, E, E, L},
                {CB, E, E, E, CW, E, E, E, CA},
                {CB, CB, CW, CW, F, CW, CW, CA, CA},
                {CB, E, E, E, CW, E, E, E, CA},
                {L, E, E, E, CW, E, E, E, L},
                {L, E, E, E, CB, E, E, E, L},
                {E, L, L, CB, CB, CB, L, L, E},

        };
    }

    public int[][] getBoard(){
        return this.board;
    }

    public List<Coord> getLCoord(){
        List<Coord> LCoord = new ArrayList<>();
        for(int i=0; i<9; i++){
            for(int j=0; j<9; j++){
                if(this.board[i][j] == L)
                    LCoord.add(new Coord(i,j));
            }
        }
        return LCoord;
    }
}
