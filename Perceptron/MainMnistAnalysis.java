import mnist.Donnees;
import mlp.*;
import java.util.*;
import java.io.*;

public class MainMnistAnalysis {
    public static void main(String[] args) {
        String trainImagePath = "data/train-images-idx3-ubyte";
        String trainLabelPath = "data/train-labels-idx1-ubyte";
        String testImagePath = "data/t10k-images-idx3-ubyte";
        String testLabelPath = "data/t10k-labels-idx1-ubyte";

        Donnees trainData = new Donnees(trainImagePath, trainLabelPath);
        Donnees testData = new Donnees(testImagePath, testLabelPath);

        // Tests avec différentes configurations
        runExperiment("neurons", trainData, testData);
        runExperiment("layers", trainData, testData);
        runExperiment("learning_rate", trainData, testData);
        runExperiment("decay", trainData, testData);
        runExperiment("shuffle", trainData, testData);
    }

    private static void runExperiment(String type, Donnees trainData, Donnees testData) {
        List<Double> trainErrors = new ArrayList<>();
        List<Double> testErrors = new ArrayList<>();
        List<Long> times = new ArrayList<>();

        switch (type) {
            case "neurons":
                for (int neurons : new int[]{50, 100, 150, 250, 500}) {
                    System.out.println("Testing with " + neurons + " neurons");
                    int[] layers = {784, neurons, 10};
                    var results = trainAndEvaluate(layers, 0.09, false, trainData, testData);
                    logResults(type, String.valueOf(neurons), results);
                }
                break;

            case "layers":
                int[][] layerConfigs = {
                    {784, 10},
                    {784, 500, 10},
                    {784, 500, 250, 10},
                    {784, 500, 250, 100, 10}
                };
                for (int[] layers : layerConfigs) {
                    System.out.println("Testing with " + (layers.length - 2) + " hidden layers");
                    var results = trainAndEvaluate(layers, 0.09, false, trainData, testData);
                    logResults(type, String.valueOf(layers.length - 2), results);
                }
                break;

            case "learning_rate":
                for (double lr : new double[]{0.01, 0.05, 0.09, 0.15}) {
                    System.out.println("Testing with learning rate " + lr);
                    int[] layers = {784, 512, 10};
                    var results = trainAndEvaluate(layers, lr, false, trainData, testData);
                    logResults(type, String.valueOf(lr), results);
                }
                break;

            case "decay":
                int[] layers = {784, 500, 10};
                System.out.println("Testing with learning rate decay");
                var results = trainAndEvaluate(layers, 0.09, true, trainData, testData);
                logResults(type, "decay", results);
                break;

            case "shuffle":
                layers = new int[]{784, 500, 10};
                System.out.println("Testing without shuffle");
                results = trainAndEvaluateWithShuffle(layers, 0.09, false, trainData, testData, false);
                logResults(type, "no_shuffle", results);
                
                System.out.println("Testing with shuffle");
                results = trainAndEvaluateWithShuffle(layers, 0.09, false, trainData, testData, true);
                logResults(type, "shuffle", results);
                break;
        }
    }

    private static double train(MLP mlp, Donnees data, int startIndex, int batchSize) {
        double totalError = 0.0;
        int endIndex = Math.min(startIndex + batchSize, data.getTaille());

        for (int i = startIndex; i < endIndex; i++) {
            double[] input = data.getInputs()[i];
            double[] output = data.getOutputs()[i];
            totalError += mlp.backPropagate(input, output);
        }
        return totalError / (endIndex - startIndex);
    }

    private static double evaluate(MLP mlp, Donnees data, int startIndex, int batchSize) {
        int totalExamples = 0;
        int incorrectPredictions = 0;

        int endIndex = Math.min(startIndex + batchSize, data.getTaille());

        for (int i = startIndex; i < endIndex; i++) {
            double[] input = data.getInputs()[i];
            double[] expectedOutput = data.getOutputs()[i];
            double[] actualOutput = mlp.execute(input);

            int predictedIndex = 0;
            for (int j = 1; j < actualOutput.length; j++) {
                if (actualOutput[j] > actualOutput[predictedIndex]) {
                    predictedIndex = j;
                }
            }

            int expectedIndex = 0;
            for (int j = 0; j < expectedOutput.length; j++) {
                if (expectedOutput[j] == 1.0) {
                    expectedIndex = j;
                    break;
                }
            }

            if (predictedIndex != expectedIndex) {
                incorrectPredictions++;
            }

            totalExamples++;
        }

        return (double) incorrectPredictions / totalExamples;
    }

