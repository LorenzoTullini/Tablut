package genetic;

import minimax.SearchManager;
import model.PlayerType;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class Individual implements Comparator, Comparable {
    private int victories, losses, capturedPawns, lostPawns, totalVictoriesTurnNumber, totalLossesTurnNumber, matchPlayed;
    private SearchManager player;
    private double[] weigths;
    private int maxDepth;


    public Individual(int maxDepth, double[] weigths) {
        victories = 0;
        losses = 0;
        capturedPawns = 0;
        lostPawns = 0;
        totalVictoriesTurnNumber = 0;
        totalLossesTurnNumber = 0;
        matchPlayed = 0;
        this.weigths = weigths;
        this.maxDepth = maxDepth;
    }

    void prepareForNewMatch(PlayerType colour) {
        player = new SearchManager(colour, maxDepth, weigths);
    }

    public int getVictories() {
        return victories;
    }

    public void addVictory() {
        victories++;
    }

    public int getLosses() {
        return losses;
    }

    public void addLoss() {
        losses++;
    }

    public int getCapturedPawns() {

        return capturedPawns;
    }

    public double getMeanCapturedPawns() {

        return ((double) capturedPawns) / matchPlayed;
    }

    public void addCapturedPawns(int capturedPawns) {
        this.capturedPawns += capturedPawns;
    }

    public int getLostPawns() {
        return lostPawns;
    }

    public double getMeanLostPawns() {
        return ((double) lostPawns) / matchPlayed;
    }

    public void addLostPawns(int lostPawns) {

        this.lostPawns += lostPawns;
    }

    public int getTotalVictoriesTurnNumber() {
        return totalVictoriesTurnNumber;
    }

    public double getMeanVictoriesTurnNumber() {
        return (victories > 0) ? ((double) totalVictoriesTurnNumber) / victories : 0;
    }

    public void addVictoryTurnNumber(int turns) {
        totalVictoriesTurnNumber += turns;
    }

    public int getTotalLossesTurnNumber() {
        return totalLossesTurnNumber;
    }

    public void addLossesTurnNumber(int turns) {

        totalLossesTurnNumber += turns;
    }

    public int getMatchPlayed() {
        return matchPlayed;
    }

    public void addMatchPlayed() {

        matchPlayed++;
    }

    public SearchManager getPlayer() {
        return player;
    }

    public double[] getWeigths() {
        return weigths;
    }

    public void applyMutation(double perc, int geneIdx) {
        this.weigths[geneIdx] = this.weigths[geneIdx] * (1 + perc);
    }

    public int getMaxDepth() {
        return maxDepth;
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
                if (a.losses < b.losses) {
                    return -1;
                } else if (a.losses > b.losses) {
                    return 1;
                } else {
                    if (a.victories > 0 && (a.totalVictoriesTurnNumber / a.victories < b.totalVictoriesTurnNumber / b.victories)) {
                        return -1;
                    } else if (a.victories > 0 && (a.totalVictoriesTurnNumber / a.victories > b.totalVictoriesTurnNumber / b.victories)) {
                        return 1;
                    } else {
                        if (a.losses > 0 && (a.totalLossesTurnNumber / a.losses > b.totalLossesTurnNumber / b.losses)) {
                            return -1;
                        } else if (a.losses > 0 && (a.totalLossesTurnNumber / a.losses < b.totalLossesTurnNumber / b.losses)) {
                            return 1;
                        } else {
                            if (a.capturedPawns / ((double) a.matchPlayed) > b.capturedPawns / ((double) b.matchPlayed)) {
                                return -1;
                            } else if (a.capturedPawns / ((double) a.matchPlayed) < b.capturedPawns / ((double) b.matchPlayed)) {
                                return 1;
                            } else {
                                if (a.lostPawns / ((double) a.matchPlayed) < b.lostPawns / ((double) b.matchPlayed)) {
                                    return -1;
                                } else if (a.lostPawns / ((double) a.matchPlayed) > b.lostPawns / ((double) b.matchPlayed)) {
                                    return 1;
                                } else {
                                    return 0;
                                }
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
