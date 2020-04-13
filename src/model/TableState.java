package model;

import java.util.*;

public class TableState implements Cloneable {

    // state
    private final int W = 0;    // pedone bianco
    private final int B = 1;    // pedone nero che lascia il suo campo
    private final int BA = 2;   // pedone nero che è ancora nel campo nord / est
    private final int BB = 3;   // pedone nero che è ancora nel campo sud / ovest
    private final int E = 4;    // cella vuota
    private final int K = 5;    // re

    // board
    private final int L = 0;    // Liberation = escapes dei Cianchi
    private final int F = 1;    // Fortress = castello
    private final int CA = 3;   // campi neri parte nord / est
    private final int CB = 5;   // campi neri parte sud / ovest
    private final int CF = 6;   // campi neri + castello


    private int state[][];
    private Board board = new Board();
    private Utils utils = new Utils();
    private boolean whiteWon;
    private boolean blackWon;
    private int whitePiecesEaten;
    private int blackPiecesEaten;

    public TableState() {
        this.state = new int[][]{
                {E, E, E, BA, BA, BA, E, E, E},
                {E, E, E, E, BA, E, E, E, E},
                {E, E, E, E, W, E, E, E, E},
                {BB, E, E, E, W, E, E, E, BA},
                {BB, BB, W, W, K, W, W, BA, BA},
                {BB, E, E, E, W, E, E, E, BA},
                {E, E, E, E, W, E, E, E, E},
                {E, E, E, E, BB, E, E, E, E},
                {E, E, E, BB, BB, BB, E, E, E}

        };
        whitePiecesEaten = 0;
        blackPiecesEaten = 0;
    }

    public TableState(int[][] state, int whitePiecesEaten, int blackPiecesEaten) {
        this.state = state;
        this.whitePiecesEaten = whitePiecesEaten;
        this.blackPiecesEaten = blackPiecesEaten;

    }


    static public TableState getAClone (TableState ts){
        TableState newTS = new TableState();
        newTS.blackPiecesEaten = ts.getBlackPiecesEaten();
        newTS.whitePiecesEaten = ts.getWhitePiecesEaten();

        for(int i =0; i <9; i++)
            for (int j = 0 ; j<9; j++)
                newTS.getState()[i][j]=ts.getState()[i][j];

        return newTS;
    }


    public int[][] getBoard() {
        return this.board.getBoard();
    }

    public int getWhitePiecesEaten(){
        return this.whitePiecesEaten;
    }

    public int getBlackPiecesEaten(){
        return this.blackPiecesEaten;
    }

    public boolean hasBlackWon() {
        return this.blackWon;
    }

    public boolean hasWhiteWon() {
        return this.whiteWon;
    }

    public int[][] getState() {
        return this.state;
    }

