package model;

public class Move {
    private Coord from, to;
    private double costo;

    public Move(Coord from, Coord to){
        this.from=from;
        this.to=to;
    }

    public double getCosto(){
        return this.costo;
    }

    public void setCosto(double costo){
        this.costo = costo;
    }

    public Coord getFrom() {
        return from;
    }

    public Coord getTo() {
        return to;
    }



    @Override
    public String toString(){
        return getFrom().toString() + " --> " + getTo().toString();
    }


}
