package model;

import java.util.Deque;
import java.util.Random;

public class Test {

    public static void main(String argv[]) {
        TableState gameState = new TableState();
        System.out.println("INIZIO\n"+gameState.toString());
        Deque<Move> moves = null;
        ////System.out.println("MOSSE PER IL BIANCO: le coordinate partono da (0,0) in alto a sinistra");
        //moves.stream().forEach(m -> //System.out.println(m));
        Random random = new Random(System.currentTimeMillis());
        int i = 0;
        int x = 0;
        int r = 0;


        while(!gameState.hasWhiteWon() && !gameState.hasBlackWon()) {
            if(!gameState.hasBlackWon()) {
                System.out.println("\n");
                moves = gameState.getAllMovesFor(PlayerType.WHITE);
                x = random.nextInt(moves.size());
                //moves.stream().forEach(m -> System.out.println(m.toString()));
                System.out.println();
                System.out.println("BIANCO: faccio una mossa lecita random: " + moves.getFirst());
                gameState = gameState.performMove(moves.getFirst());
                System.out.println(gameState.toString());
                System.out.println("Distanza del re dalla salvezza: " +gameState.getKingDistance());
                System.out.println("Il re si trova alle coordinate" +gameState.getKingCoord().toString());
                System.out.println("Totale pezzi neri mangiati: " + (16-gameState.getBlackPiecesCount()));
                System.out.println("Bianco ha vinto: "+ gameState.hasWhiteWon());
            }

            if(!gameState.hasWhiteWon()) {
                System.out.println("\n");
                moves = gameState.getAllMovesFor(PlayerType.BLACK);
                //moves.stream().forEach(m -> System.out.println(m.toString()));
                r = random.nextInt(moves.size());
                System.out.println("NERO: faccio una mossa lecita random: " + moves.getFirst());
                gameState = gameState.performMove(moves.getFirst());
                System.out.println(gameState.toString());
                System.out.println("Totale pezzi bianchi mangiati: " + (9-gameState.getWhitePiecesCount()));
                System.out.println("Nero ha vinto: "+ gameState.hasBlackWon());
            }
        }
    }
}