    public List<Move> getAllMovesFor(PlayerType player) {
        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if ( ((this.getState()[i][j] == W || this.getState()[i][j] == K) && player.equals(PlayerType.WHITE))
                        || (getPiece(this.getState()[i][j]) == B && player.equals(PlayerType.BLACK)) )
                    moves.addAll(getMovesFor(i, j, player));
            }
        }
        return moves;
    }

    private Collection<? extends Move> getMovesFor(int x, int y, PlayerType player) {
        List<Move> moves = new ArrayList<>();
        Coord i = new Coord(x, y); // coordinata iniziale
        if (player.equals(PlayerType.WHITE)) {

            //NORD
            int xN = x;
            xN--;
            while (xN > 0) {
                // se il campo non e' vuoto oppure contiene un campo/castello allora non puo' piu' essere oltrepassato in quella direzione
                if (this.state[xN][y] != E || this.getCampsAndFortress(this.getBoard()[xN][y]) == CF)
                    break;
                else moves.add(new Move(i, new Coord(xN, y)));
                xN--;
            }

            //SUD
            int xS = x;
            xS++;
            while (xS < 9) {
                if (this.state[xS][y] != E || this.getCampsAndFortress(this.getBoard()[xS][y]) == CF)
                    break;
                else moves.add(new Move(i, new Coord(xS, y)));
                xS++;
            }

            //EST
            int yE = y;
            yE++;
            while (yE < 9) {
                if (this.state[x][yE] != E || this.getCampsAndFortress(this.getBoard()[x][yE]) == CF)
                    break;
                else moves.add(new Move(i, new Coord(x, yE)));
                yE++;
            }

            //OVEST
            int yO = y;
            yO--;
            while (yO > 0) {
                if (this.state[x][yO] != E || this.getCampsAndFortress(this.getBoard()[x][yO]) == CF)
                    break;
                else moves.add(new Move(i, new Coord(x, yO)));
                yO--;
            }

        } // end if white

        if (player.equals(PlayerType.BLACK)) {
            //NORD
            int xN = x;
            xN--;
            while (xN > 0) {
                if (this.state[xN][y] != E || this.getBoard()[xN][y] == F || (this.state[x][y] == B && (this.getBoard()[xN][y] == CA || this.getBoard()[xN][y] == CB))
                        || (this.state[x][y] == BA && this.getBoard()[xN][y] == CB) || (this.state[x][y] == BB && this.getBoard()[xN][y] == CA))
                    break;
                else moves.add(new Move(i, new Coord(xN, y)));
                xN--;
            }

            //SUD
            int xS = x;
            xS++;
            while (xS < 9) {
                if (this.state[xS][y] != E || this.getBoard()[xS][y] == F || (this.state[x][y] == B && (this.getBoard()[xS][y] == CA || this.getBoard()[xS][y] == CB))
                        || (this.state[x][y] == BA && this.getBoard()[xS][y] == CB) || (this.state[x][y] == BB && this.getBoard()[xS][y] == CA))
                    break;
                else moves.add(new Move(i, new Coord(xS, y)));
                xS++;
            }
            //EST
            int yE = y;
            yE++;
            while (yE < 9) {
                if (this.state[x][yE] != E || this.getBoard()[x][yE] == F || (this.state[x][y] == B && (this.getBoard()[x][yE] == CA || this.getBoard()[x][yE] == CB))
                        || (this.state[x][y] == BA && this.getBoard()[x][yE] == CB) || (this.state[x][y] == BB && this.getBoard()[x][yE] == CA))
                    break;
                else moves.add(new Move(i, new Coord(x, yE)));
                yE++;
            }

            //OVEST
            int yO = y;
            yO--;
            while (yO > 0) {
                if (this.state[x][yO] != E || this.getBoard()[x][yO] == F || (this.state[x][y] == B && (this.getBoard()[x][yO] == CA || this.getBoard()[x][yO] == CB))
                        || (this.state[x][y] == BA && this.getBoard()[x][yO] == CB) || (this.state[x][y] == BB && this.getBoard()[x][yO] == CA))
                    break;
                else moves.add(new Move(i, new Coord(x, yO)));
                yO--;
            }

        } // end if black

        return moves;
    }


    public TableState performMove(Move m) {
        Coord i = m.getFrom();
        Coord f = m.getTo();
        int piece = this.state[i.getX()][i.getY()];

        TableState newTS = getAClone(this);

        /*try {
            newTS = (TableState) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }*/

        //newTS.state = state.clone();

        //Se il pezzo nero lascia la sua parte non ci deve piu' rientrare.
        //Viene quindi trasformato in B, ovvero, in base alle regole definite sopra, non potra' piu' andare nei campi CA e CB
        if ((piece == BA && this.getBoard()[f.getX()][f.getY()] != CA) || (piece == BB && this.getBoard()[f.getX()][f.getY()] != CB))
            piece = B;

        newTS.state[i.getX()][i.getY()] = E;
        newTS.state[f.getX()][f.getY()] = piece;


        // bianco ha vinto?
        if (piece == K && newTS.getBoard()[f.getX()][f.getY()] == L) {
            whiteWon = true;
            System.out.println("Re salvo alle coordinate: " + f.toString());
        }


        // Mappa con chiave i vicini e valore il primo vicino del vicino in direzione nord/est/ovest/sud
        HashMap<Coord, Coord> nMap = utils.getNeighbours(f);

        // controllo se bianco mangia qualcosa
        if (piece == W) {
            for (Map.Entry<Coord, Coord> e : nMap.entrySet()) {
                // se il pezzo bianco è gia nel bordo oppure è affiancato da un altro pezzo di colore diverso che è nel bordo, non puo' essere mangiato in quella direzione
                if (e.getKey().getX() != -1 && e.getValue().getX() != -1) {
                    if (newTS.state[e.getKey().getX()][e.getKey().getY()] == B && newTS.state[e.getValue().getX()][e.getValue().getY()] == W) {
                        //pezzo nero mangiato
                        newTS.state[e.getKey().getX()][e.getKey().getY()] = E;
                        System.out.println("Ho mangiato il pezzo nero che era alle coordinate: " + e.getKey().toString());
                        newTS.blackPiecesEaten++;
                    }
                }
            }
        }

        // controllo se nero mangia qualcosa
        if (getPiece(piece) == B) {
            for (Map.Entry<Coord, Coord> e : nMap.entrySet()) {
                // se il pezzo nero è gia nel bordo oppure è affiancato da un pezzo bianco che è nel bordo, non puo' essere mangiato in quella direzione
                if (e.getKey().getX() != -1 && e.getValue().getX() != -1) {
                    // per ogni direzione verifico: B-W-B || B-W-CA/CB/F
                    if (newTS.state[e.getKey().getX()][e.getKey().getY()] == W && (getPiece(newTS.state[e.getValue().getX()][e.getValue().getY()]) == B
                            || getCampsAndFortress(newTS.getBoard()[e.getValue().getX()][e.getValue().getY()]) == CF)) {
                        //pezzo bianco mangiato
                        newTS.state[e.getKey().getX()][e.getKey().getY()] = E;
                        System.out.println("Ho mangiato il pezzo bianco che era alle coordinate: " + e.getKey().toString());
                        newTS.whitePiecesEaten++;
                    }
                }
            }
        }

        // controllo se nero vince
        if (!newTS.hasWhiteWon()) {
            Coord kC = newTS.getKingCoord();
            //System.out.println(kC.toString());
            // mi bastano gli immediati vicini del re, ovvero il set di chiavi
            Set<Coord> nK = utils.getNeighbours(kC).keySet();

            // re nel castello circondato su 4 lati
            if (this.getBoard()[kC.getX()][kC.getY()] == F) {
                int totB = 0;
                for (Coord c : nK) {
                    if (getPiece(newTS.state[c.getX()][c.getY()]) == B)
                        totB++;
                }
                if (totB == 4) {
                    newTS.blackWon = true;
                    System.out.println("Ho circondato il re sui 4 lati del castello");
                }
            }

            // re adiacente al castello e circondato su 3 lati
            if (!newTS.hasBlackWon() && !newTS.hasWhiteWon() && this.getBoard()[kC.getX()][kC.getY()] != F) {
                int totB = 0;
                boolean adjacent = false;

                for (Coord c : nK) {
                    if (c.getX() != -1 && newTS.state[c.getX()][c.getY()] == B)
                        totB++;
                    if (c.getX() != -1 && newTS.getBoard()[c.getX()][c.getY()] == F)
                        adjacent = true;
                }

                if (totB == 3 && adjacent) {
                    newTS.blackWon = true;
                    System.out.println("Ho circondato il re sui 3 lati + 1 adiacente al castello alle coordinate: " + kC.toString());
                }

            }

            // re catturato come una pedina normale se non adiacente al castello
            if (!newTS.blackWon && !newTS.hasWhiteWon() && this.getBoard()[kC.getX()][kC.getY()] != F) {
                boolean adjacent = false;
                for (Coord c : nK) {
                    if (c.getX() != -1 && newTS.getBoard()[c.getX()][c.getY()] == F)
                        adjacent = true;
                }

                if (!adjacent) {
                    Coord nord = kC.goNord();
                    Coord est = kC.goEst();
                    Coord ovest = kC.goOvest();
                    Coord sud = kC.goSud();
                    if (nord.getX() != -1 && sud.getX() != 9 && getPiece(newTS.state[nord.getX()][nord.getY()]) == B && getPiece(newTS.state[sud.getX()][sud.getY()]) == B) {
                        newTS.blackWon = true;
                        System.out.println("Ho circondato il re su 2 lati alle coordinate: " + kC.toString());
                    }

                    if (est.getY() != 9 && ovest.getY() != -1 && getPiece(newTS.state[est.getX()][est.getY()]) == B && getPiece(newTS.state[ovest.getX()][ovest.getY()]) == B) {
                        newTS.blackWon = true;
                        System.out.println("Ho circondato il re su 2 lati alle coordinate: " + kC.toString());
                    }
                }
            }

        }

        return newTS;

    }

    private int getCampsAndFortress(int i) {
        if (i == CA || i == CB || i == F)
            return CF;
        else return E;
    }


    private int getPiece(int piece) {
        if (piece == BA || piece == BB || piece == B)
            return B;
        else return W;
    }

    private Coord getKingCoord() {
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++) {
                if (this.state[i][j] == K)
                    return new Coord(i, j);
            }

        System.out.println(this.toString());
        return new Coord(-1, -1);
    }


    public int getWhitePiecesCount() {
        int totW = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++)
                if (this.state[i][j] == W)
                    totW++;
        }
        //aggiungo il re
        return totW++;
    }

    public int getBlackPiecesCount() {
        int totB = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++)
                if (this.state[i][j] == B)
                    totB++;
        }
        return totB;
    }

    public PlayerType getPieceAtCoord(Coord c) {
        if (this.state[c.getX()][c.getY()] == W )
            return PlayerType.WHITE;
        if (this.state[c.getX()][c.getY()] == B)
            return PlayerType.BLACK;
        if (this.state[c.getX()][c.getY()] == K)
            return PlayerType.KING;

        return PlayerType.EMPTY;
    }


    public Object clone() throws CloneNotSupportedException {
        TableState tableState = new TableState(this.state.clone(), this.whitePiecesEaten, this.blackPiecesEaten);
        if (!tableState.equals(this)) System.out.println("Ocio è diverso !!!!!!!!!!!!");
        return tableState;
    }


    public boolean equals(Object tableState){
        TableState myTableState = (TableState) tableState;
        return Arrays.deepEquals(myTableState.state, this.state);
    }


    public String toString() {
        return Arrays.deepToString(this.state).replace("], ", "]\n").replace("[[", "[").replace("]]", "]").
                replace("0", "W").replace("1", "B").replace("2", "BA").replace("3", "BB").
                replace("4", "E").replace("5", "K");
    }




    public int getKingDistance() {
        Coord kC = this.getKingCoord();
        int d = 1000;
        for (Coord c : this.board.getLCoord())
            if (kC.manhattanDistance(c) <= d)
                d = kC.manhattanDistance(c);
        return d;
    }

    public List <Move> aggressiveGetAllMovesFor(PlayerType player, TableState ts) {

        TableState newTS = getAClone(ts);

        List<Move> moves = newTS.getAllMovesFor(player);
        List<Move> movesInOrder = new ArrayList<>();

        if(player.equals(PlayerType.WHITE)) {
            for (Move m : moves) {
                m.setPrio(newTS.performMove(m).getWhitePiecesEaten());
                /*System.out.println();
                System.out.println(newTS.toString());
                System.out.println();*/
                movesInOrder.add(m);
            }
        }

        if(player.equals(PlayerType.BLACK)) {
            for (Move m : moves) {
                m.setPrio(this.performMove(m).getBlackPiecesEaten());
                movesInOrder.add(m);
            }
        }

        movesInOrder.sort(Comparator.comparing(Move::getPrio).reversed());
        return movesInOrder;

    }


}
