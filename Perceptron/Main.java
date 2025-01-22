import mlp.*;

import java.util.Scanner;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        MLP mlp = null;
        double[][] inputs = null;
        double[][] outputs = null;

        while (true) {
            System.out.println("=== Menu Principal ===");
            System.out.println("1. Créer/Reset le MLP");
            System.out.println("2. Entraîner le MLP");
            System.out.println("3. Tester le MLP");
            System.out.println("4. Quitter");
            System.out.print("Choix: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1: // Créer ou réinitialiser le MLP
                    System.out.println("=== Configuration du MLP ===");

                    System.out.print("Nombre de neurones par couche (couches cahées uniquement) (ex: 4,3,2): ");
                    String archInput = scanner.next();
                    int[] arch = Arrays.stream(archInput.split(","))
                            .mapToInt(Integer::parseInt)
                            .toArray();

                    System.out.print("Taux d'apprentissage (ex: 0,01): ");
                    double learningRate = scanner.nextDouble();

                    System.out.print("Fonction de transfert (tanh/sigmoid): ");
                    String transferFunctionInput = scanner.next();
                    TransferFunction transferFunction = transferFunctionInput.equalsIgnoreCase("tanh")
                            ? new Tanh()
                            : new Sigmoide();

                    // Exemple de jeu de données simple
                    inputs = new double[][] {
                        {0, 0}, {0, 1}, {1, 0}, {1, 1}
                    };
                    outputs = new double[][] {
                        {0}, {1}, {1}, {0} // XOR logique
                    };

                    arch = MLPTrainAndTest.getArchitecture(inputs, outputs, arch);

                    mlp = new MLP(arch, learningRate, transferFunction);
                    System.out.println("MLP configuré avec succès.");
                    break;

                case 2: // Entraîner le MLP
                    if (mlp == null) {
                        System.out.println("Veuillez configurer le MLP avant de l'entraîner.");
                        break;
                    }

                    System.out.print("Nombre maximum d'époques: ");
                    int maxEpochs = scanner.nextInt();

                    MLPTrainAndTest.trainMLP(mlp, inputs, outputs, maxEpochs);
                    break;

                case 3: // Tester le MLP
                    if (mlp == null) {
                        System.out.println("Veuillez configurer le MLP avant de le tester.");
                        break;
                    }

                    MLPTrainAndTest.evaluateMLP(mlp, inputs, outputs);
                    break;

                case 4: // Quitter
                    System.out.println("Au revoir!");
                    scanner.close();
                    return;

                default:
                    System.out.println("Choix invalide, veuillez réessayer.");
                    break;
            }
        }
    }
}