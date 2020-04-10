package model;

public class Coord {

    public int x;
    public int y;

    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
       return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int manhattanDistance(Coord c) {
        return Math.abs(this.x - c.x) + Math.abs(this.y - c.y);
    }

    @Override
    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }

}
