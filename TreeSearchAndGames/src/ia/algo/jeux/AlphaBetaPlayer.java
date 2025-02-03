package ia.algo.jeux;

import ia.framework.common.Action;
import ia.framework.common.ActionValuePair;
import ia.framework.jeux.Game;
import ia.framework.jeux.GameState;
import ia.framework.jeux.Player;
import ia.algo.jeux.HeuristicFunction;

public class AlphaBetaPlayer extends Player {
    private int max_depth;
    private HeuristicFunction heuristic;

    /**
     * Constructeur avec fonction heuristique.
     *
     * @param g Le jeu.
     * @param player_one True si le joueur est le PLAYER1.
     * @param max_depth La profondeur maximale de la recherche.
     * @param heuristic La fonction heuristique à utiliser pour évaluer les états non terminaux.
     */
    public AlphaBetaPlayer(Game g, boolean player_one, int max_depth, HeuristicFunction heuristic) {
        super(g, player_one);
        this.max_depth = (max_depth <= 0) ? Integer.MAX_VALUE : max_depth;
        this.heuristic = heuristic;
        if (heuristic instanceof AdvancedHeuristic) {
            this.name = "AlphaBetaA";
        } else if (heuristic instanceof BasicHeuristic) {
            this.name = "AlphaBetaB";
        } else {
            this.name = "AlphaBeta";
        }
    }

    @Override
    public Action getMove(GameState state) {
        ActionValuePair avp;
        if (this.player == PLAYER1) {
            avp = maxValue(state, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        } else {
            avp = minValue(state, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        }
        if (avp.getAction() == null) {
            if (!game.getActions(state).isEmpty()) {
                return game.getActions(state).get(0);
            }
        }
        return avp.getAction();
    }


    private ActionValuePair maxValue(GameState state, int depth, double alpha, double beta) {
        if (state.isFinalState() || depth >= max_depth) {
            return new ActionValuePair(null, heuristic.evaluate(state));
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
                return new ActionValuePair(bestAction, maxValue); // Coupure Beta
            }

            alpha = Math.max(alpha, maxValue);
        }

        return new ActionValuePair(bestAction, maxValue);
    }

    private ActionValuePair minValue(GameState state, int depth, double alpha, double beta) {
        if (state.isFinalState() || depth >= max_depth) {
            return new ActionValuePair(null, heuristic.evaluate(state));
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
                return new ActionValuePair(bestAction, minValue); // Coupure Alpha
            }

            beta = Math.min(beta, minValue);
        }

        return new ActionValuePair(bestAction, minValue);
    }
}
