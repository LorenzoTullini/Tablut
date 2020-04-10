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
               if ((this.state[i][j] == W || this.state[i][j] == K) && player.toString().equals("WHITE"))
                   moves.addAll(getMovesFor(i, j, player));
               if (this.state[i][j] == B && player.equals("BLACK"))
                   moves.addAll(getMovesFor(i, j, player));
           }
       }

       return moves;

    }

    private Collection<? extends Move> getMovesFor(int x, int y, PlayerType player) {
       List<Move> moves = new ArrayList<>();
       Coord i = new Coord(x,y);
       if(player.toString().equals("WHITE")){
           //NORD
           int xN=x;
           xN--;
           while(xN>0){
               if(this.state[xN][y]!=E || this.board.getBoard()[xN][y]==C || this.board.getBoard()[xN][y]==F)
                   break;
               else moves.add(new Move(i, new Coord(xN,y)));
               xN--;
           }

           //SUD
           int xS=x;
           xS++;
           while(xS<9){
               if(this.state[xS][y]!=E || this.board.getBoard()[xS][y]==C || this.board.getBoard()[xS][y]==F)
                   break;
               else moves.add(new Move(i, new Coord(xS,y)));
               xS++;
           }

           //EST
           int yE=y;
           yE++;
           while(yE<9){
               if(this.state[x][yE]!=E || this.board.getBoard()[x][yE]==C || this.board.getBoard()[x][yE]==F)
                   break;
               else moves.add(new Move(i, new Coord(x,yE)));
               yE++;
           }

           //OVEST
           int yO=y;
           yO--;
           while(yO>0){
               if(this.state[x][yO]!=E || this.board.getBoard()[x][yO]==C || this.board.getBoard()[x][yO]==F)
                   break;
               else moves.add(new Move(i, new Coord(x,yO)));
               yO--;
           }

       } // end if white

       return moves;
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
       int totW=0;
       for(int i=0; i<9; i++){
           for(int j=0; i<9; j++)
               if(this.state[i][j]==W)
                   totW++;
       }
       //aggiungo il re
       return totW++;
    }

    public int getBlackPiecesCount() {
        int totB=0;
        for(int i=0; i<9; i++){
            for(int j=0; i<9; j++)
                if(this.state[i][j]==B)
                    totB++;
        }
        return totB;
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
