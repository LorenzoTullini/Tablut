package genetic;

import com.google.gson.Gson;
import model.PlayerType;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;

public class TestTraining {
    private static int NUM_INDIVIDUI = 20;
    private static int NUM_SELEZIONE = 4;
    private static int PROB_SELEZIONE = 95;
    private static int PROB_MUTAZIONE = 5;
    private static int DIM_PESI = 10;
    private static int NUM_PARTITE = 10;
    private static int NUM_GENERAZIONI = 5;
    private static int maxDepth = 5;
    private static Random rndGen;

    public static void main(String[] args) {
        //genera la popolazione iniziale
        Individual[] population = new Individual[NUM_INDIVIDUI];

        double[][] weights = null;
        int[] accoppiamenti = new int[NUM_PARTITE];
        rndGen = new Random();
        rndGen.setSeed(System.currentTimeMillis());

        //1.1 Ottieni il vettore dei pesi da cui partire
        Path salvataggi = Path.of("weights.json");
        if (Files.exists(salvataggi)) {
            //carica i dati dal file
            Gson gson = new Gson();
            try (FileReader reader = new FileReader(salvataggi.toFile())) {
                weights = gson.fromJson(reader, double[][].class);
            } catch (FileNotFoundException e) {
                System.err.println("Impossibile trovare il file di salvataggio");
                System.exit(-1);
            } catch (IOException e) {
                System.err.println("Errore durante la lettura del file di salvataggio");
                System.exit(-2);
            }
        } else {
            //è la prima generazione crea dati casuali
            weights = new double[NUM_INDIVIDUI][DIM_PESI];

            for (int i = 0; i < NUM_INDIVIDUI; i++) {
                for (int j = 0; j < NUM_INDIVIDUI; j++) {
                    weights[i][j] = rndGen.nextInt(101) - 50;
                }
            }
        }

        //1.2 Genera la popolazione iniziale
        for (int i = 0; i < NUM_INDIVIDUI; i++) {
            population[i] = new Individual(maxDepth, weights[i]);
        }


        //2.2 Fai tutte le sfide
        for (int i = 0; i < NUM_PARTITE; i++) {

        }
        //3.1 Ordina i partecipanti e seleziona i migliori
        Arrays.sort(population);


        //3.2 Genera la generazione successiva
//            WhiteIndividual[] tmpWhite = new WhiteIndividual[NUM_INDIVIDUI];
//            tmpWhite[0] = new WhiteIndividual();


        try (PrintWriter writer = new PrintWriter(salvataggi.toFile())) {
            Gson gson = new Gson();
            writer.print(gson.toJson(weights));
        } catch (
                IOException e) {
            e.printStackTrace();
        }


    }

    private static Individual generaIndividuoCasuale(PlayerType type) {
        double[] w = new double[DIM_PESI];

        for (int i = 0; i < DIM_PESI; i++) {
            w[i] = rndGen.nextInt(101) - 50;
        }

        return new Individual(maxDepth, w);
    }

    private static Individual generaIndividuo(Individual a, Individual b) {
        //TODO genera un individuo a partire da altri due
        return null;
    }

}
