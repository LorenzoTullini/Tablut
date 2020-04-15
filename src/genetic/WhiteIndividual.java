package genetic;

import minimax.Minimax;
import model.PlayerType;

public class WhiteIndividual extends Individual {

    public WhiteIndividual(double[] weights, int maxDepth) {
        super(weights, maxDepth);
        player = new Minimax(PlayerType.WHITE, maxDepth, weights);
    }


    @Override
    public int compare(Object o, Object t1) {
        if (o instanceof WhiteIndividual && t1 instanceof WhiteIndividual) {
            super.compare(o,t1);
        }
        return 0;
    }
}