package mlp;

public class Sigmoide implements TransferFunction{

    /**
     * Function de transfert
     * @param value entrée
     * @return sortie de la fonction sur l'entrée
     */
    public double evaluate(double value) {
        //fonction : σ(x) = 1 / (1+e(−x))
        return 1 / (1 + Math.exp(-value));
    }

    /**
     * Dérivée de la fonction de tranfert
     * @param value entrée
     * @return sortie de la fonction dérivée sur l'entrée
     */
    public double evaluateDer(double value) {
        //dérivée : σ′(x)=σ(x)⋅(1−σ(x))
        double sigmoid = evaluate(value);
        return sigmoid * (1 - sigmoid);
    }

}
