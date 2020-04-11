package model;

import java.util.*;

public class TableState implements Cloneable {

    private final int W = 0;
    private final int B = 1; // pedone nero che lascia il suo campo
    private final int BA = 2; //pedone nero che è ancora nel campo nord / est
    private final int BB = 3; //pedone nero che è ancora nel campo sud / ovest
    private final int E = 4;
    private final int K = 5;
    private final int L = 1; // Liberation = escapes dei Cianchi
    private final int F = 2; // Fortress = castello
    private final int CA = -2; // campi neri parte nord / est
    private final int CB = -3; // campi neri parte sud / ovest
    private final int CF = -4; //campi neri + castello


    private int state[][];
    private Board board;
    private boolean whiteWon;
    private boolean blackWon;

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
                {E, E, E, BB, BB, BB, E, E, E},

        };

        board = new Board();

    }

    public List<Move> getAllMovesFor(PlayerType player) {
        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if ((this.state[i][j] == W || this.state[i][j] == K) && player.toString().equals("WHITE"))
                    moves.addAll(getMovesFor(i, j, player));

                if (getPiece(this.state[i][j]) == B && player.toString().equals("BLACK"))
                    moves.addAll(getMovesFor(i, j, player));
            }
        }

        return moves;

    }

    private Collection<? extends Move> getMovesFor(int x, int y, PlayerType player) {
        List<Move> moves = new ArrayList<>();
        Coord i = new Coord(x, y);
        if (player.toString().equals("WHITE")) {
            //NORD
            int xN = x;
            xN--;
            while (xN > 0) {
                if (this.state[xN][y] != E || this.board.getBoard()[xN][y] == CA || this.board.getBoard()[xN][y] == CB || this.board.getBoard()[xN][y] == F)
                    break;
                else moves.add(new Move(i, new Coord(xN, y)));
                xN--;
            }

            //SUD
            int xS = x;
            xS++;
            while (xS < 9) {
                if (this.state[xS][y] != E || this.board.getBoard()[xS][y] == CA || this.board.getBoard()[xS][y] == CB || this.board.getBoard()[xS][y] == F)
                    break;
                else moves.add(new Move(i, new Coord(xS, y)));
                xS++;
            }
            //EST
            int yE = y;
            yE++;
            while (yE < 9) {
                if (this.state[x][yE] != E || this.board.getBoard()[x][yE] == CA || this.board.getBoard()[x][yE] == CB || this.board.getBoard()[x][yE] == F)
                    break;
                else moves.add(new Move(i, new Coord(x, yE)));
                yE++;
            }

            //OVEST
            int yO = y;
            yO--;
            while (yO > 0) {
                if (this.state[x][yO] != E || this.board.getBoard()[x][yO] == CA || this.board.getBoard()[x][yO] == CB || this.board.getBoard()[x][yO] == F)
                    break;
                else moves.add(new Move(i, new Coord(x, yO)));
                yO--;
            }

        } // end if white

        if (player.toString().equals("BLACK")) {
            //NORD
            int xN = x;
            xN--;
            while (xN > 0) {
                if (this.state[xN][y] != E || this.board.getBoard()[xN][y] == F || (this.state[x][y]) == B && (this.board.getBoard()[xN][y] == CA || this.board.getBoard()[xN][y] == CB)
                        || (this.state[x][y] == BA && this.board.getBoard()[xN][y] == CB) || (this.state[x][y] == BB && this.board.getBoard()[xN][y] == CA)
                )
                    break;
                else moves.add(new Move(i, new Coord(xN, y)));
                xN--;
            }

            //SUD
            int xS = x;
            xS++;
            while (xS < 9) {
                if (this.state[xS][y] != E || this.board.getBoard()[xS][y] == F || (this.state[x][y]) == B && (this.board.getBoard()[xS][y] == CA || this.board.getBoard()[xS][y] == CB)
                        || (this.state[x][y] == BA && this.board.getBoard()[xS][y] == CB) || (this.state[x][y] == BB && this.board.getBoard()[xS][y] == CA)
                )
                    break;
                else moves.add(new Move(i, new Coord(xS, y)));
                xS++;
            }
            //EST
            int yE = y;
            yE++;
            while (yE < 9) {
                if (this.state[x][yE] != E || this.board.getBoard()[x][yE] == F || (this.state[x][y]) == B && (this.board.getBoard()[x][yE] == CA || this.board.getBoard()[x][yE] == CB)
                        || (this.state[x][y] == BA && this.board.getBoard()[x][yE] == CB) || (this.state[x][y] == BB && this.board.getBoard()[x][yE] == CA)
                )
                    break;
                else moves.add(new Move(i, new Coord(x, yE)));
                yE++;
            }

            //OVEST
            int yO = y;
            yO--;
            while (yO > 0) {
                if (this.state[x][yO] != E || this.board.getBoard()[x][yO] == F || (this.state[x][y]) == B && (this.board.getBoard()[x][yO] == CA || this.board.getBoard()[x][yO] == CB)
                        || (this.state[x][y] == BA && this.board.getBoard()[x][yO] == CB) || (this.state[x][y] == BB && this.board.getBoard()[x][yO] == CA)
                )
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
        TableState newTS = null;
        try {
            newTS = (TableState) this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        //Se il pezzo nero lascia la sua parte non ci deve piu' rientrare.
        //Viene quindi trasformato in B, ovvero, in base alle regole definirte sopra, non potra' piuù andare nei campi CA e CB
        if ((piece == BA && this.board.getBoard()[f.getX()][f.getY()] != CA) || (piece == BB && this.board.getBoard()[f.getX()][f.getY()] != CB))
            piece = B;
        newTS.state[i.getX()][i.getY()] = E;
        newTS.state[f.getX()][f.getY()] = piece;

        // bianco ha vinto?
        if (newTS.state[f.getX()][f.getY()] == K && newTS.board.getBoard()[f.getX()][f.getY()] == L) {
            whiteWon = true;
            System.out.println("Re salvo alle coordintate: " + f.toString());
        }


        // Mappa con chiave i vicini e valore il vicino del vicino in direzione nord/est/ovest/sud
        HashMap<Coord, Coord> nMap = getNeighbours(f, newTS);

        // controllo se bianco mangia qualcosa
        if (piece == W) {
            for (Map.Entry<Coord, Coord> e : nMap.entrySet()) {
                // se il pezzo è gia nel bordo oppure è affiancato da un altro pezzo di colore diverso che è nel bordo, non puo' essere mangiato in quella direzione
                if (e.getKey().getX() != -1 && e.getValue().getX() != -1)
                    if (newTS.state[e.getKey().getX()][e.getKey().getY()] == B && newTS.state[e.getValue().getX()][e.getValue().getY()] == W) {
                        //pezzo nero mangiato
                        newTS.state[e.getKey().getX()][e.getKey().getY()] = E;
                        System.out.println("Ho mangiato il pezzo nero che era alle coordintate: " + e.getKey().toString());
                    }
            }
        }

        // controllo se nero mangia qualcosa
        if (getPiece(piece) == B) {
            for (Map.Entry<Coord, Coord> e : nMap.entrySet()) {
                // se il pezzo è gia nel bordo oppure è affiancato da un altro pezzo di colore diverso che è nel bordo, non puo' essere mangiato in quella direzione
                if (e.getKey().getX() != -1 && e.getValue().getX() != -1) {
                    if (newTS.state[e.getKey().getX()][e.getKey().getY()] == W && (getPiece(newTS.state[e.getValue().getX()][e.getValue().getY()]) == B
                            || getCampsAndFortress(newTS.board.getBoard()[e.getValue().getX()][e.getValue().getY()]) == CF)) {
                        //pezzo bianco mangiato
                        newTS.state[e.getKey().getX()][e.getKey().getY()] = E;
                        System.out.println("Ho mangiato il pezzo bianco che era alle coordintate: " + e.getKey().toString());
                    }
                }
            }
        }

        if (newTS.hasWhiteWon() == false) {
            // controllo se nero vince
            Coord kC = getKingCoord();
            Set<Coord> nK = getNeighbours(kC, newTS).keySet();

            // re nel castello circondato su 4 lati
            if (this.board.getBoard()[kC.getX()][kC.getY()] == F) {
                int totB = 0;
                for (Coord c : nK) {
                    if (c.getX() != -1 && getPiece(newTS.state[c.getX()][c.getY()]) == B)
                        totB++;
                }
                if (totB == 4) {
                    newTS.blackWon = true;
                    System.out.println("Ho circondato il re sui 4 lati del castello");
                }
            }

            // re adiacente al castello e circondato su 3 lati
            if (newTS.blackWon == false && this.board.getBoard()[kC.getX()][kC.getY()] != F) {
                int totB = 0;
                boolean adjacent = false;

                for (Coord c : nK) {
                    if (c.getX() != -1 && newTS.state[c.getX()][c.getY()] == B)
                        totB++;
                    if (c.getX() != -1 && newTS.board.getBoard()[c.getX()][c.getY()] == F)
                        adjacent = true;
                }

                if (totB == 3 && adjacent == true) {
                    newTS.blackWon = true;
                    System.out.println("Ho circondato il re sui 3 lati + 1 adiacente al castello alle coordinate: " + kC.toString());
                }

            }

            // re catturato come una pedina normale se non adiacente al castello
            if (newTS.blackWon == false && this.board.getBoard()[kC.getX()][kC.getY()] != F) {
                boolean adjacent = false;
                for (Coord c : nK) {
                    if (c.getX() != -1 && newTS.board.getBoard()[c.getX()][c.getY()] == F)
                        adjacent = true;
                }

                if (adjacent == false) {
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
            for (int j = 0; j < 9; j++)
                if (this.state[i][j] == K)
                    return new Coord(i, j);
        return new Coord(-1, -1);
    }

    private HashMap<Coord, Coord> getNeighbours(Coord f, TableState newTS) {
        HashMap<Coord, Coord> nMap = new HashMap<>();
        Coord key;
        Coord value;
        Coord limit = new Coord(-1, -1);

        //nord
        if (f.getX() - 1 >= 0) key = f.goNord();
        else key = limit;

        if (f.getX() - 2 >= 0) value = key.goNord();
        else value = limit;

        nMap.put(key, value);


        //est
        if (f.getY() + 1 <= 8) key = f.goEst();
        else key = limit;

        if (f.getY() + 2 <= 8) value = key.goEst();
        else value = limit;

        nMap.put(key, value);

        //ovest
        if (f.getY() - 1 >= 0) key = f.goOvest();
        else key = limit;

        if (f.getY() - 2 >= 0) value = key.goOvest();
        else value = limit;

        nMap.put(key, value);


        //sud
        if (f.getX() + 1 <= 8) key = f.goSud();
        else key = limit;

        if (f.getX() + 2 <= 8) value = key.goSud();
        else value = limit;

        nMap.put(key, value);

        return nMap;

    }

    public int getWhitePiecesCount() {
        int totW = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; i < 9; j++)
                if (this.state[i][j] == W)
                    totW++;
        }
        //aggiungo il re
        return totW++;
    }

    public int getBlackPiecesCount() {
        int totB = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; i < 9; j++)
                if (this.state[i][j] == B)
                    totB++;
        }
        return totB;
    }

    public PlayerType getPieceAtCoord(Coord c) {
        if (this.state[c.getX()][c.getY()] == W)
            return PlayerType.WHITE;
        if (this.state[c.getX()][c.getY()] == B)
            return PlayerType.BLACK;
        if (this.state[c.getX()][c.getY()] == K)
            return PlayerType.KING;

        return PlayerType.EMPTY;
    }


    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }


    public String toString() {
        return Arrays.deepToString(this.state).replace("], ", "]\n").replace("[[", "[").replace("]]", "]").
                replace("0", "W").replace("1", "B").replace("2", "BA").replace("3", "BB").
                replace("4", "E").replace("5", "K");
    }

    public boolean hasBlackWon() {
        return blackWon;
    }

    public boolean hasWhiteWon() {
        return whiteWon;
    }

    public int getKingDistance() {
        Coord kC = this.getKingCoord();
        int d = 10000;
        for (Coord c : this.board.getLCoord())
            if (kC.manhattanDistance(c) <= d)
                d = kC.manhattanDistance(c);
        return d;
    }


}
