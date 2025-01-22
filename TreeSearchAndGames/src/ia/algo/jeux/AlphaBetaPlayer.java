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
        this.name = "MinMaxAlphaBeta";
        this.max_depth = (max_depth <= 0) ? Integer.MAX_VALUE : max_depth;
    }

    @Override
    public Action getMove(GameState state) {
        if (this.player == PLAYER1) {
            return maxValue(state, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0).getAction();
        } else {
            return minValue(state, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0).getAction();
        }
    }

    private ActionValuePair maxValue(GameState state, double alpha, double beta, int depth) {
        if (state.isFinalState() || depth >= max_depth) {
            return new ActionValuePair(null, state.getGameValue());
        }

        double maxValue = Double.NEGATIVE_INFINITY;
        Action bestAction = null;

        for (Action action : game.getActions(state)) {
            GameState nextState = (GameState) game.doAction(state, action);
            incStateCounter();
            ActionValuePair result = minValue(nextState, alpha, beta, depth + 1);

            if (result.getValue() > maxValue) {
                maxValue = result.getValue();
                bestAction = action;

                if (maxValue > alpha) {
                    alpha = maxValue;
                }
                if (maxValue >= beta) {
                    return new ActionValuePair(bestAction, maxValue);
                }
            }
        }
        return new ActionValuePair(bestAction, maxValue);
    }

    private ActionValuePair minValue(GameState state, double alpha, double beta, int depth) {
        if (state.isFinalState() || depth >= max_depth) {
            return new ActionValuePair(null, state.getGameValue());
        }

        double minValue = Double.POSITIVE_INFINITY;
        Action bestAction = null;

        for (Action action : game.getActions(state)) {
            GameState nextState = (GameState) game.doAction(state, action);
            incStateCounter();
            ActionValuePair result = maxValue(nextState, alpha, beta, depth + 1);

            if (result.getValue() < minValue) {
                minValue = result.getValue();
                bestAction = action;

                if (minValue < beta) {
                    beta = minValue;
                }
                if (minValue <= alpha) {
                    return new ActionValuePair(bestAction, minValue);
                }
            }
        }
        return new ActionValuePair(bestAction, minValue);
    }
}