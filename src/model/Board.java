package model;

public class Board {
    private final int L = 1; // Liberation = escapes dei Cianchi
    private final int C = -1; // Camps = campi dei neri
    private final int F = 2; // Fortress = castello
    private final int E = 0; // Empty = celle vuote
    private int board[][];

    public Board() {
        this.board = new int[][]{
                {E, L, L, C, C, C, L, L, E},
                {L, E, E, E, C, E, E, E, L},
                {L, E, E, E, E, E, E, E, L},
                {C, E, E, E, E, E, E, E, C},
                {C, C, E, E, F, E, E, C, C},
                {C, E, E, E, E, E, E, E, C},
                {L, E, E, E, E, E, E, E, L},
                {L, E, E, E, C, E, E, E, L},
                {E, L, L, C, C, C, L, L, E},

        };
    }

    public int[][] getBoard(){
        return this.board;
    }
}
