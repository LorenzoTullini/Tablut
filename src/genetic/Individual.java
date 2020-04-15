package genetic;

import minimax.Minimax;

import java.util.Comparator;

public abstract class Individual implements Comparator {
    protected int victories, losses, capturedPawns, lostPawns, totalVictoriesTurnNumber, totalLossesTurnNumber, matchPlayed;
    protected Minimax player;
    protected double[] weights;

    public Individual(double[] weights, int maxDepth) {
        this.weights = weights;
        victories = 0;
        losses = 0;
        capturedPawns = 0;
        lostPawns = 0;
        totalVictoriesTurnNumber = 0;
        totalLossesTurnNumber = 0;
        matchPlayed = 0;
    }

    public int getVictories() {
        return victories;
    }

    public int getLosses() {
        return losses;
    }

    public int getCapturedPawns() {
        return capturedPawns;
    }

    public int getLostPawns() {
        return lostPawns;
    }

    public double[] getWeights() {
        return weights;
    }

    @Override
    public int compare(Object o, Object t1) {
        if (o instanceof Individual && t1 instanceof Individual) {
            Individual a = (Individual) o;
            Individual b = (Individual) t1;

            //vengono prima gli individui che vincono di più ed in meno mosse
            if (a.victories < b.victories) {
                return -1;
            } else if (a.victories > b.victories) {
                return 1;
            } else {
                if (a.totalVictoriesTurnNumber < b.totalVictoriesTurnNumber) {
                    return -1;
                } else if (a.totalVictoriesTurnNumber > b.totalVictoriesTurnNumber) {
                    return 1;
                } else {
                    if (a.losses > b.losses) {
                        return -1;
                    } else if (a.losses < b.losses) {
                        return 1;
                    } else {
                        if (a.capturedPawns < b.capturedPawns) {
                            return -1;
                        } else if (a.capturedPawns > b.capturedPawns) {
                            return 1;
                        } else {
                            if (a.lostPawns > b.lostPawns) {
                                return -1;
                            } else if (a.lostPawns < b.lostPawns) {
                                return 1;
                            } else {
                                return 0;
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }
}
