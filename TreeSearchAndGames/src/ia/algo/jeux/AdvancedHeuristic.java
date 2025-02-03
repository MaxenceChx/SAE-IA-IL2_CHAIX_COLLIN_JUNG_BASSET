package ia.algo.jeux;

import ia.framework.jeux.GameState;
import ia.problemes.AbstractMnkGameState;
import java.util.ArrayList;
import java.util.List;

public class AdvancedHeuristic implements HeuristicFunction {

    @Override
    public double evaluate(GameState state) {
        // Si l'état est final, retourner sa valeur.
        if (state.isFinalState()) {
            return state.getGameValue();
        }

        AbstractMnkGameState mks = (AbstractMnkGameState) state;
        char[] board = mks.getBoard();
        int rows = mks.getRows();
        int cols = mks.getCols();
        int streak = mks.getStreak();
        double score = 0;

        // Calculer tous les alignements potentiels
        List<List<Integer>> alignments = computeAlignments(rows, cols, streak);

        for (List<Integer> alignment : alignments) {
            int countX = 0;
            int countO = 0;
            for (int pos : alignment) {
                char cell = board[pos];
                if (cell == 'X') {
                    countX++;
                } else if (cell == 'O') {
                    countO++;
                }
            }
            // Si l'alignement est bloqué, ignorer.
            if (countX > 0 && countO > 0) {
                continue;
            }
            // Sinon, valoriser exponentiellement l'alignement.
            if (countX > 0) {
                score += Math.pow(10, countX);
            } else if (countO > 0) {
                score -= Math.pow(10, countO);
            }
        }

        return score;
    }

    /**
     * Calcule tous les alignements potentiels (lignes, colonnes, diagonales)
     * en fonction du nombre de lignes, colonnes et de la longueur gagnante.
     *
     * @param rows le nombre de lignes du plateau
     * @param cols le nombre de colonnes du plateau
     * @param streak la longueur requise pour gagner
     * @return une liste d'alignements, chacun étant une liste d'indices dans le tableau 1D du plateau
     */
    private List<List<Integer>> computeAlignments(int rows, int cols, int streak) {
        List<List<Integer>> alignments = new ArrayList<>();

        // Alignements horizontaux
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j <= cols - streak; j++) {
                List<Integer> alignment = new ArrayList<>();
                for (int d = 0; d < streak; d++) {
                    alignment.add(i * cols + (j + d));
                }
                alignments.add(alignment);
            }
        }

        // Alignements verticaux
        for (int j = 0; j < cols; j++) {
            for (int i = 0; i <= rows - streak; i++) {
                List<Integer> alignment = new ArrayList<>();
                for (int d = 0; d < streak; d++) {
                    alignment.add((i + d) * cols + j);
                }
                alignments.add(alignment);
            }
        }

        // Diagonales (haut-gauche vers bas-droite)
        for (int i = 0; i <= rows - streak; i++) {
            for (int j = 0; j <= cols - streak; j++) {
                List<Integer> alignment = new ArrayList<>();
                for (int d = 0; d < streak; d++) {
                    alignment.add((i + d) * cols + (j + d));
                }
                alignments.add(alignment);
            }
        }

        // Diagonales (haut-droite vers bas-gauche)
        for (int i = 0; i <= rows - streak; i++) {
            for (int j = streak - 1; j < cols; j++) {
                List<Integer> alignment = new ArrayList<>();
                for (int d = 0; d < streak; d++) {
                    alignment.add((i + d) * cols + (j - d));
                }
                alignments.add(alignment);
            }
        }

        return alignments;
    }
}

