package ia.algo.jeux;

import ia.framework.common.Action;
import ia.framework.common.ActionValuePair;
import ia.framework.jeux.Game;
import ia.framework.jeux.GameState;
import ia.framework.jeux.Player;

public class AlphaBetaPlayer extends Player {
    private int max_depth;

    public AlphaBetaPlayer(Game g, boolean player_one, int max_depth) {
        super(g, player_one);
        this.name = "AlphaBeta";
        this.max_depth = (max_depth <= 0) ? Integer.MAX_VALUE : max_depth;
    }

    @Override
    public Action getMove(GameState state) {
        if (this.player == PLAYER1) {
            return maxValue(state, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY).getAction();
        } else {
            return minValue(state, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY).getAction();
        }
    }

    private ActionValuePair maxValue(GameState state, int depth, double alpha, double beta) {
        if (state.isFinalState() || depth >= max_depth) {
            return new ActionValuePair(null, state.getGameValue());
        }

        double maxValue = Double.NEGATIVE_INFINITY;
        Action bestAction = null;

        for (Action action : game.getActions(state)) {
            GameState nextState = (GameState) game.doAction(state, action);
            incStateCounter();
            ActionValuePair result = minValue(nextState, depth + 1, alpha, beta);

            if (result.getValue() > maxValue) {
                maxValue = result.getValue();
                bestAction = action;
            }

            if (maxValue >= beta) {
                return new ActionValuePair(bestAction, maxValue); // Beta cutoff
            }

            alpha = Math.max(alpha, maxValue);
        }

        return new ActionValuePair(bestAction, maxValue);
    }

    private ActionValuePair minValue(GameState state, int depth, double alpha, double beta) {
        if (state.isFinalState() || depth >= max_depth) {
            return new ActionValuePair(null, state.getGameValue());
        }

        double minValue = Double.POSITIVE_INFINITY;
        Action bestAction = null;

        for (Action action : game.getActions(state)) {
            GameState nextState = (GameState) game.doAction(state, action);
            incStateCounter();
            ActionValuePair result = maxValue(nextState, depth + 1, alpha, beta);

            if (result.getValue() < minValue) {
                minValue = result.getValue();
                bestAction = action;
            }

            if (minValue <= alpha) {
                return new ActionValuePair(bestAction, minValue); // Alpha cutoff
            }

            beta = Math.min(beta, minValue);
        }

        return new ActionValuePair(bestAction, minValue);
    }
}
