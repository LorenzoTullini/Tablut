package model;

import java.util.HashMap;

public class Utils {

    public Utils(){

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

}
