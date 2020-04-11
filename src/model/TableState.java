package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class TableState {

   private final int W = 0;
   private final int B = 1; // pedone nero che lascia il suo campo
    private final int BA = 2; //pedone nero che è ancora nel campo nord / est
    private final int BB = 3; //pedone nero che è ancora nel campo sud / ovest
   private final int E = 4;
   private final int K = 5;
    private final int L = 1; // Liberation = escapes dei Cianchi
    private final int F = 2; // Fortress = castello
    private final int CA = -2; // campi neri parte nord / est
    private final int CB =-3; // campi neri parte sud / ovest


   private int state[][];
   private Board board;

   public TableState (){
       this.state = new int[][]{
               {E, E, E, BA, BA, BA, E, E, E},
               {E, E, E, E, BA, E, E, E, E},
               {E, E, E, E, W, E, E, E, E},
               {BB, E, E, E, W, E, E, E, BA},
               {BB, BB, W, W, K, W, W, BA, BA},
               {BB, E, E, E, W, E, E, E, BA},
               {E, E, E, E, W, E, E, E, E},
               {E, E, E, E, BB, E, E, E, E},
               {E, E, E, BB, BB, BB, E, E, E},

       };

       board = new Board();
       
   }


    public List<Move> getAllMovesFor(PlayerType player) {
       List<Move> moves = new ArrayList<>();
       for(int i=0; i<9; i++){
           for(int j=0; j<9; j++) {
               if ((this.state[i][j] == W || this.state[i][j] == K) && player.toString().equals("WHITE"))
                   moves.addAll(getMovesFor(i, j, player));

               if ((this.state[i][j] == B || this.state[i][j] == BA || this.state[i][j] == BB) && player.toString().equals("BLACK"))
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
               if(this.state[xN][y]!=E || this.board.getBoard()[xN][y]==CA || this.board.getBoard()[xN][y]==CB || this.board.getBoard()[xN][y]==F)
                   break;
               else moves.add(new Move(i, new Coord(xN,y)));
               xN--;
           }

           //SUD
           int xS=x;
           xS++;
           while(xS<9){
               if(this.state[xS][y]!=E || this.board.getBoard()[xS][y]==CA || this.board.getBoard()[xS][y]==CB || this.board.getBoard()[xS][y]==F)
                   break;
               else moves.add(new Move(i, new Coord(xS,y)));
               xS++;
           }
           //EST
           int yE=y;
           yE++;
           while(yE<9){
               if(this.state[x][yE]!=E || this.board.getBoard()[x][yE]==CA || this.board.getBoard()[x][yE]==CB ||this.board.getBoard()[x][yE]==F)
                   break;
               else moves.add(new Move(i, new Coord(x,yE)));
               yE++;
           }

           //OVEST
           int yO=y;
           yO--;
           while(yO>0){
               if(this.state[x][yO]!=E || this.board.getBoard()[x][yO]==CA || this.board.getBoard()[x][yO]==CB || this.board.getBoard()[x][yO]==F)
                   break;
               else moves.add(new Move(i, new Coord(x,yO)));
               yO--;
           }

       } // end if white

        if(player.toString().equals("BLACK")){
            //NORD
            int xN=x;
            xN--;
            while(xN>0){
                if(this.state[xN][y]!=E || this.board.getBoard()[xN][y]==F || (this.state[x][y])==B && (this.board.getBoard()[xN][y]==CA || this.board.getBoard()[xN][y]==CB)
                 || (this.state[x][y]==BA && this.board.getBoard()[xN][y]==CB) || (this.state[x][y]==BB && this.board.getBoard()[xN][y]==CA)
                )
                    break;
                else moves.add(new Move(i, new Coord(xN,y)));
                xN--;
            }

            //SUD
            int xS=x;
            xS++;
            while(xS<9){
                if(this.state[xS][y]!=E || this.board.getBoard()[xS][y]==F || (this.state[x][y])==B && (this.board.getBoard()[xS][y]==CA || this.board.getBoard()[xS][y]==CB)
                 || (this.state[x][y]==BA && this.board.getBoard()[xS][y]==CB) || (this.state[x][y]==BB && this.board.getBoard()[xS][y]==CA)
                )
                    break;
                else moves.add(new Move(i, new Coord(xS,y)));
                xS++;
            }
            //EST
            int yE=y;
            yE++;
            while(yE<9){
                if(this.state[x][yE]!=E || this.board.getBoard()[x][yE]==F || (this.state[x][y])==B && (this.board.getBoard()[x][yE]==CA || this.board.getBoard()[x][yE]==CB)
                 || (this.state[x][y]==BA && this.board.getBoard()[x][yE]==CB) || (this.state[x][y]==BB && this.board.getBoard()[x][yE]==CA)
                )
                    break;
                else moves.add(new Move(i, new Coord(x,yE)));
                yE++;
            }

            //OVEST
            int yO=y;
            yO--;
            while(yO>0){
                if(this.state[x][yO]!=E || this.board.getBoard()[x][yO]==F || (this.state[x][y])==B && (this.board.getBoard()[x][yO]==CA || this.board.getBoard()[x][yO]==CB)
                 || (this.state[x][y]==BA && this.board.getBoard()[x][yO]==CB) || (this.state[x][y]==BB && this.board.getBoard()[x][yO]==CA)
                )
                    break;
                else moves.add(new Move(i, new Coord(x,yO)));
                yO--;
            }

    } // end if black


       return moves;
    }

    /*public Move performMove(Move m) {
       Coord i = m.getFrom();
       Coord f = m.getTo();
       int piece = this.state[i.getX()][i.getY()];
       //Se il pezzo nero lascia la sua parte non ci deve piu' rientrare.
       //Viene quindi trasformato in B, ovvero, in base alle regole definirte sopra, non potra' piuù andare nei campi CA  e CB
       if((piece==BA && this.board.getBoard()[f.getX()][f.getY()]!=CA) || (piece==BB && this.board.getBoard()[f.getX()][f.getY()]!=CB))
           piece=B;
       this.state[i.getX()][i.getY()] = E;
       this.state[f.getX()][f.getY()] = piece;
       return m;
    }*/

    public TableState performMove(Move m) {return null;}

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

    public String toString(){
        return Arrays.deepToString(this.state).replace("], ", "]\n").replace("[[", "[").replace("]]", "]").
                replace("0","W").replace("1","B").replace("2","BA").replace("3","BB").
                replace("4","E").replace("5","K");
    }

}
