package ia.algo.jeux;

import ia.framework.common.Action;
import ia.framework.common.ActionValuePair;
import ia.framework.jeux.Game;
import ia.framework.jeux.GameState;
import ia.framework.jeux.Player;
import ia.algo.jeux.HeuristicFunction;

public class MinMaxPlayer extends Player {
    private int max_depth;
    private HeuristicFunction heuristic; // La fonction heuristique à utiliser

    /**
     * Constructeur de MinMaxPlayer avec fonction heuristique.
     *
     * @param g Le jeu.
     * @param player_one True si le joueur est PLAYER1.
     * @param max_depth La profondeur maximale pour la recherche.
     * @param heuristic L'instance de la fonction heuristique à utiliser pour évaluer les états non terminaux.
     */
    public MinMaxPlayer(Game g, boolean player_one, int max_depth, HeuristicFunction heuristic) {
        super(g, player_one);
        this.max_depth = (max_depth <= 0) ? Integer.MAX_VALUE : max_depth;
        this.heuristic = heuristic;
        if (heuristic instanceof AdvancedHeuristic) {
            this.name = "MinMaxA";
        } else if (heuristic instanceof BasicHeuristic) {
            this.name = "MinMaxB";
        } else {
            this.name = "MinMax";
        }
    }

    public Action getMove(GameState state) {
        ActionValuePair avp;
        if (this.player == PLAYER1) {
            avp = maxValue(state, 0);
        } else {
            avp = minValue(state, 0);
        }
        // Si l'action renvoyée est null, on retourne par défaut une action légale (si disponible)
        if (avp.getAction() == null) {
            if (!game.getActions(state).isEmpty()) {
                return game.getActions(state).get(0);
            }
        }
        return avp.getAction();
    }

    private ActionValuePair maxValue(GameState state, int depth) {
        // Condition de terminaison : état final ou profondeur maximale atteinte.
        // Si l'état est final, utiliser state.getGameValue(), sinon évaluer via l'heuristique.
        if (state.isFinalState() || depth >= max_depth) {
            if (state.isFinalState()) {
                return new ActionValuePair(null, state.getGameValue());
            } else {
                return new ActionValuePair(null, heuristic.evaluate(state));
            }
        }

        double maxValue = Double.NEGATIVE_INFINITY;
        Action bestAction = null;

        for (Action action : game.getActions(state)) {
            GameState nextState = (GameState) game.doAction(state, action);
            incStateCounter();
            ActionValuePair result = minValue(nextState, depth + 1);

            if (result.getValue() > maxValue) {
                maxValue = result.getValue();
                bestAction = action;
            }
        }
        return new ActionValuePair(bestAction, maxValue);
    }

    private ActionValuePair minValue(GameState state, int depth) {
        // Condition de terminaison : état final ou profondeur maximale atteinte.
        if (state.isFinalState() || depth >= max_depth) {
            if (state.isFinalState()) {
                return new ActionValuePair(null, state.getGameValue());
            } else {
                return new ActionValuePair(null, heuristic.evaluate(state));
            }
        }

        double minValue = Double.POSITIVE_INFINITY;
        Action bestAction = null;

        for (Action action : game.getActions(state)) {
            GameState nextState = (GameState) game.doAction(state, action);
            incStateCounter();
            ActionValuePair result = maxValue(nextState, depth + 1);

            if (result.getValue() < minValue) {
                minValue = result.getValue();
                bestAction = action;
            }
        }
        return new ActionValuePair(bestAction, minValue);
    }
}
