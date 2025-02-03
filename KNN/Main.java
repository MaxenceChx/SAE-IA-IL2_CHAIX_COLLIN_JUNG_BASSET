public class Main {
    public static void main(String[] args) {
        Donnees donnees = new Donnees("train-images-idx3-ubyte", "train-labels-idx1-ubyte");

        System.out.println(donnees.getImagette(0));

        // Sauvegarder la première et la dernière imagette
        //donnees.getImagettes()[0].saveAsImage("image1_label_" + donnees.getImagettes()[0].getLabel() + ".png");
        //donnees.getImagettes()[donnees.getImagettes().length - 1]
                //.saveAsImage("image" + donnees.getImagettes().length + "_label_" + donnees.getImagettes()[donnees.getImagettes().length - 1].getLabel() + ".png");
    
        // Exemple d'utilisation de la classe PlusProche
        //PlusProche algo = new PlusProche(donnees);
        //Donnees donneesTest = new Donnees("t10k-images-idx3-ubyte", "t10k-labels-idx1-ubyte");

        //Statistiques stats = new Statistiques(algo, donneesTest);
        //System.out.println("Taux d'erreur: " + stats.tauxErreur());
    }
}

// DonneesEntrainement : 60k images
// DonneesTest : 10k images
// Taux d'erreur avec k = 1 : 0.0309 (96,91% de réussite)
// Taux d'erreur avec k = 10 : 0.0335 (96,65% de réussite)
// Taux d'erreur avec k = 20 : 0.0375 (96,25% de réussite)
// Taux d'erreur avec k = 100 : 0.056 (94,4% de réussite)