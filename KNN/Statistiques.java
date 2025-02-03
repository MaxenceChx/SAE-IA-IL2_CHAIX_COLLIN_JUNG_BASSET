public class Statistiques {
    private AlgoClassification algo;
    private Donnees donneesTest;

    public Statistiques(AlgoClassification algo, Donnees donneesTest) {
        this.algo = algo;
        this.donneesTest = donneesTest;
    }
    
    public double tauxErreur() {
        int erreurs = 0;
        for (int i = 0; i < donneesTest.getTaille(); i++) {
            Imagette imagetteTest = donneesTest.getImagette(i);
            int etiquettePredite = algo.predireEtiquette(imagetteTest, 1);
            System.out.println("Etiquette predite: " + etiquettePredite + ", Etiquette reelle: " + imagetteTest.getLabel());
        }
        return (double) erreurs / donneesTest.getTaille();
    }
}
