package perceptron;

public class Tanh {

    /**
     * Function de transfert
     * @param value entrée
     * @return sortie de la fonction sur l'entrée
     */
    public double evaluate(double value) {
        //fonction : σ(x) = tanh(x)
        return Math.tanh(value);
    }

    /**
     * Dérivée de la fonction de tranfert
     * @param value entrée
     * @return sortie de la fonction dérivée sur l'entrée
     */
    public double evaluateDer(double value) {
        //dérivée : σ'(x) = 1−σ2(x)
        double sig = evaluate(value);
        return 1 - sig * sig;
    }
}
