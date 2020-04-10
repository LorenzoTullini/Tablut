package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TableState {

   private final int W = 1;
   private final int B = -1;
   private final int E = 0;
   private final int K = 2;
    private final int L = 1; // Liberation = escapes dei Cianchi
    private final int C = -1; // Camps = campi dei neri
    private final int F = 2; // Fortress = castello
   private int state[][];
   private Board board;

   public TableState (){
       this.state = new int[][]{
               {E, E, E, B, B, B, E, E, E},
               {E, E, E, E, B, E, E, E, E},
               {E, E, E, E, W, E, E, E, E},
               {B, E, E, E, W, E, E, E, B},
               {B, B, W, W, K, W, W, B, B},
               {B, E, E, E, W, E, E, E, B},
               {E, E, E, E, W, E, E, E, E},
               {E, E, E, E, B, E, E, E, E},
               {E, E, E, B, B, B, E, E, E},

       };

       board = new Board();
       
   }


    public List<Move> getAllMovesFor(PlayerType player) {
       List<Move> moves = new ArrayList<>();
       for(int i=0; i<9; i++){
           for(int j=0; j<9; j++) {
               if (this.state[i][j] == W || this.state[i][j] == K && player.equals("WHITE"))
                   moves.addAll(getMovesFor(i, j, player));
               if (this.state[i][j] == B && player.equals("BLACK"))
                   moves.addAll(getMovesFor(i, j, player));
           }
       }

       return moves;

    }

    private Collection<? extends Move> getMovesFor(int x, int y, PlayerType player) {
       List<Move> moves = new ArrayList<>();
       if(player.equals("WHITE")){
           //NORD
           for(int i=0; x+i>9; i++){
               x--;
               //if(this.state[x][y]==B || this.board.getBoard()[x][y]==C)

           }
           //EST
           //OVEST
           //SUD
       }
       return null;
    }

    public int[][] performMove(Move m) {
       Coord i = m.getFrom();
       Coord f = m.getTo();
       int piece = this.state[i.getX()][i.getY()];
       this.state[i.getX()][i.getY()] = E;
       this.state[f.getX()][f.getY()] = piece;
       return state;
    }

    public int getWhitePiecesCount(){
       return 0;
    }

    public int getBlackPiecesCount() {
        return 0;
    }

    public PlayerType getPieceAtCoord(Coord c){
       if(this.state[c.getX()][c.getY()]==W)
           return PlayerType.WHITE;
        if(this.state[c.getX()][c.getY()]==B)
            return PlayerType.BLACK;
        if(this.state[c.getX()][c.getY()]==K)
            return PlayerType.KING;

        return PlayerType.EMPTY;
    }

}
