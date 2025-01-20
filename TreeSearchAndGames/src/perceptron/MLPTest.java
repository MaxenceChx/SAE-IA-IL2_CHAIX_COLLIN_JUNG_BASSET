package perceptron;

import perceptron.mlp.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MLPTest {

    // Données d'entraînement pour AND, OR, XOR
    private static final double[][] AND_INPUTS = {
            {0, 0},
            {0, 1},
            {1, 0},
            {1, 1}
    };

    private static final double[][] AND_OUTPUTS_1D = {
            {0},
            {0},
            {0},
            {1}
    };

    private static final double[][] AND_OR_OUTPUTS_2D = {
            {0, 0},
            {0, 1},
            {0, 1},
            {1, 1}
    };

    private static final double[][] OR_INPUTS = {
            {0, 0},
            {0, 1},
            {1, 0},
            {1, 1}
    };

    private static final double[][] OR_OUTPUTS_1D = {
            {0},
            {1},
            {1},
            {1}
    };

    private static final double[][] OR_NAND_OUTPUTS_2D = {
            {0, 1},
            {1, 0},
            {1, 0},
            {1, 0}
    };

    private static final double[][] XOR_INPUTS = {
            {0, 0},
            {0, 1},
            {1, 0},
            {1, 1}
    };

    private static final double[][] XOR_OUTPUTS_1D = {
            {0},
            {1},
            {1},
            {0}
    };

    private static final double[][] XOR_AND_OUTPUTS_2D = {
            {0, 0},
            {1, 0},
            {1, 0},
            {0, 1}
    };

    private static final double LEARNING_RATE = 0.1;
    private static final int MAX_EPOCHS       = 50_000;

    public static void main(String[] args) {


        TransferFunction[] functions = { new Sigmoide(), new Tanh() };

        // Différentes architectures de réseaux de neurones
        int[][] architectures = {
                {2, 2, 1},
                {2, 3, 2}
        };

        // Liste des opérateurs ET, OU, XOR
        List<OpTest> tests = Arrays.asList(
                new OpTest("AND", AND_INPUTS, AND_OUTPUTS_1D, AND_OR_OUTPUTS_2D),
                new OpTest("OR",  OR_INPUTS,  OR_OUTPUTS_1D,  OR_NAND_OUTPUTS_2D),
                new OpTest("XOR", XOR_INPUTS, XOR_OUTPUTS_1D, XOR_AND_OUTPUTS_2D)
        );

        // On parcourt chaque fonction de transfert
        for (TransferFunction tf : functions) {
            System.out.println("\n=== Tests avec " + tf.getClass().getSimpleName() + " ===");

            for (int[] arch : architectures) {

                int outputSize = arch[arch.length - 1];

                System.out.println("\n--- Architecture " + Arrays.toString(arch) + " ---");

                for (OpTest op : tests) {

                    // Sélection des outputs selon la taille de sortie
                    double[][] outputs = (outputSize == 1) ?
                            op.outputs1D :
                            op.outputs2D;

                    System.out.println("Test de l'opérateur " + op.name + " (Sortie "
                            + (outputSize == 1 ? "1D" : "2D") + ")");

                    // Création du MLP
                    MLP mlp = new MLP(arch, LEARNING_RATE, tf);

                    // Entraînement
                    trainMLP(mlp, op.inputs, outputs);

                    // Évaluation finale
                    evaluateMLP(mlp, op.inputs, outputs);
                }
            }
        }
    }

    public static int[] getArchitecture(double[][] inputs, double[][] outputs, int[] arch) {
        int[] res = new int[2+arch.length];

        res[0] = inputs[0].length;
        res[res.length-1] = outputs[0].length;

        for (int i = 0; i < arch.length; i++) {
            res[i+1] = arch[i];
        }

        return res;
    }

    /**
     * Entraîne le réseau avec permutation des données à chaque epoch.
     */
    private static void trainMLP(MLP mlp, double[][] inputs, double[][] outputs) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < inputs.length; i++) {
            indices.add(i);
        }

        int epoch = 0;
        while (epoch < MAX_EPOCHS) {
            Collections.shuffle(indices);

            for (int idx : indices) {
                mlp.backPropagate(inputs[idx], outputs[idx]);
            }

            if (allExamplesCorrect(mlp, inputs, outputs)) {
                System.out.println("==> Convergence après " + epoch + " époques.");
                return;
            }

            epoch++;
        }
        System.out.println("==> Max epochs (" + MAX_EPOCHS + ") atteint, arrêt de l'entraînement.");
    }

    private static boolean allExamplesCorrect(MLP mlp, double[][] inputs, double[][] outputs) {
        for (int i = 0; i < inputs.length; i++) {
            double[] result = mlp.execute(inputs[i]);

            for (int j = 0; j < result.length; j++) {
                // On arrondit la sortie
                double rounded = Math.round(result[j]);
                double expected = outputs[i][j];
                if (rounded != expected) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void evaluateMLP(MLP mlp, double[][] inputs, double[][] outputs) {
        int correct = 0;
        for (int i = 0; i < inputs.length; i++) {
            double[] result = mlp.execute(inputs[i]);
            double[] rounded = new double[result.length];

            boolean isCorrect = true;
            for (int j = 0; j < result.length; j++) {
                rounded[j] = Math.round(result[j]);
                if (rounded[j] != outputs[i][j]) {
                    isCorrect = false;
                }
            }

            if (isCorrect) correct++;

            System.out.printf("Entrée: %s -> Sortie: %s (Attendu: %s)%n",
                    Arrays.toString(inputs[i]),
                    Arrays.toString(rounded),
                    Arrays.toString(outputs[i]));
        }
        double accuracy = 100.0 * correct / inputs.length;
        System.out.printf("Taux de réussite: %.2f %% (%d/%d)\n",
                accuracy, correct, inputs.length);
    }

    private record OpTest(
            String name,
            double[][] inputs,
            double[][] outputs1D,
            double[][] outputs2D
    ) {}
}
