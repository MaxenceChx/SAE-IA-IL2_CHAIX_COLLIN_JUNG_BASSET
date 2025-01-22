import mnist.Donnees;

import mlp.*;

public class MainMnist {
    public static void main(String[] args) {
        // Chemins des fichiers MNIST
        String trainImagePath = "data/train-images-idx3-ubyte";
        String trainLabelPath = "data/train-labels-idx1-ubyte";
        String testImagePath = "data/t10k-images-idx3-ubyte";
        String testLabelPath = "data/t10k-labels-idx1-ubyte";

        // Chargement des données
        System.out.println("Chargement des données d'entraînement...");
        Donnees trainData = new Donnees(trainImagePath, trainLabelPath);
        System.out.println("Chargement des données de test...");
        Donnees testData = new Donnees(testImagePath, testLabelPath);

        // Initialisation des paramètres du MLP
        int[] layers = {784, 512, 10};
        double learningRate = 0.09;
        MLP mlp = new MLP(layers, learningRate, new Sigmoide());

        // Paramètres d'entraînement
        int epochsPerTraining = 10; // Nombre d'époques par phase d'entraînement
        int totalTrainingPhases = 100; // Nombre total de phases (train + test)
        int batchSize = 50;        // Taille du sous-échantillon
        int startIndex = 0;          // Indice de départ pour l'entraînement et le test

        for (int phase = 1; phase <= totalTrainingPhases; phase++) {
            System.out.println("Phase " + phase + " : Entraînement et évaluation");

            // Entraîner sur x époques
            for (int epoch = 1; epoch <= epochsPerTraining; epoch++) {
                double trainingError = train(mlp, trainData, startIndex, batchSize);
                System.out.printf("  Epoch %d - Training Error: %.6f\n", epoch, trainingError);
            }

            // Tester le modèle
            double testError = evaluate(mlp, testData, startIndex, batchSize);
            System.out.printf("  Test Error: %.6f\n", testError);
        }
    }

    /**
     * Entraîne le MLP sur un sous-ensemble de données.
     * @param mlp Réseau de neurones.
     * @param data Ensemble de données d'entraînement.
     * @param startIndex Indice de départ dans les données.
     * @param batchSize Nombre d'échantillons à traiter.
     * @return Erreur moyenne sur le sous-ensemble de données.
     */
    public static double train(MLP mlp, Donnees data, int startIndex, int batchSize) {
        double totalError = 0.0;
        int endIndex = Math.min(startIndex + batchSize, data.getTaille());

        data.shuffle();

        for (int i = startIndex; i < endIndex; i++) {
            double[] input = data.getInputs()[i];
            double[] output = data.getOutputs()[i];
            totalError += mlp.backPropagate(input, output);
        }
        return totalError / (endIndex - startIndex);
    }

    /**
     * Évalue le MLP sur un sous-ensemble de données.
     * @param mlp Réseau de neurones.
     * @param data Ensemble de données de test.
     * @param startIndex Indice de départ dans les données.
     * @param batchSize Nombre d'échantillons à traiter.
     * @return Pourcentage d'erreurs (entre 0 et 1).
     */
    public static double evaluate(MLP mlp, Donnees data, int startIndex, int batchSize) {
        int totalExamples = 0; // Nombre total d'exemples évalués
        int incorrectPredictions = 0; // Nombre de prédictions incorrectes

        int endIndex = Math.min(startIndex + batchSize, data.getTaille());

        data.shuffle();

        for (int i = startIndex; i < endIndex; i++) {
            double[] input = data.getInputs()[i];
            double[] expectedOutput = data.getOutputs()[i];
            double[] actualOutput = mlp.execute(input);

            // Trouver l'indice de la valeur maximale dans actualOutput
            int predictedIndex = 0;
            for (int j = 1; j < actualOutput.length; j++) {
                if (actualOutput[j] > actualOutput[predictedIndex]) {
                    predictedIndex = j;
                }
            }

            // Trouver l'indice du 1 attendu dans expectedOutput
            int expectedIndex = 0;
            for (int j = 0; j < expectedOutput.length; j++) {
                if (expectedOutput[j] == 1.0) {
                    expectedIndex = j;
                    break;
                }
            }

            // Vérifier si la prédiction est correcte
            if (predictedIndex != expectedIndex) {
                incorrectPredictions++;
            }

            totalExamples++;
        }

        // Retourner le pourcentage d'erreurs
        return (double) incorrectPredictions / totalExamples;
    }
}