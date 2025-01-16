package perceptron;

import perceptron.mlp.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestMLPComplet {

    // Données d'entraînement pour AND, OR, XOR
    private static final double[][] AND_INPUTS = {{0,0}, {0,1}, {1,0}, {1,1}};
    private static final double[][] AND_OUTPUTS = {{0}, {0}, {0}, {1}};
    private static final double[][] OR_INPUTS = {{0,0}, {0,1}, {1,0}, {1,1}};
    private static final double[][] OR_OUTPUTS = {{0}, {1}, {1}, {1}};
    private static final double[][] XOR_INPUTS = {{0,0}, {0,1}, {1,0}, {1,1}};
    private static final double[][] XOR_OUTPUTS = {{0}, {1}, {1}, {0}};

    // Configuration des tests
    private static final int MAX_EPOCHS = 1000000;
    private static final double LEARNING_RATE = 0.1;

    public static void main(String[] args) {
        // Test avec différentes configurations
        TransferFunction[] functions = {new Sigmoide(), new Tanh()};
        String[] operateurs = {"AND", "OR", "XOR"};
        int[][] architectures = {
            {2, 4, 1},
            {2, 6, 1},
            {2, 4, 4, 1},
        };

        for (TransferFunction tf : functions) {
            System.out.println("\n=== Tests avec " + tf.getClass().getSimpleName() + " ===");
            
            for (String op : operateurs) {
                System.out.println("\n--- Test de l'opérateur " + op + " ---");
                
                for (int[] architecture : architectures) {
                    testConfiguration(op, architecture, tf);
                }
            }
        }
    }

    private static void testConfiguration(String operateur, int[] architecture, TransferFunction tf) {
        // Préparation des données selon l'opérateur
        double[][] inputs;
        double[][] outputs;
        
        switch (operateur) {
            case "AND":
                inputs = AND_INPUTS;
                outputs = adaptOutputs(AND_OUTPUTS, architecture[architecture.length-1]);
                break;
            case "OR":
                inputs = OR_INPUTS;
                outputs = adaptOutputs(OR_OUTPUTS, architecture[architecture.length-1]);
                break;
            case "XOR":
                inputs = XOR_INPUTS;
                outputs = adaptOutputs(XOR_OUTPUTS, architecture[architecture.length-1]);
                break;
            default:
                throw new IllegalArgumentException("Opérateur non reconnu");
        }

        // Création et entraînement du réseau
        MLP mlp = new MLP(architecture, LEARNING_RATE, tf);
        
        // Mélange des données
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < inputs.length; i++) {
            indices.add(i);
        }

        int epoch = 0;
        boolean allExamplesCorrect = false;

        while (epoch < MAX_EPOCHS && !allExamplesCorrect) {
            Collections.shuffle(indices);
            
            // Une époque d'entraînement
            for (int idx : indices) {
                mlp.backPropagate(inputs[idx], outputs[idx]);
            }

            // Vérification si tous les exemples sont corrects
            allExamplesCorrect = true;
            for (int i = 0; i < inputs.length; i++) {
                double[] result = mlp.execute(inputs[i]);
                for (int j = 0; j < result.length; j++) {
                    if (Math.round(result[j]) != Math.round(outputs[i][j])) {
                        allExamplesCorrect = false;
                        break;
                    }
                }
                if (!allExamplesCorrect) {
                    break;
                }
            }

            if (allExamplesCorrect || epoch == MAX_EPOCHS) {
                System.out.printf("Architecture: %s - Itération %d - Tous exemples corrects: %b%n", 
                    Arrays.toString(architecture), epoch, allExamplesCorrect);
            }

            epoch++;
        }

        // Test final
        System.out.println("Résultats finaux:");
        evaluateNetwork(mlp, inputs, outputs);
        System.out.printf("Convergence: %s, Itérations: %d%n", 
            allExamplesCorrect ? "Réussie" : "Échec", epoch);
    }

    private static double[][] adaptOutputs(double[][] originalOutputs, int outputSize) {
        if (outputSize == 1) return originalOutputs;
        
        double[][] newOutputs = new double[originalOutputs.length][outputSize];
        for (int i = 0; i < originalOutputs.length; i++) {
            Arrays.fill(newOutputs[i], originalOutputs[i][0]);
        }
        return newOutputs;
    }

    private static void evaluateNetwork(MLP mlp, double[][] inputs, double[][] outputs) {
        int correct = 0;
        for (int i = 0; i < inputs.length; i++) {
            double[] result = mlp.execute(inputs[i]);
            boolean match = true;
            
            for (int j = 0; j < result.length; j++) {
                result[j] = Math.round(result[j]);
                if (j < outputs[i].length && Math.abs(result[j] - outputs[i][j]) > 0.1) {
                    match = false;
                }
            }
            
            if (match) correct++;
            
            System.out.printf("Entrée: %s -> Sortie: %s (Attendu: %s)%n",
                Arrays.toString(inputs[i]),
                Arrays.toString(result),
                Arrays.toString(outputs[i]));
        }
        System.out.printf("Précision: %.2f%% (%d/%d)%n", 
            (correct * 100.0 / inputs.length), correct, inputs.length);
    }
}