package model;

import java.util.HashMap;


public class Utils {

    public Utils(){

    }


    public int getCampsAndFortress(int i) {
        if (i == TableState.CA || i == TableState.CB || i == TableState.F)
            return TableState.CF;
        else return TableState.E;
    }


    public int getPiece(int piece) {
        if (piece == TableState.BA || piece == TableState.BB || piece == TableState.B)
            return TableState.B;
        else return TableState.W;
    }



    public HashMap<Coord, Coord> getNeighbours(Coord f) {
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


    // se il campo non e' vuoto oppure contiene un campo/castello allora non puo' piu' essere oltrepassato in quella direzione
    public boolean checkWhite(TableState ts, int cX, int cY){
        if (ts.getState()[cX][cY] != TableState.E || getCampsAndFortress(ts.getBoard()[cX][cY]) == TableState.CF)
            return false;
        else return true;

    }


    //cX e cY sono le coordinate del punto immediatamente vicino a quello di coordinate x,y, ovvero il punto di partenza
    public boolean checkBlack(TableState ts, int cX, int cY, int x, int y){
        if (ts.getState()[cX][cY] != TableState.E || ts.getBoard()[cX][cY] == TableState.F || (ts.getState()[x][y] == TableState.B && (ts.getBoard()[cX][cY] == TableState.CA || ts.getBoard()[cX][cY] == TableState.CB))
                || (ts.getState()[x][y] == TableState.BA && ts.getBoard()[cX][cY] == TableState.CB) || (ts.getState()[x][y] == TableState.BB && ts.getBoard()[cX][cY] == TableState.CA))
            return false;
        else return true;

    }


    public int getCamps(int i) {
        if (i == TableState.CA || i == TableState.CB)
            return TableState.C;
        else return TableState.E;
    }
}