    private static ExperimentResults trainAndEvaluateWithShuffle(int[] layers, double initialLR, 
        boolean useLRDecay, Donnees trainData, Donnees testData, boolean doShuffle) {
        long startTime = System.currentTimeMillis();
        
        MLP mlp = new MLP(layers, initialLR, new Sigmoide());
        List<Double> trainErrors = new ArrayList<>();
        List<Double> testErrors = new ArrayList<>();
        List<Long> times = new ArrayList<>();
        
        int epochs = 50;
        int batchSize = 100;
        double convergenceThreshold = 0.1;
        boolean hasConverged = false;
        long convergenceTime = 0;
        
        for (int epoch = 0; epoch < epochs; epoch++) {
            long epochStartTime = System.currentTimeMillis();
            
            if (doShuffle) {
                trainData.shuffle();
            }
            
            double lr = useLRDecay ? initialLR * (1.0 / (1.0 + epoch * 0.1)) : initialLR;
            mlp.setLearningRate(lr);
            
            double trainError = train(mlp, trainData, 0, batchSize);
            double testError = evaluate(mlp, testData, 0, batchSize);
            
            long epochTime = System.currentTimeMillis() - epochStartTime;
            times.add(epochTime);
            
            trainErrors.add(trainError);
            testErrors.add(testError);
            
            if (!hasConverged && trainError < convergenceThreshold) {
                hasConverged = true;
                convergenceTime = System.currentTimeMillis() - startTime;
            }
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        return new ExperimentResults(trainErrors, testErrors, times, convergenceTime);
    }

    private static ExperimentResults trainAndEvaluate(int[] layers, double initialLR, 
        boolean useLRDecay, Donnees trainData, Donnees testData) {
        long startTime = System.currentTimeMillis();
        
        MLP mlp = new MLP(layers, initialLR, new Sigmoide());
        List<Double> trainErrors = new ArrayList<>();
        List<Double> testErrors = new ArrayList<>();
        List<Long> times = new ArrayList<>();
        
        int epochs = 50;
        int batchSize = 100;
        double convergenceThreshold = 0.1; // Seuil de convergence
        boolean hasConverged = false;
        long convergenceTime = 0;
        
        for (int epoch = 0; epoch < epochs; epoch++) {
            long epochStartTime = System.currentTimeMillis();
            
            double lr = useLRDecay ? initialLR * (1.0 / (1.0 + epoch * 0.1)) : initialLR;
            mlp.setLearningRate(lr);
            
            double trainError = train(mlp, trainData, 0, batchSize);
            double testError = evaluate(mlp, testData, 0, batchSize);
            
            long epochTime = System.currentTimeMillis() - epochStartTime;
            times.add(epochTime);
            
            trainErrors.add(trainError);
            testErrors.add(testError);
            
            // Vérifier la convergence
            if (!hasConverged && trainError < convergenceThreshold) {
                hasConverged = true;
                convergenceTime = System.currentTimeMillis() - startTime;
            }
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        return new ExperimentResults(trainErrors, testErrors, times, convergenceTime);
    }

    private static void logResults(String expType, String param, ExperimentResults results) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(
                "results_" + expType + "_" + param + ".csv", true))) {
            writer.println("epoch,train_error,test_error,epoch_time,convergence_time");
            for (int i = 0; i < results.trainErrors.size(); i++) {
                writer.println(String.format("%d,%.6f,%.6f,%d,%d", 
                    i, results.trainErrors.get(i), results.testErrors.get(i), 
                    results.times.get(i), results.convergenceTime));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static class ExperimentResults {
        List<Double> trainErrors;
        List<Double> testErrors;
        List<Long> times;
        long convergenceTime;
        
        ExperimentResults(List<Double> trainErrors, List<Double> testErrors, List<Long> times, long convergenceTime) {
            this.trainErrors = trainErrors;
            this.testErrors = testErrors;
            this.times = times;
            this.convergenceTime = convergenceTime;
        }
    }
}