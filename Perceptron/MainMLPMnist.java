import mlp.Sigmoide;
import mlp.TransferFunction;
import mnist.Donnees;
import mnist.MLPMnist;
import mnist.Statistiques;


public class MainMLPMnist {

    public static void main(String[] args) {
        String trainImagePath = "data/train-images-idx3-ubyte";
        String trainLabelPath = "data/train-labels-idx1-ubyte";
        String testImagePath = "data/t10k-images-idx3-ubyte";
        String testLabelPath = "data/t10k-labels-idx1-ubyte";

        // Chargement des données
        Donnees trainData = new Donnees(trainImagePath, trainLabelPath);
        Donnees testData = new Donnees(testImagePath, testLabelPath);

        // Initialisation des paramètres du MLP
        int[] layers = {784, 256, 128, 10};
        double learningRate = 0.06;
        TransferFunction sig = new Sigmoide();
        MLPMnist mlp = new MLPMnist(trainData,layers,sig, learningRate);

        int epochsPerTraining = 10;
        int totalTrainingPhases = 20;


        Statistiques statsTrain = new Statistiques(mlp, testData);
        Statistiques statsTest = new Statistiques(mlp, testData);

        for (int phase = 1; phase <= totalTrainingPhases; phase++) {
            System.out.println("Phase " + phase + " : Entraînement et évaluation");
            double trainingError = mlp.train(epochsPerTraining);
            System.out.println("Phase : "+ phase + " - Training Error: " + trainingError + "Précision de l'entrainement : " + statsTrain.tauxErreur() + "Précision du test : " + statsTest.tauxErreur());
        }
    }
}
