package genetic;

import minimax.Minimax;
import model.PlayerType;

public class BlackIndividual extends Individual {

    public BlackIndividual(double[] weights, int maxDepth) {
        super(weights, maxDepth);
        player = new Minimax(PlayerType.BLACK, maxDepth, weights);
    }

    @Override
    public int compare(Object o, Object t1) {
        if (o instanceof BlackIndividual && t1 instanceof BlackIndividual) {
            super.compare(o, t1);
        }
        return 0;
    }
}
