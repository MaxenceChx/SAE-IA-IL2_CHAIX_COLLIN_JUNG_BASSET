package mnist;

import mlp.MLP;
import mlp.TransferFunction;

import java.util.stream.IntStream;

public class MLPMnist extends AlgoClassification {
    private MLP mlp;

    public MLPMnist(Donnees donnees, int[] layers, TransferFunction fonction, double learningRate) {
        super(donnees);
        this.mlp = new MLP(layers, learningRate, fonction);
    }

    @Override
    public int predireEtiquette(Imagette imagette) {
        double[] output = mlp.execute(imagette.getInput());
        int indice = IntStream.range(0, output.length)
                .reduce((i, j) -> output[j] > output[i] ? j : i)
                .orElse(-1);
        return indice;
    }

    @Override
    public int predireEtiquette(Imagette imagette, int k) {
        return predireEtiquette(imagette);
    }

    public double train(int maxIterations) {
        double erreur= 0.0;

        for (int iteration = 0; iteration < maxIterations; iteration++) {
            // On calcule la somme des erreurs sur tout l'ensemble d'apprentissage
            double sommeErreurs = IntStream.range(0, donnees.getTaille())
                    .mapToDouble(i -> {
                        double[] oneHot = new double[mlp.getOutputLayerSize()];
                        int label = donnees.getImagette(i).getLabel();
                        oneHot[label] = 1.0;

                        return mlp.backPropagate(
                                donnees.getImagette(i).getInput(), oneHot);
                    }).sum(); // On récupère la somme de toutes les erreurs

            // On en déduit l'erreur moyenne
            erreur = sommeErreurs / donnees.getTaille();

        }
        // La dernière erreur moyenne calculée
        return erreur;
    }

}
