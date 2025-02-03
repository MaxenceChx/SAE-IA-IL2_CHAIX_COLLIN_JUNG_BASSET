import mlp.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MLPTrainAndTest {
    public static void main(String[] args) {
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
    public static void trainMLP(MLP mlp, double[][] inputs, double[][] outputs, int MAX_EPOCHS) {
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

            System.out.println("Epoch: " + epoch);

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

    public static void evaluateMLP(MLP mlp, double[][] inputs, double[][] outputs) {
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
}
