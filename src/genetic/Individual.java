package genetic;

import minimax.Minimax;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class Individual implements Comparator, Comparable {
    private int victories, losses, capturedPawns, lostPawns, totalVictoriesTurnNumber, totalLossesTurnNumber, matchPlayed;
    private Minimax player;
    private double[] weigths;


    public Individual(int maxDepth, double[] weigths) {
        victories = 0;
        losses = 0;
        capturedPawns = 0;
        lostPawns = 0;
        totalVictoriesTurnNumber = 0;
        totalLossesTurnNumber = 0;
        matchPlayed = 0;
        this.weigths = weigths;
    }


    @Override
    public int compare(Object o, Object t1) {
        //Prima vengono gli individui più forti
        if (o instanceof Individual && t1 instanceof Individual) {
            Individual a = (Individual) o;
            Individual b = (Individual) t1;

            //vengono prima gli individui che vincono di più ed in meno mosse
            if (a.victories > b.victories) {
                return -1;
            } else if (a.victories < b.victories) {
                return 1;
            } else {
                if (a.totalVictoriesTurnNumber / a.matchPlayed < b.totalVictoriesTurnNumber / b.matchPlayed) {
                    return -1;
                } else if (a.totalVictoriesTurnNumber / a.matchPlayed > b.totalVictoriesTurnNumber / b.matchPlayed) {
                    return 1;
                } else {
                    if (a.losses < b.losses) {
                        return -1;
                    } else if (a.losses > b.losses) {
                        return 1;
                    } else {
                        if (a.capturedPawns > b.capturedPawns) {
                            return -1;
                        } else if (a.capturedPawns < b.capturedPawns) {
                            return 1;
                        } else {
                            if (a.lostPawns < b.lostPawns) {
                                return -1;
                            } else if (a.lostPawns > b.lostPawns) {
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

    @Override
    public int compareTo(@NotNull Object o) {
        return compare(this, o);
    }
}
