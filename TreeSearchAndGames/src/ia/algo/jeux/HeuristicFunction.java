package ia.algo.jeux;

import ia.framework.jeux.GameState;

public interface HeuristicFunction {
    /**
     * Évalue l'état de jeu et retourne un score.
     * Un score positif est favorable à PLAYER1 et un score négatif à PLAYER2.
     *
     * @param state l'état du jeu à évaluer
     * @return le score heuristique
     */
    double evaluate(GameState state);
}

