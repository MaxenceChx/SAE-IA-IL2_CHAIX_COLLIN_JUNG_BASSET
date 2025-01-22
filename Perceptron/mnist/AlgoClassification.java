package mnist;

public abstract class AlgoClassification {
    protected Donnees donnees; // Données d'entraînement

    // Constructeur qui initialise les données d'entraînement
    public AlgoClassification(Donnees donnees) {
        this.donnees = donnees;
    }

    // Méthode abstraite pour prédire l'étiquette d'une imagette
    public abstract int predireEtiquette(Imagette imagette);

    // Méthode abstraite pour prédire l'étiquette d'une imagette en utilisant k plus proches voisins
    public abstract int predireEtiquette(Imagette imagette, int k);
}