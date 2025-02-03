import java.util.Arrays;

public class PlusProche extends AlgoClassification {
    public PlusProche(Donnees donnees) {
        super(donnees);
    }

    public int predireEtiquette(Imagette imagette) {
        int indexPlusProche = 0;
        double distanceMin = donnees.getImagette(0).distance(imagette);
        for (int i = 1; i < donnees.getTaille(); i++) {
            double distance = donnees.getImagette(i).distance(imagette);
            if (distance < distanceMin) {
                distanceMin = distance;
                indexPlusProche = i;
            }
        }
        return donnees.getImagette(indexPlusProche).getLabel();
    }

    public int predireEtiquette(Imagette imagette, int k) {
        Imagette[] plusProches = new Imagette[k];
        double[] distances = new double[k];
        for (int i = 0; i < k; i++) {
            plusProches[i] = donnees.getImagette(i);
            distances[i] = donnees.getImagette(i).distance(imagette);
        }   
        for (int i = k; i < donnees.getTaille(); i++) {
            double distance = donnees.getImagette(i).distance(imagette);
            int indexMax = 0;
            double distanceMax = distances[0];
            for (int j = 1; j < k; j++) {
                if (distances[j] > distanceMax) {
                    distanceMax = distances[j];
                    indexMax = j;
                }
            }
            if (distance < distanceMax) {
                plusProches[indexMax] = donnees.getImagette(i);
                distances[indexMax] = distance;
            }
        }
        int[] labels = new int[10];
        for (int i = 0; i < k; i++) {
            labels[plusProches[i].getLabel()]++;
        }
        System.out.println(Arrays.toString(labels));
        int labelMax = 0;
        int max = labels[0];
        for (int i = 1; i < 10; i++) {
            if (labels[i] > max) {
                max = labels[i];
                labelMax = i;
            }
        }
        return labelMax;
    }
}
