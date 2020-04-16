package model;

public class Coord {

    private int x, y;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coord c = (Coord) o;
        return x == c.x && y == c.y;
    }

    @Override
    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }

    public Coord goNord() {
        return new Coord(this.getX() - 1, this.getY());
    }

    public Coord goEst() {
        return new Coord(this.getX(), this.getY() + 1);
    }

    public Coord goOvest() {
        return new Coord(this.getX(), this.getY() - 1);
    }

    public Coord goSud() {
        return new Coord(this.getX() + 1, this.getY());
    }
}
