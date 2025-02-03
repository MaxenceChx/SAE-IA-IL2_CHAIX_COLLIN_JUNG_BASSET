import java.util.Random;
import mlp.*;
import mnist.*;

public class MNISTTrainer {
    public static void main(String[] args) {
        String trainImagePath = "data.old/train-images-idx3-ubyte";
        String trainLabelPath = "data.old/train-labels-idx1-ubyte";
        String testImagePath = "data.old/t10k-images-idx3-ubyte";
        String testLabelPath = "data.old/t10k-labels-idx1-ubyte";

        // Charger les données d'entraînement et de test
        System.out.println("Chargement des données d'entraînement...");
        Donnees trainData = new Donnees(trainImagePath, trainLabelPath);
        System.out.println("Chargement des données de test...");
        Donnees testData = new Donnees(testImagePath, testLabelPath);

        // Configuration du réseau
        int[] layers = {784, 256, 128, 10};
        double learningRate = 0.08;
        TransferFunction transferFunction = new Sigmoide();
        MLP mlp = new MLP(layers, learningRate, transferFunction);

        // Paramètres d'entraînement
        int batchSize = 50;
        int epochs = 10;
        int displayStep = 1;

        Random random = new Random();

        // Entraînement
        for (int epoch = 1; epoch <= epochs; epoch++) {
            trainData.shuffle();
            double totalError = 0.0;

            for (int batchStart = 0; batchStart < trainData.getTaille(); batchStart += batchSize) {
                int batchEnd = Math.min(batchStart + batchSize, trainData.getTaille());

                // Sélectionner un batch aléatoire
                for (int i = batchStart; i < batchEnd; i++) {
                    double[] input = trainData.getImagette(i).getInput();
                    double[] output = trainData.getImagette(i).getOutput();

                    totalError += mlp.backPropagate(input, output);
                }
            }

            // Affichage des performances à chaque itération
            if (epoch % displayStep == 0) {
                double averageError = totalError / trainData.getTaille();
                System.out.printf("Epoch %d, Average Error: %.6f%n", epoch, averageError);
            }
        }

        // Tester sur les données de test
        int correct = 0;
        for (Imagette testImagette : testData.getImagettes()) {
            double[] prediction = mlp.execute(testImagette.getInput());
            int predictedLabel = getPrediction(prediction);
            if (predictedLabel == testImagette.getLabel()) {
                correct++;
            }
        }

        double accuracy = (double) correct / testData.getTaille() * 100.0;
        System.out.printf("Test Accuracy: %.2f%%%n", accuracy);
    }

    private static int getPrediction(double[] output) {
        int maxIndex = 0;
        for (int i = 1; i < output.length; i++) {
            if (output[i] > output[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }
}
