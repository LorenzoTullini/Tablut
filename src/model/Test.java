package model;

import java.util.List;
import java.util.Random;

public class Test {

    public static void main(String argv[]) {
        TableState gameState = new TableState();
        System.out.println("INIZIO\n"+gameState.toString());
        List<Move> moves = null;
        //System.out.println("MOSSE PER IL BIANCO: le coordinate partono da (0,0) in alto a sinistra");
        //moves.stream().forEach(m -> System.out.println(m));
        Random random = new Random();
        int i = 0;
        int x = 0;
        int r = 0;

        while(!gameState.hasWhiteWon() && !gameState.hasBlackWon()) {
            if(!gameState.hasBlackWon()) {
                System.out.println("\n");
                moves = gameState.getAllMovesFor(PlayerType.WHITE);
                //x = random.nextInt(moves.size());
                //moves.stream().forEach(m -> System.out.println(m.toString()));
                System.out.println();
                System.out.println("BIANCO: faccio una mossa lecita random: " + moves.get(0));
                gameState = gameState.performMove(moves.get(x));
                System.out.println(gameState.toString());
                System.out.println("Distanza del re dalla salvezza: " +gameState.getKingDistance());
            }

            if(!gameState.hasBlackWon()) {
                System.out.println("\n");
                moves = gameState.getAllMovesFor(PlayerType.BLACK);
                r = random.nextInt(moves.size());
                System.out.println("NERO: faccio una mossa lecita random: " + moves.get(r));
                gameState = gameState.performMove(moves.get(r));
                System.out.println(gameState.toString());
            }
        }


    }
}

