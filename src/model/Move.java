package model;

public class Move {
    private Coord from, to;
    private double costo;
    private int prio;

    public Move(Coord from, Coord to){
        this.from=from;
        this.to=to;
        prio=0;
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

    public void setPrio(int prio){
        this.prio = prio;
    }

    public int getPrio(){
        return this.prio;
    }

    @Override
    public String toString(){
        return getFrom().toString() + " --> " + getTo().toString() + " prio: " + prio;
    }


}
