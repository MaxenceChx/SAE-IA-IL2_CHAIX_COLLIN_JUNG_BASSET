package ia.algo.jeux;

import ia.framework.jeux.GameState;
import ia.problemes.AbstractMnkGameState;

public class BasicHeuristic implements HeuristicFunction {

    @Override
    public double evaluate(GameState state) {
        // Si l'état est final, on retourne directement sa valeur (victoire, défaite ou match nul).
        if (state.isFinalState()) {
            return state.getGameValue();
        }

        // On se base sur le nombre de cases vides.
        AbstractMnkGameState mks = (AbstractMnkGameState) state;
        char[] board = mks.getBoard();
        int emptyCount = 0;
        for (char cell : board) {
            if (cell == ' ') {
                emptyCount++;
            }
        }

        // Si c'est le tour de PLAYER1, renvoyer le nombre de cases vides, sinon son opposé.
        return (state.getPlayerToMove() == 1) ? emptyCount : -emptyCount;
    }
}
