package model;

import java.util.List;
import java.util.Random;

public class Test {

    public static void main(String argv[]) {
        TableState gameState = new TableState();
        System.out.println("INIZIO\n"+gameState.toString());
        System.out.println("\n");
        List<Move> moves = gameState.getAllMovesFor(PlayerType.WHITE);
        //System.out.println("MOSSE PER IL BIANCO: le coordinate partono da (0,0) in alto a sinistra");
        //moves.stream().forEach(m -> System.out.println(m));
        Random random = new Random();
        int r = random.nextInt(moves.size());
        System.out.println("BIANCO: faccio una mossa lecita random: " + moves.get(r));
        gameState.performMove(moves.get(r));
        System.out.println(gameState.toString());
        System.out.println("\n");
        moves = gameState.getAllMovesFor(PlayerType.BLACK);
        r = random.nextInt(moves.size());
        System.out.println("NERO: faccio una mossa lecita random: " + moves.get(r));
        gameState.performMove(moves.get(r));
        System.out.println(gameState.toString());

    }
}

