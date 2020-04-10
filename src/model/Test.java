package model;

import java.util.List;

public class Test {

    public static void main(String argv[]) {
        TableState gameState = new TableState();
        List<Move> moves = gameState.getAllMovesFor(PlayerType.WHITE);
        System.out.println("MOSSE PER IL BIANCO: le coordinate partono da (0,0) in alto a sinistra");
        moves.stream().forEach(m -> System.out.println(m));

        System.out.println("Faccio la prima mossa lecita");
        gameState.performMove(moves.get(0));

        System.out.println("MOSSE PER IL BIANCO dopo la prima mossa");
        moves = gameState.getAllMovesFor(PlayerType.WHITE);
        moves.stream().forEach(m -> System.out.println(m));

    }
}

