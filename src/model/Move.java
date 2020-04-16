package model;

public class Move {
    private Coord from, to;
    private double costo;

    public Move(Coord from, Coord to) {
        this.from = from;
        this.to = to;
    }

    public double getCosto() {
        return this.costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public Coord getFrom() {
        return from;
    }

    public Coord getTo() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return from.equals(move.from) &&
                to.equals(move.to);
    }


    @Override
    public String toString() {
        return getFrom().toString() + " --> " + getTo().toString() + "  [" + costo + "]";
    }


}
